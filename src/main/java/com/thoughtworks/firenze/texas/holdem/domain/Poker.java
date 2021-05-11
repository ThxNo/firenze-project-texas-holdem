package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerType;
import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
public class Poker {
    Queue<Card> cards;

    public Poker() {
        List<Card> cards = Stream.of(PokerValue.values())
                                 .flatMap(value -> Stream.of(PokerType.values())
                                                         .map(type -> Card.builder()
                                                                          .value(value)
                                                                          .type(type)
                                                                          .build()))
                                 .collect(Collectors.toList());
        Collections.shuffle(cards);
        this.cards = new LinkedList<>(cards);
    }

    public List<Card> deal(int count) {
        return IntStream.range(0, count)
                        .mapToObj(it -> cards.poll())
                        .collect(Collectors.toList());
    }
}
