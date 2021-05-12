package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.domain.enums.RoundName;
import com.thoughtworks.firenze.texas.holdem.domain.operation.Operation;
import com.thoughtworks.firenze.texas.holdem.exception.GameNotEndedException;
import com.thoughtworks.firenze.texas.holdem.utils.CardCombinationComparator;
import com.thoughtworks.firenze.texas.holdem.utils.CardCombiner;
import com.thoughtworks.firenze.texas.holdem.utils.CloneUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
    @Builder.Default
    private Poker poker = new Poker();
    @Builder.Default
    private List<Card> publicCards = new ArrayList<>();

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
        if (Objects.nonNull(round)) {
            publicCards.addAll(poker.deal(round.getName().getDealCount()));
        }
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
                                                               .peek(it -> it.setPublicCards(publicCards))
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
        List<String> winners = calcuWinner();
        GameSettlement gameSettlement = GameSettlement.getGameSettlement(players);
        gameSettlement.settle(winners);
        return gameSettlement;
    }

    public List<String> calcuWinner() {
        List<Pair<Player, CardCombination>> player2CardCombination = players
                .stream().map(player ->
                        Pair.of(player, CardCombinationComparator.getLargestCombination(CardCombiner.combine(publicCards, player))))
                .sorted(Comparator.comparing(pair -> pair.getRight().getScore(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < player2CardCombination.size() - 1; i++) {
            if (i == 0) {
                result.add(player2CardCombination.get(i).getKey().getName());
            }
            if (Objects.equals(player2CardCombination.get(i).getRight().getScore(),
                    player2CardCombination.get(i + 1).getRight().getScore())) {
                result.add(player2CardCombination.get(i + 1).getKey().getName());
            }
        }
        return result;
    }

    public List<Card> getPublicCards() {
        return publicCards;
    }

    public void start() {
        players.forEach(player -> player.addCards(poker.deal(2)));
    }

}
