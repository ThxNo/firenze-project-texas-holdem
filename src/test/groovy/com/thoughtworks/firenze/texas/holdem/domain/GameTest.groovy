package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.builder.GameBuilder
import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.builder.RoundBuilder
import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerType
import com.thoughtworks.firenze.texas.holdem.domain.enums.PokerValue
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
        given:
        def card1 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def card3 = Card.builder().value(PokerValue.SIX).type(PokerType.CLUB).build()
        def card4 = Card.builder().value(PokerValue.NINE).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.JACK).type(PokerType.HEART).build()

        def card6 = Card.builder().value(PokerValue.TWO).type(PokerType.SPADE).build()
        def card7 = Card.builder().value(PokerValue.FIVE).type(PokerType.CLUB).build()

        def card8 = Card.builder().value(PokerValue.FOUR).type(PokerType.SPADE).build()
        def card9 = Card.builder().value(PokerValue.FIVE).type(PokerType.SPADE).build()

        def card10 = Card.builder().value(PokerValue.JACK).type(PokerType.DIAMOND).build()
        def card11 = Card.builder().value(PokerValue.JACK).type(PokerType.SPADE).build()

        def card12 = Card.builder().value(PokerValue.A).type(PokerType.HEART).build()
        def card13 = Card.builder().value(PokerValue.TEN).type(PokerType.DIAMOND).build()

        def publicCards = [card1, card2, card3, card4, card5]

        def completedPlayers = new LinkedList<Player>([PlayerBuilder.withDefault().name("A").tookAction(true).cards([card6, card7]).wagers(4).roundWagers(1).build(),
                                                       PlayerBuilder.withDefault().name("B").tookAction(true).cards([card8, card9]).wagers(4).roundWagers(1).build(),
                                                       PlayerBuilder.withDefault().name("C").tookAction(true).cards([card10, card11]).wagers(4).roundWagers(1).build(),
                                                       PlayerBuilder.withDefault().name("D").tookAction(true).cards([card12, card13]).wagers(4).roundWagers(1).build()])
        def preFlop = createCompletedRound(1, completedPlayers)
        def flop = createCompletedRound(1, completedPlayers)
        def turn = createCompletedRound(1, completedPlayers)
        def river = createCompletedRound(1, completedPlayers)
        def game = GameBuilder.withDefault()
                .players(new ArrayList<Player>(completedPlayers))
                .publicCards(publicCards)
                .currentRound(null)
                .ended(true)
                .completedRounds(new LinkedList<Round>([preFlop, flop, turn, river])).build()
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
        given:
        def card1 = Card.builder().value(PokerValue.FOUR).type(PokerType.HEART).build()
        def card2 = Card.builder().value(PokerValue.FIVE).type(PokerType.HEART).build()
        def card3 = Card.builder().value(PokerValue.SIX).type(PokerType.CLUB).build()
        def card4 = Card.builder().value(PokerValue.NINE).type(PokerType.HEART).build()
        def card5 = Card.builder().value(PokerValue.JACK).type(PokerType.HEART).build()

        def card6 = Card.builder().value(PokerValue.TWO).type(PokerType.SPADE).build()
        def card7 = Card.builder().value(PokerValue.FIVE).type(PokerType.CLUB).build()

        def card8 = Card.builder().value(PokerValue.FOUR).type(PokerType.SPADE).build()
        def card9 = Card.builder().value(PokerValue.FIVE).type(PokerType.SPADE).build()

        def card10 = Card.builder().value(PokerValue.JACK).type(PokerType.DIAMOND).build()
        def card11 = Card.builder().value(PokerValue.JACK).type(PokerType.SPADE).build()

        def card12 = Card.builder().value(PokerValue.A).type(PokerType.HEART).build()
        def card13 = Card.builder().value(PokerValue.TEN).type(PokerType.DIAMOND).build()

        def publicCards = [card1, card2, card3, card4, card5]

        def completedPlayers = new LinkedList<Player>([PlayerBuilder.withDefault().name("A").tookAction(true).cards([card6, card7]).wagers(5).roundWagers(2).build(),
                                                       PlayerBuilder.withDefault().name("B").tookAction(true).cards([card8, card9]).wagers(5).roundWagers(2).build(),
                                                       PlayerBuilder.withDefault().name("C").tookAction(true).cards([card10, card11]).wagers(5).roundWagers(2).build(),
                                                       PlayerBuilder.withDefault().name("D").tookAction(true).cards([card12, card13]).wagers(5).roundWagers(2).totalChip(5).build()])
        def preFlop = createCompletedRound(2, completedPlayers)
        def flop = createCompletedRound(2, completedPlayers)
        def turn1 = createCompletedRound(1, new LinkedList<Player>([PlayerBuilder.withDefault().name("A").wagers(5).roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("B").wagers(5).roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("C").wagers(5).roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("D").wagers(5).roundWagers(1).totalChip(5).build()]))
        def turn2 = createCompletedRound(1, new LinkedList<Player>([PlayerBuilder.withDefault().name("A").roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("B").roundWagers(1).build(),
                                                                    PlayerBuilder.withDefault().name("C").roundWagers(1).build()]))
        def playersAfterAllIn = [PlayerBuilder.withDefault().name("A").cards([card6, card7]).wagers(3).roundWagers(2).build(),
                                 PlayerBuilder.withDefault().name("B").cards([card8, card9]).wagers(3).roundWagers(2).build(),
                                 PlayerBuilder.withDefault().name("C").cards([card10, card11]).wagers(3).roundWagers(2).build()]
        def river = createCompletedRound(2, new LinkedList<Player>(playersAfterAllIn))
        def settlePointGame = GameBuilder.withDefault()
                .players(completedPlayers)
                .currentRound(null)
                .ended(true)
                .completedRounds(new LinkedList<Round>([preFlop, flop, turn1])).build()
        def game = GameBuilder.withDefault()
                .players(playersAfterAllIn)
                .currentRound(null)
                .ended(true)
                .publicCards(publicCards)
                .settlementPointGames([settlePointGame])
                .completedRounds(new LinkedList<Round>([turn2, river])).build()
        when:
        def result = game.calcuSettlement()
        then:
        result.playerSettlements[0].name == "A"
        result.playerSettlements[0].totalChips == 92
        result.playerSettlements[0].winChips == -8
        result.playerSettlements[1].name == "B"
        result.playerSettlements[1].totalChips == 92
        result.playerSettlements[1].winChips == -8
        result.playerSettlements[2].name == "C"
        result.playerSettlements[2].totalChips == 101
        result.playerSettlements[2].winChips == 1
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
