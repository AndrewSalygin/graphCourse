package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.Color;
import ru.andrewsalygin.graph.core.Connection;

import java.io.Serializable;
import java.util.Objects;

public class VisualConnection extends Connection implements Serializable {
    private Color color;

    public VisualConnection(VisualNode srcNode, VisualNode destNode, Color color) {
        super(srcNode, destNode,0);
        this.color = color;
    }

    public VisualNode getSrcNode() {
        return (VisualNode) srcNode;
    }

    public VisualNode getDestNode() {
        return (VisualNode) destNode;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VisualConnection that = (VisualConnection) o;
        return Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color);
    }
}
