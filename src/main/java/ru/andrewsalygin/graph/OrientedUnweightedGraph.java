package ru.andrewsalygin.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.andrewsalygin.graph.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.utils.NodeNotExistException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * @author Andrew Salygin
 */

/* TO DO:
public final void addNode(T srcNodeName, List<String> destNodeNames);
public final void addArcs(T srcNodeName, List<String> destNodeName);
 */
public class OrientedUnweightedGraph extends Graph {
    // Пустой граф
    public OrientedUnweightedGraph() {
        graph = new HashMap<>();
    }

    // Конструктор для json
    public OrientedUnweightedGraph(String pathFile) throws JsonProcessingException {
        String fileContent = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            fileContent = Files.readString(Paths.get(pathFile));
        } catch (IOException e) {
            throw new RuntimeException("Данного файла не существует.");
        }
        this.graph = mapper.readValue(fileContent, HashMap.class);
    }


    // Сохранение в json
    public void saveGraphToFile(String pathFile) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(this.getGraph());
        File f = new File(pathFile);
        try {
            f.createNewFile();
        } catch (IOException e) {}
        try (PrintWriter out = new PrintWriter(pathFile)) {
            out.println(json);
        } catch (FileNotFoundException e) {}
    }
    // Конструктор для копии
    public void OrientedUnweightedGraph(OrientedUnweightedGraph currentGraph) {

    }

    @Override
    public HashMap<Node, Connection> getConnectedNodes(String nameNode) {
        Node tmpNode = new Node(nameNode);
        return graph.get(tmpNode);
    }

    @Override
    public void addNode(String nodeName) {
        Node tmpNode = new Node(nodeName);
        // проверка на существование такой ноды
        if (isExistNode(tmpNode)) {
            throw new NodeAlreadyExistException("Такая нода уже существует.");
        }
        graph.put(tmpNode, new HashMap<>());
    }

    @Override
    public void deleteNode(String nodeName) {
        Node nodeToDelete = new Node(nodeName);
        if (!isExistNode(nodeToDelete))
            throw new NodeNotExistException("Указанного узла не существует.");
        // Прохожу по всем нодам
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            // Получаю список нод к которым имеет связь текущая
            HashMap<Node, Connection> tmpHMNodes = entry.getValue();
            // Ищу среди них удаляемую
            if (tmpHMNodes.containsKey(nodeToDelete)) {
                tmpHMNodes.remove(nodeToDelete);
            }
        }
        // Удалить саму ноду
        graph.remove(nodeToDelete);
    }

    @Override
    public void addConnection(String srcNodeName, String destNodeName) {
        Node srcNode = new Node(srcNodeName);
        Node destNode = new Node(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        if (getConnectedNodes(srcNodeName).containsKey(destNode)) {
            throw new ConnectionAlreadyExistException("Такая дуга уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node, Connection> tmpHashMap = graph.getOrDefault(srcNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMap.put(destNode, new Connection("0"));
        graph.put(srcNode, tmpHashMap);
    }

    @Override
    public  void deleteConnection(String srcNodeName, String destNodeName) {
        Node srcNode = new Node(srcNodeName);
        Node destNode = new Node(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodes = graph.get(srcNode);
        // Удаляю указанную ноду
        if (connectedNodes.containsKey(destNode)) {
            connectedNodes.remove(destNode);
        } else {
            throw new ConnectionNotExistException("Данной дуги между нодами не существует.");
        }
    }

    @Override
    protected boolean isExistNode(Node node) {
        return graph.containsKey(node);
    }

    @Override
    public HashMap<Node, HashMap<Node, Connection>> getGraph() {
        return graph;
    }

    public void setGraph(HashMap<Node, HashMap<Node, Connection>> graph) {
        this.graph = graph;
    }

    protected void checkExistTwoNodes(Node srcNode, Node destNode) {
        if (!isExistNode(srcNode))
            throw new NodeNotExistException("Исходного узла не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Узла назначения не существует в текущем графе.");
    }
}

