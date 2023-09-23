package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

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
    public void printAdjacencyList() {
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            System.out.print("(" + entry.getKey() + "):");
            for (Map.Entry<Node, Connection> innerEntry : entry.getValue().entrySet()) {
                System.out.print("(" + innerEntry.getKey() + ")[" + innerEntry.getValue() + "];");
            }
            System.out.println();
        }
    }

    public void updateWeight(String srcNodeName, String destNodeName, Integer weight) {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodes = graph.get(srcNode);
        if (connectedNodes.containsKey(destNode)) {
            connectedNodes.put(destNode, new Connection(weight));
        } else {
            throw new ConnectionNotExistException("Данного ребра между вершинами не существует.");
        }
    }
}
