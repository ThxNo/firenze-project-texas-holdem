package com.thoughtworks.firenze.texas.holdem.utils;

import com.thoughtworks.firenze.texas.holdem.domain.Card;
import com.thoughtworks.firenze.texas.holdem.domain.CardCombination;
import com.thoughtworks.firenze.texas.holdem.domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CardCombiner {
    public static final Integer COMBINATION_COUNT = 5;

    public static List<CardCombination> combine(List<Card> publicCards, Player player) {
        List<Card> cards = Stream.concat(player.getCards().stream(), publicCards.stream())
                                 .collect(Collectors.toList());
        if (cards.size() < COMBINATION_COUNT) {
            return new ArrayList<>();
        }
        return combine(cards, COMBINATION_COUNT);
    }

    public static List<CardCombination> combine(List<Card> cards, Integer combineCount) {
        if (combineCount == 1) {
            return cards.stream().map(CardCombination::new).collect(Collectors.toList());
        }
        return IntStream.range(0, cards.size() - combineCount + 1)
                 .mapToObj(index -> combine(cards.subList(index + 1, cards.size()), combineCount - 1)
                         .stream().map(it -> new CardCombination(it, cards.get(index))))
                 .flatMap(it -> it).collect(Collectors.toList());
    }
}
