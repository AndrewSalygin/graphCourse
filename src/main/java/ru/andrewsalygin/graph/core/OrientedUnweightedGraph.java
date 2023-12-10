package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.*;

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
            throw new NodeNotExistException("Исходного узла не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Узла назначения не существует в текущем графе.");
    }

    public Triple<List<List<Node>>, HashMap<Node, HashMap<Node, Connection>>, Integer> fordFulkerson(String sourceNodeName, String sinkNodeName) {
        Node sourceNode = getObjectNodeByName(sourceNodeName);
        Node sinkNode = getObjectNodeByName(sinkNodeName);
        checkExistTwoNodes(sourceNode, sinkNode);

        // Создаем копию графа
        HashMap<Node, HashMap<Node, Connection>> residualGraph = new OrientedUnweightedGraph(this).graph;

        int maxFlow = 0;
        List<List<Node>> augmentingPaths = new ArrayList<>();

        // Находим увеличивающий путь в остаточной сети
        List<Node> augmentingPath = findAugmentingPath(residualGraph, sourceNode, sinkNode);

        // Пока увеличивающий путь существует
        while (augmentingPath != null) {
            augmentingPaths.add(augmentingPath);
            // Находим минимальную пропускную способность на увеличивающем пути
            int minCapacity = findMinCapacity(residualGraph, augmentingPath);

            // Обновляем поток вдоль увеличивающего пути
            updateFlow(residualGraph, augmentingPath, minCapacity);

            // Находим следующий увеличивающий путь
            augmentingPath = findAugmentingPath(residualGraph, sourceNode, sinkNode);

            // Увеличиваем общий поток
            maxFlow += minCapacity;
        }

        return new Triple<>(augmentingPaths, residualGraph, maxFlow);
    }

    private void updateFlow(HashMap<Node, HashMap<Node, Connection>> residualGraph, List<Node> augmentingPath, int minCapacity) {
        for (int i = 0; i < augmentingPath.size() - 1; i++) {
            Node current = augmentingPath.get(i);
            Node next = augmentingPath.get(i + 1);

            Connection connection = residualGraph.get(current).get(next);
            if (residualGraph.get(next).get(current) == null) {
                HashMap<Node, Connection> tmpHM = residualGraph.getOrDefault(next, new HashMap<>());
                tmpHM.put(current, new Connection(connection.weight));
                residualGraph.put(next, tmpHM);
            }
            connection.setFlow(connection.getFlow() + minCapacity);
        }
    }

    private int findMinCapacity(HashMap<Node, HashMap<Node, Connection>> residualGraph, List<Node> augmentingPath) {
        int minCapacity = Integer.MAX_VALUE;

        for (int i = 0; i < augmentingPath.size() - 1; i++) {
            Node current = augmentingPath.get(i);
            Node next = augmentingPath.get(i + 1);

            Connection connection = residualGraph.get(current).get(next);

            int capacity = connection.getCapacity();
            minCapacity = Math.min(minCapacity, capacity);
        }

        return minCapacity;
    }

    private List<Node> findAugmentingPath(HashMap<Node, HashMap<Node, Connection>> residualGraph, Node sourceNode, Node sinkNode) {
        Queue<Node> queue = new LinkedList<>();
        HashMap<Node, Node> parents = new HashMap<>();

        queue.add(sourceNode);
        parents.put(sourceNode, sourceNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Перебираем всех соседей текущей вершины в остаточной сети
            for (Node neighbour : residualGraph.getOrDefault(current, new HashMap<>()).keySet()) {
                Connection connection = residualGraph.get(current).get(neighbour);
                // Проверяем, что сосед еще не посещен и есть пропускная способность
                if (!parents.containsKey(neighbour) && connection.getCapacity() > 0) {
                    queue.add(neighbour);
                    parents.put(neighbour, current);

                    // Если сосед - целевая вершина, значит, нашли увеличивающий путь
                    if (neighbour.equals(sinkNode)) {
                        // Найден увеличивающий путь, восстанавливаем его
                        LinkedList<Node> path = new LinkedList<>();
                        Node node = sinkNode;
                        while (node != sourceNode) {
                            path.addFirst(node);
                            node = parents.get(node);
                        }
                        path.addFirst(sourceNode);
                        return path;
                    }
                }
            }
        }

        // Увеличивающий путь не найден
        return null;
    }

    private static HashMap<Node, HashMap<Node, Connection>> toUndirectedGraph(HashMap<Node, HashMap<Node, Connection>> graph) {
        HashMap<Node, HashMap<Node, Connection>> undirectedGraph = new HashMap<>();
        for (var entry : graph.entrySet()) {
            for (var innerEntry : entry.getValue().entrySet()) {
                if (!undirectedGraph.containsKey(entry.getKey())) {
                    undirectedGraph.put(entry.getKey(), new HashMap<>());
                }
                if (!undirectedGraph.containsKey(innerEntry.getKey())) {
                    undirectedGraph.put(innerEntry.getKey(), new HashMap<>());
                }
                HashMap<Node, Connection> tmpHM1 = undirectedGraph.get(entry.getKey());
                if (!tmpHM1.containsKey(innerEntry.getKey())) {
                    tmpHM1.put(innerEntry.getKey(), innerEntry.getValue());
                }

                if (!undirectedGraph.containsKey(innerEntry.getKey())) {
                    undirectedGraph.put(innerEntry.getKey(), new HashMap<>());
                }
                HashMap<Node, Connection> tmpHM2 = undirectedGraph.get(innerEntry.getKey());
                if (!tmpHM2.containsKey(entry.getKey())) {
                    tmpHM2.put(entry.getKey(), new Connection(innerEntry.getValue().weight));
                }
            }
        }
        return undirectedGraph;
    }
}

