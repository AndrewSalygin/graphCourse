package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Salygin
 */

/* TO DO:
public final void addNode(T srcNodeName, List<String> destNodeNames);
public final void addArcs(T srcNodeName, List<String> destNodeName);
 */
public class OrientedUnweightedGraph extends Graph {
    // Пустой граф
    public OrientedUnweightedGraph() {
        graph = new HashMap<>();
    }

    // Конструктор для переданной мапы
    public OrientedUnweightedGraph(HashMap<Object, HashMap<Object, Object>> map) {
        HashMap<Node, HashMap<Node, Connection>> correctMap = new HashMap<>();
        HashMap<Node, Connection> tmpHashMap;
        for (Object currentNode : map.keySet()) {
            tmpHashMap = new HashMap<>();
            for (Object currentDest : map.get(currentNode).keySet()) {
                tmpHashMap.put((Node) currentDest, (Connection) map.get(currentNode).get(currentDest));
            }
            correctMap.put((Node) currentNode, tmpHashMap);
        }

        graph = correctMap;
    }

    // Конструктор для файла
    public OrientedUnweightedGraph(String pathFile) throws FileNotFoundException {
        HashMap<Object, HashMap<Object, Object>> map = GraphSerializer.openGraphFromFile(pathFile).t1();
        graph = new OrientedUnweightedGraph(map).getGraph();
    }

    // Конструктор для копии
    public OrientedUnweightedGraph(OrientedUnweightedGraph currentGraph) {
        graph = new HashMap<>();
        HashMap<Node, HashMap<Node, Connection>> tmpGraph = new HashMap<>();
        HashMap<Node, Connection> tmpHashMap;
        for (Node currentNode : currentGraph.graph.keySet()) {
            tmpHashMap = new HashMap<>();
            for (Node currentDest : currentGraph.graph.get(currentNode).keySet()) {
                tmpHashMap.put(currentDest, currentGraph.graph.get(currentNode).get(currentDest));
            }
            tmpGraph.put(currentNode, tmpHashMap);
        }
        graph = tmpGraph;
    }

    @Override
    public HashMap<Node, Connection> getConnectedNodes(String nameNode) {
        Node tmpNode = getObjectNodeByName(nameNode);
        return graph.get(tmpNode);
    }

    @Override
    public void addNode(String nodeName) {
        Node tmpNode = getObjectNodeByName(nodeName);
        // проверка на существование такой ноды
        if (isExistNode(tmpNode)) {
            throw new NodeAlreadyExistException("Такая нода уже существует.");
        }
        graph.put(tmpNode, new HashMap<>());
    }

    @Override
    public void deleteNode(String nodeName) {
        Node nodeToDelete = getObjectNodeByName(nodeName);
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

    @Override
    public void addConnection(String srcNodeName, String destNodeName) {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        if (getConnectedNodes(srcNodeName).containsKey(destNode)) {
            throw new ConnectionAlreadyExistException("Такая дуга уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node, Connection> tmpHashMap = graph.getOrDefault(srcNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMap.put(destNode, new Connection(0));
        graph.put(srcNode, tmpHashMap);
    }

    @Override
    public void deleteConnection(String srcNodeName, String destNodeName) throws ConnectionNotExistException {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodes = graph.get(srcNode);
        // Удаляю указанную ноду
        if (connectedNodes.containsKey(destNode)) {
            connectedNodes.remove(destNode);
        } else {
            throw new ConnectionNotExistException("Данной дуги между вершинами не существует.");
        }
    }

    @Override
    protected Node getObjectNodeByName(String nodeName) {
        return new Node(nodeName);
    }


    @Override
    protected boolean isExistNode(Node node) {
        return graph.containsKey(node);
    }

    @Override
    public boolean isExistNodeByName(String nodeName) {
        return isExistNode(getObjectNodeByName(nodeName));
    }

    @Override
    public String getAdjacencyList() {
        StringBuilder resultString = new StringBuilder();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            resultString.append("(").append(entry.getKey()).append("):");
            for (Map.Entry<Node, Connection> innerEntry : entry.getValue().entrySet()) {
                resultString.append("(").append(innerEntry.getKey()).append(");");
            }
            resultString.append('\n');
        }
        return resultString.toString();
    }

    @Override
    public HashMap<Node, HashMap<Node, Connection>> getGraph() {
        return graph;
    }

    protected void checkExistTwoNodes(Node srcNode, Node destNode) {
        if (!isExistNode(srcNode))
            throw new NodeNotExistException("Исходного узла не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Узла назначения не существует в текущем графе.");
    }

    @Override
    public final Graph expandArcs() {
        Graph tmpGraph = new OrientedUnweightedGraph();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            // Добавляю ноду в которую пойду
            if (!tmpGraph.graph.containsKey(entry.getKey())) {
                tmpGraph.graph.put(entry.getKey(), new HashMap<>());
            }
            for (Map.Entry<Node, Connection> localEntry : entry.getValue().entrySet()) {
                // Если не ходили от этой ноды, то добавляю новую ноду во временный граф
                if (!tmpGraph.graph.containsKey(localEntry.getKey())) {
                    tmpGraph.graph.put(localEntry.getKey(), new HashMap<>());
                }
                // Меняю направление дуги
                tmpGraph.graph.get(localEntry.getKey()).put(entry.getKey(), localEntry.getValue());
            }
        }
        return tmpGraph;
    }

    public Pair<Map<Node, List<Node>>, Map<Node, Connection>> shortestPathsToNode(String u) {
        // Разворачиваю дуги графа
        graph = expandArcs().getGraph();

        // Ищу кратчайшие пути от вершины u <(Путь до вершины, вес), список предков для каждой вершины>
        Pair<Map<Node, Connection>, Map<Node, Node>> dijkstraResult = dijkstra(u);

        // Вершина, путь
        Map<Node, List<Node>> result = new HashMap<>();
        LinkedList<Node> path;

        // Восстанавливаем путь от всех вершин, до вершины u
        for (Node node : graph.keySet()) {
            path = new LinkedList<>();
            shortestPathToNode(node, dijkstraResult.t2(), path);
            if (path.peekLast().equals(new Node(u))) {
                result.put(node, path);
            }
        }

        // Разворачиваю граф обратно
        graph = expandArcs().getGraph();

        return new Pair<>(result, dijkstraResult.t1());
    }

    private void shortestPathToNode(Node currentNode, Map<Node, Node> parents, LinkedList<Node> path) {
        path.addLast(currentNode);

        // Идём рекурсивно до тех пор, пока родителем вершины не окажется сама вершина (*)
        if (!parents.get(currentNode).equals(currentNode)) {
            shortestPathToNode(parents.get(currentNode), parents, path);
        }
    }

    public Pair<Map<Node, Connection>, Map<Node, Node>> dijkstra(String u) {
        // Проверка корректности ноды
        Node nodeU = getObjectNodeByName(u);
        if (!isExistNode(nodeU))
            throw new NodeNotExistException("Вершины u не существует в текущем графе.");

        // Наикратчайшие расстояния
        Map<Node, Connection> shortestDistances = new HashMap<>(graph.size());

        // Список предков
        Map<Node, Node> parents = new HashMap<>(graph.size());

        // Список посещенных вершин
        Map<Node, Boolean> visited = new HashMap<>(graph.size());

        // Инициализация алгоритма
        for (Node node : graph.keySet()) {
            shortestDistances.put(node, new Connection(Integer.MAX_VALUE));
            parents.put(node, node); // node (второй параметр) значение по умолчанию (*)
            visited.put(node, false);
        }
        // для стартовой вершины расстояние 0
        shortestDistances.put(nodeU, new Connection(0));

        Node currentNode = nodeU;
        int minValue;
        int localMinValue;
        for (int i = 0; i < graph.size() - 1; i++) {
            minValue = Integer.MAX_VALUE;
            // Выбираем вершину до которой наикратчайший путь среди всех непросмотренных вершин
            for (Node node : shortestDistances.keySet()) {
                if (!visited.get(node)) {
                    localMinValue = shortestDistances.get(node).getWeight();
                    if (localMinValue < minValue) {
                        currentNode = node;
                        minValue = localMinValue;
                    }
                }
            }
            // Просматриваем эту вершину
            visited.put(currentNode, true);

            // Проходимся по непросмотренным соседям и обновляем расстояние, если оно получилось короче прежнего
            for (Map.Entry<Node, Connection> neighbour : graph.get(currentNode).entrySet()) {
                if (!visited.get(neighbour.getKey())
                        && shortestDistances.get(currentNode).getWeight() + neighbour.getValue().getWeight() < shortestDistances.get(neighbour.getKey()).getWeight()) {
                    shortestDistances.put(neighbour.getKey(), new Connection(shortestDistances.get(currentNode).getWeight() + neighbour.getValue().getWeight()));
                    parents.put(neighbour.getKey(), currentNode);
                }
            }
        }
        return new Pair<>(shortestDistances, parents);
    }
}

