package ru.andrewsalygin.graph;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;

import ru.andrewsalygin.graph.game.visualgraph.Component;
import ru.andrewsalygin.graph.game.visualgraph.Edge;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;

public class Game extends BasicGame {
    // Размеры таблицы
    public static int cellSize; // Размер каждой ячейки
    private int tableScale;
    Image backgroundImage;
    VisualGraph visualGraph;
    private Color highlightColor = Color.yellow;
    private boolean isHighlighted = false;

    public Game() {
        super("Infection Graph");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setShowFPS(false); // Скрыть отображение FPS
        backgroundImage = new Image("/src/main/resources/backgrounds/background4.png");

        tableScale = 25;
        cellSize = gc.getHeight() / tableScale;

        visualGraph = new VisualGraph();

        // Берём центр экрана
        int x = (gc.getWidth() - visualGraph.gridWidth) / 2;
        int y = (gc.getHeight() - visualGraph.gridHeight) / 2;

        // Распределяем компоненты случайным образом по игровому полю
        visualGraph.randomizeComponentPlacements(x, y);

        // Соедините компоненты связности между собой, добавив рёбра
        visualGraph.connectComponents();
    }


    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Input input = gc.getInput();

        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();

        // Нужно иметь HashTable, который будет иметь координаты каждой вершины
        // и при наведении на какую-то будет рассчитываться расстояние и искаться
        // в соответсвующей таблице



//        // Проверяем, наведена ли мышь на круг
//        isHighlighted = (Math.pow(mouseX - circleX, 2) + Math.pow(mouseY - circleY, 2) <= Math.pow(circleRadius, 2));
//
//        // Если мышь наведена на круг, меняем цвет
//        if (isHighlighted) {
//            circleColor = highlightColor;
//        } else {
//            circleColor = Color.blue;
//        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.drawImage(backgroundImage, 0, 0, gc.getWidth(), gc.getHeight(), 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());

        g.setColor(Color.white);

        int gridWidth = visualGraph.cols * cellSize;
        int gridHeight = visualGraph.rows * cellSize;

        // Рассчитываем координаты начала отрисовки, чтобы разместить поле по центру экрана
        int x = (gc.getWidth() - gridWidth) / 2;
        int y = (gc.getHeight() - gridHeight) / 2;

        // Рисуем горизонтальные линии
        for (int row = 0; row <= visualGraph.rows; row++) {
            int lineY = y + row * cellSize;
            g.drawLine(x, lineY, x + gridWidth, lineY);
        }

        // Рисуем вертикальные линии
        for (int col = 0; col <= visualGraph.cols; col++) {
            int lineX = x + col * cellSize;
            g.drawLine(lineX, y, lineX, y + gridHeight);
        }

        // Отрисовка рёбер
        g.setLineWidth(3.0f);
        for (Component component : visualGraph.components) {
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
        for (Edge edge : visualGraph.edges) {
            // Устанавливаем толщину линий
            g.setColor(edge.getColor());
            int startX = (int) edge.getStartNode().getCenterX();
            int startY = (int) edge.getStartNode().getCenterY();
            int endX = (int) edge.getEndNode().getCenterX();
            int endY = (int) edge.getEndNode().getCenterY();
            g.drawLine(startX, startY, endX, endY);
        }
    }
}