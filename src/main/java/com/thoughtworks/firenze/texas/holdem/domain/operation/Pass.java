package com.thoughtworks.firenze.texas.holdem.domain.operation;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;

public class Pass implements Operation {
    @Override
    public Action getAction() {
        return Action.PASS;
    }

    @Override
    public void execute(Round round, Player currentPlayer) {
        round.await(currentPlayer);
    }
}
