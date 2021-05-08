package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.builder.RoundBuilder
import com.thoughtworks.firenze.texas.holdem.constants.Constants
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action
import spock.lang.Specification

class RoundTest extends Specification {
    def "should update round when current player pet"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.PET).build())
        then:
        result.awaitingPlayers.peek().name == "B"
        result.awaitingPlayers.stream().filter { it.name == "A" }.findFirst().get().tookAction
        result.awaitingPlayers.stream().filter { it.name == "A" }.findFirst().get().wager == followChip
        !result.ended
    }

    def "should update round when current player pass"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.PASS).build())
        then:
        result.awaitingPlayers.peek().name == "B"
        result.awaitingPlayers.stream().filter { it.name == "A" }.findFirst().get().tookAction
        !result.ended
    }

    def "should update round when current player fold"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.FOLD).build())
        then:
        result.awaitingPlayers.peek().name == "B"
        result.players.stream().filter { it.name == "A" }.findFirst().get().tookAction
        !result.players.stream().filter { it.name == "A" }.findFirst().get().active
        !result.ended
    }

    def "should update round when current player raise"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("B").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.RAISE).build())
        then:
        result.followChip == followChip * Constants.RAISE_MULTIPLE
        result.awaitingPlayers.peek().name == "B"
        result.awaitingPlayers.stream().filter { it.name == "A" }.findFirst().get().tookAction
        !result.ended
    }

    def "should end round when not waiting and operating player"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("B").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wager(followChip).roundWager(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.PET).build())
        then:
        result.ended
    }
}
