package ru.andrewsalygin.graph.core;

import ru.andrewsalygin.graph.core.utils.*;

import java.io.FileNotFoundException;
import java.util.*;

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

    // Конструктор для переданной мапы
    public OrientedUnweightedGraph(HashMap<Object, HashMap<Object, Object>> map) {
        HashMap<Node, HashMap<Node, Connection>> correctMap = new HashMap<>();
        HashMap<Node, Connection> tmpHashMap;
        for (Object currentNode : map.keySet()) {
            tmpHashMap = new HashMap<>();
            for (Object currentDest : map.get(currentNode).keySet()) {
                tmpHashMap.put((Node) currentDest, (Connection) map.get(currentNode).get(currentDest));
            }
            correctMap.put((Node) currentNode, tmpHashMap);
        }

        graph = correctMap;
    }

    // Конструктор для файла
    public OrientedUnweightedGraph(String pathFile) throws FileNotFoundException {
        HashMap<Object, HashMap<Object, Object>> map = GraphSerializer.openGraphFromFile(pathFile).t1();
        graph = new OrientedUnweightedGraph(map).getGraph();
    }

    // Конструктор для копии
    public OrientedUnweightedGraph(OrientedUnweightedGraph currentGraph) {
        graph = new HashMap<>();
        HashMap<Node, HashMap<Node, Connection>> tmpGraph = new HashMap<>();
        HashMap<Node, Connection> tmpHashMap;
        for (Node currentNode : currentGraph.graph.keySet()) {
            tmpHashMap = new HashMap<>();
            for (Node currentDest : currentGraph.graph.get(currentNode).keySet()) {
                tmpHashMap.put(currentDest, currentGraph.graph.get(currentNode).get(currentDest));
            }
            tmpGraph.put(currentNode, tmpHashMap);
        }
        graph = tmpGraph;
    }

    @Override
    public HashMap<Node, Connection> getConnectedNodes(String nameNode) {
        Node tmpNode = getObjectNodeByName(nameNode);
        return graph.get(tmpNode);
    }

    @Override
    public void addNode(String nodeName) {
        Node tmpNode = getObjectNodeByName(nodeName);
        // проверка на существование такой ноды
        if (isExistNode(tmpNode)) {
            throw new NodeAlreadyExistException("Такая нода уже существует.");
        }
        graph.put(tmpNode, new HashMap<>());
    }

    @Override
    public void deleteNode(String nodeName) {
        Node nodeToDelete = getObjectNodeByName(nodeName);
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
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        if (getConnectedNodes(srcNodeName).containsKey(destNode)) {
            throw new ConnectionAlreadyExistException("Такая дуга уже существует.");
        }

        // получаем список существующих дуг
        HashMap<Node, Connection> tmpHashMap = graph.getOrDefault(srcNode, new HashMap<>());

        // вес дуги 0 по умолчанию
        tmpHashMap.put(destNode, new Connection(0));
        graph.put(srcNode, tmpHashMap);
    }

    @Override
    public void deleteConnection(String srcNodeName, String destNodeName) throws ConnectionNotExistException {
        Node srcNode = getObjectNodeByName(srcNodeName);
        Node destNode = getObjectNodeByName(destNodeName);
        checkExistTwoNodes(srcNode, destNode);

        // Получаю все ноды, с которыми имеет связь источник
        HashMap<Node, Connection> connectedNodes = graph.get(srcNode);
        // Удаляю указанную ноду
        if (connectedNodes.containsKey(destNode)) {
            connectedNodes.remove(destNode);
        } else {
            throw new ConnectionNotExistException("Данной дуги между вершинами не существует.");
        }
    }

    @Override
    protected Node getObjectNodeByName(String nodeName) {
        return new Node(nodeName);
    }


    @Override
    protected boolean isExistNode(Node node) {
        return graph.containsKey(node);
    }

    @Override
    public boolean isExistNodeByName(String nodeName) {
        return isExistNode(getObjectNodeByName(nodeName));
    }

    @Override
    public String getAdjacencyList() {
        StringBuilder resultString = new StringBuilder();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : graph.entrySet()) {
            resultString.append("(").append(entry.getKey()).append("):");
            for (Map.Entry<Node, Connection> innerEntry : entry.getValue().entrySet()) {
                resultString.append("(").append(innerEntry.getKey()).append(");");
            }
            resultString.append('\n');
        }
        return resultString.toString();
    }

    @Override
    public HashMap<Node, HashMap<Node, Connection>> getGraph() {
        return graph;
    }

    protected void checkExistTwoNodes(Node srcNode, Node destNode) {
        if (!isExistNode(srcNode))
            throw new NodeNotExistException("Исходного узла не существует в текущем графе.");
        if (!isExistNode(destNode))
            throw new NodeNotExistException("Узла назначения не существует в текущем графе.");
    }

    public LinkedHashMap<ArrayList<Node>, Integer> shortestPathsForAllPairs() {
        Pair<Map<Node, HashMap<Node, Connection>>, Map<Node, HashMap<Node, Node>>> resultPair = floydWarshall();

        LinkedHashMap<ArrayList<Node>, Integer> shortestPaths = new LinkedHashMap<>();
        ArrayList<Node> tmpList;
        Node tmpNode;
        Node tmpMainNode;
        int index;
        for (Node lineNode : graph.keySet()) {
            for (Node columnNode : graph.keySet()) {
                // Петли это не кратчайший путь, и учитывается несвязный граф
                if (!lineNode.equals(columnNode) && resultPair.t1().get(lineNode).get(columnNode).getWeight() != Integer.MAX_VALUE) {
                    tmpList = new ArrayList<>();
                    // Получил следующую ноду в пути
                    tmpNode = resultPair.t2().get(lineNode).get(columnNode);

                    // Добавляю начальную ноду
                    tmpList.add(lineNode);
                    // Случай, если ноды соединены напрямую
                    if (tmpNode == null) {
                        tmpList.add(columnNode);
                        shortestPaths.put(tmpList, resultPair.t1().get(lineNode).get(columnNode).getWeight());
                    } else {
                        tmpList.add(tmpList.size(), columnNode);
                        index = 0;
                        tmpMainNode = tmpList.get(0);
                        while (!tmpNode.equals(columnNode)) {
                            if (!tmpList.contains(tmpNode)) {
                                tmpList.add(index + 1, tmpNode);
                            }
                            // Если текущая вершина итератора и следующая соединены напрямую
                            if (resultPair.t2().get(tmpMainNode).get(tmpNode) == null) {
                                tmpMainNode = tmpList.get(++index);
                                if (index + 1 < tmpList.size()) {
                                    if (resultPair.t2().get(tmpMainNode).get(tmpList.get(index + 1)) == null) {
                                        tmpNode = tmpList.get(index + 1);
                                    } else {
                                        tmpNode = resultPair.t2().get(tmpMainNode).get(tmpList.get(index + 1));
                                    }
                                }
                            } else {
                                // Берём следующую ноду в качестве временной
                                tmpNode = resultPair.t2().get(tmpMainNode).get(tmpNode);
                            }
                        }
                        // Добавляем вес последней ноды
                        shortestPaths.put(tmpList, resultPair.t1().get(lineNode).get(columnNode).getWeight());
                    }
                }
            }
        }
        return shortestPaths;
    }

    public Pair<Map<Node, HashMap<Node, Connection>>, Map<Node, HashMap<Node, Node>>> floydWarshall() {
        HashMap<Node, HashMap<Node, Connection>> distanceMatrix = new HashMap<>();
        HashMap<Node, HashMap<Node, Node>> passages = new HashMap<>();

        // Инициализация матриц
        for (Node lineNode : graph.keySet()) {
            HashMap<Node, Connection> tmpMap = new HashMap<>();
            passages.put(lineNode, new HashMap<>());
            for (Node columnNode : graph.keySet()) {
                // Инициализируем проходы
                // Элементы на главной диагонали
                if (lineNode.equals(columnNode)) {
                    tmpMap.put(columnNode, new Connection(0));
                    passages.get(lineNode).put(columnNode, null);
                } else {
                    // 0 шаг алгоритма (инициализация матрицы)
                    if (graph.get(lineNode).containsKey(columnNode)) {
                        tmpMap.put(columnNode, graph.get(lineNode).get(columnNode));
                        passages.get(lineNode).put(columnNode, columnNode);
                    } else {
                        tmpMap.put(columnNode, new Connection(Integer.MAX_VALUE));
                    }
                    // Инициализируем изначально всё null-ами
                    passages.get(lineNode).put(columnNode, null);
                }
            }
            distanceMatrix.put(lineNode, tmpMap);
        }

        int firstSummand;
        int secondSummand;
        int tmpValue;
        for (int i = 0; i < graph.size(); i++) {
            for (Node mainNode : graph.keySet()) {
                for (Node lineNode : graph.keySet()) {
                    for (Node columnNode : graph.keySet()) {
                        if (!lineNode.equals(columnNode)) {
                            firstSummand = distanceMatrix.get(lineNode).get(mainNode).getWeight();
                            secondSummand = distanceMatrix.get(mainNode).get(columnNode).getWeight();

                            // Проверка на то, что одно из слагаемых является inf
                            if (firstSummand != Integer.MAX_VALUE && secondSummand != Integer.MAX_VALUE) {
                                tmpValue = firstSummand + secondSummand;
                                if (tmpValue < distanceMatrix.get(lineNode).get(columnNode).getWeight()) {
                                    // Обновляю расстояние между lineNode и columnNode
                                    distanceMatrix.get(lineNode).put(columnNode, new Connection(tmpValue));
                                    // Кладу ноду между lineNode и columnNode
                                    passages.get(lineNode).put(columnNode, mainNode);
                                }
                            }
                        }
                    }
                }
            }
        }

        return new Pair<>(distanceMatrix, passages);
    }
}

