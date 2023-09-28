package ru.andrewsalygin.graph.game;

import org.newdawn.slick.*;

import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.visualgraph.VisualConnection;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.HashMap;
import java.util.Map;

public class Game extends BasicGame {
    // Размеры таблицы
    public static int cellSize; // Размер каждой ячейки
    public static int nodeRadius; // Размер каждой ячейки
    private int tableScale;
    Image backgroundImage;
    VisualGraph visualGraph;
    private VisualNode highlightedNode;
    private Color highlightColor = Color.yellow;

    public Game() {
        super("Infection Graph");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setShowFPS(false); // Скрыть отображение FPS
        backgroundImage = new Image("/src/main/resources/backgrounds/background4.png");

        tableScale = 25;
        cellSize = gc.getHeight() / tableScale;
        nodeRadius = cellSize / 3;

        visualGraph = new VisualGraph();

        // Берём левый верхний угол клетчатой сетки
        int x = (gc.getWidth() - visualGraph.gridWidth) / 2;
        int y = (gc.getHeight() - visualGraph.gridHeight) / 2;

        // Распределяем компоненты случайным образом по игровому полю
        visualGraph.randomizeComponentPlacements(x, y);

        // Соедините компоненты связности между собой, добавив рёбра
        visualGraph.connectComponents();

        // Больше отдельные компоненты не нужны
        visualGraph.setComponents(null);
    }


    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Input input = gc.getInput();

        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();

        highlightedNode = null;
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            VisualNode tmpNode = (VisualNode) entry.getKey();
            if (tmpNode.contains(mouseX, mouseY)) {
                highlightedNode = tmpNode;
                break;
            }
        }
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

        // Отрисовка вершин
        g.setLineWidth(3.0f);
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            VisualNode visualNode = (VisualNode) entry.getKey();
            if (visualNode.equals(highlightedNode)) {
                g.setColor(highlightColor); // Цвет подсветки
            } else {
                g.setColor(visualNode.getEllipseColor());
            }
            g.fill(visualNode.getEllipse());
            g.setColor(Color.black);
            g.drawOval(visualNode.getEllipse().getCenterX() - nodeRadius, visualNode.getEllipse().getCenterY() - nodeRadius, cellSize / 1.5f, cellSize / 1.5f);
        }

        // Отрисовка рёбер в компонентах
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            for (Map.Entry<Node, Connection> localEntry : entry.getValue().entrySet()) {
                VisualConnection vc = (VisualConnection) localEntry.getValue();
                g.setColor(vc.getColor());
                int startX = (int) vc.getSrcNode().getEllipse().getCenterX();
                int startY = (int) vc.getSrcNode().getEllipse().getCenterY();
                int endX = (int) vc.getDestNode().getEllipse().getCenterX();
                int endY = (int) vc.getDestNode().getEllipse().getCenterY();
                g.fillOval(startX - 5, startY - 5, 10, 10); // Точка на начале отрезка
                g.fillOval(endX - 5, endY - 5, 10, 10); // Точка на конце отрезка
                g.drawLine(startX, startY, endX, endY);
            }
        }
    }
}