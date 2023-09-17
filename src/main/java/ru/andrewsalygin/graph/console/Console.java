package ru.andrewsalygin.graph.console;
import ru.andrewsalygin.graph.console.utils.BackToPreviousMenuException;
import ru.andrewsalygin.graph.console.utils.ExitProgramException;
import ru.andrewsalygin.graph.core.*;

import java.io.FileNotFoundException;
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
        String option = "";
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
        String option = "";
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

    private static Graph inputGraphFromFile(String path) throws BackToPreviousMenuException, ExitProgramException {
        Graph graph = createNewGraph();
        try {
            graph = new OrientedUnweightedGraph(GraphSerializer.openGraphFromFile(path));
            workWithGraph(graph);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: указанный файл не найден.");
        }
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
                        graph = inputGraphFromFile(scanner.nextLine());
                        workWithGraph(graph);
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

    private static void workWithGraph(Graph graph) {
        // Определение типа графа
        if (graph instanceof OrientedUnweightedGraph) {
            graph = (OrientedUnweightedGraph) graph;
        } else if (graph instanceof OrientedWeightedGraph) {
            graph = (OrientedWeightedGraph) graph;
        } else if (graph instanceof UndirectedUnweightedGraph) {
            graph = (UndirectedUnweightedGraph) graph;
        } else if (graph instanceof UndirectedWeightedGraph) {
            graph = (UndirectedWeightedGraph) graph;
        }

        System.out.println("Введите цифру действия, которое хотите выполнить:");
        System.out.println("1. Добавить вершину");
        System.out.println("2. Удалить вершину");
        if (graph instanceof OrientedUnweightedGraph || graph instanceof OrientedWeightedGraph) {
            System.out.println("3. Добавить дугу");
            System.out.println("4. Удалить дугу");
        } else {
            System.out.println("3. Добавить ребро");
            System.out.println("4. Удалить ребро");
        }
        System.out.println("5. Обновить вес");
        System.out.println("6. Проверить существует ли вершина");
        if (graph instanceof OrientedUnweightedGraph) {
            System.out.println("7. Проверить существует ли дуга");
        } else if (graph instanceof OrientedWeightedGraph) {
            System.out.println("7. Проверить существует ли дуга и вывести её вес");
        } else if (graph instanceof UndirectedUnweightedGraph) {
            System.out.println("7. Проверить существует ли ребро");
        } else {
            System.out.println("7. Проверить существует ли ребро и вывести её вес");
        }
        System.out.println("8. Посмотреть список смежности");
        System.out.println("9. Сохранить граф в файл");
    }
}
