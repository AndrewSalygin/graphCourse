package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.geom.Ellipse;
import ru.andrewsalygin.graph.core.Node;

import static ru.andrewsalygin.graph.Game.nodeRadius;

public class VisualNode extends Node {
    private static int counterName = 0;
    private Ellipse ellipse;
    public VisualNode(Ellipse ellipse) {
        super(String.valueOf(counterName));
        this.ellipse = ellipse;
        counterName++;
    }

    public Ellipse getEllipse() {
        return ellipse;
    }


    public boolean contains(int mouseX, int mouseY) {
        // Проверяем, находится ли точка мыши внутри радиуса вершины
        double distance = Math.sqrt(Math.pow(mouseX - ellipse.getCenterX(), 2) + Math.pow(mouseY - ellipse.getCenterY(), 2));
        return distance <= nodeRadius;
    }
}
