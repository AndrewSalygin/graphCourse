package ru.andrewsalygin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.andrewsalygin.graph.Node;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;

import java.io.*;
import java.util.HashMap;

/**
 * @author Andrew Salygin
 */
public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        OrientedUnweightedGraph<Integer> graph = new OrientedUnweightedGraph<>();
        graph.addNode(1);
        graph.addNode(2);
        // Сериализация
        try (FileOutputStream fileOut = new FileOutputStream("complexMap.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(graph);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Десериализация
        try (FileInputStream fileIn = new FileInputStream("complexMap.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            OrientedUnweightedGraph<Integer> deserializedGraph = (OrientedUnweightedGraph<Integer>) in.readObject();
            deserializedGraph.deleteNode(1);
            deserializedGraph.deleteNode(2);
            // Используйте deserializedMap как нужно
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}


/*
ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        OrientedUnweightedGraph<Integer> graph = new OrientedUnweightedGraph<>();
        graph.addNode(5);
        graph.addNode(7);

        graph.addConnection(5, 7);
        String graphJson = mapper.writeValueAsString(graph);
        System.out.println(graphJson);
        TypeReference<HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>> typeReference =
                new TypeReference<HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>>() {};

        // Deserialize the JSON into your map
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> map = mapper.readValue(graphJson, typeReference);

        // Use the deserialized map
        for (Node<Integer> outerKey : map.keySet()) {
            System.out.println("Outer Key: " + outerKey);
            HashMap<Node<Integer>, Integer> innerMap = map.get(outerKey);
            for (Node<Integer> innerKey : innerMap.keySet()) {
                System.out.println("Inner Key: " + innerKey + ", Value: " + innerMap.get(innerKey));
            }
        }

 */