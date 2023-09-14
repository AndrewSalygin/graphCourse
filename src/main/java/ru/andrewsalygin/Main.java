package ru.andrewsalygin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;
import ru.andrewsalygin.graph.OrientedWeightedGraph;

/**
 * @author Andrew Salygin
 */
public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        OrientedWeightedGraph graph = new OrientedWeightedGraph();
        graph.addNode("5");
        graph.addNode("7");
        graph.addConnection("5", "7");
        String mpJson = mapper.writeValueAsString(graph);
        OrientedUnweightedGraph mpJsonValue = mapper.readValue(mpJson, OrientedUnweightedGraph.class);
        System.out.println(mpJson);
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