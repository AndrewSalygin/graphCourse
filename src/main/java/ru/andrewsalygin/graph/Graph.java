package ru.andrewsalygin.graph;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Salygin
 */

public abstract class Graph<T> implements Serializable {
    @JsonProperty("graph")
    protected HashMap<Node<T>, HashMap<Node<T>, Integer>> graph;

    public abstract void addNode(T nodeName);
  //  public abstract void addNode(T srcNodeName, List<T> destNodeNames);
    public abstract void deleteNode(T nodeName);
    public abstract void addConnection(T srcNodeName, T destNodeName);
    public abstract void deleteConnection(T srcNodeName, T destNodeName);
    public abstract HashMap<Node<T>, Integer> getConnectedNodes(T nameNode);
    protected abstract boolean isExistNode(Node<T> node);

    protected abstract HashMap<Node<T>, HashMap<Node<T>, Integer>> getGraph();
}
