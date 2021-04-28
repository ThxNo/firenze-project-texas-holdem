package com.thoughtworks.firenze.texas.holdem.domain;

import com.google.common.collect.Streams;
import com.thoughtworks.firenze.texas.holdem.constants.Constants;
import com.thoughtworks.firenze.texas.holdem.exception.InvalidOperationException;
import com.thoughtworks.firenze.texas.holdem.utils.CloneUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Round {
    private Queue<Player> waitingPlayers;
    private Queue<Player> completedPlayers;
    private List<Player> abstainedPlayer;
    private Player currentPlayer;
    private Integer followChip;
    private Integer chipPool;
    private Boolean ended;

    public Round next(Operation operation) {
        if (ended) {
            log.warn("round is ended, Operation: {} is invalid", operation);
            return this;
        }
        Round result = CloneUtil.clone(this, Round.class);
        switch (operation.getAction()) {
            case PET:
                result.pet();
                break;
            case PASS:
                result.pass();
                break;
            case FOLD:
                result.fold();
                break;
            case RAISE:
                result.raise();
                break;
            default:
                throw new InvalidOperationException("Not Allowed Action");
        }
        if (result.shouldEndRound()) {
            result.ended = true;
        }
        return result;
    }

    private void pet() {
        chipPool += currentPlayer.pet(followChip);
        completedPlayers.add(currentPlayer);
        currentPlayer = waitingPlayers.poll();
    }

    private void pass() {
        waitingPlayers.add(currentPlayer);
        currentPlayer = waitingPlayers.poll();
    }

    private void fold() {
        abstainedPlayer.add(currentPlayer);
        currentPlayer = waitingPlayers.poll();
    }

    private void raise() {
        while (!completedPlayers.isEmpty()) {
            waitingPlayers.add(completedPlayers.poll());
        }
        chipPool += currentPlayer.raise(followChip);
        followChip = followChip * Constants.RAISE_MULTIPLE;
        completedPlayers.add(currentPlayer);
        currentPlayer = waitingPlayers.poll();
    }

    private boolean shouldEndRound() {
        return (Objects.isNull(currentPlayer) && waitingPlayers.isEmpty()) ||
                countInGamePlayerCount() <= 1;
    }

    public Integer countInGamePlayerCount() {
        int result = waitingPlayers.size() + completedPlayers.size();
        return Objects.isNull(currentPlayer) ? result : result + 1;
    }

    public List<Player> getAllPlayers() {
        List<Player> result = Streams.concat(waitingPlayers.stream(), abstainedPlayer.stream(), completedPlayers.stream())
                                     .collect(Collectors.toList());
        if (Objects.nonNull(currentPlayer)) {
            result.add(currentPlayer);
        }
        return result;
    }
}
