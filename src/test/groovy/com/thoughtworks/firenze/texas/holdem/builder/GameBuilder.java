package com.thoughtworks.firenze.texas.holdem.builder;

import com.thoughtworks.firenze.texas.holdem.domain.Game;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBuilder {

    public static Game.GameBuilder withDefault() {
        return Game.builder()
                   .completedRounds(new ArrayList<>())
                   .currentRound(RoundBuilder.withDefault().build())
                   .settlementPointGames(new ArrayList<>())
                   .ended(false);
    }
}
