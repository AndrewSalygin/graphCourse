package ru.andrewsalygin.graph.game.visualgraph;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Rectangle;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.core.UndirectedUnweightedGraph;
import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.core.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.NodeNotExistException;
import ru.andrewsalygin.graph.game.utils.UI;

import java.io.Serializable;
import java.util.*;

/**
 * @author Andrew Salygin
 */
public class VisualGraph extends UndirectedUnweightedGraph implements Serializable {
    private final static Random RANDOM = new Random();
    private List<Component> components;

    private final Color[] connectionColors = new Color[] {
            Color.cyan,
            Color.orange,
            Color.pink,
            Color.magenta,
            Color.black
    };
    // итератор по connectionsColors
    private int colorIndex = 0;

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public VisualGraph() {
        components = new ArrayList<>();
        graph = new HashMap<>();
    }

    public void addConnection(VisualConnection connection) {
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
        tmpHashMapSrc.put(connection.getDestNode(), connection);
        tmpHashMapDest.put(connection.getSrcNode(), new VisualConnection(connection.getDestNode(), connection.getSrcNode(), connection.getColor()));
        graph.put(connection.getSrcNode(), tmpHashMapSrc);
        graph.put(connection.getDestNode(), tmpHashMapDest);
    }

    public void deleteConnection(VisualConnection connection) {
        checkExistTwoNodes(connection.getSrcNode(), connection.getDestNode());

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodesSrc = graph.get(connection.getSrcNode());
        HashMap<Node, Connection> connectedNodesDest = graph.get(connection.getDestNode());
        // Удаляю указанную ноду
        if (connectedNodesSrc.containsKey(connection.getDestNode()) && connectedNodesDest.containsKey(connection.getSrcNode())) {
            connectedNodesSrc.remove(connection.getDestNode());
            connectedNodesDest.remove(connection.getSrcNode());
        } else {
            throw new ConnectionNotExistException("Данного ребра между вершинами не существует.");
        }
    }

    public void addNode(VisualNode node) {
        // проверка на существование такой ноды
        if (isExistNode(node)) {
            throw new NodeAlreadyExistException("Такая нода уже существует.");
        }
        graph.put(node, new HashMap<>());
    }

    public void deleteNode(VisualNode nodeToDelete) {
        if (!isExistNode(nodeToDelete))
            throw new NodeNotExistException("Указанного узла не существует.");
        // Прохожу по всем нодам
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            // Получаю список нод к которым имеет связь текущая
            HashMap<Node, Connection> tmpHMNodes = entry.getValue();
            // Ищу среди них удаляемую
            if (tmpHMNodes.containsKey(nodeToDelete)) {
                tmpHMNodes.remove(nodeToDelete);
            }
        }
        // Удалить саму ноду
        graph.remove(nodeToDelete);
    }

    protected boolean isExistNode(VisualNode node) {
        return graph.containsKey(node);
    }

    public void randomizeComponentPlacements(int x, int y) {
        int cellSize = UI.cellSize;
        int cols = UI.cols;
        int rows = UI.rows;

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
                componentX = x + RANDOM.nextInt(cols - 3) * cellSize + cellSize;
                componentY = y + RANDOM.nextInt(rows - 3) * cellSize + cellSize;

                // Проверяем, не пересекаются ли компоненты
                for (Component existingComponent : components) {
                    for (VisualNode node : existingComponent.getNodes()) {
                        if (node.getEllipse().intersects(new Rectangle(componentX, componentY, cellSize * 3, cellSize * 3))) {
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
        System.out.println("Клёво");
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
        int cellSize = UI.cellSize;
        int nodeRadius = UI.nodeRadius;

        Component component = new Component();
        int maxValueHp = 100;
        int minValueHp = 50;

        Color ellipseColor = Color.red;
        VisualNode centerNode = new VisualNode(ellipseColor, new Ellipse(x + cellSize * 2, y + cellSize * 2, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);
        VisualNode topNode = new VisualNode(ellipseColor, new Ellipse(x + cellSize * 2, y + cellSize, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);
        VisualNode bottomNode = new VisualNode(ellipseColor, new Ellipse(x + cellSize * 2, y + cellSize * 3, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);
        VisualNode leftNode = new VisualNode(ellipseColor, new Ellipse(x + cellSize, y + cellSize * 2, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);
        VisualNode rightNode = new VisualNode(ellipseColor, new Ellipse(x + cellSize * 3, y + cellSize * 2, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);

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
        int cellSize = UI.cellSize;
        int nodeRadius = UI.nodeRadius;

        Component component = new Component();
        int maxValueHp = 100;
        int minValueHp = 50;

        Color ellipseColor = Color.red;
        VisualNode vertex1 = new VisualNode(ellipseColor, new Ellipse(x + cellSize, y + cellSize * 2, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);
        VisualNode vertex2 = new VisualNode(ellipseColor, new Ellipse(x + cellSize * 2, y + cellSize, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);
        VisualNode vertex3 = new VisualNode(ellipseColor, new Ellipse(x + cellSize * 2, y + cellSize * 3, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);
        VisualNode vertex4 = new VisualNode(ellipseColor, new Ellipse(x + cellSize * 3, y + cellSize * 2, nodeRadius, nodeRadius), RANDOM.nextInt(maxValueHp - minValueHp + 1) + minValueHp);

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
}
