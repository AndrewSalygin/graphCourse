package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;

import java.util.HashMap;

/**
 * @author Andrew Salygin
 */
public class UndirectedWeightedGraph extends UndirectedUnweightedGraph {
    public UndirectedWeightedGraph() {
        super();
    }
    public UndirectedWeightedGraph(String pathFile) {
        super(pathFile);
    }
    public UndirectedWeightedGraph(UndirectedWeightedGraph currentGraph) {
        super(currentGraph);
    }
    public final void addConnection(String srcNodeName, String destNodeName, Integer weight) {
        Node srcNode = new Node(srcNodeName);
        Node destNode = new Node(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

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
}
