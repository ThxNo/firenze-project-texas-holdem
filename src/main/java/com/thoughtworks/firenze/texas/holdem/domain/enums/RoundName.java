package com.thoughtworks.firenze.texas.holdem.domain.enums;

import lombok.Getter;

@Getter
public enum RoundName {
    PRE_FLOP(0), FLOP(3), TURN(1), RIVER(1);

    private final int dealCount;

    RoundName(int dealCount) {
        this.dealCount = dealCount;
    }
}
