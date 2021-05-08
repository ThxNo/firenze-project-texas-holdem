package com.thoughtworks.firenze.texas.holdem.domain;

import com.google.common.collect.ImmutableList;
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;
import com.thoughtworks.firenze.texas.holdem.exception.GameNotEndedException;
import com.thoughtworks.firenze.texas.holdem.exception.PlayerNotFoundException;
import com.thoughtworks.firenze.texas.holdem.utils.CloneUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    private Round currentRound;
    private Queue<Round> waitingRounds;
    private List<Round> completedRounds;
    private Boolean ended;
    private List<Game> settlementPointGames;

    public Game next(Operation operation) {
        if (ended) {
            log.warn("Game is ended, operation:{} is invalid", operation);
            return this;
        }
        if (Action.ALL_IN.equals(operation.getAction())) {
            Integer currentPlayerRemainChips = getCurrentPlayerRemainChips();
            buildSettlementPointGame(currentPlayerRemainChips);
            completedRounds = new ArrayList<>();
            currentRound.updateRoundAfterAllIn(currentPlayerRemainChips);
        }else {
            currentRound = currentRound.next(operation);
        }
        if (shouldEndGame()) {
            ended = true;
        } else if (shouldBeginNextRound()) {
            completedRounds.add(currentRound);
            currentRound = waitingRounds.poll();
        }
        return this;
    }

    private void buildSettlementPointGame(Integer currentPlayerRemainChips) {
        List<Player> players = new ArrayList<>();
        currentRound.getAllPlayers().forEach(player -> players.add(CloneUtil.clone(player, Player.class)));
        players.stream().filter(Player::getActive).forEach(player -> {
            player.setWager(player.getWager() - player.getRoundWager() + currentPlayerRemainChips);
            player.setRoundWager(currentPlayerRemainChips);
        });
        Round endedRound = Round.builder()
                           .players(players)
                           .awaitingPlayers(new LinkedList<>())
                           .followChip(currentPlayerRemainChips)
                           .ended(true)
                           .build();
        List<Round> settlementPointCompletedRounds = new ArrayList<>(completedRounds);
        settlementPointCompletedRounds.add(endedRound);
        settlementPointGames.add(Game.builder()
                                     .completedRounds(settlementPointCompletedRounds)
                                     .waitingRounds(new LinkedList<>())
                                     .ended(true)
                                     .build());
    }

    private Integer getCurrentPlayerRemainChips() {
        Player currentPlayer = currentRound.getCurrentPlayer();
        Integer wagers = currentPlayer.getRoundWager();
        for (Round completedRound : completedRounds) {
            Player playerInCompletedRound = completedRound.getAllPlayers().stream()
                                                          .filter(player -> StringUtils.equals(player.getName(), currentPlayer.getName()))
                                                          .findAny().orElseThrow(PlayerNotFoundException::new);
            wagers += playerInCompletedRound.getRoundWager();
        }
        return currentPlayer.getTotalChip() - wagers;
    }

    private Boolean shouldBeginNextRound() {
        return currentRound.getEnded() &&
                currentRound.countInGamePlayerCount() > 1 &&
                !waitingRounds.isEmpty();
    }

    private Boolean shouldEndGame() {
        return (currentRound.getEnded() && currentRound.countInGamePlayerCount() <= 1) ||
                waitingRounds.isEmpty();
    }

    public GameSettlement calcuSettlement() {
        if (!ended) {
            throw new GameNotEndedException();
        }
        List<GameSettlement> settlements = settlementPointGames.stream()
                                                               .map(Game::doSettlement)
                                                               .collect(Collectors.toList());
        settlements.add(doSettlement());
        return merge(settlements);
    }

    private GameSettlement merge(List<GameSettlement> settlements) {
        HashMap<String, PlayerSettlement> playerName2PlayerSettlement = new HashMap<>();
        settlements.forEach(gameSettlement -> gameSettlement.getPlayerSettlements().forEach(
                playerSettlement -> {
                    if (playerName2PlayerSettlement.containsKey(playerSettlement.getName())) {
                        PlayerSettlement settlement = playerName2PlayerSettlement.get(playerSettlement.getName());
                        settlement.setBettingChips(settlement.getBettingChips() + playerSettlement.getBettingChips());
                        settlement.setWinChips(settlement.getWinChips() + playerSettlement.getWinChips());
                        settlement.setTotalChips(settlement.getTotalChips() + playerSettlement.getWinChips());
                    } else {
                        playerName2PlayerSettlement.putIfAbsent(playerSettlement.getName(), playerSettlement);
                    }
                }
        ));
        return GameSettlement.builder().playerSettlements(new ArrayList<>(playerName2PlayerSettlement.values())).build();
    }

    private GameSettlement doSettlement() {
        GameSettlement gameSettlement = getGameSettlement();
        List<String> winner = calcuWinner();
        gameSettlement.settle(winner);
        return gameSettlement;
    }

    private GameSettlement getGameSettlement() {
        HashMap<String, PlayerSettlement> playerName2Settlement = new HashMap<>();
        completedRounds.stream().map(Round::getAllPlayers).forEach(players -> {
            players.forEach(player -> {
                if (playerName2Settlement.containsKey(player.getName())) {
                    PlayerSettlement settlement = playerName2Settlement.get(player.getName());
                    settlement.setBettingChips(settlement.getBettingChips() + player.getRoundWager());
                } else {
                    playerName2Settlement.putIfAbsent(player.getName(), PlayerSettlement.of(player));
                }
            });
        });
        return GameSettlement.builder()
                             .playerSettlements(new ArrayList<>(playerName2Settlement.values()))
                             .build();
    }

    public List<String> calcuWinner() {
        //TODO: 计算组合牌大小
        return ImmutableList.of("A");
    }
}
