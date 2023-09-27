package ru.andrewsalygin.graph.core;

import java.util.Objects;

public class Connection {
    protected Node srcNode;
    protected Node destNode;
    protected Integer weight;

    public Connection(Node srcNode, Node destNode, Integer weight) {
        this.weight = weight;
        this.srcNode = srcNode;
        this.destNode = destNode;
    }

    public Integer getWeight() {
        return weight;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public Node getDestNode() {
        return destNode;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Objects.equals(weight, that.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight);
    }

    @Override
    public String toString() {
        return weight.toString();
    }
}
