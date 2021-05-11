package com.thoughtworks.firenze.texas.holdem.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String name;
    private Boolean tookAction;
    @Builder.Default
    private Boolean active = true;
    private Integer wagers;
    private Integer roundWagers;
    private Integer totalChip;
    @Builder.Default
    private List<Card> cards = new ArrayList<>();

    public void wager(Integer followChip) {
        wagers += followChip - roundWagers;
        roundWagers = followChip;
    }

    int getRemainChips() {
        return getTotalChip() - getWagers();
    }

    boolean addCards(List<Card> deal) {
        return getCards().addAll(deal);
    }
}
