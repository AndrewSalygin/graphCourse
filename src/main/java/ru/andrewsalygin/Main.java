package ru.andrewsalygin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.andrewsalygin.graph.Connection;
import ru.andrewsalygin.graph.Node;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;
import ru.andrewsalygin.graph.OrientedWeightedGraph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * @author Andrew Salygin
 */
public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        OrientedUnweightedGraph graph = new OrientedUnweightedGraph();
        graph.addNode("5");
        graph.addNode("33");
        graph.addConnection("5","33");
        graph.saveGraphToFile("src/main/resources/file.txt");
        OrientedUnweightedGraph orientedUnweightedGraph = new OrientedUnweightedGraph("src/main/resources/file.txt");
    }
}

/*
ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Node.class, new NodeDeserializer());
        mapper.registerModule(module);

        OrientedUnweightedGraph<Integer> graph = new OrientedUnweightedGraph<>();
        graph.addNode("5");
        graph.addNode("7");

        graph.addConnection("5", "7");
        String graphJson = mapper.writeValueAsString(graph);
        System.out.println(graphJson);
        TypeReference<HashMap<Node, HashMap<Node, Integer>>> typeReference =
                new TypeReference<HashMap<Node, HashMap<Node, Integer>>>() {};

        // Deserialize the JSON into your map
        HashMap<Node, HashMap<Node, Integer>> map = mapper.readValue(graphJson, typeReference);

        // Use the deserialized map
        for (Node outerKey : map.keySet()) {
            System.out.println("Outer Key: " + outerKey);
            HashMap<Node, Integer> innerMap = map.get(outerKey);
            for (Node innerKey : innerMap.keySet()) {
                System.out.println("Inner Key: " + innerKey + ", Value: " + innerMap.get(innerKey));
            }
        }

 */