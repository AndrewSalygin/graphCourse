package ru.andrewsalygin.graph;

import java.util.HashMap;

/**
 * @author Andrew Salygin
 */
public class OrientedUnweightedGraph<T> extends AbstractGraph<T> {

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
        
    }

    @Override
    public void deleteNode(T nodeName) {

    }

    @Override
    public void addConnection(T nodeName, T otherNodeName, Integer weight) {

    }

    @Override
    public void deleteConnection(T nodeName, T otherNodeName) {

    }
}
