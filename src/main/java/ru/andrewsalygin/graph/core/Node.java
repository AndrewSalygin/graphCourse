package ru.andrewsalygin.graph.core;

import java.util.Objects;

/**
 * @author Andrew Salygin
 */
public class Node {
    protected String nodeName;

    public Node(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(getNodeName(), node.getNodeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeName());
    }

    @Override
    public String toString() {
        return nodeName;
    }
}
