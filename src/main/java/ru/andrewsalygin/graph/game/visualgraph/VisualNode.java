package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Ellipse;
import ru.andrewsalygin.graph.core.Node;

import java.util.Objects;

import static ru.andrewsalygin.graph.game.Game.nodeRadius;

public class VisualNode extends Node {
    private static int counterName = 0;
    private Color ellipseColor;
    private Ellipse ellipse;
    public VisualNode(Color ellipseColor, Ellipse ellipse) {
        super(String.valueOf(counterName));
        this.ellipseColor = ellipseColor;
        this.ellipse = ellipse;
        counterName++;
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public Color getEllipseColor() {
        return ellipseColor;
    }


    public boolean contains(int mouseX, int mouseY) {
        // Проверяем, находится ли точка мыши внутри радиуса вершины
        double distance = Math.sqrt(Math.pow(mouseX - ellipse.getCenterX(), 2) + Math.pow(mouseY - ellipse.getCenterY(), 2));
        return distance <= nodeRadius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisualNode that = (VisualNode) o;
        return (Objects.equals(ellipse.getCenterX(), that.ellipse.getCenterX()) &&
                Objects.equals(ellipse.getCenterY(), that.ellipse.getCenterY()) &&
                Objects.equals(ellipse.getRadius1(), that.ellipse.getRadius1()) &&
                Objects.equals(ellipse.getRadius2(), that.ellipse.getRadius2()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadius1(), ellipse.getRadius2());
    }
}
