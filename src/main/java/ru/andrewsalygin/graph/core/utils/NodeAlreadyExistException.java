package ru.andrewsalygin.graph.core.utils;

/**
 * @author Andrew Salygin
 */
public class NodeAlreadyExistException extends RuntimeException {
    public NodeAlreadyExistException(String message) {
        super(message);
    }
}
