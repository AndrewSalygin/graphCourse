package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

/**
 * @author Andrew Salygin
 */
public class UndirectedUnweightedGraph extends OrientedUnweightedGraph {
    public UndirectedUnweightedGraph() {
        super();
    }
    public UndirectedUnweightedGraph(String pathFile) throws FileNotFoundException {
        super(pathFile);
    }
    public UndirectedUnweightedGraph(HashMap<Object, HashMap<Object, Object>> map) {
        super(map);
    }
    public UndirectedUnweightedGraph(UndirectedUnweightedGraph currentGraph) {
        super(currentGraph);
    }
    public final void addConnection(String srcNodeName, String destNodeName) {
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
        tmpHashMapSrc.put(destNode, new Connection(0));
        tmpHashMapDest.put(srcNode, new Connection(0));
        graph.put(srcNode, tmpHashMapSrc);
        graph.put(destNode, tmpHashMapDest);
    }
    public final void deleteConnection(String srcNodeName, String destNodeName) {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
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

    private HashMap<Node, Integer> bfsLayers(Node startNode) {
        HashMap<Node, Integer> nodeLayers = new HashMap<>();
        HashMap<Node, Boolean> visitedNodes = new HashMap<>(graph.size());
        // вершины текущего слоя
        LinkedList<Node> queue = new LinkedList<>();
        // вершины следующего слоя
        LinkedList<Node> nextQueue = new LinkedList<>();

        int layerCount = 0;
        visitedNodes.put(startNode, true);
        nodeLayers.put(startNode, 0);
        queue.addLast(startNode);

        layerCount++;
        while (!queue.isEmpty()) {
            startNode = queue.removeFirst();

            for (Node node : graph.get(startNode).keySet()) {
                if (!visitedNodes.containsKey(node)) {
                    visitedNodes.put(node, true);
                    nextQueue.addLast(node);
                    nodeLayers.put(node, layerCount);
                }
            }
            if (queue.size() == 0 && nextQueue.size() != 0) {
                queue.addAll(nextQueue);
                nextQueue.clear();
                layerCount++;
            }
        }
        return nodeLayers;
    }

    public String findPathWithSameEdges(String uName, String vName) {
        Node u = getObjectNodeByName(uName);
        Node v = getObjectNodeByName(vName);
        checkExistTwoNodes(u, v);

        HashMap<Node, Integer> uLayers = bfsLayers(u);
        HashMap<Node, Integer> vLayers = bfsLayers(v);

        Integer tmpValue;
        for (Map.Entry<Node, Integer> entry : uLayers.entrySet()) {
            tmpValue = vLayers.get(entry.getKey());
            // Если вершины в разных компонентах связности
            if (tmpValue == null) {
                throw new RuntimeException("Такой вершины не существует.");
            }
            if (tmpValue.equals(entry.getValue())) {
                return entry.getKey().nodeName;
            }
        }

        throw new RuntimeException("Такой вершины не существует.");
    }
}
