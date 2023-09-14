package ru.andrewsalygin.graph;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;


/**
 * @author Andrew Salygin
 */

public abstract class Graph {
    @JsonProperty("graph")
    protected HashMap<Node, HashMap<Node, Connection>> graph;

    public abstract void addNode(String nodeName);
    //  public abstract void addNode(T srcNodeName, List<String> destNodeNames);
    public abstract void deleteNode(String nodeName);
    public abstract void addConnection(String srcNodeName, String destNodeName);
    public abstract void deleteConnection(String srcNodeName, String destNodeName);
    public abstract HashMap<Node, Connection> getConnectedNodes(String nameNode);
    protected abstract boolean isExistNode(Node node);

    public abstract HashMap<Node, HashMap<Node, Connection>> getGraph();

    public abstract void saveGraphToFile(String pathFile) throws JsonProcessingException;
}