package com.thoughtworks.firenze.texas.holdem.domain.enums;

import lombok.Getter;

@Getter
public enum CombinationType {
    ROYAL_FLUSH(19),
    STRAIGHT_FLUSH(18),
    FOUR_OF_A_KIND(17),
    FULL_HOUSE(16),
    FLUSH(15),
    STRAIGHT(14),
    THREE_OF_A_KIND(13),
    TWO_PAIR(12),
    ONE_PAIR(11),
    HIGH_CARD(10);

    private final Integer value;

    CombinationType(Integer value) {
        this.value = value;
    }
}
