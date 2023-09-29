package ru.andrewsalygin.graph.game;

import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

/**
 * @author Andrew Salygin
 */
public class GameLogic {
    static int[] virusNodes = new int[]{0, 0};
    static int[] skillPoints = new int[]{0, 0};
    static int[] powers = new int[]{10, 10};
    static int[] protections = new int[]{10, 10};
    static int[] replications = new int[]{10, 10};
    static int powerDelta = 5;
    static int protectionDelta = 5;
    static int replicationDelta = 5;
    static String motion;
    static int day;
    public static void startGame() {
        motion = "Green move";
        day = 1;
    }

    public static void moveVirus(VisualNode startVirusMove, VisualNode endVirusMove) {

    }
}
