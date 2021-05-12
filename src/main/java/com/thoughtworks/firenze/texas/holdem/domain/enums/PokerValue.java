package com.thoughtworks.firenze.texas.holdem.domain.enums;

import com.thoughtworks.firenze.texas.holdem.exception.InvalidPokerValue;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum PokerValue {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13), A(14);

    private final Integer value;

    PokerValue(Integer value) {
        this.value = value;
    }

    public PokerValue getByValue(Integer value) {
        return Stream.of(values()).filter(it -> it.getValue().equals(value)).findAny()
                     .orElseThrow(InvalidPokerValue::new);
    }
}
