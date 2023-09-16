package ru.andrewsalygin.graph.core.utils;

/**
 * @author Andrew Salygin
 */
public class NodeNotExistException extends RuntimeException {
    public NodeNotExistException(String message) {
        super(message);
    }
}
