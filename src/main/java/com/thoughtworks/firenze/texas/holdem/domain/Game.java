package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.utils.CloneUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Queue;

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
        Game game = CloneUtil.clone(this, Game.class);
        game.currentRound = game.currentRound.next(operation);
        if (game.shouldEndGame()) {
            game.ended = true;
        } else if (game.shouldBeginNextRound()) {
            game.completedRounds.add(game.currentRound);
            game.currentRound = waitingRounds.poll();
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
}
