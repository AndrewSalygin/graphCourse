package ru.andrewsalygin.graph.core;

import java.util.HashMap;

public class Algorithms {
    static class FordBellman {
        public static HashMap<Node, Integer> d = new HashMap<>();
        public static HashMap<Node, Node> parents = new HashMap<>();
        public static Node nodeU = null;
    }
}
