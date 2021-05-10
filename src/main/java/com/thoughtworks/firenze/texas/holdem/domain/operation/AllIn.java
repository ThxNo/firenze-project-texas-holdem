package com.thoughtworks.firenze.texas.holdem.domain.operation;

import com.thoughtworks.firenze.texas.holdem.domain.Game;
import com.thoughtworks.firenze.texas.holdem.domain.Player;
import com.thoughtworks.firenze.texas.holdem.domain.Round;
import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;

import java.util.ArrayList;

public class AllIn implements Operation {
    @Override
    public Action getAction() {
        return Action.ALL_IN;
    }

    @Override
    public void execute(Round round, Player currentPlayer) {
        round.updateRoundAfterAllIn(currentPlayer);
    }

    @Override
    public void execute(Game game) {
        game.buildSettlementPointGame();
        game.setCompletedRounds(new ArrayList<>());
        game.getCurrentRound().next(this);
    }
}
