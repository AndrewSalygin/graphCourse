package ru.andrewsalygin.graph.core;

public class KraskalsConnection extends Connection {
    Node node1;
    Node node2;

    public KraskalsConnection(Integer weight, Node node1, Node node2) {
        super(weight);
        this.node1 = node1;
        this.node2 = node2;
    }
}
