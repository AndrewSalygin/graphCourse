package ru.andrewsalygin.graph;

/**
 * @author Andrew Salygin
 */
public class UndirectedWeightedGraph<T> extends UndirectedUnweightedGraph<T> {
    public final void addConnection(T srcNodeName, T destNodeName, Integer weight) {}
    public final void deleteConnection(T srcNodeName, T destNodeName, Integer weight) {}
}
