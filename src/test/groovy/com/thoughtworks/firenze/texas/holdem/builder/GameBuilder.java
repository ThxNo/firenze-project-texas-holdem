package com.thoughtworks.firenze.texas.holdem.builder;

import com.thoughtworks.firenze.texas.holdem.domain.Game;
import com.thoughtworks.firenze.texas.holdem.domain.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBuilder {

    public static Game.GameBuilder withDefault() {
        return Game.builder()
                   .completedRounds(new ArrayList<>())
                   .currentRound(RoundBuilder.withDefault().build())
                   .settlementPointGames(new ArrayList<>())
                   .ended(false);
    }

    public static Game.GameBuilder withDefaultPlayer(List<Player> players) {
        return Game.builder()
                   .players(players)
                   .completedRounds(new ArrayList<>())
                   .currentRound(RoundBuilder.withDefaultPlayers(players).build())
                   .settlementPointGames(new ArrayList<>())
                   .ended(false);
    }
}
