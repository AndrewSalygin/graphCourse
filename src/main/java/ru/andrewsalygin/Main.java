package ru.andrewsalygin;
import ru.andrewsalygin.graph.GraphSerializer;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;

/**
 * @author Andrew Salygin
 */
public class Main {
    public static void main(String[] args) {
        OrientedUnweightedGraph graph = new OrientedUnweightedGraph();
        graph.addNode("5");
        graph.addNode("33");
        graph.addConnection("5","33");
        graph.addConnection("5","5");
        GraphSerializer.saveGraphToFile("src/main/resources/graphFile.txt", graph);
        OrientedUnweightedGraph graph1 = new OrientedUnweightedGraph("src/main/resources/graphFile.txt");
        OrientedUnweightedGraph graph2 = new OrientedUnweightedGraph(graph1);
        graph1.deleteNode("5");
        System.out.println();
    }
}