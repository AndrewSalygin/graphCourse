package ru.andrewsalygin.graph;

import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.utils.NodeNotExistException;

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
    public final void addNode(T srcNodeName, List<T> destNodeNames) {
     //   Node<T> tmpNode = new Node<>(nodeName);
        // проверка на существование такой ноды
        // checkExistNode(nodeName);
        // проверки на существование уже связей текущих
     //   graph.put(tmpNode, new HashMap<>());
    }

    @Override
    public void deleteNode(T nodeName) {

    }

    public final void addArc(T srcNodeName, T destNodeName) {
        Node<T> srcNode = new Node<>(srcNodeName);
        Node<T> destNode = new Node<>(destNodeName);
        if (!isExistNode(srcNode))
            throw new NodeNotExistException("Исходного узла не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Узла назначения не существует в текущем графе.");
        // получаем список существующих дуг
        HashMap<Node<T>, Integer> tmpHashMap = graph.getOrDefault(srcNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMap.put(destNode, 0);
        graph.put(srcNode, tmpHashMap);
    }

//    public final void addArcs(T srcNodeName, List<T> destNodeName) {
//        Node<T> srcNode = new Node<>(srcNodeName);
//        Node<T> destNode = new Node<>(destNodeName);
//        if (isrcNode)
//    }

    public void deleteConnection(T srcNodeName, T destNodeName) {

    }

    @Override
    protected boolean isExistNode(Node<T> node) {
        return graph.containsKey(node);
    }
}
