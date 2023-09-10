package ru.andrewsalygin.graph;

import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;

import java.util.HashMap;
import java.util.List;

/**
 * @author Andrew Salygin
 */
public class OrientedUnweightedGraph<T> extends Graph<T> {
    public OrientedUnweightedGraph() {
        graph = new HashMap<>();
    }

    // For file
//    public Graph() {
//        graph = new HashMap<>();
//    }

    // For copy
//    public Graph() {
//        graph = new HashMap<>();
//    }

    @Override
    public void addNode(T nodeName) {
        Node<T> tmpNode = new Node<>(nodeName);
        // проверка на существование такой ноды
        if (isExistNode(tmpNode)) {
            throw new NodeAlreadyExistException("Такая нода уже существует.");
        }
        graph.put(tmpNode, new HashMap<>());
    }

    @Override
    public final void addNode(T nodeName, List<T> otherNodeNames) {
     //   Node<T> tmpNode = new Node<>(nodeName);
        // проверка на существование такой ноды
        // checkExistNode(nodeName);
        // проверки на существование уже связей текущих
     //   graph.put(tmpNode, new HashMap<>());
    }

    @Override
    public void deleteNode(T nodeName) {

    }

    @Override
    public void addConnection(T nodeName, T otherNodeName, Integer weight) {

    }

    @Override
    public void deleteConnection(T nodeName, T otherNodeName) {

    }

    @Override
    protected boolean isExistNode(Node<T> node) {
        return graph.containsKey(node);
    }
}
