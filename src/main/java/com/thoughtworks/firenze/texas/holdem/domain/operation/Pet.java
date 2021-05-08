package com.thoughtworks.firenze.texas.holdem.domain.operation;

import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;

public class Pet implements Operation {
    @Override
    public Action getAction() {
        return Action.PET;
    }

    @Override
    public void execute(Round round, Player currentPlayer) {
        currentPlayer.wager(round.getFollowChip());
        round.await(currentPlayer);
    }
}
