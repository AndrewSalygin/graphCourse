package ru.andrewsalygin.graph.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GraphSerializer {
    public static void saveGraphToFile(String pathFile, Graph graph) {
        String result = "";
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.getGraph().entrySet()) {
            result += "(" + entry.getKey().nodeName + "):";
            for (Map.Entry<Node, Connection> connection : entry.getValue().entrySet()) {
                result += "(" + connection.getKey().nodeName + ")[" + connection.getValue().weight + "];";
            }
            result += "\n";
        }
        result = result.substring(0, result.length() - 1);
        try (PrintWriter out = new PrintWriter(pathFile)) {
            out.println(result);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Node, HashMap<Node, Connection>> openGraphFromFile(String pathFile) {
        try {
            Scanner scanner = new Scanner(new File(pathFile));
            String line;
            Node tmpMainNode;
            Node tmpNode;
            int endBracket;
            HashMap<Node, HashMap<Node, Connection>> tmpMap = new HashMap<>();
            int semicolon;
            int startBracket;
            Connection tmpConnection;
            HashMap<Node, Connection> tmpNodes;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();

                // Здесь если параметров будет много, то в цикле проходить до
                // каждого следующего параметра через запятую

                endBracket = line.indexOf(')');
                tmpMainNode = new Node(line.substring(1, endBracket));
                startBracket = line.indexOf('(', endBracket);
                while (startBracket != -1) {
                    endBracket = line.indexOf(')', startBracket);
                    tmpNode = new Node(line.substring(startBracket + 1, endBracket));
                    startBracket = endBracket + 1;
                    endBracket = line.indexOf(']', startBracket);
                    tmpConnection = new Connection(Integer.parseInt(line.substring(startBracket + 1, endBracket)));
                    tmpNodes = tmpMap.getOrDefault(tmpMainNode, new HashMap<>());
                    tmpNodes.put(tmpNode, tmpConnection);
                    tmpMap.put(tmpMainNode, tmpNodes);
                    startBracket = line.indexOf('(', endBracket);
                }
                if (!tmpMap.containsKey(tmpMainNode)) {
                    tmpMap.put(tmpMainNode, new HashMap<>());
                }
            }
            scanner.close();
            return tmpMap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
