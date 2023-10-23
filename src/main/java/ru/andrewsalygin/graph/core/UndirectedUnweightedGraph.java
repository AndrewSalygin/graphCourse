package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Andrew Salygin
 */
public class UndirectedUnweightedGraph extends OrientedUnweightedGraph implements Serializable {
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
        tmpHashMapSrc.put(destNode, new Connection(srcNode, destNode,0));
        tmpHashMapDest.put(srcNode, new Connection(destNode, srcNode, 0));
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
        // Удаляю указанную вершину
        if (connectedNodesSrc.containsKey(destNode) && connectedNodesDest.containsKey(srcNode)) {
            connectedNodesSrc.remove(destNode);
            connectedNodesDest.remove(srcNode);
        } else {
            throw new ConnectionNotExistException("Данного ребра между вершинами не существует.");
        }
    }
}
