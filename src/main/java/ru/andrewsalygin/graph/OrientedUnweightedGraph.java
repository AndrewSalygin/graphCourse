package ru.andrewsalygin.graph;

import ru.andrewsalygin.graph.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.utils.NodeNotExistException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Node<T> nodeToDelete = new Node<>(nodeName);
        if (!isExistNode(nodeToDelete))
            throw new NodeNotExistException("Указанного узла не существует.");
        // Прохожу по всем нодам
        for (Map.Entry<Node<T>, HashMap<Node<T>, Integer>> entry : graph.entrySet()) {
            // Получаю список нод к которым имеет связь текущая
            HashMap<Node<T>, Integer> tmpHMNodes = entry.getValue();
            // Ищу среди них удаляемую
            if (tmpHMNodes.containsKey(nodeToDelete)) {
                tmpHMNodes.remove(nodeToDelete);
            }
        }
        // Удалить саму ноду
        graph.remove(nodeToDelete);
    }

    @Override
    public HashMap<Node<T>, Integer> getConnectedNodes(Integer nameNode) {
        Node<Integer> tmpNode = new Node<>(nameNode);
        return graph.get(tmpNode);
    }

    public final void addArc(T srcNodeName, T destNodeName) {
        Node<T> srcNode = new Node<>(srcNodeName);
        Node<T> destNode = new Node<>(destNodeName);
        checkExistTwoNodes(srcNode, destNode);
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

    public final void deleteArc(T srcNodeName, T destNodeName) {
        Node<T> srcNode = new Node<>(srcNodeName);
        Node<T> destNode = new Node<>(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node<T>, Integer> connectedNodes = graph.get(srcNode);
        // Удаляю указанную ноду
        if (connectedNodes.containsKey(destNode)) {
            connectedNodes.remove(destNode);
        } else {
            throw new ConnectionNotExistException("Данной дуги между нодами не существует.");
        }
    }

    @Override
    protected boolean isExistNode(Node<T> node) {
        return graph.containsKey(node);
    }

    protected void checkExistTwoNodes(Node<T> srcNode, Node<T> destNode) {
        if (!isExistNode(srcNode))
            throw new NodeNotExistException("Исходного узла не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Узла назначения не существует в текущем графе.");
    }
}
