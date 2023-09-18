package ru.andrewsalygin.graph.console;
import ru.andrewsalygin.graph.console.utils.*;
import ru.andrewsalygin.graph.core.*;
import ru.andrewsalygin.graph.core.utils.NodeNotExistException;
import ru.andrewsalygin.graph.core.utils.Pair;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

// Внутренние ошибки приложения #1 - #2 никогда не сработают, но в силу того, что компилятор капризный
// в плане не инициализированных значений, в конце метода я выбрасываю InternalApplicationException
public class Console {
    private static int printMainMenu() {
        System.out.println("Введите цифру действия, которое хотите выполнить:");
        System.out.println("1. Создать новый граф");
        System.out.println("2. Открыть граф из файла");
        System.out.println("3. Выйти из программы");
        Scanner scanner = new Scanner(System.in);
        boolean wrongOption = true;
        String option;
        do {
            try {
                option = scanner.nextLine();
                if (Integer.parseInt(option) >= 1 && Integer.parseInt(option) <= 3) {
                    wrongOption = false;
                } else {
                    System.out.println("Введите одну из цифр пункта меню.");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Введите цифру пункта меню.");
                option = scanner.nextLine();
            }
        } while (wrongOption);
        return Integer.parseInt(option);
    }

    private static GraphType askTypeOfGraph() throws BackToPreviousMenuException, ExitProgramException {
        Scanner scanner = new Scanner(System.in);
        boolean wrongOption = true;
        String option;
        System.out.println("Выберите тип графа:");
        System.out.println("1. Ориентированный без весовой граф");
        System.out.println("2. Ориентированный весовой граф");
        System.out.println("3. Неориентированный без весовой граф");
        System.out.println("4. Неориентированный весовой граф");
        System.out.println("5. Вернуться назад");
        System.out.println("6. Выйти из программы");
        option = scanner.nextLine();
        do {
            try {
                if (Integer.parseInt(option) >= 1 && Integer.parseInt(option) <= 6) {
                    switch (option) {
                        case "1" -> {
                            return GraphType.ORIENTED_UNWEIGHTED;
                        }
                        case "2" -> {
                            return GraphType.ORIENTED_WEIGHTED;
                        }
                        case "3" -> {
                            return GraphType.UNDIRECTED_UNWEIGHTED;
                        }
                        case "4" -> {
                            return GraphType.UNDIRECTED_WEIGHTED;
                        }
                        case "5" -> throw new BackToPreviousMenuException();
                        case "6" -> throw new ExitProgramException();
                    }
                    wrongOption = false;
                } else {
                    System.out.println("Введите одну из цифр пункта меню.");
                }
            } catch (InputMismatchException|NumberFormatException ex) {
                System.out.println("Введите цифру пункта меню.");
                option = scanner.nextLine();
            }
        } while (wrongOption);
        // Это никогда не вернется
        return GraphType.ORIENTED_UNWEIGHTED;
    }

    private static Graph createNewGraph() throws BackToPreviousMenuException, ExitProgramException {
        GraphType graphType = askTypeOfGraph();
        switch (graphType) {
            case ORIENTED_UNWEIGHTED -> {
                return new OrientedUnweightedGraph();
            }
            case ORIENTED_WEIGHTED -> {
                return new OrientedWeightedGraph();
            }
            case UNDIRECTED_UNWEIGHTED -> {
                return new UndirectedUnweightedGraph();
            }
            case UNDIRECTED_WEIGHTED -> {
                return new UndirectedWeightedGraph();
            }
        }
        // Никогда не вернётся
        return new OrientedUnweightedGraph();
    }

    private static Graph inputGraphFromFile(String path) throws BackToPreviousMenuException, ExitProgramException, FileNotFoundException, NotCorrectGraphNameException {
        Graph graph = null;
        Pair<HashMap<Object, HashMap<Object, Object>>, String> result = GraphSerializer.openGraphFromFile(path);
        graph = switch (result.t2()) {
            case "OrientedWeightedGraph" -> new OrientedWeightedGraph(result.t1());
            case "UndirectedWeightedGraph" -> new UndirectedWeightedGraph(result.t1());
            case "OrientedUnweightedGraph" -> new OrientedUnweightedGraph(result.t1());
            case "UndirectedUnweightedGraph" -> new UndirectedUnweightedGraph(result.t1());
            default -> throw new NotCorrectGraphNameException("Граф с таким названием класса не существует.");
        };
        // Никогда не вернётся null
        return graph;
    }
    public static void launchApplication() {
        Graph graph;
        Scanner scanner = new Scanner(System.in);
        do {
            try {
                switch (printMainMenu()) {
                    case 1 -> {
                        graph = createNewGraph();
                        workWithGraph(graph);
                    }
                    case 2 -> {
                        System.out.println("Введите название файла:");
                        try {
                            graph = inputGraphFromFile(scanner.nextLine());
                            workWithGraph(graph);
                        }
                        catch (FileNotFoundException e) {
                            System.out.println("Ошибка: указанный файл не найден.");
                        } catch (NotCorrectGraphNameException e) {
                            System.out.println("Ошибка:" + e.getMessage());
                        }
                    }
                    case 3 -> {
                        return;
                    }
                }
            } catch (BackToPreviousMenuException ex) {
                continue;
            } catch (ExitProgramException ex) {
                return;
            }
        } while (true);
    }

    private static ReturnCode nodeError(Graph graph, String nodeName) {
        String option;
        Scanner scanner = new Scanner(System.in);
        try {
            throw new NodeNotExistException("Указанной вершины не существует.");
        } catch (NodeNotExistException ex) {
            System.out.println("Хотите её создать или прекратить действие? (с/П)");
            option = scanner.nextLine();
            if (option.equals("с")) {
                try {
                    graph.addNode(nodeName);
                } catch (Exception e) {
                    System.out.println("Ошибка:" + e.getMessage());
                }
            } else {
                return ReturnCode.CONTINUE_WHILE;
            }
        }
        return ReturnCode.OK;
    }

    private static void workWithGraph(Graph graph) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Введите цифру действия, которое хотите выполнить:");
            System.out.println("1. Добавить вершину");
            System.out.println("2. Удалить вершину");
            if (graph instanceof UndirectedWeightedGraph || graph instanceof UndirectedUnweightedGraph) {
                System.out.println("3. Добавить ребро");
                System.out.println("4. Удалить ребро");
            } else {
                System.out.println("3. Добавить дугу");
                System.out.println("4. Удалить дугу");
            }
            System.out.println("5. Обновить вес");
            System.out.println("6. Проверить существует ли вершина");
            if (graph instanceof UndirectedWeightedGraph) {
                System.out.println("7. Проверить существует ли ребро и вывести её вес");
            } else if (graph instanceof UndirectedUnweightedGraph) {
                System.out.println("7. Проверить существует ли ребро");
            } else if (graph instanceof OrientedWeightedGraph) {
                System.out.println("7. Проверить существует ли дуга и вывести её вес");
            } else {
                System.out.println("7. Проверить существует ли дуга");
            }
            System.out.println("8. Посмотреть список смежности");
            System.out.println("9. Сохранить граф в файл");
            System.out.println("10. Вернуться назад");
            System.out.println("11. Выйти из программы");
            String option = scanner.nextLine();

            // TO DO: Перенести в отдельный метод повторение кода для двух вершин
            try {
                if (Integer.parseInt(option) >= 1 && Integer.parseInt(option) <= 11) {
                    switch (option) {
                        case "1" -> {
                            System.out.println("Введите название вершины для добавления:");
                            try {
                                graph.addNode(scanner.nextLine());
                            } catch (Exception ex) {
                                System.out.println("Ошибка:" + ex.getMessage());
                            }
                        }
                        case "2" -> {
                            System.out.println("Введите название вершины для удаления:");
                            try {
                                graph.deleteNode(scanner.nextLine());
                            } catch (Exception ex) {
                                System.out.println("Ошибка:" + ex.getMessage());
                            }
                        }
                        case "3" -> {
                            System.out.println("Введите название вершины источника (или 'выход' для выхода из действия):");
                            String nodeSrc = scanner.nextLine();
                            if (!graph.isExistNodeByName(nodeSrc) || nodeSrc.equals("выход")) {
                                if (nodeSrc.equals("выход")) {
                                    continue;
                                } else {
                                    if (nodeError(graph, nodeSrc) == ReturnCode.CONTINUE_WHILE) {
                                        continue;
                                    }
                                }
                            }
                            System.out.println("Введите название вершины приемника (или 'выход' для выхода из действия):");
                            String nodeDest = scanner.nextLine();
                            if (!graph.isExistNodeByName(nodeDest) || nodeDest.equals("выход")) {
                                if (nodeDest.equals("выход")) {
                                    continue;
                                } else {
                                    if (nodeError(graph, nodeDest) == ReturnCode.CONTINUE_WHILE) {
                                        continue;
                                    }
                                }
                            }
                            if (graph instanceof UndirectedWeightedGraph) {
                                UndirectedWeightedGraph tmpGraph = (UndirectedWeightedGraph) graph;
                                System.out.println("Введите вес ребра:");
                                int weight;
                                do {
                                    try {
                                        weight = scanner.nextInt();
                                        break;
                                    } catch (InputMismatchException ex) {
                                        System.out.println("Ошибка: Введите число.");
                                    }
                                } while (true);
                                tmpGraph.addConnection(nodeSrc, nodeDest, weight);
                            } else if (graph instanceof UndirectedUnweightedGraph) {
                                UndirectedUnweightedGraph tmpGraph = (UndirectedUnweightedGraph) graph;
                                tmpGraph.addConnection(nodeSrc, nodeDest);
                            } else if (graph instanceof OrientedWeightedGraph) {
                                OrientedWeightedGraph tmpGraph = (OrientedWeightedGraph) graph;
                                System.out.println("Введите вес дуги:");
                                int weight;
                                do {
                                    try {
                                        weight = scanner.nextInt();
                                        break;
                                    } catch (InputMismatchException ex) {
                                        System.out.println("Ошибка: Введите число.");
                                    }
                                } while (true);
                                tmpGraph.addConnection(nodeSrc, nodeDest, weight);
                            } else {
                                OrientedUnweightedGraph tmpGraph = (OrientedUnweightedGraph) graph;
                                tmpGraph.addConnection(nodeSrc, nodeDest);
                            }
                        }
                        case "4" -> {
                            System.out.println("Введите название вершины источника (или 'выход' для выхода из действия):");
                            String nodeSrc = scanner.nextLine();
                            if (!graph.isExistNodeByName(nodeSrc) || nodeSrc.equals("выход")) {
                                if (nodeSrc.equals("выход")) {
                                    continue;
                                } else {
                                    if (nodeError(graph, nodeSrc) == ReturnCode.CONTINUE_WHILE) {
                                        continue;
                                    }
                                }
                            }
                            System.out.println("Введите название вершины приемника (или 'выход' для выхода из действия):");
                            String nodeDest = scanner.nextLine();
                            if (!graph.isExistNodeByName(nodeDest) || nodeDest.equals("выход")) {
                                if (nodeDest.equals("выход")) {
                                    continue;
                                } else {
                                    if (nodeError(graph, nodeDest) == ReturnCode.CONTINUE_WHILE) {
                                        continue;
                                    }
                                }
                            }
                            graph.deleteConnection(nodeSrc, nodeDest);
                        }
                        case "5" -> {
                            System.out.println("Введите название вершины источника (или 'выход' для выхода из действия):");
                            String nodeSrc = scanner.nextLine();
                            if (!graph.isExistNodeByName(nodeSrc) || nodeSrc.equals("выход")) {
                                if (nodeSrc.equals("выход")) {
                                    continue;
                                } else {
                                    if (nodeError(graph, nodeSrc) == ReturnCode.CONTINUE_WHILE) {
                                        continue;
                                    }
                                }
                            }
                            System.out.println("Введите название вершины приемника (или 'выход' для выхода из действия):");
                            String nodeDest = scanner.nextLine();
                            if (!graph.isExistNodeByName(nodeDest) || nodeDest.equals("выход")) {
                                if (nodeDest.equals("выход")) {
                                    continue;
                                } else {
                                    if (nodeError(graph, nodeDest) == ReturnCode.CONTINUE_WHILE) {
                                        continue;
                                    }
                                }
                            }
                            // СДЕЛАТЬ ПУНКТ ТОЛЬКО ДЛЯ ВЕСОВЫХ ГРАФОВ
//                            graph.updateWeight();
                        }
                        case "6" -> {

                        }
                        case "7" -> {}
                        case "8" -> {
                            // Сделать метод для отображения списка смежности
                        }
                        case "9" -> {
                            System.out.println("Введите путь до файла, в который нужно сохранить граф");
                            String path = scanner.nextLine();
                            try {
                                GraphSerializer.saveGraphToFile(path, graph);
                            } catch (FileNotFoundException e) {
                                System.out.println("Ошибка: указанный файл не найден.");
                            }
                        }
                        case "10" -> {
                            return;
                        }
                        case "11" -> System.exit(0);
                    }
                } else {
                    System.out.println("Введите одну из цифр пункта меню.");
                }
            } catch (InputMismatchException|NumberFormatException ex) {
                System.out.println("Введите цифру пункта меню.");
                option = scanner.nextLine();
            }
        } while (true);
    }
}
