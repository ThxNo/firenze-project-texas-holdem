package com.thoughtworks.firenze.texas.holdem.domain

import com.thoughtworks.firenze.texas.holdem.builder.PlayerBuilder
import com.thoughtworks.firenze.texas.holdem.builder.RoundBuilder
import com.thoughtworks.firenze.texas.holdem.domain.Operation
import com.thoughtworks.firenze.texas.holdem.domain.Player
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action
import spock.lang.Specification

class RoundTest extends Specification {
    def "should update round when current player pet"() {
        given:
        def waitingPlayers = new LinkedList<>([
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build()])
        def round = RoundBuilder.withDefault()
                .waitingPlayers(waitingPlayers)
                .currentPlayer(PlayerBuilder.withDefault().name("D").build())
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.PET).build())
        then:
        result.abstainedPlayer.size() == 0
        result.chipPool == round.chipPool + round.followChip
        result.completedPlayers.peek().name == round.currentPlayer.name
        result.completedPlayers.peek().bettingChips == result.followChip
        result.currentPlayer.name == round.waitingPlayers.peek().name
        result.waitingPlayers.size() == round.waitingPlayers.size() - 1
    }

    def "should update round when current player pass"() {
        given:
        def waitingPlayers = new LinkedList<>([
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build()])
        def round = RoundBuilder.withDefault()
                .waitingPlayers(waitingPlayers)
                .currentPlayer(PlayerBuilder.withDefault().name("D").build())
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.PASS).build())
        then:
        result.completedPlayers.size() == 0
        result.abstainedPlayer.size() == 0
        result.currentPlayer.name == round.waitingPlayers.peek().name
        result.waitingPlayers.stream().anyMatch({ it -> it.name == round.currentPlayer.name })
    }

    def "should update round when current player fold"() {
        given:
        def waitingPlayers = new LinkedList<>([
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build(),
                PlayerBuilder.withDefault().name("C").build()])
        def round = RoundBuilder.withDefault()
                .waitingPlayers(waitingPlayers)
                .currentPlayer(PlayerBuilder.withDefault().name("D").build())
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.FOLD).build())
        then:
        result.completedPlayers.size() == 0
        result.abstainedPlayer.size() == 1
        result.abstainedPlayer.get(0).name == round.currentPlayer.name
        result.abstainedPlayer.get(0).bettingChips == round.currentPlayer.bettingChips
        result.currentPlayer.name == round.waitingPlayers.peek().name
    }

    def "should update round when current player raise"() {
        given:
        def waitingPlayers = new LinkedList<>([
                PlayerBuilder.withDefault().name("A").build(),
                PlayerBuilder.withDefault().name("B").build()])
        def completedPlayers = new LinkedList<>([PlayerBuilder.withDefault().name("C").build()])
        def round = RoundBuilder.withDefault()
                .waitingPlayers(waitingPlayers)
                .completedPlayers(completedPlayers)
                .currentPlayer(PlayerBuilder.withDefault().name("D").build())
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.RAISE).build())
        then:
        result.completedPlayers.size() == 1
        result.abstainedPlayer.size() == 0
        result.currentPlayer.name == round.waitingPlayers.peek().name
        result.followChip == round.followChip * 2
        result.chipPool == round.chipPool + result.followChip
        result.waitingPlayers.stream().anyMatch({ it.name == round.completedPlayers[0].name })
    }

    def "should end round when not waiting and operating player"() {
        given:
        def waitingPlayers = new LinkedList<Player>()
        def completedPlayers = new LinkedList<>([PlayerBuilder.withDefault().name("C").build(),
                                                 PlayerBuilder.withDefault().name("A").build(),
                                                 PlayerBuilder.withDefault().name("B").build()])
        def round = RoundBuilder.withDefault()
                .waitingPlayers(waitingPlayers)
                .completedPlayers(completedPlayers)
                .currentPlayer(PlayerBuilder.withDefault().name("D").build())
                .build()
        when:
        def result = round.next(Operation.builder().action(Action.PET).build())
        then:
        result.ended
    }
}
