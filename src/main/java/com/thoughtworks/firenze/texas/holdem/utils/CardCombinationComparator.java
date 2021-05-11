package com.thoughtworks.firenze.texas.holdem.utils;

import com.thoughtworks.firenze.texas.holdem.domain.CardCombination;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CardCombinationComparator {

    public static CardCombination getLargestCombination(List<CardCombination> cardCombinations) {
        if (Objects.isNull(cardCombinations) || cardCombinations.isEmpty()) {
            return null;
        }
        return cardCombinations.stream()
                               .max(Comparator.comparing(CardCombination::getScore))
                               .get();
    }
}
