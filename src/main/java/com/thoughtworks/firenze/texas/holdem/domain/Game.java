package com.thoughtworks.firenze.texas.holdem.domain;

import com.google.common.collect.ImmutableList;
import com.thoughtworks.firenze.texas.holdem.domain.enums.RoundName;
import com.thoughtworks.firenze.texas.holdem.domain.operation.Operation;
import com.thoughtworks.firenze.texas.holdem.exception.GameNotEndedException;
import com.thoughtworks.firenze.texas.holdem.utils.CloneUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    public List<Player> players;
    public Round currentRound;
    private List<Round> completedRounds;
    private Boolean ended;
    private List<Game> settlementPointGames;

    public Game next(Operation operation) {
        if (ended) {
            log.warn("Game is ended, operation:{} is invalid", operation);
            return this;
        }
        operation.execute(this);
        if (shouldEndGame()) {
            end();
        } else if (shouldBeginNextRound()) {
            nextRound(currentRound.initNextRound());
        }
        return this;
    }

    private void nextRound(Round round) {
        completedRounds.add(currentRound);
        currentRound = round;
    }

    private void end() {
        nextRound(null);
        ended = true;
    }

    public void buildSettlementPointGame() {
        Integer currentPlayerRemainChips = currentRound.getCurrentPlayerRemainChips();
        List<Player> players = new ArrayList<>();
        currentRound.getAllPlayers().forEach(player -> players.add(CloneUtil.clone(player, Player.class)));
        players.stream().filter(Player::getActive).forEach(player -> {
            player.setWagers(player.getWagers() - player.getRoundWagers() + currentPlayerRemainChips);
            player.setRoundWagers(currentPlayerRemainChips);
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
                                     .ended(true)
                                     .build());
    }

    private Boolean shouldBeginNextRound() {
        return currentRound.getEnded() &&
                currentRound.countInGamePlayerCount() > 1 &&
                !RoundName.RIVER.equals(currentRound.getName());
    }

    private Boolean shouldEndGame() {
        return (currentRound.getEnded() && currentRound.countInGamePlayerCount() <= 1) ||
                RoundName.RIVER.equals(currentRound.getName());
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
                        settlement.setWagers(settlement.getWagers() + playerSettlement.getWagers());
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
        return GameSettlement.builder()
                             .playerSettlements(players.stream().map(PlayerSettlement::of).collect(Collectors.toList()))
                             .build();
    }

    public List<String> calcuWinner() {
        //TODO: 计算组合牌大小
        return ImmutableList.of("A");
    }
}
