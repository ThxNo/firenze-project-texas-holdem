package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.builder.RoundBuilder
import com.thoughtworks.firenze.texas.holdem.constants.Constants
import com.thoughtworks.firenze.texas.holdem.domain.operation.Fold
import com.thoughtworks.firenze.texas.holdem.domain.operation.Pass
import com.thoughtworks.firenze.texas.holdem.domain.operation.Pet
import com.thoughtworks.firenze.texas.holdem.domain.operation.Raise
import spock.lang.Specification

class RoundTest extends Specification {
    def "should update round when current player pet"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(new Pet())
        then:
        result.awaitingPlayers.peek().name == "B"
        result.awaitingPlayers.stream().filter { it.name == "A" }.findFirst().get().tookAction
        result.awaitingPlayers.stream().filter { it.name == "A" }.findFirst().get().wagers == followChip
        !result.ended
    }

    def "should update round when current player pass"() {
        given:
        def followChip = 1
        def players = [
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(new Pass())
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
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(new Fold())
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
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("B").build(),
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(new Raise())
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
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("B").build(),
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("C").build(),
                PlayerBuilder.withDefault().tookAction(true).wagers(followChip).roundWagers(followChip).name("D").build()]
        def waitingPlayers = new LinkedList<>(players)
        def round = RoundBuilder.withDefault()
                .players(players)
                .awaitingPlayers(waitingPlayers)
                .followChip(followChip)
                .build()
        when:
        def result = round.next(new Pet())
        then:
        result.ended
    }
}
