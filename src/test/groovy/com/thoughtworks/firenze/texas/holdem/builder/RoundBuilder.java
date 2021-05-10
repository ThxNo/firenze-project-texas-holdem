package com.thoughtworks.firenze.texas.holdem.builder;

import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.RoundName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoundBuilder {

    public static Round.RoundBuilder withDefault() {
        return Round.builder()
                    .name(RoundName.PRE_FLOP)
                    .awaitingPlayers(new LinkedList<>())
                    .followChip(1)
                    .ended(false);
    }
}
