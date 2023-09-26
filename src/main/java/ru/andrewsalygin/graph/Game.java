package ru.andrewsalygin.graph;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;

import java.util.*;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import ru.andrewsalygin.graph.game.visualgraph.Edge;

public class Game extends BasicGame {
    private int rows = 12;
    private int cols = 12;
    private int cellSize; // Размер каждой ячейки
    private Color cellColor1 = Color.white;
    private Color cellColor2 = Color.black;
    private int screenWidth;
    private int screenHeight;
    private static Random random = new Random();
    Image backgroundImage;
    private List<Ellipse> healthNodes;
    private List<Component> components = new ArrayList<>();
    private static List<Edge> edges = new ArrayList<>();
    public Game() {
        super("Game");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setShowFPS(false); // Скрыть отображение FPS

        backgroundImage = new Image("/src/main/resources/backgrounds/background4.png");

        int screenWidth = gc.getWidth();
        int screenHeight = gc.getHeight();
        cellSize = gc.getHeight() / 15;
        healthNodes = new ArrayList<>();

        int gridWidth = cols * cellSize;
        int gridHeight = rows * cellSize;
        int x = (gc.getWidth() - gridWidth) / 2;
        int y = (gc.getHeight() - gridHeight) / 2;

        // Создайте заготовки компонент связности и добавьте их в список
//        components.add(createTemplateComponent(x, y));
//        components.add(createTemplateComponent2x3(x, y));
//        components.add(createTemplateComponent(x + cellSize * 6, y));
//        components.add(createTemplateComponent(x, y + cellSize * 6));

        // Распределите компоненты случайным образом по игровому полю
        for (Component component : components) {
            int centerX = x + random.nextInt(cols) * cellSize + cellSize / 2;
            int centerY = y + random.nextInt(rows) * cellSize + cellSize / 2;
            int radius = cellSize / 3; // Размер овала
            Ellipse ellipse = new Ellipse(centerX, centerY, radius, radius);
            component.move(x, y);
        }

        randomizeComponentPlacements(x, y);

        // Соедините компоненты связности между собой, добавив рёбра
        connectComponents();

//        // Генерируем рёбра между вершинами
//        generateEdges();
    }

    private void randomizeComponentPlacements(int x, int y) {
        int numComponents = 4; // Количество компонент
        int minSpacing = cellSize * 2; // Минимальное расстояние между компонентами
        int maxAttempts = 30; // Максимальное количество попыток размещения компонент

        for (int i = 0; i < numComponents; i++) {
            boolean overlap;
            int componentX, componentY;
            int attempts = 0;

            do {
                overlap = false;
                componentX = x + random.nextInt(cols - 2) * cellSize + cellSize;
                componentY = y + random.nextInt(rows - 2) * cellSize + cellSize;

                // Проверяем, не пересекаются ли компоненты
                for (Component existingComponent : components) {
                    for (Ellipse node : existingComponent.getNodes()) {
                        if (node.intersects(new Rectangle(componentX, componentY, cellSize, cellSize))) {
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

            components.add(createTemplateComponent(componentX, componentY));
        }
    }



    private Component createTemplateComponent(int x, int y) {
        Component component = new Component();

        // Создайте вершины и рёбра для заготовки компоненты
//        Ellipse centerNode = new Ellipse(x + cellSize * 2, y + cellSize * 2, cellSize / 3, cellSize / 3);
//        Ellipse topNode = new Ellipse(x + cellSize * 2, y, cellSize / 3, cellSize / 3);
//        Ellipse bottomNode = new Ellipse(x + cellSize * 2, y + cellSize * 4, cellSize / 3, cellSize / 3);
//        Ellipse leftNode = new Ellipse(x, y + cellSize * 2, cellSize / 3, cellSize / 3);
//        Ellipse rightNode = new Ellipse(x + cellSize * 4, y + cellSize * 2, cellSize / 3, cellSize / 3);

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

        component.addEdge(new Edge(centerNode, topNode, Color.red));
        component.addEdge(new Edge(centerNode, bottomNode, Color.green));
        component.addEdge(new Edge(centerNode, leftNode, Color.blue));
        component.addEdge(new Edge(centerNode, rightNode, Color.orange));

        return component;
    }

//    private void randomizeComponentPlacements(int x, int y) {
//        for (int i = 0; i < 4; i++) {
//            int componentX = x + random.nextInt(cols - 2) * cellSize + cellSize;
//            int componentY = y + random.nextInt(rows - 2) * cellSize + cellSize;
//            components.add(createTemplateComponent2x3(componentX, componentY));
//        }
//    }

    private Component createTemplateComponent2x3(int x, int y) {
        Component component = new Component();

        // Создайте вершины и рёбра для заготовки компоненты
        Ellipse vertex1 = new Ellipse(x + cellSize, y + cellSize * 2, cellSize / 3, cellSize / 3);
        Ellipse vertex2 = new Ellipse(x + cellSize * 2, y + cellSize, cellSize / 3, cellSize / 3);
        Ellipse vertex3 = new Ellipse(x + cellSize * 2, y + cellSize * 3, cellSize / 3, cellSize / 3);

        component.addNode(vertex1);
        component.addNode(vertex2);
        component.addNode(vertex3);

        component.addEdge(new Edge(vertex1, vertex2, Color.red));
        component.addEdge(new Edge(vertex1, vertex3, Color.green));

        return component;
    }

    private void connectComponents() {
        for (int i = 0; i < components.size(); i++) {
            int nextIndex = (i + 1) % components.size();
            Component current = components.get(i);
            Component next = components.get(nextIndex);

            Ellipse currentNode = current.getRandomNode();
            Ellipse nextNode = next.getRandomNode();

            edges.add(new Edge(currentNode, nextNode, Color.yellow));
        }
    }

    private void generateEdges() {
        // Создаем список вершин в случайном порядке
        List<Ellipse> shuffledNodes = new ArrayList<>(healthNodes);
        Collections.shuffle(shuffledNodes);

        // Создаем рёбра, соединяя вершины в случайном порядке и задаем им цвет
        Color[] edgeColors = new Color[]{Color.green, Color.cyan, Color.orange, Color.pink, Color.magenta, Color.red, Color.yellow, Color.transparent};
        int colorIndex = 0;

        // Создаем рёбра, соединяя вершины в случайном порядке
        for (int i = 0; i < shuffledNodes.size() - 1; i++) {
            Ellipse startNode = shuffledNodes.get(i);
            Ellipse endNode = shuffledNodes.get(i + 1);
            Color edgeColor = edgeColors[colorIndex];
            edges.add(new Edge(startNode, endNode, edgeColor));
            colorIndex = (colorIndex + 1) % edgeColors.length;
        }

        // Соединяем последнюю вершину с первой для создания цикла
        Ellipse startNode = shuffledNodes.get(shuffledNodes.size() - 1);
        Ellipse endNode = shuffledNodes.get(0);
        Color edgeColor = edgeColors[colorIndex];
        edges.add(new Edge(startNode, endNode, edgeColor));
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {

    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.drawImage(backgroundImage, 0, 0, gc.getWidth(), gc.getHeight(), 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());

//        g.setColor(Color.white);
//
//        int gridWidth = cols * cellSize;
//        int gridHeight = rows * cellSize;
//
//        // Рассчитываем координаты начала отрисовки, чтобы разместить поле по центру экрана
//        int x = (gc.getWidth() - gridWidth) / 2;
//        int y = (gc.getHeight() - gridHeight) / 2;
//
//        // Рисуем горизонтальные линии
//        for (int row = 0; row <= rows; row++) {
//            int lineY = y + row * cellSize;
//            g.drawLine(x, lineY, x + gridWidth, lineY);
//        }
//
//        // Рисуем вертикальные линии
//        for (int col = 0; col <= cols; col++) {
//            int lineX = x + col * cellSize;
//            g.drawLine(lineX, y, lineX, y + gridHeight);
//        }

        for (Component component : components) {
            // Отрисовка красных вершин
            g.setColor(Color.red);
            for (Ellipse node : component.getNodes()) {
                g.fill(node);
            }

            // Отрисовка рёбер
            g.setLineWidth(3.0f);
            for (Edge edge : component.getEdges()) {
                g.setColor(edge.getColor());
                int startX = (int) edge.getStartNode().getCenterX();
                int startY = (int) edge.getStartNode().getCenterY();
                int endX = (int) edge.getEndNode().getCenterX();
                int endY = (int) edge.getEndNode().getCenterY();
                g.drawLine(startX, startY, endX, endY);
            }
        }

        // Устанавливаем толщину линий
        g.setLineWidth(3.0f);

        // Рисуем рёбра между вершинами
        for (Edge edge : edges) {
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
    }
}