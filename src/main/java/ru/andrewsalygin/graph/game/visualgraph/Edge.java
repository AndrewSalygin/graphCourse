package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Ellipse;

public class Edge {
    private Ellipse startNode;
    private Ellipse endNode;
    private Color color;

    public Edge(Ellipse startNode, Ellipse endNode, Color color) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.color = color;
    }

    public Ellipse getStartNode() {
        return startNode;
    }

    public Ellipse getEndNode() {
        return endNode;
    }

    public Color getColor() {
        return color;
    }
}
