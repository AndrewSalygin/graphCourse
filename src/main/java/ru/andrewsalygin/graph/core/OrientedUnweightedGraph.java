package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.core.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.NodeNotExistException;

import java.io.FileNotFoundException;
import java.util.*;

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
            throw new NodeNotExistException("Исходной вершины не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Вершина назначения не существует в текущем графе.");
    }

    @Override
    public boolean checkPossibleToDeleteNodeToGetTree() {
        // Проверяем, что количество непросмотренных вершин не больше одной (компоненты связности)
        HashMap<Node, Boolean> visitedToCheckGraphConnectivity = toUndirectedGraph().getVisitedToCheckGraphConnectivity();
        int countOfNotVisited = 0;
        for (boolean value : visitedToCheckGraphConnectivity.values()) {
            if (!value) {
                countOfNotVisited++;
            }
        }
        if (countOfNotVisited > 1) {
            return false;
        }

        // Ищем циклы в графе
        HashSet<HashSet<Node>> cycles = new HashSet<>();
        HashSet<Node> currentCycle = new HashSet<>();

        // Инициализируем массив посещенных вершин
        HashMap<Node, Boolean> visited = new HashMap<>(graph.size());
        for (Node node : graph.keySet()) {
            visited.put(node, false);
        }

        // Запускаем рекурсивный DFS
        for (Node node : graph.keySet()) {
            dfsRecursiveUtil(node, node, visited, currentCycle, cycles);
            // Чтобы повторно не считать циклы
            visited.put(node, true);
        }

        // Если циклов нет (то это дерево, так как компонента связности одна или существует одна изолированная вершина)
        if (cycles.size() == 0) {
            return true;
        } else {
            // Ищем пересечение циклов
            HashSet<Node> intersectionNodes = cycles.iterator().next();
            for (HashSet<Node> cycle : cycles) {
                intersectionNodes.retainAll(cycle);
            }
            // Если пересечение циклов ровно одна вершина, то нужно проверить не станет ли при её удалении две компоненты
            // связности
            OrientedUnweightedGraph localGraph = new OrientedUnweightedGraph(this).toUndirectedGraph();
            if (intersectionNodes.size() == 1) {
                // удаляем её из копии графа
                localGraph.deleteNode(intersectionNodes.iterator().next().nodeName);
            }
            visitedToCheckGraphConnectivity = localGraph.getVisitedToCheckGraphConnectivity();
            countOfNotVisited = 0;
            for (boolean value : visitedToCheckGraphConnectivity.values()) {
                if (!value) {
                    countOfNotVisited++;
                }
            }
            // Все вершины должны быть просмотрены
            if (countOfNotVisited > 0) {
                return false;
            }
            // Если не удовлетворяет свойству дерева
            if (graph.size() - 1 != countConnections()) {
                return false;
            }

            // Если вершин, которые входят во все циклы несколько, то тем более можно удалить какую-то из них и получить
            // дерево
            return intersectionNodes.size() >= 1;
        }
    }

    private int countConnections() {
        int count = 0;
        for (var entry : graph.values()) {
            count += entry.size();
        }
        return count;
    }

    private UndirectedUnweightedGraph toUndirectedGraph() {
        UndirectedUnweightedGraph localGraph = new UndirectedUnweightedGraph();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            if (!localGraph.isExistNodeByName(entry.getKey().nodeName)) {
                localGraph.addNode(entry.getKey().nodeName);
            }

            for (Map.Entry<Node, Connection> innerEntry : entry.getValue().entrySet()) {
                if (!localGraph.isExistNodeByName(innerEntry.getKey().nodeName)) {
                    localGraph.addNode(innerEntry.getKey().nodeName);
                }
                //if (!graph.get(entry.getKey().nodeName).containsKey(innerEntry.getKey().nodeName)) {
                try {
                    localGraph.addConnection(entry.getKey().nodeName, innerEntry.getKey().nodeName);
                } catch (RuntimeException ex) {}
            }
        }
        return localGraph;
    }

    // DFS (Нерекурсивный)
    public HashMap<Node, Boolean> getVisitedToCheckGraphConnectivity() {
        HashMap<Node, Boolean> visited = new HashMap<>(graph.size());
        if (graph.size() == 0) {
            return new HashMap<>();
        }
        Iterator<Node> iterator = graph.keySet().iterator();
        Node currentNode = iterator.next();

        for (var node : graph.keySet()) {
            visited.put(node, false);
        }

        LinkedList<Node> stack = new LinkedList<>();
        stack.addFirst(currentNode);
        // Просматриваю вершину и добавляю вершину в цикл
        visited.put(currentNode, true);

        while (!stack.isEmpty()) {
            currentNode = stack.pop();
            for (Node adjacentNode : graph.get(currentNode).keySet()) {
                if (!visited.get(adjacentNode)) {
                    visited.put(adjacentNode, true);
                    stack.addFirst(adjacentNode);
                }
            }
        }

        return visited;
    }

    private void dfsRecursiveUtil(Node startNode, Node currentNode, HashMap<Node, Boolean> visited,
                         HashSet<Node> currentCycle, HashSet<HashSet<Node>> cycles) {
        visited.put(currentNode, true);
        currentCycle.add(currentNode);

        // Прохожусь по всем смежным вершинам текущей
        for (Node adjacencyNode : graph.get(currentNode).keySet()) {
            // Запускаюсь от смежной вершины, если она не была просмотрена
            if (!visited.get(adjacencyNode)) {
                dfsRecursiveUtil(startNode, adjacencyNode, visited, currentCycle, cycles);
                // Если она была просмотрена и является стартовой, то тогда добавляем цикл
            } else if (adjacencyNode.equals(startNode)) {
                cycles.add(new HashSet<>(currentCycle));
            }
        }
        // Откатываемся на одну ноду назад
        currentCycle.remove(currentNode);
        visited.put(currentNode, false);
    }
}