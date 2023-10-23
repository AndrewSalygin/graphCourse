package ru.andrewsalygin.graph.game.utils;

import java.io.Serializable;

public enum Motion implements Serializable {
    Green("Green move"),
    Blue("Blue move");

    private final String stringMotion;
    Motion(String s) {
        stringMotion = s;
    }

    public String getStringMotion() {
        return stringMotion;
    }
}
