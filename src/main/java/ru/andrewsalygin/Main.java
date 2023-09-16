package ru.andrewsalygin;
import ru.andrewsalygin.graph.*;
import ru.andrewsalygin.graph.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;

/**
 * @author Andrew Salygin
 */
public class Main {
    public static void main(String[] args) {
//        OrientedUnweightedGraph graph = new OrientedUnweightedGraph();
//        graph.addNode("5");
//        graph.addNode("33");
//        graph.addConnection("5","33");
//        graph.addConnection("5","5");
//        GraphSerializer.saveGraphToFile("src/main/resources/graphFile.txt", graph);
//        OrientedUnweightedGraph graph1 = new OrientedUnweightedGraph("src/main/resources/graphFile.txt");
//        OrientedUnweightedGraph graph2 = new OrientedUnweightedGraph(graph1);
//        graph1.deleteNode("5");

//        OrientedWeightedGraph graph = new OrientedWeightedGraph();
//        graph.addNode("5");
//        graph.addNode("33");
//        graph.addNode("7");
//        try {
//            graph.addNode("5");
//        } catch (NodeAlreadyExistException ex) {
//
//        }
//        graph.addConnection("5","33", 5);
//        graph.addConnection("7","33", 10);
//        try {
//            graph.addConnection("5","5", 4);
//        } catch (ConnectionNotExistException ex) {
//
//        }
//        GraphSerializer.saveGraphToFile("src/main/resources/graphFile.txt", graph);
//        OrientedWeightedGraph graph1 = new OrientedWeightedGraph("src/main/resources/graphFile.txt");
//        OrientedUnweightedGraph graph2 = new OrientedUnweightedGraph(graph1);
//        graph1.deleteNode("5");
//
//        System.out.println();

//        UndirectedUnweightedGraph graph = new UndirectedUnweightedGraph();
//        graph.addNode("5");
//        graph.addNode("33");
//        graph.addNode("7");
//        try {
//            graph.addNode("5");
//        } catch (NodeAlreadyExistException ex) {
//
//        }
//        graph.addConnection("5","33");
//        graph.addConnection("7","33");
//        try {
//            graph.addConnection("5","5");
//        } catch (ConnectionNotExistException ex) {
//
//        }
//        GraphSerializer.saveGraphToFile("src/main/resources/graphFile.txt", graph);
//        OrientedWeightedGraph graph1 = new OrientedWeightedGraph("src/main/resources/graphFile.txt");
//        OrientedUnweightedGraph graph2 = new OrientedUnweightedGraph(graph1);
//        graph1.deleteNode("5");
//
//        System.out.println();

        UndirectedWeightedGraph graph = new UndirectedWeightedGraph();
        graph.addNode("5");
        graph.addNode("33");
        graph.addNode("7");
        try {
            graph.addNode("5");
        } catch (NodeAlreadyExistException ex) {

        }
        graph.addConnection("5","33", 7);
        graph.addConnection("7","33", 10);
        try {
            graph.addConnection("5","5");
        } catch (ConnectionNotExistException ex) {

        }
        GraphSerializer.saveGraphToFile("src/main/resources/graphFile.txt", graph);
        OrientedWeightedGraph graph1 = new OrientedWeightedGraph("src/main/resources/graphFile.txt");
        OrientedUnweightedGraph graph2 = new OrientedUnweightedGraph(graph1);
        graph1.deleteNode("5");

        System.out.println();
    }
}