package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.constants.Constants;
import com.thoughtworks.firenze.texas.holdem.exception.InvalidOperationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Round {
    private List<Player> players;
    private Queue<Player> awaitingPlayers;
    private Integer followChip;
    private Boolean ended;

    public Round next(Operation operation) {
        if (ended) {
            log.warn("round is ended, Operation: {} is invalid", operation);
            return this;
        }
        switch (operation.getAction()) {
            case PET:
                pet();
                break;
            case PASS:
                pass();
                break;
            case FOLD:
                fold();
                break;
            case RAISE:
                raise();
                break;
            default:
                throw new InvalidOperationException("Not Allowed Action");
        }
        if (shouldEndRound()) {
            ended = true;
        }
        return this;
    }

    private void pet() {
        Player currentPlayer = awaitingPlayers.poll();

        currentPlayer.wager(followChip);
        currentPlayer.setTookAction(true);

        awaitingPlayers.add(currentPlayer);
    }

    private void pass() {
        Player currentPlayer = awaitingPlayers.poll();

        currentPlayer.setTookAction(true);

        awaitingPlayers.add(currentPlayer);
    }

    private void fold() {
        Player currentPlayer = awaitingPlayers.poll();

        currentPlayer.setActive(false);
        currentPlayer.setTookAction(true);
    }

    private void raise() {
        Player currentPlayer = awaitingPlayers.poll();

        followChip = followChip * Constants.RAISE_MULTIPLE;
        currentPlayer.wager(followChip);
        currentPlayer.setTookAction(true);

        awaitingPlayers.add(currentPlayer);
    }

    private boolean shouldEndRound() {
        return getActivePlayers().stream().allMatch(player -> player.getRoundWager().equals(followChip)) ||
                countInGamePlayerCount() <= 1;
    }

    public List<Player> getActivePlayers() {
        return players.stream().filter(Player::getActive).collect(Collectors.toList());
    }

    public Integer countInGamePlayerCount() {
        return ((Long) players.stream().filter(Player::getActive).count()).intValue();
    }

    public List<Player> getAllPlayers() {
        return players;
    }

    void updateRoundAfterAllIn(Integer currentPlayerRemainChips) {
        Player currentPlayer = awaitingPlayers.poll();
        getActivePlayers().forEach(player -> {
            player.setWager(player.getRoundWager() - currentPlayerRemainChips);
            player.setRoundWager(player.getRoundWager() - currentPlayerRemainChips);
        });
        currentPlayer.setActive(false);
    }

    public Player getCurrentPlayer() {
        return awaitingPlayers.peek();
    }
}
