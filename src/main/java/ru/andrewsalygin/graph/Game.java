package ru.andrewsalygin.graph;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;

import java.util.List;
import java.util.Random;
import ru.andrewsalygin.graph.game.visualgraph.Edge;

import java.util.ArrayList;

public class Game extends BasicGame {
    private int rows = 8;
    private int cols = 8;
    private int cellSize; // Размер каждой ячейки
    private Color cellColor1 = Color.white;
    private Color cellColor2 = Color.black;
    private int screenWidth;
    private int screenHeight;
    private Random random = new Random();
    private List<Ellipse> heathNodes;
    private List<Edge> edges = new ArrayList<>();
    public Game() {
        super("Game");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setShowFPS(false); // Скрыть отображение FPS

        int screenWidth = gc.getWidth();
        int screenHeight = gc.getHeight();
        cellSize = gc.getHeight() / 10;
        heathNodes = new ArrayList<>();

        int gridWidth = cols * cellSize;
        int gridHeight = rows * cellSize;
        int x = (gc.getWidth() - gridWidth) / 2;
        int y = (gc.getHeight() - gridHeight) / 2;

        // Генерируем случайные координаты для 10 красных овалов внутри клеток
        for (int i = 0; i < 10; i++) {
            int centerX = x + random.nextInt(cols) * cellSize + cellSize / 2;
            int centerY = y + random.nextInt(rows) * cellSize + cellSize / 2;
            int radius = cellSize / 3; // Размер овала
            Ellipse ellipse = new Ellipse(centerX, centerY, radius, radius);
            heathNodes.add(ellipse);
        }

        // Генерируем рёбра между отрисованными вершинами
        for (int i = 0; i < heathNodes.size(); i++) {
            int startX, startY, endX, endY;

            Ellipse startOval = heathNodes.get(i);
            startX = (int) (startOval.getCenterX() - x) / cellSize;
            startY = (int) (startOval.getCenterY() - y) / cellSize;

            // Генерируем случайные координаты для конечной вершины, исключая текущую
            do {
                int randomIndex = random.nextInt(heathNodes.size());
                Ellipse endOval = heathNodes.get(randomIndex);
                endX = (int) (endOval.getCenterX() - x) / cellSize;
                endY = (int) (endOval.getCenterY() - y) / cellSize;
            } while ((startX == endX && startY == endY) || hasEdge(startX, startY, endX, endY));

            edges.add(new Edge(startX, startY, endX, endY));
        }
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {

    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
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

        g.setColor(Color.red);

        // Рисуем случайные красные овалы
        for (Ellipse oval : heathNodes) {
            g.fill(oval);
        }

        g.setColor(Color.blue);
        // Устанавливаем толщину линий
        g.setLineWidth(3.0f);

        // Рисуем случайные рёбра
        for (Edge edge : edges) {
            int startX = x + edge.getStartX() * cellSize + cellSize / 2;
            int startY = y + edge.getStartY() * cellSize + cellSize / 2;
            int endX = x + edge.getEndX() * cellSize + cellSize / 2;
            int endY = y + edge.getEndY() * cellSize + cellSize / 2;
            g.drawLine(startX, startY, endX, endY);
        }
    }

    // Проверяет, существует ли ребро между заданными вершинами
    private boolean hasEdge(int startX, int startY, int endX, int endY) {
        for (Edge edge : edges) {
            if ((edge.getStartX() == startX && edge.getStartY() == startY &&
                    edge.getEndX() == endX && edge.getEndY() == endY) ||
                    (edge.getStartX() == endX && edge.getStartY() == endY &&
                            edge.getEndX() == startX && edge.getEndY() == startY)) {
                return true;
            }
        }
        return false;
    }
}