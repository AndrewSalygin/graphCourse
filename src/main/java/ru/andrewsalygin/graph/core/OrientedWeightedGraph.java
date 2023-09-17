package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * @author Andrew Salygin
 */
public class OrientedWeightedGraph extends OrientedUnweightedGraph {
    public OrientedWeightedGraph() {
        super();
    }
    public OrientedWeightedGraph(String pathFile) throws FileNotFoundException {
        super(pathFile);
    }

    public OrientedWeightedGraph(HashMap<Object, HashMap<Object, Object>> map) {
        super(map);
    }
    public OrientedWeightedGraph(OrientedWeightedGraph currentGraph) {
        super(currentGraph);
    }
    public final void addConnection(String srcNodeName, String destNodeName, Integer weight) {
        Node srcNode = new Node(srcNodeName);
        Node destNode = new Node(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        if (getConnectedNodes(srcNodeName).containsKey(destNode)) {
            throw new ConnectionAlreadyExistException("Такая дуга уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node, Connection> tmpHashMap = graph.getOrDefault(srcNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMap.put(destNode, new Connection(weight));
        graph.put(srcNode, tmpHashMap);
    }
}
