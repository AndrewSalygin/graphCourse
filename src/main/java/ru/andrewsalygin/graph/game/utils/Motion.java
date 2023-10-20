package ru.andrewsalygin.graph.game.utils;

public enum Motion {
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
