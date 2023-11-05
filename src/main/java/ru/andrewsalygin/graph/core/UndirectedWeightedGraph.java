package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.core.utils.NodeNotExistException;
import ru.andrewsalygin.graph.core.utils.Pair;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Andrew Salygin
 */
public class UndirectedWeightedGraph extends UndirectedUnweightedGraph {
    public UndirectedWeightedGraph() {
        super();
    }

    public UndirectedWeightedGraph(String pathFile) throws FileNotFoundException {
        super(pathFile);
    }

    public UndirectedWeightedGraph(HashMap<Object, HashMap<Object, Object>> map) {
        super(map);
    }

    public UndirectedWeightedGraph(UndirectedWeightedGraph currentGraph) {
        super(currentGraph);
    }

    public final void addConnection(String srcNodeName, String destNodeName, Integer weight) {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);
        if (srcNode.equals(destNode)) {
            throw new ConnectionNotExistException("Петлей в неориентированном графе быть не может.");
        }

        if (getConnectedNodes(srcNodeName).containsKey(destNode)) {
            throw new ConnectionAlreadyExistException("Такое ребро уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node, Connection> tmpHashMapSrc = graph.getOrDefault(srcNode, new HashMap<>());
        HashMap<Node, Connection> tmpHashMapDest = graph.getOrDefault(destNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMapSrc.put(destNode, new Connection(weight));
        tmpHashMapDest.put(srcNode, new Connection(weight));
        graph.put(srcNode, tmpHashMapSrc);
        graph.put(destNode, tmpHashMapDest);
    }

    @Override
    public String getAdjacencyList() {
        StringBuilder resultString = new StringBuilder();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            resultString.append("(").append(entry.getKey()).append("):");
            for (Map.Entry<Node, Connection> innerEntry : entry.getValue().entrySet()) {
                resultString.append("(").append(innerEntry.getKey()).append(")[").append(innerEntry.getValue()).append("];");
            }
            resultString.append('\n');
        }
        return resultString.toString();
    }

    public void updateWeight(String srcNodeName, String destNodeName, Integer weight) {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodesSrc = graph.get(srcNode);
        HashMap<Node, Connection> connectedNodesDest = graph.get(destNode);
        if (connectedNodesSrc.containsKey(destNode)) {
            connectedNodesSrc.put(destNode, new Connection(weight));
            connectedNodesDest.put(srcNode, new Connection(weight));
        } else {
            throw new ConnectionNotExistException("Данного ребра между вершинами не существует.");
        }
    }

    public Pair<Map<Node, List<Node>>, Map<Node, Integer>> shortestPathsToNode(String u) {
        Pair<Map<Node, Integer>, Map<Node, Node>> dijkstraResult = Dijkstra(u);

        Map<Node, List<Node>> result = new HashMap<>();
        LinkedList<Node> path;
        for (Node node : graph.keySet()) {
            path = new LinkedList<>();
            shortestPathToNode(node, dijkstraResult.t2(), path);
            if (path.peekLast().equals(new Node(u))) {
                result.put(node, path);
            }
        }
        return new Pair<>(result, dijkstraResult.t1());
    }

    private void shortestPathToNode(Node startNode, Map<Node, Node> parents, LinkedList<Node> path) {
        path.addLast(startNode);
        if (!parents.get(startNode).equals(startNode)) {
            shortestPathToNode(parents.get(startNode), parents, path);
        }
    }

    public Pair<Map<Node, Integer>, Map<Node, Node>> Dijkstra(String u) {
        Node nodeU = getObjectNodeByName(u);
        if (!isExistNode(nodeU))
            throw new NodeNotExistException("Вершины u не существует в текущем графе.");

        Map<Node, Integer> shortestDistances = new HashMap<>(graph.size());
        Map<Node, Node> parents = new HashMap<>(graph.size());
        Map<Node, Boolean> visited = new HashMap<>(graph.size());

        for (Node node : graph.keySet()) {
            shortestDistances.put(node, Integer.MAX_VALUE);
            parents.put(node, node); // node (второй параметр) значение по умолчанию
            visited.put(node, false);
        }
        shortestDistances.put(nodeU, 0);

        Node currentNode = nodeU;
        int minValue;
        int localMinValue;
        for (int i = 0; i < graph.size() - 1; i++) {
            minValue = Integer.MAX_VALUE;
            for (Node node : graph.keySet()) {
                if (!visited.get(node)) {
                    localMinValue = shortestDistances.get(node);
                    if (localMinValue < minValue) {
                        currentNode = node;
                        minValue = localMinValue;
                    }
                }
            }
            visited.put(currentNode, true);

            for (Map.Entry<Node, Connection> neighbour : graph.get(currentNode).entrySet()) {
                if (!visited.get(neighbour.getKey())
                        && shortestDistances.get(currentNode) + neighbour.getValue().getWeight() < shortestDistances.get(neighbour.getKey())) {
                    shortestDistances.put(neighbour.getKey(), shortestDistances.get(currentNode) + neighbour.getValue().getWeight());
                    parents.put(neighbour.getKey(), currentNode);
                }
            }
        }
        return new Pair<>(shortestDistances, parents);
    }
}


/*
 Map<Node, Node> parents = new HashMap<>(graph.size());

        for (Node node : graph.keySet()) {
            shortestDistances.put(node, Integer.MAX_VALUE);
            parents.put(node, node); // node (второй параметр) значение по умолчанию
        }
        shortestDistances.put(nodeU, 0);

        int minValue;
        for (int i = 0; i < graph.size() - 1; i++) {
            minValue = Integer.MAX_VALUE;

        }
 */