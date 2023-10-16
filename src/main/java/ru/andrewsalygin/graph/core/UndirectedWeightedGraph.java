package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Andrew Salygin
 */
public class UndirectedWeightedGraph extends UndirectedUnweightedGraph {
    public UndirectedWeightedGraph() {
        super();
    }
    public UndirectedWeightedGraph(String pathFile) throws FileNotFoundException {
        super(pathFile);
    }
    public UndirectedWeightedGraph(HashMap<Object, HashMap<Object, Object>> map) {
        super(map);
    }
    public UndirectedWeightedGraph(UndirectedWeightedGraph currentGraph) {
        super(currentGraph);
    }
    public final void addConnection(String srcNodeName, String destNodeName, Integer weight) {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);
        if (srcNode.equals(destNode)) {
            throw new ConnectionNotExistException("Петлей в неориентированном графе быть не может.");
        }

        if (getConnectedNodes(srcNodeName).containsKey(destNode)) {
            throw new ConnectionAlreadyExistException("Такое ребро уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node, Connection> tmpHashMapSrc = graph.getOrDefault(srcNode, new HashMap<>());
        HashMap<Node, Connection> tmpHashMapDest = graph.getOrDefault(destNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMapSrc.put(destNode, new Connection(weight));
        tmpHashMapDest.put(srcNode, new Connection(weight));
        graph.put(srcNode, tmpHashMapSrc);
        graph.put(destNode, tmpHashMapDest);
    }

    @Override
    public String getAdjacencyList() {
        StringBuilder resultString = new StringBuilder();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            resultString.append("(").append(entry.getKey()).append("):");
            for (Map.Entry<Node, Connection> innerEntry : entry.getValue().entrySet()) {
                resultString.append("(").append(innerEntry.getKey()).append(")[").append(innerEntry.getValue()).append("];");
            }
            resultString.append('\n');
        }
        return resultString.toString();
    }

    public void updateWeight(String srcNodeName, String destNodeName, Integer weight) {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodes = graph.get(srcNode);
        if (connectedNodes.containsKey(destNode)) {
            connectedNodes.put(destNode, new Connection(weight));
        } else {
            throw new ConnectionNotExistException("Данного ребра между вершинами не существует.");
        }
    }

    public void kraskalsAlgorithm() {
        ArrayList<KraskalsConnection> edges = new ArrayList<>();
        // нода и её индекс компоненты
        HashMap<Node, Integer> components = new HashMap<>();
        // индекс компоненты и её ноды
        HashMap<Integer, List<Node>> nodesInComponent = new HashMap<>();

        int countOfComponents = 0;
        // Добавил ребра
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            for (Map.Entry<Node, Connection> innerEntry : entry.getValue().entrySet()) {
                edges.add(new KraskalsConnection(innerEntry.getValue().weight, entry.getKey(), innerEntry.getKey()));
            }
            countOfComponents++;
            components.put(entry.getKey(), countOfComponents);
            nodesInComponent.computeIfAbsent(countOfComponents, k -> new ArrayList<>()).add(entry.getKey());
        }

        edges.sort(Comparator.naturalOrder());

        int totalWeight = 0;

        HashMap<Object, HashMap<Object, Object>> mst = new HashMap<>();
        for (KraskalsConnection edge : edges) {
            int component = components.get(edge.node1);
            int otherComponent = components.get(edge.node2);
            // если компоненты смежных нод различны
            if (!(component == otherComponent)) {
                // Если количество вершин в первой компоненте больше, то добавляем в первую все из другой
                if (nodesInComponent.get(component).size() > nodesInComponent.get(otherComponent).size()) {
                    for (Node node : nodesInComponent.get(otherComponent)) {
                        components.put(node, component);
                    }
                    nodesInComponent.get(component).addAll(nodesInComponent.get(otherComponent));
                    nodesInComponent.remove(otherComponent);
                } else {
                    for (Node node : nodesInComponent.get(component)) {
                        components.put(node, otherComponent);
                    }
                    nodesInComponent.get(otherComponent).addAll(nodesInComponent.get(component));
                    nodesInComponent.remove(component);
                }
                // формирую mst
                mst.computeIfAbsent(edge.node1, k -> new HashMap<>()).put(edge.node2, edge);
                mst.computeIfAbsent(edge.node2, k -> new HashMap<>()).put(edge.node1, edge);
                totalWeight += edge.weight;
                countOfComponents--;
            }
        }

        String type = countOfComponents > 1 ? "Лес" : "Дерево";
        System.out.println("Общий вес: " + totalWeight + ", тип: " + type);
        UndirectedWeightedGraph resultGraph;
        resultGraph = new UndirectedWeightedGraph(mst);
        System.out.println(resultGraph.getAdjacencyList());
    }
}
