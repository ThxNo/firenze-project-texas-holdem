package com.thoughtworks.firenze.texas.holdem.domain.operation;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;

public class Fold implements Operation {
    @Override
    public Action getAction() {
        return Action.FOLD;
    }

    @Override
    public void execute(Round round, Player currentPlayer) {
        round.inactive(currentPlayer);
    }
}
