package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.builder.GameBuilder
import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.builder.RoundBuilder
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
        def completedPlayers = new LinkedList<Player>([PlayerBuilder.withDefault().name("A").bettingChips(1).build(),
                                                       PlayerBuilder.withDefault().name("B").bettingChips(1).build(),
                                                       PlayerBuilder.withDefault().name("C").bettingChips(1).build(),
                                                       PlayerBuilder.withDefault().name("D").bettingChips(1).build()])
        given:
        def preFlop = createCompletedRound(1, completedPlayers)
        def flop = createCompletedRound(1, completedPlayers)
        def turn = createCompletedRound(1, completedPlayers)
        def river = createCompletedRound(1, completedPlayers)
        def game = Spy(GameBuilder.withDefault()
                .waitingRounds()
                .currentRound(null)
                .ended(true)
                .completedRounds(new LinkedList<Round>([preFlop, flop, turn, river])).build())
        game.calcuWinner() >> ["D"]
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

    def "should split game settlement point when player all in"() {
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
        def result = game.next(Operation.builder().action(Action.ALL_IN).build())
        then:
        result.settlementPointGames.size() == 1
        result.settlementPointGames[0].ended
    }


    def "should calculate game settlement with all in when game is over"() {
        def completedPlayers = new LinkedList<Player>([PlayerBuilder.withDefault().name("A").bettingChips(2).build(),
                                                       PlayerBuilder.withDefault().name("B").bettingChips(2).build(),
                                                       PlayerBuilder.withDefault().name("C").bettingChips(2).build(),
                                                       PlayerBuilder.withDefault().name("D").bettingChips(2).totalChip(5).build()])
        given:
        def preFlop = createCompletedRound(2, completedPlayers)
        def flop = createCompletedRound(2, completedPlayers)
        def turn1 = createCompletedRound(1, new LinkedList<Player>([PlayerBuilder.withDefault().name("A").bettingChips(1).build(),
                                                                    PlayerBuilder.withDefault().name("B").bettingChips(1).build(),
                                                                    PlayerBuilder.withDefault().name("C").bettingChips(1).build(),
                                                                    PlayerBuilder.withDefault().name("D").bettingChips(1).totalChip(5).build()]))
        def turn2 = createCompletedRound(1, new LinkedList<Player>([PlayerBuilder.withDefault().name("A").bettingChips(1).build(),
                                                                    PlayerBuilder.withDefault().name("B").bettingChips(1).build(),
                                                                    PlayerBuilder.withDefault().name("C").bettingChips(1).build()]))
        def river = createCompletedRound(2, new LinkedList<Player>([PlayerBuilder.withDefault().name("A").bettingChips(2).build(),
                                                                    PlayerBuilder.withDefault().name("B").bettingChips(2).build(),
                                                                    PlayerBuilder.withDefault().name("C").bettingChips(2).build()]))
        def settlePointGame = Spy(GameBuilder.withDefault()
                .waitingRounds()
                .currentRound(null)
                .ended(true)
                .completedRounds(new LinkedList<Round>([preFlop, flop, turn1])).build())
        def game = Spy(GameBuilder.withDefault()
                .waitingRounds()
                .currentRound(null)
                .ended(true)
                .settlementPointGames([settlePointGame]))
                .completedRounds(new LinkedList<Round>([turn2, river])).build()
        settlePointGame.calcuWinner() >> ["D"]
        game.calcuWinner() >> ["A"]
        when:
        def result = game.calcuSettlement()
        then:
        result.playerSettlements[0].name == "A"
        result.playerSettlements[0].totalChips == 101
        result.playerSettlements[0].winChips == 1
        result.playerSettlements[1].name == "B"
        result.playerSettlements[1].totalChips == 92
        result.playerSettlements[1].winChips == -8
        result.playerSettlements[2].name == "C"
        result.playerSettlements[2].totalChips == 92
        result.playerSettlements[2].winChips == -8
        result.playerSettlements[3].name == "D"
        result.playerSettlements[3].totalChips == 20
        result.playerSettlements[3].winChips == 15
    }

    private static Round createCompletedRound(Integer followChip, LinkedList<Player> completedPlayers) {
        RoundBuilder.withDefault()
                .followChip(followChip)
                .chipPool(followChip * completedPlayers.size())
                .ended(true)
                .currentPlayer(null)
                .completedPlayers(completedPlayers)
                .build()
    }

}
