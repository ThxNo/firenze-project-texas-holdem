package com.thoughtworks.firenze.texas.holdem.builder;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerBuilder {

    public static Player.PlayerBuilder withDefault() {
        return Player.builder().active(true).tookAction(false).roundWagers(0).totalChip(100).wagers(0);
    }
}
