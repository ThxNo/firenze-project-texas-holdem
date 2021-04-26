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
public class Round {
    private Queue<Player> waitingPlayers;
    private Queue<Player> completedPlayers;
    private List<Player> abstainedPlayer;
    private Player currentPlayer;
    private Integer followChip;
    private Integer chipPool;

    public Round play(Operation operation) {
        Round result = CloneUtil.clone(this, Round.class);
        switch (operation.getAction()) {
            case PET:
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
                result.completedPlayers.add(currentPlayer);
                result.currentPlayer = result.waitingPlayers.poll();
                break;
            default:
                throw new RuntimeException("Not Allowed Action");
        }
        return result;
    }
}
