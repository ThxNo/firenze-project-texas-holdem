package com.thoughtworks.firenze.texas.holdem.domain.operation;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;

public class AllIn implements Operation {
    @Override
    public Action getAction() {
        return Action.ALL_IN;
    }

    @Override
    public void execute(Round round, Player currentPlayer) {

    }
}
