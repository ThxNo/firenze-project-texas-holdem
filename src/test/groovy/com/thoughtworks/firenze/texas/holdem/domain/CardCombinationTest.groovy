package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerType
import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerValue
import spock.lang.Specification

class CardCombinationTest extends Specification {
    def "should calculate score with straight flush"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.TWO).type(PokerType.HEART).build()
        def card3 = Card.builder().value(PokerValue.THREE).type(PokerType.HEART).build()
        def card4 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 180605040302
    }

    def "should calculate score with straight"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.TWO).type(PokerType.HEART).build()
        def card3 = Card.builder().value(PokerValue.THREE).type(PokerType.DIAMOND).build()
        def card4 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 140605040302
    }

    def "should calculate score with flush"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.JACK).type(PokerType.HEART).build()
        def card3 = Card.builder().value(PokerValue.THREE).type(PokerType.HEART).build()
        def card4 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 151106050403
    }

    def "should calculate score with FOUR_OF_A_KIND"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.SIX).type(PokerType.DIAMOND).build()
        def card3 = Card.builder().value(PokerValue.SIX).type(PokerType.CLUB).build()
        def card4 = Card.builder().value(PokerValue.SIX).type(PokerType.SPADE).build()
        def card5 = Card.builder().value(PokerValue.SEVEN).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 170606060607
    }

    def "should calculate score with FULL_HOUSE"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.SIX).type(PokerType.DIAMOND).build()
        def card3 = Card.builder().value(PokerValue.SIX).type(PokerType.CLUB).build()
        def card4 = Card.builder().value(PokerValue.SEVEN).type(PokerType.SPADE).build()
        def card5 = Card.builder().value(PokerValue.SEVEN).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 160606060707
    }

    def "should calculate score with THREE_OF_A_KIND"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.SIX).type(PokerType.DIAMOND).build()
        def card3 = Card.builder().value(PokerValue.SIX).type(PokerType.CLUB).build()
        def card4 = Card.builder().value(PokerValue.SEVEN).type(PokerType.SPADE).build()
        def card5 = Card.builder().value(PokerValue.EIGHT).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 130606060807
    }

    def "should calculate score with TWO_PAIR"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.SIX).type(PokerType.DIAMOND).build()
        def card3 = Card.builder().value(PokerValue.SEVEN).type(PokerType.CLUB).build()
        def card4 = Card.builder().value(PokerValue.SEVEN).type(PokerType.SPADE).build()
        def card5 = Card.builder().value(PokerValue.EIGHT).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 120707060608
    }

    def "should calculate score with ONE_PAIR"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.FOUR).type(PokerType.CLUB).build()
        def card3 = Card.builder().value(PokerValue.THREE).type(PokerType.HEART).build()
        def card4 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 110404060503
    }

    def "should calculate score with HIGH_CARD"() {
        given:
        def card1 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.JACK).type(PokerType.HEART).build()
        def card3 = Card.builder().value(PokerValue.NINE).type(PokerType.CLUB).build()
        def card4 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def cardCombination = CardCombination.builder().cards([card1, card2, card3, card4, card5]).build()
        when:
        cardCombination.calculateScore()
        then:
        cardCombination.score == 101109060504
    }
}
