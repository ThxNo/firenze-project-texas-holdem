package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.builder.GameBuilder
import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.builder.RoundBuilder
import com.thoughtworks.firenze.texas.holdem.domain.operation.AllIn
import com.thoughtworks.firenze.texas.holdem.domain.operation.Fold
import com.thoughtworks.firenze.texas.holdem.domain.operation.Pet
import spock.lang.Specification

class GameTest extends Specification {

    def "should end game when only one player remain"() {
        given:
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().active(false).name("C").build(),
                PlayerBuilder.withDefault().active(false).name("D").build()]
        def currentRound = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(new LinkedList<>(players.subList(0, 2)))
                .followChip(1)
                .build()
        def game = GameBuilder.withDefault()
                .currentRound(currentRound).build()
        when:
        def result = game.next(new Fold())
        then:
        result.ended
        result.completedRounds.last().ended
    }

    def "should start a new round when current round ended"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().tookAction(true).roundWagers(followChip).name("B").build(),
                PlayerBuilder.withDefault().active(false).name("C").build(),
                PlayerBuilder.withDefault().active(false).name("D").build()]
        def currentRound = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(new LinkedList<>(players))
                .followChip(followChip)
                .build()
        def game = GameBuilder.withDefault()
                .currentRound(currentRound).build()
        when:
        def result = game.next(new Pet())
        then:
        !result.ended
        result.completedRounds.last().ended
    }

    def "should calculate game settlement when game is over"() {
        def completedPlayers = new LinkedList<Player>([PlayerBuilder.withDefault().name("A").tookAction(true).wagers(4).roundWagers(1).build(),
                                                       PlayerBuilder.withDefault().name("B").tookAction(true).wagers(4).roundWagers(1).build(),
                                                       PlayerBuilder.withDefault().name("C").tookAction(true).wagers(4).roundWagers(1).build(),
                                                       PlayerBuilder.withDefault().name("D").tookAction(true).wagers(4).roundWagers(1).build()])
        given:
        def preFlop = createCompletedRound(1, completedPlayers)
        def flop = createCompletedRound(1, completedPlayers)
        def turn = createCompletedRound(1, completedPlayers)
        def river = createCompletedRound(1, completedPlayers)
        def game = Spy(GameBuilder.withDefault()
                .players(new ArrayList<Player>(completedPlayers))
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
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").tookAction(true).wagers(followChip).roundWagers(followChip).build(),
                PlayerBuilder.withDefault().active(false).name("C").build(),
                PlayerBuilder.withDefault().active(false).name("D").build()]
        def currentRound = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(new LinkedList<>(players.subList(0, 2)))
                .followChip(followChip)
                .build()
        def game = GameBuilder.withDefault()
                .currentRound(currentRound).build()
        when:
        def result = game.next(new AllIn())
        then:
        result.settlementPointGames.size() == 1
        result.settlementPointGames[0].ended
    }


    def "should calculate game settlement with all in when game is over"() {
        def completedPlayers = new LinkedList<Player>([PlayerBuilder.withDefault().name("A").tookAction(true).wagers(5).roundWagers(2).build(),
                                                       PlayerBuilder.withDefault().name("B").tookAction(true).wagers(5).roundWagers(2).build(),
                                                       PlayerBuilder.withDefault().name("C").tookAction(true).wagers(5).roundWagers(2).build(),
                                                       PlayerBuilder.withDefault().name("D").tookAction(true).wagers(5).roundWagers(2).totalChip(5).build()])
        given:
        def preFlop = createCompletedRound(2, completedPlayers)
        def flop = createCompletedRound(2, completedPlayers)
        def turn1 = createCompletedRound(1, new LinkedList<Player>([PlayerBuilder.withDefault().name("A").wagers(5).roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("B").wagers(5).roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("C").wagers(5).roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("D").wagers(5).roundWagers(1).totalChip(5).build()]))
        def turn2 = createCompletedRound(1, new LinkedList<Player>([PlayerBuilder.withDefault().name("A").roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("B").roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("C").roundWagers(1).build()]))
        def playersAfterAllIn = [PlayerBuilder.withDefault().name("A").wagers(3).roundWagers(2).build(),
                                 PlayerBuilder.withDefault().name("B").wagers(3).roundWagers(2).build(),
                                 PlayerBuilder.withDefault().name("C").wagers(3).roundWagers(2).build()]
        def river = createCompletedRound(2, new LinkedList<Player>(playersAfterAllIn))
        def settlePointGame = Spy(GameBuilder.withDefault()
                .players(completedPlayers)
                .currentRound(null)
                .ended(true)
                .completedRounds(new LinkedList<Round>([preFlop, flop, turn1])).build())
        def game = Spy(GameBuilder.withDefault()
                .players(playersAfterAllIn)
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
                .players(completedPlayers)
                .awaitingPlayers(completedPlayers)
                .ended(true)
                .followChip(followChip)
                .build()
    }

}
