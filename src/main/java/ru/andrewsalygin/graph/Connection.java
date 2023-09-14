package ru.andrewsalygin.graph;

import java.util.Objects;

/**
 * @author Andrew Salygin
 */
public class Connection {
    String weight;

    public Connection(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
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
