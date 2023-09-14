package ru.andrewsalygin.graph;

/**
 * @author Andrew Salygin
 */
public class UndirectedUnweightedGraph<T> extends OrientedUnweightedGraph<T> {
    public final void addConnection(T srcNodeName, T destNodeName) {}
    public final void deleteConnection(T srcNodeName, T destNodeName) {}
}
