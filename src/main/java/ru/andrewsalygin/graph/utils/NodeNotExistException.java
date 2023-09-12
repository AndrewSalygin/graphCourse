package ru.andrewsalygin.graph.utils;

/**
 * @author Andrew Salygin
 */
public class NodeNotExistException extends RuntimeException {
    public NodeNotExistException(String message) {
        super(message);
    }
}
