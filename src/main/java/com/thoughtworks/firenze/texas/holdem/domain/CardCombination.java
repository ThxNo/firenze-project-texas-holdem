package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.domain.enums.CombinationType;
import com.thoughtworks.firenze.texas.holdem.exception.NoCombinationTypeException;
import com.thoughtworks.firenze.texas.holdem.exception.WrongCardCombinationSizeException;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CardCombination {
    @Builder.Default
    private List<Card> cards = new ArrayList<>();
    private Long score;

    public CardCombination(CardCombination cardCombination, Card card) {
        cards = new ArrayList<>(cardCombination.getCards());
        cards.add(card);
    }

    public CardCombination(Card card) {
        cards = new ArrayList<>();
        cards.add(card);
    }

    public void calculateScore() {
        if (cards.size() != 5) {
            throw new WrongCardCombinationSizeException();
        }
        List<Card> sortedCards = sortCards();
        CombinationType type = getCombinationType(sortedCards);
        score = generateScore(type, sortedCards);
    }

    private List<Card> sortCards() {
        int weight;
        List<Pair<Card, Integer>> cardsWeight = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            weight = 0;
            for (Card card : cards) {
                if (cards.get(i).getNumericValue().equals(card.getNumericValue())) {
                    weight++;
                }
            }
            cardsWeight.add(new Pair(cards.get(i), weight));
        }
        return cardsWeight.stream()
                          .sorted(Comparator.comparing(pair -> pair.getKey().getNumericValue() + pair.getValue() * 100, Comparator.reverseOrder()))
                          .map(Pair::getKey)
                          .collect(Collectors.toList());
    }

    private CombinationType getCombinationType(List<Card> cards) {
        if (isStraight(cards) && isFlush(cards)) {
            return CombinationType.STRAIGHT_FLUSH;
        } else if (isStraight(cards)) {
            return CombinationType.STRAIGHT;
        } else if (isFlush(cards)) {
            return CombinationType.FLUSH;
        }
        int flag = 0;
        for (int i = 0; i < cards.size(); i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                if (cards.get(i).getNumericValue().equals(cards.get(j).getNumericValue())) {
                    flag++;
                }
            }
        }
        switch (flag) {
            case 6:
                return CombinationType.FOUR_OF_A_KIND;
            case 4:
                return CombinationType.FULL_HOUSE;
            case 3:
                return CombinationType.THREE_OF_A_KIND;
            case 2:
                return CombinationType.TWO_PAIR;
            case 1:
                return CombinationType.ONE_PAIR;
            case 0:
                return CombinationType.HIGH_CARD;
            default:
                throw new NoCombinationTypeException();
        }
    }

    private Long generateScore(CombinationType type, List<Card> sortedCards) {
        return Long.valueOf(type.getValue().toString() + getCombinationScore(sortedCards));
    }

    private String getCombinationScore(List<Card> sortedCards) {
        return sortedCards.stream()
                          .map(card -> String.format("%02d", card.getNumericValue()))
                          .reduce("", (result, current) -> result + current);
    }

    private boolean isStraight(List<Card> sortedCards) {
        for (int i = 0; i < sortedCards.size() - 1; i++) {
            if (sortedCards.get(i).getNumericValue() - sortedCards.get(i + 1).getNumericValue() != 1) {
                return false;
            }
        }
        return true;
    }

    private boolean isFlush(List<Card> sortedCards) {
        for (int i = 0; i < sortedCards.size() - 1; i++) {
            if (sortedCards.get(i).getType() != sortedCards.get(i + 1).getType()) {
                return false;
            }
        }
        return true;
    }
}
