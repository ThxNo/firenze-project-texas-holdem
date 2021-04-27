package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.constants.Constants;
import com.thoughtworks.firenze.texas.holdem.utils.CloneUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Queue;

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
        Round result = CloneUtil.clone(this, Round.class);
        switch (operation.getAction()) {
            case PET:
                result.chipPool += currentPlayer.pet(followChip);
                result.completedPlayers.add(currentPlayer);
                result.currentPlayer = result.waitingPlayers.poll();
                break;
            case PASS:
                result.waitingPlayers.add(currentPlayer);
                result.currentPlayer = result.waitingPlayers.poll();
                break;
            case FOLD:
                result.abstainedPlayer.add(currentPlayer);
                result.currentPlayer = result.waitingPlayers.poll();
                break;
            case RAISE:
                while (!result.completedPlayers.isEmpty()) {
                    result.waitingPlayers.add(result.completedPlayers.poll());
                }
                result.chipPool += currentPlayer.raise(followChip);
                result.followChip = result.followChip * Constants.RAISE_MULTIPLE;
                result.completedPlayers.add(currentPlayer);
                result.currentPlayer = result.waitingPlayers.poll();
                break;
            default:
                throw new RuntimeException("Not Allowed Action");
        }
        if (result.shouldEndRound()) {
            result.ended = true;
        }
        return result;
    }

    private boolean shouldEndRound() {
        return (Objects.isNull(currentPlayer) && waitingPlayers.isEmpty()) ||
                countInGamePlayerCount() <= 1;
    }

    public Integer countInGamePlayerCount() {
        int result = waitingPlayers.size() + completedPlayers.size();
        return Objects.isNull(currentPlayer) ? result : result + 1;
    }
}
