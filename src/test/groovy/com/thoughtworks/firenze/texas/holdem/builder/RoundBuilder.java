package com.thoughtworks.firenze.texas.holdem.builder;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.RoundName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoundBuilder {

    public static Round.RoundBuilder withDefault() {
        return Round.builder()
                    .name(RoundName.PRE_FLOP)
                    .awaitingPlayers(new LinkedList<>())
                    .followChip(1)
                    .ended(false);
    }

    public static Round.RoundBuilder withDefaultPlayers(List<Player> players) {
        return withDefault().players(players).awaitingPlayers(new LinkedList<>(players));
    }
}
