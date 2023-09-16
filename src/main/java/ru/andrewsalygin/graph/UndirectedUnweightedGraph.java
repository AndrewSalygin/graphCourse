package ru.andrewsalygin.graph;

import ru.andrewsalygin.graph.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.utils.ConnectionNotExistException;

import java.util.HashMap;

/**
 * @author Andrew Salygin
 */
public class UndirectedUnweightedGraph extends OrientedUnweightedGraph {
    public UndirectedUnweightedGraph() {
        super();
    }
    public UndirectedUnweightedGraph(String pathFile) {
        super(pathFile);
    }
    public UndirectedUnweightedGraph(UndirectedUnweightedGraph currentGraph) {
        super(currentGraph);
    }
    public final void addConnection(String srcNodeName, String destNodeName) {
        Node srcNode = new Node(srcNodeName);
        Node destNode = new Node(destNodeName);
        if (srcNode.equals(destNode)) {
            throw new ConnectionNotExistException("Петлей в неориентированном графе быть не может.");
        }
        checkExistTwoNodes(srcNode, destNode);

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
        Node srcNode = new Node(srcNodeName);
        Node destNode = new Node(destNodeName);
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
}
