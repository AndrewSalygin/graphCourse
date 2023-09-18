package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Andrew Salygin
 */
public class GraphSerializer {
    public static void saveGraphToFile(String pathFile, Graph graph) throws FileNotFoundException {
        StringBuilder result = new StringBuilder();
        String className = graph.getClass().toString();
        int lastDot = className.lastIndexOf('.');
        className = className.substring(lastDot + 1);
        result.append(className).append('\n');
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.getGraph().entrySet()) {
            result.append("(").append(entry.getKey().nodeName).append("):");
            if (graph instanceof UndirectedWeightedGraph || graph instanceof OrientedWeightedGraph) {
                for (Map.Entry<Node, Connection> connection : entry.getValue().entrySet()) {
                    result.append("(").append(connection.getKey().nodeName).append(")[").append(connection.getValue().weight).append("];");
                }
            } else {
                for (Map.Entry<Node, Connection> connection : entry.getValue().entrySet()) {
                    result.append("(").append(connection.getKey().nodeName).append(");");
                }
            }
            result.append("\n");
        }
        result = new StringBuilder(result.substring(0, result.length() - 1));
        PrintWriter out = new PrintWriter(pathFile);
        out.println(result);
        out.close();
    }

    public static Pair<HashMap<Object, HashMap<Object, Object>>, String> openGraphFromFile(String pathFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(pathFile));
        String line;
        Node tmpMainNode;
        Node tmpNode;
        int endBracket;
        HashMap<Object, HashMap<Object, Object>> tmpMap = new HashMap<>();
        int startBracket;
        Connection tmpConnection;
        HashMap<Object, Object> tmpNodes;
        String className = scanner.nextLine();
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
                tmpNodes = tmpMap.getOrDefault(tmpMainNode, new HashMap<>());
                if (className.equals("UndirectedWeightedGraph") || className.equals("OrientedWeightedGraph")) {
                    startBracket = endBracket + 1;
                    endBracket = line.indexOf(']', startBracket);
                    tmpConnection = new Connection(Integer.parseInt(line.substring(startBracket + 1, endBracket)));
                    tmpNodes.put(tmpNode, tmpConnection);
                } else {
                    tmpNodes.put(tmpNode, new Connection(0));
                }
                tmpMap.put(tmpMainNode, tmpNodes);
                startBracket = line.indexOf('(', endBracket);
            }
            if (!tmpMap.containsKey(tmpMainNode)) {
                tmpMap.put(tmpMainNode, new HashMap<>());
            }
        }
        scanner.close();
        return new Pair<>(tmpMap, className);
    }
}
