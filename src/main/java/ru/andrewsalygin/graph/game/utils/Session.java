package ru.andrewsalygin.graph.game.utils;

import lombok.*;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.io.Serializable;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {
    private int[] virusNodes;
    private int[] skillPoints;
    private int[] powers;
    private int[] protections;
    private int[] replications;
    private int powerDelta;
    private int protectionDelta;
    private int replicationDelta;
    private Motion motion;
    private int day;
    private HashMap<Flag, Boolean> flags;
    private HashMap<VisualNode, HashMap<Node, Connection>> greenGraph;
    private HashMap<VisualNode, HashMap<Node, Connection>> blueGraph;
    private HashMap<VisualNode, HashMap<Node, Connection>> redGraph;
    private VisualGraph visualGraph;
}
