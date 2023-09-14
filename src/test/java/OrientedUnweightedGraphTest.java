import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.andrewsalygin.graph.Graph;
import ru.andrewsalygin.graph.Node;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;
import ru.andrewsalygin.graph.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.utils.NodeNotExistException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Salygin
 */

// HM = HashMap
class OrientedUnweightedGraphTest {
    private OrientedUnweightedGraph<Integer> graph;
    @BeforeEach
    void setUp() {
        graph = new OrientedUnweightedGraph<>();
    }

    // ТЕСТЫ ДЛЯ НОД

    // ДОБАВЛЕНИЕ
    // Обычное добавление ноды
    @Test
    void addNode_withoutExceptions() throws InvocationTargetException, IllegalAccessException {
        Assertions.assertDoesNotThrow(() -> graph.addNode(5));
        Method getGraph = null;
        try {
            getGraph = graph.getClass().getDeclaredMethod("getGraph");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGraph.setAccessible(true);
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> localGraph = (HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.containsKey(new Node<Integer>(5)));
    }

    // Уже существование ноды, которая добавляется
    @Test
    void addNode_nodeAlreadyExist() {
        graph.addNode(5);
        Assertions.assertThrows(NodeAlreadyExistException.class, () -> graph.addNode(5));
    }

    // УДАЛЕНИЕ
    // Удаление ноды, которая не существует
    @Test
    void deleteNode_nodeNotExist() {
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.deleteNode(5));
    }

    // Удаление ноды, которая не связана с другими нодами
    @Test
    void deleteNode_nodeExistWithoutOthers() throws IllegalAccessException, InvocationTargetException {
        graph.addNode(5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        Method getGraph = null;
        try {
            getGraph = graph.getClass().getDeclaredMethod("getGraph");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGraph.setAccessible(true);
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> localGraph = (HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.isEmpty());
    }

    // Удаление ноды, к которой есть связь от одной ноды
    @Test
    void deleteNode_nodeExistWithOneNodeToDeleteNode() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addConnection(7, 5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(7));
    }

    // Удаление ноды, когда несколько нод имеют связь с удаляемой
    @Test
    void deleteNode_nodeExistWithSeveralNodesToDeleteNode() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addNode(10);
        graph.addConnection(7, 5);
        graph.addConnection(10, 5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(7));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(10));
    }

    // В нодах источника есть несколько нод, и удаляется одна из их списка
    @Test
    void deleteNode_nodeExistWithOneNodeToDeleteAndSeveralNodes() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addNode(10);
        graph.addConnection(7, 5);
        graph.addConnection(10, 5);
        graph.addConnection(7, 10);
        graph.addConnection(10, 7);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        HashMap<Node<Integer>, Integer> tmpNodeList1 = new HashMap<>();
        HashMap<Node<Integer>, Integer> tmpNodeList2 = new HashMap<>();
        tmpNodeList1.put(new Node<>(7), 0);
        tmpNodeList2.put(new Node<>(10), 0);
        Assertions.assertEquals(tmpNodeList2, graph.getConnectedNodes(7));
        Assertions.assertEquals(tmpNodeList1, graph.getConnectedNodes(10));
    }

    // ТЕСТЫ ДЛЯ ДУГ

    // Тест на добавление петли (обычный)
    @Test
    void addArc_withLoop() throws IllegalAccessException, InvocationTargetException {
        graph.addNode(5);
        Assertions.assertDoesNotThrow(() -> graph.addConnection(5, 5));
        Method getGraph = null;
        try {
            getGraph = graph.getClass().getDeclaredMethod("getGraph");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGraph.setAccessible(true);
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> localGraph = (HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.get(new Node(5)).containsKey(new Node<Integer>(5)));
    }

    // Тест на проверку добавления петли, которая уже существует
    @Test
    void addArc_withLoopTwiceError() {
        graph.addNode(5);
        graph.addConnection(5, 5);
        Assertions.assertThrows(ConnectionAlreadyExistException.class, () -> graph.addConnection(5, 5));
    }

    // Тест на удаление петли
    @Test
    void deleteArc_withLoop() throws InvocationTargetException, IllegalAccessException {
        graph.addNode(5);
        graph.addConnection(5, 5);
        Assertions.assertDoesNotThrow(() -> graph.deleteConnection(5, 5));
        Method getGraph = null;
        try {
            getGraph = graph.getClass().getDeclaredMethod("getGraph");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGraph.setAccessible(true);
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> localGraph = (HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.get(new Node(5)).isEmpty());
    }

    // Тест на удаление ноды с петлёй
    @Test
    void deleteNode_withLoop() throws InvocationTargetException, IllegalAccessException {
        graph.addNode(5);
        graph.addConnection(5, 5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        Method getGraph = null;
        try {
            getGraph = graph.getClass().getDeclaredMethod("getGraph");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGraph.setAccessible(true);
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> localGraph = (HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>) getGraph.invoke(graph);
        Assertions.assertEquals(false, localGraph.containsKey(new Node<Integer>(5)));
    }

    // Обычное добавление дуги
    @Test
    void addArc_withEmptyHMAndTwoNodes() throws InvocationTargetException, IllegalAccessException {
        graph.addNode(5);
        graph.addNode(7);
        Assertions.assertDoesNotThrow(() -> graph.addConnection(5, 7));
        Method getGraph = null;
        try {
            getGraph = graph.getClass().getDeclaredMethod("getGraph");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGraph.setAccessible(true);
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> localGraph = (HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.get(new Node(5)).containsKey(new Node<Integer>(7)));
    }

    // Добавление дуги, но ноды источника не существует
    @Test
    void addArc_withEmptyHMAndOneNodeSrc() {
        graph.addNode(5);
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection(5, 7));
    }

    // Добавление дуги, но ноды назначения не существует
    @Test
    void addArc_withEmptyHMAndOneNodeDest() {
        graph.addNode(5);
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection(7, 5));
    }

    // Тест на добавление дуги у несуществующих нод 1
    @Test
    void addArc_FirstNodeNotExist() {
        graph.addNode(5);
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection(7, 5));
    }

    // Тест на добавление дуги у несуществующих нод 2
    @Test
    void addArc_SecondNodeNotExist() {
        graph.addNode(5);
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection(5, 7));
    }

    // Тест на добавление дуги, которая уже существует
    @Test
    void addArc_testTwice() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addConnection(5, 7);
        Assertions.assertThrows(ConnectionAlreadyExistException.class, () -> graph.addConnection(5, 7));
    }

    // Удаление дуги, которая не существует
    @Test
    void deleteArc_notExistArc() {
        graph.addNode(5);
        graph.addNode(7);
        Assertions.assertThrows(ConnectionNotExistException.class, () -> graph.deleteConnection(5, 7));
    }

    // Обычное удаление дуги, которая соединяет две ноды
    @Test
    void deleteArc_betweenTwoNodes() throws InvocationTargetException, IllegalAccessException {
        graph.addNode(5);
        graph.addNode(7);
        graph.addConnection(5, 7);
        Assertions.assertDoesNotThrow(() -> graph.deleteConnection(5, 7));
        Method getGraph = null;
        try {
            getGraph = graph.getClass().getDeclaredMethod("getGraph");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGraph.setAccessible(true);
        HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>> localGraph = (HashMap<Node<Integer>, HashMap<Node<Integer>, Integer>>) getGraph.invoke(graph);
        Assertions.assertEquals(false, localGraph.get(new Node(5)).containsKey(new Node<Integer>(5)));
    }

    // Удаление дуги, которая существует в ту сторону, но не в обратную
    @Test
    void deleteArc_wrongDirectionTest() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addConnection(5, 7);
        Assertions.assertThrows(ConnectionNotExistException.class, () -> graph.deleteConnection(7, 5));
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // Проверка работоспособности метода существования ноды (нода существует)
    @Test
    void isExistNode_nodeExist() {
        graph.addNode(5);
        try {
            Method isExistMethod = OrientedUnweightedGraph.class.getDeclaredMethod("isExistNode", Node.class);
            isExistMethod.setAccessible(true);
            Assertions.assertEquals(true, isExistMethod.invoke(graph, new Node<>(5)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Проверка работоспособности метода существования ноды (нода не существует)
    @Test
    void isExistNode_nodeNotExist() {
        try {
            Method isExistMethod = OrientedUnweightedGraph.class.getDeclaredMethod("isExistNode", Node.class);
            isExistMethod.setAccessible(true);
            Assertions.assertEquals(false, isExistMethod.invoke(graph, new Node<>(5)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Тест для получения списка нод
    @Test
    void getConnectedNodes_test() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addConnection(5, 7);
        HashMap<Node<Integer>, Integer> connectedNodes = new HashMap<>();
        connectedNodes.put(new Node<>(7), 0);
        Assertions.assertEquals(connectedNodes, graph.getConnectedNodes(5));
    }
}
