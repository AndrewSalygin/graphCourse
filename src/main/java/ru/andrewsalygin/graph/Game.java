package ru.andrewsalygin.graph;

import org.newdawn.slick.*;

import org.newdawn.slick.geom.Ellipse;
import ru.andrewsalygin.graph.game.visualgraph.Component;
import ru.andrewsalygin.graph.game.visualgraph.VisualConnection;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.Iterator;
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
        nodeRadius = cellSize / 3;

        visualGraph = new VisualGraph();

        // Берём левый верхний угол клетчатой сетки
        int x = (gc.getWidth() - visualGraph.gridWidth) / 2;
        int y = (gc.getHeight() - visualGraph.gridHeight) / 2;

        // Распределяем компоненты случайным образом по игровому полю
        visualGraph.randomizeComponentPlacements(x, y);

        // Соедините компоненты связности между собой, добавив рёбра
        visualGraph.connectComponents();

        visualGraph.deleteNode(visualGraph.getComponents().get(0).getNodes().get(0));
        visualGraph.deleteNode(visualGraph.getComponents().get(0).getNodes().get(0));
        visualGraph.deleteNode(visualGraph.getComponents().get(0).getNodes().get(0));
        visualGraph.getComponents().get(0).getNodes().remove(0);
        visualGraph.getComponents().get(0).getNodes().remove(0);
        visualGraph.getComponents().get(0).getNodes().remove(0);
//        .remove(0);
//        visualGraph.getComponents().get(0).getNodes().remove(1);
//        visualGraph.getComponents().get(0).getNodes().remove(2);
//        VisualNode node1 = new VisualNode(new Ellipse(x, y, nodeRadius, nodeRadius));
//        VisualNode node2 = new VisualNode(new Ellipse(x + 50, y + 50, nodeRadius, nodeRadius));
//       visualGraph.addNodeSeparated(node1);
//       visualGraph.addNodeSeparated(node2);
//       visualGraph.addConnection(new VisualConnection(node1, node2, Color.green));
    }


    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Input input = gc.getInput();

        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();

        highlightedNode = null;
        boolean stopFlag = false;
        for (VisualNode node : visualGraph.getNodes()) {
            if (node.contains(mouseX, mouseY)) {
                highlightedNode = node;
                break;
            }
        }
        for (Component component : visualGraph.getComponents()) {
            for (VisualNode node : component.getNodes()) {
                if (node.contains(mouseX, mouseY)) {
                    highlightedNode = node;
                    stopFlag = true;
                    break;
                }
            }
            if (stopFlag) {
                break;
            }
        }


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
        for (Component component : visualGraph.getComponents()) {
            // Отрисовываем вершины и подсвечиваем вершину, если на неё навели мышкой
            // в отдельных вершинах
            for (VisualNode node : visualGraph.getNodes()) {
                if (node.equals(highlightedNode)) {
                    g.setColor(Color.yellow); // Цвет подсветки
                } else {
                    g.setColor(Color.red);
                }
                g.fill(node.getEllipse());
                g.setColor(Color.black);
                g.drawOval(node.getEllipse().getCenterX() - nodeRadius, node.getEllipse().getCenterY() - nodeRadius, cellSize / 1.5f, cellSize / 1.5f);
            }
//            if (visualGraph.getNodes().size() != 0) {
//                var it = visualGraph.getNodes().iterator().next();
//                if (visualGraph.getNodes().size() != 0 && highlightedNode == it)
//                {
//                    visualGraph.deleteNodeSeparated(it);
//                    highlightedNode = null;
//                    visualGraph.deleteNode(visualGraph.getComponents().get(0).getNodes().get(0));
//                }
//            }

            // проходимся и проверяем в компонентах
            for (VisualNode node : component.getNodes()) {
                if (node.equals(highlightedNode)) {
                    g.setColor(Color.yellow); // Цвет подсветки
                } else {
                    g.setColor(Color.red);
                }
                g.fill(node.getEllipse());
                g.setColor(Color.black);
                g.drawOval(node.getEllipse().getCenterX() - nodeRadius, node.getEllipse().getCenterY() - nodeRadius, cellSize / 1.5f, cellSize / 1.5f);
            }

            // Отрисовка рёбер в компонентах
            for (VisualConnection edge : component.getConnections()) {
                g.setColor(edge.getColor());
                int startX = (int) edge.getSrcNode().getEllipse().getCenterX();
                int startY = (int) edge.getSrcNode().getEllipse().getCenterY();
                int endX = (int) edge.getDestNode().getEllipse().getCenterX();
                int endY = (int) edge.getDestNode().getEllipse().getCenterY();
                g.fillOval(startX - 5, startY - 5, 10, 10); // Точка на начале отрезка
                g.fillOval(endX - 5, endY - 5, 10, 10); // Точка на конце отрезка
                g.drawLine(startX, startY, endX, endY);
            }
        }

        // Рисуем рёбра между компонентами
        for (VisualConnection edge : visualGraph.getConnections()) {
            // Устанавливаем толщину линий
            g.setColor(edge.getColor());
            int startX = (int) edge.getSrcNode().getEllipse().getCenterX();
            int startY = (int) edge.getSrcNode().getEllipse().getCenterY();
            int endX = (int) edge.getDestNode().getEllipse().getCenterX();
            int endY = (int) edge.getDestNode().getEllipse().getCenterY();
            g.fillOval(startX - 5, startY - 5, 10, 10); // Точка на начале отрезка
            g.fillOval(endX - 5, endY - 5, 10, 10); // Точка на конце отрезка
            g.drawLine(startX, startY, endX, endY);
        }
    }
}