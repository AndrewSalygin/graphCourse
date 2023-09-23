package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

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
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
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
            throw new ConnectionNotExistException("Данной дуги между вершинами не существует.");
        }
    }
}
