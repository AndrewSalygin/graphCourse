package ru.andrewsalygin.graph;

import java.util.HashMap;

/**
 * @author Andrew Salygin
 */

public abstract class Graph<T> {
    HashMap<Node<T>, HashMap<Node<T>, Integer>> graph;

    public abstract void addNode(T nodeName);
    public abstract void deleteNode(T nodeName);
    public abstract void addConnection(T nodeName, T otherNodeName, Integer weight);
    public abstract void deleteConnection(T nodeName, T otherNodeName);
}
