package com.thoughtworks.firenze.texas.holdem.domain.operation;

import com.thoughtworks.firenze.texas.holdem.constants.Constants;
import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;

public class Raise implements Operation {
    @Override
    public Action getAction() {
        return Action.RAISE;
    }

    @Override
    public void execute(Round round, Player currentPlayer) {
        round.setFollowChip(round.getFollowChip() * Constants.RAISE_MULTIPLE);
        currentPlayer.wager(round.getFollowChip());
        round.await(currentPlayer);
    }
}
