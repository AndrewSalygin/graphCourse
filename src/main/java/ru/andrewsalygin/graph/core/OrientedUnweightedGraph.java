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

    Algorithms algorithms;
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

    public Pair<List<Node>, Integer> shortestPathToV(String uName, String vName) {
        Node u = getObjectNodeByName(uName);
        if (!isExistNode(u))
            throw new NodeNotExistException("Вершины u не существует в текущем графе.");
        Node v = getObjectNodeByName(vName);
        if (!isExistNode(v))
            throw new NodeNotExistException("Вершины v не существует в текущем графе.");

        // Проверяем, нужно ли пересчитать Форда-Беллмана (первый раз, или от той же вершины)
        if (Algorithms.FordBellman.nodeU == null || !Algorithms.FordBellman.nodeU.equals(u)) {
            getParentsAndDForUNode(uName, Algorithms.FordBellman.d, Algorithms.FordBellman.parents);
        }

        LinkedList<Node> pathToV = new LinkedList<>();
        try {
            // Проверяем, что вершина достижима и не находится в отрицательном цикле
            checkCorrectPathToNode(Algorithms.FordBellman.d.get(v));
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        // Получаем путь и вес пути
        shortestPathToNode(v, u, Algorithms.FordBellman.parents, pathToV);
        return new Pair<>(pathToV, Algorithms.FordBellman.d.get(v));
    }

    private void getParentsAndDForUNode(String uName, HashMap<Node, Integer> d, HashMap<Node, Node> parents) {
        Node u = getObjectNodeByName(uName);
        if (!isExistNode(u))
            throw new NodeNotExistException("Вершины u не существует в текущем графе.");
        fordBellman(u, d, parents);
    }

    private void checkCorrectPathToNode(Integer vDist) {
        if (vDist == Integer.MAX_VALUE) {
            throw new RuntimeException("Вершина недостижима");
        }
        if (vDist == Integer.MIN_VALUE) {
            throw new RuntimeException("Вершина находится в отрицательном цикле");
        }
    }

    private void shortestPathToNode(Node currentNode, Node startNode, Map<Node, Node> parents, LinkedList<Node> path) {
        path.addLast(currentNode);

        // Идём рекурсивно до тех пор, пока родителем вершины не окажется сама вершина (*)
        if (!parents.get(currentNode).equals(startNode)) {
            shortestPathToNode(parents.get(currentNode), startNode, parents, path);
        } else {
            path.addLast(startNode);
        }
    }

    private void fordBellman(Node u, HashMap<Node, Integer> d, HashMap<Node, Node> parents) {
        for (Node node : graph.keySet()) {
            d.put(node, Integer.MAX_VALUE);
            parents.put(node, node);
        }
        d.put(u, 0);

        Set<Node> negativeCyclesNodes = new HashSet<>();

        for (int i = 0; i < 2 * graph.size() - 2; i++) {
            boolean flag = false;
            for (Node mainNode : graph.keySet() ) {
                for (Map.Entry<Node, Connection> adjacentNode : graph.get(mainNode).entrySet()) {
                    if (d.get(mainNode) != Integer.MAX_VALUE) {
                        if (d.get(mainNode) + adjacentNode.getValue().weight < d.get(adjacentNode.getKey())) {
                            d.put(adjacentNode.getKey(), d.get(mainNode) + adjacentNode.getValue().weight);
                            parents.put(adjacentNode.getKey(), mainNode);
                            flag = true;
                            // На итерациях больше n-ой смотрим какие вершины изменились
                            if (i >= graph.size()) {
                                negativeCyclesNodes.add(adjacentNode.getKey());
                            }
                        }
                    }
                }
            }
            if (!flag) {
                break;
            }
        }
        for (Node negativeNode : negativeCyclesNodes) {
            d.put(negativeNode, Integer.MIN_VALUE);
            parents.put(negativeNode, negativeNode);
        }
    }
}

