package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Rectangle;
import ru.andrewsalygin.graph.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Andrew Salygin
 */
public class VisualGraph {
    // Размеры таблицы
    public int rows;
    public int cols;
    public int gridWidth;
    public int gridHeight;
    private final Random random;
    public List<Component> components;
    public List<Edge> edges;
    final Color[] edgeColors = new Color[] {
            Color.cyan,
            Color.orange,
            Color.pink,
            Color.magenta,
            Color.black
    };
    // итератор по edgeColors
    int colorIndex = 0;

    public VisualGraph() {
        components = new ArrayList<>();
        edges = new ArrayList<>();
        rows = 15;
        cols = 15;
        gridWidth = cols * Game.cellSize;
        gridHeight = rows * Game.cellSize;
        random = new Random();
    }
    public void randomizeComponentPlacements(int x, int y) {
        // Магические числа :) На самом деле можно брать любые, взял такие.
        int numComponents = 25; // Количество компонент
        int maxAttempts = 30; // Максимальное количество попыток размещения компонент

        for (int i = 0; i < numComponents; i++) {
            // отвечает за перекрытие друг другом компонент
            boolean overlap;
            int componentX, componentY;
            int attempts = 0;

            do {
                overlap = false;
                // верхний левый угол будущего прямоугольника
                componentX = x + random.nextInt(cols - 3) * Game.cellSize + Game.cellSize;
                componentY = y + random.nextInt(rows - 3) * Game.cellSize + Game.cellSize;

                // Проверяем, не пересекаются ли компоненты
                for (Component existingComponent : components) {
                    for (Ellipse node : existingComponent.getNodes()) {
                        if (node.intersects(new Rectangle(componentX, componentY, Game.cellSize * 3, Game.cellSize * 3))) {
                            overlap = true;
                            break;
                        }
                    }
                }

                // Проверяем, не выходит ли компонент за границы поля
                if (componentX < x || componentX + Game.cellSize > x + cols * Game.cellSize ||
                        componentY < y || componentY + Game.cellSize > y + rows * Game.cellSize) {
                    overlap = true;
                }

                attempts++;
                if (attempts >= maxAttempts) {
                    // Если не удается разместить компоненты в разумных пределах, прерываем генерацию
                    return;
                }

            } while (overlap);

            switch (i % 2) {
                case 0 -> components.add(createTemplateComponentCross(componentX, componentY));
                case 1 -> components.add(createTemplateComponentTree(componentX, componentY));
            }
        }
    }

    public void connectComponents() {
        if (components.isEmpty()) {
            return;
        }

        // Создаёт копию списка компонент
        List<Component> remainingComponents = new ArrayList<>(components);

        // Перебирает компоненты, пока в списке не останется одна компонента.
        while (remainingComponents.size() > 1) {
            Component closestA = null;
            Component closestB = null;
            double closestDistance = Double.MAX_VALUE;

            // Найдём ближайшие компоненты
            for (int i = 0; i < remainingComponents.size(); i++) {
                for (int j = i + 1; j < remainingComponents.size(); j++) {
                    Component componentA = remainingComponents.get(i);
                    Component componentB = remainingComponents.get(j);
                    double distance = computeDistanceBetweenComponents(componentA, componentB);

                    if (distance < closestDistance) {
                        closestA = componentA;
                        closestB = componentB;
                        closestDistance = distance;
                    }
                }
            }

            if (closestA != null && closestB != null) {
                // Создаём ребро между ближайшими компонентами
                connectClosestComponents(closestA, closestB);
                remainingComponents.remove(closestA);
                remainingComponents.remove(closestB);
                remainingComponents.add(mergeComponents(closestA, closestB));
            }
        }
    }

    private double computeDistanceBetweenComponents(Component a, Component b) {
        // Вычисляет расстояние между двумя компонентами.
        double minDistance = Double.MAX_VALUE;

        // Находится ближайшее расстояние между всеми нодами двух компонент
        for (Ellipse nodeA : a.getNodes()) {
            for (Ellipse nodeB : b.getNodes()) {
                double dx = nodeA.getCenterX() - nodeB.getCenterX();
                double dy = nodeA.getCenterY() - nodeB.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        return minDistance;
    }

    private void connectClosestComponents(Component a, Component b) {
        for (Ellipse nodeA : a.getNodes()) {
            for (Ellipse nodeB : b.getNodes()) {
                double dx = nodeA.getCenterX() - nodeB.getCenterX();
                double dy = nodeA.getCenterY() - nodeB.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Создаём ребро между ближайшими вершинами
                if (distance == computeDistanceBetweenComponents(a, b)) {
                    edges.add(new Edge(nodeA, nodeB, edgeColors[colorIndex]));
                    colorIndex = (colorIndex + 1) % edgeColors.length;
                }
            }
        }
    }

    private Component mergeComponents(Component a, Component b) {
        // Объединяем вершины и рёбра компонент a и b в одну компоненту и возвращаем её.
        a.mergeWith(b);
        return a;
    }

    private Component createTemplateComponentCross(int x, int y) {
        Component component = new Component();

        Ellipse centerNode = new Ellipse(x + Game.cellSize * 2, y + Game.cellSize * 2, Game.cellSize / 3, Game.cellSize / 3);
        Ellipse topNode = new Ellipse(x + Game.cellSize * 2, y + Game.cellSize, Game.cellSize / 3, Game.cellSize / 3);
        Ellipse bottomNode = new Ellipse(x + Game.cellSize * 2, y + Game.cellSize * 3, Game.cellSize / 3, Game.cellSize / 3);
        Ellipse leftNode = new Ellipse(x + Game.cellSize, y + Game.cellSize * 2, Game.cellSize / 3, Game.cellSize / 3);
        Ellipse rightNode = new Ellipse(x + Game.cellSize * 3, y + Game.cellSize * 2, Game.cellSize / 3, Game.cellSize / 3);

        component.addNode(centerNode);
        component.addNode(topNode);
        component.addNode(bottomNode);
        component.addNode(leftNode);
        component.addNode(rightNode);

        component.addEdge(new Edge(centerNode, bottomNode, edgeColors[colorIndex]));
        colorIndex = (colorIndex + 1) % edgeColors.length;
        component.addEdge(new Edge(centerNode, topNode, edgeColors[colorIndex]));
        colorIndex = (colorIndex + 1) % edgeColors.length;
        component.addEdge(new Edge(centerNode, leftNode, edgeColors[colorIndex]));
        colorIndex = (colorIndex + 1) % edgeColors.length;
        component.addEdge(new Edge(centerNode, rightNode, edgeColors[colorIndex]));
        colorIndex = (colorIndex + 1) % edgeColors.length;

        return component;
    }

    private Component createTemplateComponentTree(int x, int y) {
        Component component = new Component();

        Ellipse vertex1 = new Ellipse(x + Game.cellSize, y + Game.cellSize * 2, Game.cellSize / 3, Game.cellSize / 3);
        Ellipse vertex2 = new Ellipse(x + Game.cellSize * 2, y + Game.cellSize, Game.cellSize / 3, Game.cellSize / 3);
        Ellipse vertex3 = new Ellipse(x + Game.cellSize * 2, y + Game.cellSize * 3, Game.cellSize / 3, Game.cellSize / 3);
        Ellipse vertex4 = new Ellipse(x + Game.cellSize * 3, y + Game.cellSize * 2, Game.cellSize / 3, Game.cellSize / 3);

        component.addNode(vertex1);
        component.addNode(vertex2);
        component.addNode(vertex3);
        component.addNode(vertex4);

        component.addEdge(new Edge(vertex1, vertex2, edgeColors[colorIndex]));
        colorIndex = (colorIndex + 1) % edgeColors.length;
        component.addEdge(new Edge(vertex2, vertex3, edgeColors[colorIndex]));
        colorIndex = (colorIndex + 1) % edgeColors.length;
        component.addEdge(new Edge(vertex3, vertex4, edgeColors[colorIndex]));
        colorIndex = (colorIndex + 1) % edgeColors.length;

        return component;
    }

    // Проверяет, существует ли ребро между заданными вершинами
    private boolean hasEdge(Ellipse startNode, Ellipse endNode) {
        for (Edge edge : edges) {
            if ((edge.getStartNode() == startNode && edge.getEndNode() == endNode) ||
                    (edge.getStartNode() == endNode && edge.getEndNode() == startNode)) {
                return true;
            }
        }
        return false;
    }
}
