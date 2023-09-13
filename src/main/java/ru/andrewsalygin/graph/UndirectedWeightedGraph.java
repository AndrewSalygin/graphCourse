package ru.andrewsalygin.graph;

/**
 * @author Andrew Salygin
 */
public class UndirectedWeightedGraph<T> extends UndirectedUnweightedGraph<T> {
    public final void addEdge(T srcNodeName, T destNodeName, Integer weight) {}
    public final void deleteEdge(T srcNodeName, T destNodeName, Integer weight) {}
}
