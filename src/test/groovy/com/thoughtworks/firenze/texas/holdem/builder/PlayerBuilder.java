package com.thoughtworks.firenze.texas.holdem.builder;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerBuilder {

    public static Player.PlayerBuilder withDefault() {
        return Player.builder().totalChip(100).bettingChips(0);
    }
}
