package com.thoughtworks.firenze.texas.holdem.exception;

public class GameNotEndedException extends RuntimeException {
    public GameNotEndedException() {
        this("Game has not ended.");
    }

    public GameNotEndedException(String message) {
        super(message);
    }
}
