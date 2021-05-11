package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.builder.GameBuilder
import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.domain.enums.RoundName
import com.thoughtworks.firenze.texas.holdem.domain.operation.Pet
import spock.lang.Specification

class PokerTest extends Specification {
    def "should get public pokers in flop"() {
        given:
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build(),
                PlayerBuilder.withDefault().name("D").build()]

        def game = GameBuilder.withDefaultPlayer(players).build()
        when:
        game.start()
        then:
        game.publicCards.size() == 0
        game.players.forEach( {
            assert it.cards.size() == 2
        })
        when:
        game.next(new Pet())
        game.next(new Pet())
        game.next(new Pet())
        game.next(new Pet())
        then:
        game.currentRound.name == RoundName.FLOP
        game.publicCards.size() == 3
    }

    def "should init poker"() {
        given:
        when:
        def poker = new Poker()
        then:
        poker.getCards().size() == 52
    }
}
