package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Ellipse;
import ru.andrewsalygin.graph.core.Connection;

public class VisualConnection extends Connection {
    private Color color;

    public VisualConnection(VisualNode srcNode, VisualNode destNode, Color color) {
        super(srcNode, destNode,0);
        this.color = color;
    }

    public VisualNode getStartNode() {
        return (VisualNode) srcNode;
    }

    public VisualNode getEndNode() {
        return (VisualNode) destNode;
    }

    public Color getColor() {
        return color;
    }
}
