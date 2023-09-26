package ru.andrewsalygin.graph;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;

import java.util.*;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import ru.andrewsalygin.graph.game.visualgraph.Edge;

public class Game extends BasicGame {
    // Размеры таблицы
    private int rows = 15;
    private int cols = 15;
    private static int cellSize; // Размер каждой ячейки
    private static Random random = new Random();
    Image backgroundImage;
    private List<Component> components = new ArrayList<>();
    private static List<Edge> edges = new ArrayList<>();
    Color[] edgeColors = new Color[]{Color.cyan, Color.orange, Color.pink, Color.magenta, Color.black};
    // итератор по edgeColors
    int colorIndex = 0;

    public Game() {
        super("Infection Graph");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setShowFPS(false); // Скрыть отображение FPS
        backgroundImage = new Image("/src/main/resources/backgrounds/background4.png");

        cellSize = gc.getHeight() / 25;

        int gridWidth = cols * cellSize;
        int gridHeight = rows * cellSize;
        // Берём центр экрана
        int x = (gc.getWidth() - gridWidth) / 2;
        int y = (gc.getHeight() - gridHeight) / 2;

        // Распределяем компоненты случайным образом по игровому полю
        randomizeComponentPlacements(x, y);

        // Соедините компоненты связности между собой, добавив рёбра
        connectComponents();
    }

    private void randomizeComponentPlacements(int x, int y) {
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
                componentX = x + random.nextInt(cols - 3) * cellSize + cellSize;
                componentY = y + random.nextInt(rows - 3) * cellSize + cellSize;

                // Проверяем, не пересекаются ли компоненты
                for (Component existingComponent : components) {
                    for (Ellipse node : existingComponent.getNodes()) {
                        if (node.intersects(new Rectangle(componentX, componentY, cellSize * 3, cellSize * 3))) {
                            overlap = true;
                            break;
                        }
                    }
                }

                // Проверяем, не выходит ли компонент за границы поля
                if (componentX < x || componentX + cellSize > x + cols * cellSize ||
                        componentY < y || componentY + cellSize > y + rows * cellSize) {
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

    private Component createTemplateComponentCross(int x, int y) {
        Component component = new Component();

        Ellipse centerNode = new Ellipse(x + cellSize * 2, y + cellSize * 2, cellSize / 3, cellSize / 3);
        Ellipse topNode = new Ellipse(x + cellSize * 2, y + cellSize, cellSize / 3, cellSize / 3);
        Ellipse bottomNode = new Ellipse(x + cellSize * 2, y + cellSize * 3, cellSize / 3, cellSize / 3);
        Ellipse leftNode = new Ellipse(x + cellSize, y + cellSize * 2, cellSize / 3, cellSize / 3);
        Ellipse rightNode = new Ellipse(x + cellSize * 3, y + cellSize * 2, cellSize / 3, cellSize / 3);

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

        Ellipse vertex1 = new Ellipse(x + cellSize, y + cellSize * 2, cellSize / 3, cellSize / 3);
        Ellipse vertex2 = new Ellipse(x + cellSize * 2, y + cellSize, cellSize / 3, cellSize / 3);
        Ellipse vertex3 = new Ellipse(x + cellSize * 2, y + cellSize * 3, cellSize / 3, cellSize / 3);
        Ellipse vertex4 = new Ellipse(x + cellSize * 3, y + cellSize * 2, cellSize / 3, cellSize / 3);

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

    private void connectComponents() {
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

    @Override
    public void update(GameContainer gc, int i) throws SlickException {

    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.drawImage(backgroundImage, 0, 0, gc.getWidth(), gc.getHeight(), 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());

        g.setColor(Color.white);

        int gridWidth = cols * cellSize;
        int gridHeight = rows * cellSize;

        // Рассчитываем координаты начала отрисовки, чтобы разместить поле по центру экрана
        int x = (gc.getWidth() - gridWidth) / 2;
        int y = (gc.getHeight() - gridHeight) / 2;

        // Рисуем горизонтальные линии
        for (int row = 0; row <= rows; row++) {
            int lineY = y + row * cellSize;
            g.drawLine(x, lineY, x + gridWidth, lineY);
        }

        // Рисуем вертикальные линии
        for (int col = 0; col <= cols; col++) {
            int lineX = x + col * cellSize;
            g.drawLine(lineX, y, lineX, y + gridHeight);
        }

        // Отрисовка рёбер
        g.setLineWidth(3.0f);
        for (Component component : components) {
            // Отрисовка красных вершин
            for (Ellipse node : component.getNodes()) {
                g.setColor(Color.red);
                g.fill(node);
                // Добавляем обводку
                g.setColor(Color.black);
                g.drawOval(node.getCenterX() - cellSize / 3, node.getCenterY() - cellSize / 3, cellSize / 1.5f, cellSize / 1.5f);

            }

            // Отрисовка рёбер в компонентах
            for (Edge edge : component.getEdges()) {
                g.setColor(edge.getColor());
                int startX = (int) edge.getStartNode().getCenterX();
                int startY = (int) edge.getStartNode().getCenterY();
                int endX = (int) edge.getEndNode().getCenterX();
                int endY = (int) edge.getEndNode().getCenterY();
                g.fillOval(startX - 5, startY - 5, 10, 10); // Точка на начале отрезка
                g.fillOval(endX - 5, endY - 5, 10, 10); // Точка на конце отрезка
                g.drawLine(startX, startY, endX, endY);
            }
        }


        // Рисуем рёбра между компонентами
        for (Edge edge : edges) {
            // Устанавливаем толщину линий
            g.setColor(edge.getColor());
            int startX = (int) edge.getStartNode().getCenterX();
            int startY = (int) edge.getStartNode().getCenterY();
            int endX = (int) edge.getEndNode().getCenterX();
            int endY = (int) edge.getEndNode().getCenterY();
            g.drawLine(startX, startY, endX, endY);
        }
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

    private static class Component {
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

        public void move(int x, int y) {
            for (Ellipse node : nodes) {
                node.setX(node.getX() + x);
                node.setY(node.getY() + y);
            }
        }

        public Ellipse getCenter() {
            float totalX = 0;
            float totalY = 0;
            int numNodes = nodes.size();

            for (Ellipse node : nodes) {
                totalX += node.getCenterX();
                totalY += node.getCenterY();
            }

            if (numNodes > 0) {
                float centerX = totalX / numNodes;
                float centerY = totalY / numNodes;
                // Создаем Ellipse, представляющий центр компоненты
                float radius = cellSize / 3; // Радиус центральной вершины компоненты
                return new Ellipse(centerX, centerY, radius, radius);
            } else {
                // Если компонента не содержит вершин, вернуть пустой Ellipse
                return new Ellipse(0, 0, 0, 0);
            }
        }
        public void mergeWith(Component other) {
            nodes.addAll(other.getNodes());
            edges.addAll(other.getEdges());
        }
    }
}