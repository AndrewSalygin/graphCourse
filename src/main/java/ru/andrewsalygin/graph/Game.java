package ru.andrewsalygin.graph;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;

import java.util.*;

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
    private Random random = new Random();
    Image backgroundImage;
    private List<Ellipse> healthNodes;
    private List<Edge> edges = new ArrayList<>();
    public Game() {
        super("Game");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setShowFPS(false); // Скрыть отображение FPS

        backgroundImage = new Image("/src/main/resources/backgrounds/background1.jpg");

        int screenWidth = gc.getWidth();
        int screenHeight = gc.getHeight();
        cellSize = gc.getHeight() / 15;
        healthNodes = new ArrayList<>();

        int gridWidth = cols * cellSize;
        int gridHeight = rows * cellSize;
        int x = (gc.getWidth() - gridWidth) / 2;
        int y = (gc.getHeight() - gridHeight) / 2;

        // Генерируем случайные координаты для 8 красных овалов внутри клеток
        for (int i = 0; i < 12; i++) {
            int centerX, centerY;
            boolean isValidPosition;

            do {
                isValidPosition = true;
                centerX = x + random.nextInt(cols) * cellSize + cellSize / 2;
                centerY = y + random.nextInt(rows) * cellSize + cellSize / 2;

                // Проверяем, что новый узел не находится на одной строке или столбце
                for (Ellipse node : healthNodes) {
                    if (node.getCenterX() == centerX || node.getCenterY() == centerY) {
                        isValidPosition = false;
                        break;
                    }
                }
            } while (!isValidPosition);

            int radius = cellSize / 3; // Размер овала
            Ellipse healthNode = new Ellipse(centerX, centerY, radius, radius);
            healthNodes.add(healthNode);
        }

        // Перемешиваем вершины в случайном порядке
        Collections.shuffle(healthNodes);

        // Генерируем рёбра между вершинами
        generateEdges();
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

        g.setColor(Color.red);

        // Рисуем случайные красные овалы
        for (Ellipse ellipse : healthNodes) {
            g.fill(ellipse);
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
}