package com.thoughtworks.firenze.texas.holdem.builder;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoundBuilder {

    public static Round.RoundBuilder withDefault() {
        return Round.builder()
                    .abstainedPlayer(new ArrayList<>())
                    .completedPlayers(new LinkedList<>())
                    .waitingPlayers(new LinkedList<>())
                    .currentPlayer(null)
                    .chipPool(0)
                    .followChip(0);
    }
}
