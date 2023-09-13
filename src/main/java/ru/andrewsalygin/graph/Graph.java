package ru.andrewsalygin.graph;

import java.util.HashMap;
import java.util.List;

/**
 * @author Andrew Salygin
 */

public abstract class Graph<T> {
    protected HashMap<Node<T>, HashMap<Node<T>, Integer>> graph;

    public abstract void addNode(T nodeName);
    public abstract void addNode(T srcNodeName, List<T> destNodeNames);
    public abstract void deleteNode(T nodeName);
    public abstract HashMap<Node<T>, Integer> getConnectedNodes(Integer nameNode);
    protected abstract boolean isExistNode(Node<T> node);
}
