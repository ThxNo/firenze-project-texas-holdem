package com.thoughtworks.firenze.texas.holdem.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSettlement {
    private List<PlayerSettlement> playerSettlements;

    public static GameSettlement getGameSettlement(List<Player> players) {
        return builder()
                .playerSettlements(players.stream().map(PlayerSettlement::of).collect(Collectors.toList()))
                .build();
    }

    public void settle(List<String> winners) {
        playerSettlements.forEach(playerSettlement -> {
            playerSettlement.setTotalChips(playerSettlement.getTotalChips() - playerSettlement.getWagers());
            playerSettlement.setWinChips(-playerSettlement.getWagers());
        });

        if (!winners.isEmpty()) {
            Integer winningChips = getTotalWagers() / winners.size();
            winners.forEach(winner -> playerSettlements.stream()
                                                       .filter(playerSettlement -> winner.equals(playerSettlement.getName()))
                                                       .forEach(playerSettlement -> {
                                                           playerSettlement.setTotalChips(playerSettlement.getTotalChips() + winningChips);
                                                           playerSettlement.setWinChips(winningChips + playerSettlement.getWinChips());
                                                       }));
        }
    }

    private Integer getTotalWagers() {
        return playerSettlements.stream()
                                .map(PlayerSettlement::getWagers)
                                .reduce(Integer::sum).get();
    }
}
