package ru.andrewsalygin.graph;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;


/**
 * @author Andrew Salygin
 */

public abstract class Graph {
    @JsonProperty("graph")
    protected HashMap<Node, HashMap<Node, Integer>> graph;

    public abstract void addNode(String nodeName);
    //  public abstract void addNode(T srcNodeName, List<String> destNodeNames);
    public abstract void deleteNode(String nodeName);
    public abstract void addConnection(String srcNodeName, String destNodeName);
    public abstract void deleteConnection(String srcNodeName, String destNodeName);
    public abstract HashMap<Node, Integer> getConnectedNodes(String nameNode);
    protected abstract boolean isExistNode(Node node);

    protected abstract HashMap<Node, HashMap<Node, Integer>> getGraph();
}
