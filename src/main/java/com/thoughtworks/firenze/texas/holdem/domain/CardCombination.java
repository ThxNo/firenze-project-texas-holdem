package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.exception.WrongCardCombinationSizeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    }
}
