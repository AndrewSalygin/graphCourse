package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Rectangle;
import ru.andrewsalygin.graph.Game;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.core.UndirectedUnweightedGraph;
import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.core.utils.NodeAlreadyExistException;

import java.util.*;

import static ru.andrewsalygin.graph.Game.nodeRadius;

/**
 * @author Andrew Salygin
 */
public class VisualGraph extends UndirectedUnweightedGraph {
    // Размеры таблицы
    public int rows;
    public int cols;
    public int gridWidth;
    public int gridHeight;
    private final Random random;
    private List<Component> components;
    private List<VisualNode> nodes;
    private List<VisualConnection> connections;

    private final Color[] connectionColors = new Color[] {
            Color.cyan,
            Color.orange,
            Color.pink,
            Color.magenta,
            Color.black
    };
    // итератор по connectionsColors
    private int colorIndex = 0;

    public List<Component> getComponents() {
        return components;
    }

    public List<VisualConnection> getConnections() {
        return connections;
    }

    public List<VisualNode> getNodes() {
        return nodes;
    }

    public VisualGraph() {
        components = new ArrayList<>();
        connections = new ArrayList<>();
        rows = 15;
        cols = 15;
        gridWidth = cols * Game.cellSize;
        gridHeight = rows * Game.cellSize;
        random = new Random();
        graph = new HashMap<>();
        nodes = new ArrayList<>();
    }
    public void addNodeSeparated(VisualNode node) {
        addNode(node);
        nodes.add(node);
    }
    public void deleteNodeSeparated(VisualNode node) {
        deleteNode(node);
        // Прохожу по всем нодам
        List<VisualConnection> connectionToDeleteList = new ArrayList<>();
        // Получаю список нод к которым имеет связь текущая
        for (VisualConnection connection : connections) {
            if (connection.getSrcNode().equals(node) || connection.getDestNode().equals(node)) {
                connectionToDeleteList.add(connection);
            }
        }
        for (VisualConnection connection : connectionToDeleteList) {
            connections.remove(connection);
        }
        nodes.remove(node);
    }
    public final void addConnection(VisualConnection connection) {
        checkExistTwoNodes(connection.getSrcNode(), connection.getDestNode());
        if (connection.getSrcNode().equals(connection.getDestNode())) {
            throw new ConnectionNotExistException("Петлей в неориентированном графе быть не может.");
        }

        if (getConnectedNodes(connection.getSrcNode()).containsKey(connection.getDestNode())) {
            throw new ConnectionAlreadyExistException("Такое ребро уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node, Connection> tmpHashMapSrc = graph.getOrDefault(connection.getSrcNode(), new HashMap<>());
        HashMap<Node, Connection> tmpHashMapDest = graph.getOrDefault(connection.getDestNode(), new HashMap<>());

        // вес дуги 0 по умолчанию
        VisualConnection connection1 = new VisualConnection(connection.getSrcNode(), connection.getDestNode(), Color.black);
        VisualConnection connection2 = new VisualConnection(connection.getDestNode(), connection.getSrcNode(), Color.black);
        tmpHashMapSrc.put(connection.getDestNode(), connection1);
        tmpHashMapDest.put(connection.getSrcNode(), connection2);
        graph.put(connection.getSrcNode(), tmpHashMapSrc);
        graph.put(connection.getDestNode(), tmpHashMapDest);

        connections.add(connection);
    }

    // Добавить обработку графическую
    public void deleteConnection(VisualNode srcNode, VisualNode destNode) {
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodesSrc = graph.get(srcNode);
        HashMap<Node, Connection> connectedNodesDest = graph.get(destNode);
        // Удаляю указанную ноду
        if (connectedNodesSrc.containsKey(destNode) && connectedNodesDest.containsKey(srcNode)) {
            connectedNodesSrc.remove(destNode);
            connectedNodesDest.remove(srcNode);
        } else {
            throw new ConnectionNotExistException("Данного ребра между вершинами не существует.");
        }
    }

    // Добавление компоненты и её удаление

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
                    for (VisualNode node : existingComponent.getNodes()) {
                        if (node.getEllipse().intersects(new Rectangle(componentX, componentY, Game.cellSize * 3, Game.cellSize * 3))) {
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
        for (VisualNode nodeA : a.getNodes()) {
            for (VisualNode nodeB : b.getNodes()) {
                double dx = nodeA.getEllipse().getCenterX() - nodeB.getEllipse().getCenterX();
                double dy = nodeA.getEllipse().getCenterY() - nodeB.getEllipse().getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        return minDistance;
    }

    private void connectClosestComponents(Component a, Component b) {
        for (VisualNode nodeA : a.getNodes()) {
            for (VisualNode nodeB : b.getNodes()) {
                double dx = nodeA.getEllipse().getCenterX() - nodeB.getEllipse().getCenterX();
                double dy = nodeA.getEllipse().getCenterY() - nodeB.getEllipse().getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Создаём ребро между ближайшими вершинами
                if (distance == computeDistanceBetweenComponents(a, b)) {
                    VisualConnection tmpConnection = new VisualConnection(nodeA, nodeB, connectionColors[colorIndex]);
                    addConnection(tmpConnection);
                    connections.add(tmpConnection);
                    colorIndex = (colorIndex + 1) % connectionColors.length;
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

        VisualNode centerNode = new VisualNode(new Ellipse(x + Game.cellSize * 2, y + Game.cellSize * 2, nodeRadius, nodeRadius));
        VisualNode topNode = new VisualNode(new Ellipse(x + Game.cellSize * 2, y + Game.cellSize, nodeRadius, nodeRadius));
        VisualNode bottomNode = new VisualNode(new Ellipse(x + Game.cellSize * 2, y + Game.cellSize * 3, nodeRadius, nodeRadius));
        VisualNode leftNode = new VisualNode(new Ellipse(x + Game.cellSize, y + Game.cellSize * 2, nodeRadius, nodeRadius));
        VisualNode rightNode = new VisualNode(new Ellipse(x + Game.cellSize * 3, y + Game.cellSize * 2, nodeRadius, nodeRadius));

        component.addNode(centerNode);
        addNode(centerNode);
        component.addNode(topNode);
        addNode(topNode);
        component.addNode(bottomNode);
        addNode(bottomNode);
        component.addNode(leftNode);
        addNode(leftNode);
        component.addNode(rightNode);
        addNode(rightNode);

        VisualConnection tmpConnection = new VisualConnection(centerNode, bottomNode, connectionColors[colorIndex]);
        addConnection(tmpConnection);
        component.addConnection(tmpConnection);
        colorIndex = (colorIndex + 1) % connectionColors.length;
        tmpConnection = new VisualConnection(centerNode, topNode, connectionColors[colorIndex]);
        addConnection(tmpConnection);
        component.addConnection(tmpConnection);
        colorIndex = (colorIndex + 1) % connectionColors.length;
        tmpConnection = new VisualConnection(centerNode, leftNode, connectionColors[colorIndex]);
        addConnection(tmpConnection);
        component.addConnection(tmpConnection);
        colorIndex = (colorIndex + 1) % connectionColors.length;
        tmpConnection = new VisualConnection(centerNode, rightNode, connectionColors[colorIndex]);
        addConnection(tmpConnection);
        component.addConnection(tmpConnection);
        colorIndex = (colorIndex + 1) % connectionColors.length;

        return component;
    }

    private Component createTemplateComponentTree(int x, int y) {
        Component component = new Component();

        VisualNode vertex1 = new VisualNode(new Ellipse(x + Game.cellSize, y + Game.cellSize * 2, nodeRadius, nodeRadius));
        VisualNode vertex2 = new VisualNode(new Ellipse(x + Game.cellSize * 2, y + Game.cellSize, nodeRadius, nodeRadius));
        VisualNode vertex3 = new VisualNode(new Ellipse(x + Game.cellSize * 2, y + Game.cellSize * 3, nodeRadius, nodeRadius));
        VisualNode vertex4 = new VisualNode(new Ellipse(x + Game.cellSize * 3, y + Game.cellSize * 2, nodeRadius, nodeRadius));

        component.addNode(vertex1);
        addNode(vertex1);
        component.addNode(vertex2);
        addNode(vertex2);
        component.addNode(vertex3);
        addNode(vertex3);
        component.addNode(vertex4);
        addNode(vertex4);

        VisualConnection tmpConnection = new VisualConnection(vertex1, vertex2, connectionColors[colorIndex]);
        addConnection(tmpConnection);
        component.addConnection(tmpConnection);
        colorIndex = (colorIndex + 1) % connectionColors.length;
        tmpConnection = new VisualConnection(vertex2, vertex3, connectionColors[colorIndex]);
        addConnection(tmpConnection);
        component.addConnection(tmpConnection);
        colorIndex = (colorIndex + 1) % connectionColors.length;
        tmpConnection = new VisualConnection(vertex3, vertex4, connectionColors[colorIndex]);
        addConnection(tmpConnection);
        component.addConnection(tmpConnection);
        colorIndex = (colorIndex + 1) % connectionColors.length;

        return component;
    }

    // Проверяет, существует ли ребро между заданными вершинами
    private boolean hasEdge(VisualNode startNode, VisualNode endNode) {
        for (VisualConnection edge : connections) {
            if ((edge.getSrcNode() == startNode && edge.getDestNode() == endNode) ||
                    (edge.getSrcNode() == endNode && edge.getDestNode() == startNode)) {
                return true;
            }
        }
        return false;
    }
}
