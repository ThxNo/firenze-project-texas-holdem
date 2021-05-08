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
public class Player {
    private String name;
    private Boolean tookAction;
    @Builder.Default
    private Boolean active = true;
    private Integer wager;
    private Integer roundWager;
    private Integer totalChip;

    public void wager(Integer followChip) {
        wager += followChip - roundWager;
        roundWager = followChip;
    }
}
