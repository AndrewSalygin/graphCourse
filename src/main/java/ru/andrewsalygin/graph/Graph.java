package ru.andrewsalygin.graph;

import java.util.HashMap;
import java.util.List;

/**
 * @author Andrew Salygin
 */

public abstract class Graph<T> {
    protected HashMap<Node<T>, HashMap<Node<T>, Integer>> graph;

    public abstract void addNode(T nodeName);
    public abstract void addNode(T nodeName, List<T> otherNodeNames);
    public abstract void deleteNode(T nodeName);
    public abstract void addConnection(T nodeName, T otherNodeName, Integer weight);
    public abstract void deleteConnection(T nodeName, T otherNodeName);
    protected abstract boolean isExistNode(Node<T> node);
}
