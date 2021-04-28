package com.thoughtworks.firenze.texas.holdem.domain;

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
import java.util.List;
import java.util.Queue;

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

    public Game next(Operation operation) {
        if (ended) {
            log.warn("Game is ended, operation:{} is invalid", operation);
            return this;
        }
        Game game = CloneUtil.clone(this, Game.class);
        game.currentRound = game.currentRound.next(operation);
        if (game.shouldEndGame()) {
            game.ended = true;
        } else if (game.shouldBeginNextRound()) {
            game.completedRounds.add(game.currentRound);
            game.currentRound = game.waitingRounds.poll();
        }
        return game;
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
        GameSettlement gameSettlement = getGameSettlement();
        List<String> winner = getWinner();
        gameSettlement.settle(winner);
        return gameSettlement;
    }

    private GameSettlement getGameSettlement() {
        HashMap<String, PlayerSettlement> playerName2Settlement = new HashMap<>();
        completedRounds.stream().map(Round::getAllPlayers).forEach(players -> {
            players.forEach(player -> {
                if (playerName2Settlement.containsKey(player.getName())) {
                    PlayerSettlement settlement = playerName2Settlement.get(player.getName());
                    settlement.setBettingChips(settlement.getBettingChips() + player.getBettingChips());
                } else {
                    playerName2Settlement.putIfAbsent(player.getName(), PlayerSettlement.of(player));
                }
            });
        });
        return GameSettlement.builder()
                             .playerSettlements(new ArrayList<>(playerName2Settlement.values()))
                             .build();
    }

    public List<String> getWinner() {
        //TODO: 计算组合牌大小
        return new ArrayList<>();
    }
}
