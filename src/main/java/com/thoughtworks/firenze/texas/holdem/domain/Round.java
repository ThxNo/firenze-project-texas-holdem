package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.domain.enums.RoundName;
import com.thoughtworks.firenze.texas.holdem.domain.operation.Operation;
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
    private RoundName name;
    private List<Player> players;
    private Queue<Player> awaitingPlayers;
    private Integer followChip;
    private Boolean ended;

    public Round next(Operation operation) {
        if (ended) {
            log.warn("round is ended, Operation: {} is invalid", operation);
            return this;
        }
        Player currentPlayer = awaitingPlayers.poll();

        operation.execute(this, currentPlayer);

        currentPlayer.setTookAction(true);
        if (shouldEndRound()) {
            ended = true;
        }
        return this;
    }

    public void inactive(Player currentPlayer) {
        currentPlayer.setActive(false);
    }

    public boolean await(Player currentPlayer) {
        return awaitingPlayers.add(currentPlayer);
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

    public void updateRoundAfterAllIn(Player currentPlayer) {
        getActivePlayers().forEach(player -> {
            player.setWager(player.getRoundWager() - currentPlayer.getRemainChips());
            player.setRoundWager(player.getRoundWager() - currentPlayer.getRemainChips());
        });
        inactive(currentPlayer);
    }

    public Player getCurrentPlayer() {
        return awaitingPlayers.peek();
    }

    public Round initNextRound() {
        Round nextRound = Round.builder()
                               .name(RoundName.values()[getName().ordinal() + 1])
                               .ended(false)
                               .followChip(followChip)
                               .players(players)
                               .awaitingPlayers(awaitingPlayers)
                               .build();
        nextRound.getAllPlayers().forEach(player -> {
            player.setRoundWager(0);
            player.setTookAction(false);
        });
        return nextRound;
    }

    public Integer getCurrentPlayerRemainChips() {
        return getCurrentPlayer().getRemainChips();
    }

}
