package com.thoughtworks.firenze.texas.holdem.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSettlement {
    private List<PlayerSettlement> playerSettlements;

    public void settle(List<String> winners) {
        playerSettlements.forEach(playerSettlement -> {
            playerSettlement.setTotalChips(playerSettlement.getTotalChips() - playerSettlement.getBettingChips());
            playerSettlement.setWinChips(-playerSettlement.getBettingChips());
        });

        Optional<Integer> totalPetChips = playerSettlements.stream().map(PlayerSettlement::getBettingChips).reduce(Integer::sum);
        if (totalPetChips.isPresent() && !winners.isEmpty()) {
            Integer winningChips = totalPetChips.get() / winners.size();
            winners.forEach(winner -> playerSettlements.stream()
                                                       .filter(playerSettlement -> winner.equals(playerSettlement.getName()))
                                                       .forEach(playerSettlement -> {
                                                           playerSettlement.setTotalChips(playerSettlement.getTotalChips() + winningChips);
                                                           playerSettlement.setWinChips(winningChips + playerSettlement.getWinChips());
                                                       }));
        }
    }
}
