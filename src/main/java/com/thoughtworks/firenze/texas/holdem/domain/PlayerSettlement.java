package com.thoughtworks.firenze.texas.holdem.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSettlement {
    private String name;
    private Integer wagers;
    private Integer winChips;
    private Integer totalChips;

    public static PlayerSettlement of(Player player) {
        return PlayerSettlement.builder().name(player.getName())
                               .totalChips(player.getTotalChip())
                               .wagers(player.getWagers())
                               .build();
    }

    void win(Integer winningChips) {
        setTotalChips(getTotalChips() + winningChips);
        setWinChips(winningChips + getWinChips());
    }

    void deductingBettingChips() {
        setTotalChips(getTotalChips() - getWagers());
        setWinChips(-getWagers());
    }
}
