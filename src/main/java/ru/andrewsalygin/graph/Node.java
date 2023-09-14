package ru.andrewsalygin.graph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Andrew Salygin
 */
public class Node<T> implements Serializable {
    T inf;

    @JsonCreator
    public Node(@JsonProperty("inf") T inf) {
        this.inf = inf;
    }

    public T getInf() {
        return inf;
    }

    public void setInf(T inf) {
        this.inf = inf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(getInf(), node.getInf());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInf());
    }

    @Override
    public String toString() {
        return "Node{" +
                "inf=" + inf +
                '}';
    }
}
