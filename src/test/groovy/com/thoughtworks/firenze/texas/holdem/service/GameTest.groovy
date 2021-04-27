package com.thoughtworks.firenze.texas.holdem.service

import com.thoughtworks.firenze.texas.holdem.builder.GameBuilder
import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.builder.RoundBuilder
import com.thoughtworks.firenze.texas.holdem.domain.Operation
import com.thoughtworks.firenze.texas.holdem.domain.Player
import com.thoughtworks.firenze.texas.holdem.domain.Round
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action
import spock.lang.Specification

class GameTest extends Specification {
    def "should end game when only one player remain"() {
        given:
        def currentRound = RoundBuilder.withDefault()
                .currentPlayer(PlayerBuilder.withDefault().name("A").build())
                .waitingPlayers(new LinkedList<Player>([PlayerBuilder.withDefault().name("B").build()]))
                .abstainedPlayer([PlayerBuilder.withDefault().name("C").build(),
                                  PlayerBuilder.withDefault().name("D").build()])
                .build()
        def game = GameBuilder.withDefault()
                .waitingRounds(new LinkedList<Round>([RoundBuilder.withDefault().build()]))
                .currentRound(currentRound).build()
        when:
        def result = game.next(Operation.builder().action(Action.FOLD).build())
        then:
        result.ended
        result.currentRound.ended
    }

    def "should start a new round when current round ended"() {
        given:
        def currentRound = RoundBuilder.withDefault()
                .currentPlayer(PlayerBuilder.withDefault().name("A").build())
                .completedPlayers(new LinkedList<Player>([PlayerBuilder.withDefault().name("B").build()]))
                .abstainedPlayer([PlayerBuilder.withDefault().name("C").build(),
                                  PlayerBuilder.withDefault().name("D").build()])
                .build()
        def game = GameBuilder.withDefault()
                .waitingRounds(new LinkedList<Round>([RoundBuilder.withDefault().build()]))
                .currentRound(currentRound).build()
        when:
        def result = game.next(Operation.builder().action(Action.PET).build())
        then:
        !result.ended
        result.completedRounds.last().ended
    }
}
