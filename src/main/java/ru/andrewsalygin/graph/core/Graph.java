package ru.andrewsalygin.graph.core;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Andrew Salygin
 */

public abstract class Graph {
    protected HashMap<Node, HashMap<Node, Connection>> graph;
    public abstract void addNode(String nodeName);
    public abstract void deleteNode(String nodeName);
    public abstract void addConnection(String srcNodeName, String destNodeName);
    public abstract void deleteConnection(String srcNodeName, String destNodeName);
    public abstract HashMap<Node, Connection> getConnectedNodes(String nameNode);
    protected abstract boolean isExistNode(Node node);
    public abstract boolean isExistNodeByName(String nodeName);
    public abstract void printAdjacencyList();
    protected abstract HashMap<Node, HashMap<Node, Connection>> getGraph();
    public abstract HashSet<String> getAllNodesWhereOutDegreeMoreIn();
}