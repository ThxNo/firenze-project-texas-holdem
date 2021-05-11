package com.thoughtworks.firenze.texas.holdem.utils

import com.thoughtworks.firenze.texas.holdem.domain.Card
import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerType
import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerValue
import spock.lang.Specification

class CardCombinerTest extends Specification {

    def "should combine card"() {
        def card1 = Card.builder().value(PokerValue.A).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.TWO).type(PokerType.HEART).build()
        given:
        def cards = [card1, card2]
        def combineCount = 1
        when:
        def result = CardCombiner.combine(cards, combineCount)
        then:
        result.size() == 2
    }
    def "should combine multi card"() {
        def card1 = Card.builder().value(PokerValue.A).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.TWO).type(PokerType.HEART).build()
        def card3 = Card.builder().value(PokerValue.THREE).type(PokerType.HEART).build()
        def card4 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def card6 = Card.builder().value(PokerValue.SIX).type(PokerType.HEART).build()
        def card7 = Card.builder().value(PokerValue.SEVEN).type(PokerType.HEART).build()
        given:
        def cards = [card1, card2, card3, card4, card5, card6, card7]
        def combineCount = 5
        when:
        def result = CardCombiner.combine(cards, combineCount)
        then:
        result.size() == 21
    }
}
