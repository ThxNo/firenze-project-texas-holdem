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

    def "should calculate game settlement when game is over"() {
        given:
        def preFlop = createCompletedRound(1)
        def flop = createCompletedRound(1)
        def turn = createCompletedRound(1)
        def river = createCompletedRound(1)
        def game = Spy(GameBuilder.withDefault()
                .waitingRounds()
                .currentRound(null)
                .ended(true)
                .completedRounds(new LinkedList<Round>([preFlop, flop, turn, river])).build())
        game.getWinner() >> ["D"]
        when:
        def result = game.calcuSettlement()
        then:
        result.playerSettlements[0].name == "A"
        result.playerSettlements[0].totalChips == 96
        result.playerSettlements[0].winChips == -4
        result.playerSettlements[1].name == "B"
        result.playerSettlements[1].totalChips == 96
        result.playerSettlements[1].winChips == -4
        result.playerSettlements[2].name == "C"
        result.playerSettlements[2].totalChips == 96
        result.playerSettlements[2].winChips == -4
        result.playerSettlements[3].name == "D"
        result.playerSettlements[3].totalChips == 112
        result.playerSettlements[3].winChips == 12
    }

    private static Round createCompletedRound(Integer followChip) {
        RoundBuilder.withDefault()
                .followChip(followChip)
                .chipPool(followChip * 4)
                .ended(true)
                .currentPlayer(null)
                .completedPlayers(new LinkedList<Player>([PlayerBuilder.withDefault().name("A").bettingChips(followChip).build(),
                                                          PlayerBuilder.withDefault().name("B").bettingChips(followChip).build(),
                                                          PlayerBuilder.withDefault().name("C").bettingChips(followChip).build(),
                                                          PlayerBuilder.withDefault().name("D").bettingChips(followChip).build()]))
        .build()
    }

}
