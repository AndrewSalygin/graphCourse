package ru.andrewsalygin.graph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.andrewsalygin.graph.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.utils.NodeNotExistException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Salygin
 */

/* TO DO:
public final void addNode(T srcNodeName, List<T> destNodeNames);
public final void addArcs(T srcNodeName, List<T> destNodeName);
 */
public class OrientedUnweightedGraph<T> extends Graph<T> implements Serializable {
    // Пустой граф
    public OrientedUnweightedGraph() {
        graph = new HashMap<>();
    }
    @JsonCreator
    public OrientedUnweightedGraph(@JsonProperty("graph") HashMap<Node<T>, HashMap<Node<T>, Integer>> graph) {
        this.graph = graph;
    }

    // Json
//    @JsonCreator
//    public OrientedUnweightedGraph(@JsonProperty("json") HashMap<Node<T>, HashMap<Node<T>, Integer>> graph) {
//        this.graph = graph;
//    }

    // For copy
//    public Graph() {
//        graph = new HashMap<>();
//    }

    @Override
    public HashMap<Node<T>, Integer> getConnectedNodes(T nameNode) {
        Node<T> tmpNode = new Node<>(nameNode);
        return graph.get(tmpNode);
    }

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
    public void addConnection(T srcNodeName, T destNodeName) {
        Node<T> srcNode = new Node<>(srcNodeName);
        Node<T> destNode = new Node<>(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        if (getConnectedNodes(srcNodeName).containsKey(destNode)) {
            throw new ConnectionAlreadyExistException("Такая дуга уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node<T>, Integer> tmpHashMap = graph.getOrDefault(srcNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMap.put(destNode, 0);
        graph.put(srcNode, tmpHashMap);
    }

    @Override
    public  void deleteConnection(T srcNodeName, T destNodeName) {
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

    @Override
    protected HashMap<Node<T>, HashMap<Node<T>, Integer>> getGraph() {
        return graph;
    }

    public void setGraph(HashMap<Node<T>, HashMap<Node<T>, Integer>> graph) {
        this.graph = graph;
    }

    protected void checkExistTwoNodes(Node<T> srcNode, Node<T> destNode) {
        if (!isExistNode(srcNode))
            throw new NodeNotExistException("Исходного узла не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Узла назначения не существует в текущем графе.");
    }
}
