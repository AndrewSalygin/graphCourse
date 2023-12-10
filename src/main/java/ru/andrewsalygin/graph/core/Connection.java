package ru.andrewsalygin.graph.core;

import java.util.Objects;

public class Connection {
    Integer weight;
    Integer flow;

    public Connection(Integer weight) {
        this.weight = weight;
        flow = 0;
    }

    public Integer getCapacity() {
        return weight - flow;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getFlow() {
        return flow;
    }

    public void setFlow(Integer flow) {
        this.flow = flow;
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
