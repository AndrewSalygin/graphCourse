package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.geom.Ellipse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Andrew Salygin
 */
public class Component {
    private static Random random = new Random();
    private List<Ellipse> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public void addNode(Ellipse node) {
        nodes.add(node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Ellipse> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Ellipse getRandomNode() {
        return nodes.get(random.nextInt(nodes.size()));
    }

//    public void move(int x, int y) {
//        for (Ellipse node : nodes) {
//            node.setX(node.getX() + x);
//            node.setY(node.getY() + y);
//        }
//    }

//    public Ellipse getCenter() {
//        float totalX = 0;
//        float totalY = 0;
//        int numNodes = nodes.size();
//
//        for (Ellipse node : nodes) {
//            totalX += node.getCenterX();
//            totalY += node.getCenterY();
//        }
//
//        if (numNodes > 0) {
//            float centerX = totalX / numNodes;
//            float centerY = totalY / numNodes;
//            // Создаем Ellipse, представляющий центр компоненты
//            float radius = cellSize / 3; // Радиус центральной вершины компоненты
//            return new Ellipse(centerX, centerY, radius, radius);
//        } else {
//            // Если компонента не содержит вершин, вернуть пустой Ellipse
//            return new Ellipse(0, 0, 0, 0);
//        }
//    }
    public void mergeWith(Component other) {
        nodes.addAll(other.getNodes());
        edges.addAll(other.getEdges());
    }
}
