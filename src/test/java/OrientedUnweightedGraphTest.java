import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.andrewsalygin.graph.core.OrientedUnweightedGraph;
import ru.andrewsalygin.graph.core.utils.ConnectionAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.core.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.core.utils.NodeNotExistException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author Andrew Salygin
 */

// HM = HashMap
class OrientedUnweightedGraphTest {
    private OrientedUnweightedGraph graph;
    Class<?> nodeClass;
    Constructor<?> constructorNode;
    @BeforeEach
    void setUp() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        graph = new OrientedUnweightedGraph();
        nodeClass = Class.forName("ru.andrewsalygin.graph.core.Node");
        Class<?>[] constructorParameterTypes = { String.class };
        constructorNode = nodeClass.getDeclaredConstructor(constructorParameterTypes);
        constructorNode.setAccessible(true);
    }

    // ТЕСТЫ ДЛЯ НОД

    // ДОБАВЛЕНИЕ
    // Обычное добавление ноды
    @Test
    void addNode_withoutExceptions() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Assertions.assertDoesNotThrow(() -> graph.addNode("5"));
        Method getGraph = null;
        getGraph = graph.getClass().getDeclaredMethod("getGraph");
        getGraph.setAccessible(true);
        Object node = constructorNode.newInstance("5");

        HashMap<Object, HashMap<Object, Object>> localGraph = (HashMap<Object, HashMap<Object, Object>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.containsKey(node));
    }

    // Уже существование ноды, которая добавляется
    @Test
    void addNode_nodeAlreadyExist() {
        graph.addNode("5");
        Assertions.assertThrows(NodeAlreadyExistException.class, () -> graph.addNode("5"));
    }

    // УДАЛЕНИЕ
    // Удаление ноды, которая не существует
    @Test
    void deleteNode_nodeNotExist() {
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.deleteNode("5"));
    }

    // Удаление ноды, которая не связана с другими нодами
    @Test
    void deleteNode_nodeExistWithoutOthers() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        graph.addNode("5");
        Assertions.assertDoesNotThrow(() -> graph.deleteNode("5"));
        Method getGraph = null;
        getGraph = graph.getClass().getDeclaredMethod("getGraph");
        getGraph.setAccessible(true);
        HashMap<Object, HashMap<Object, Object>> localGraph = (HashMap<Object, HashMap<Object, Object>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.isEmpty());
    }

    // Удаление ноды, к которой есть связь от одной ноды
    @Test
    void deleteNode_nodeExistWithOneNodeToDeleteNode() {
        graph.addNode("5");
        graph.addNode("7");
        graph.addConnection("7", "5");
        Assertions.assertDoesNotThrow(() -> graph.deleteNode("5"));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes("7"));
    }

    // Удаление ноды, когда несколько нод имеют связь с удаляемой
    @Test
    void deleteNode_nodeExistWithSeveralNodesToDeleteNode() {
        graph.addNode("5");
        graph.addNode("7");
        graph.addNode("10");
        graph.addConnection("7", "5");
        graph.addConnection("10", "5");
        Assertions.assertDoesNotThrow(() -> graph.deleteNode("5"));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes("7"));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes("10"));
    }

    // В нодах источника есть несколько нод, и удаляется одна из их списка
    @Test
    void deleteNode_nodeExistWithOneNodeToDeleteAndSeveralNodes() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        graph.addNode("5");
        graph.addNode("7");
        graph.addNode("10");
        graph.addConnection("7", "5");
        graph.addConnection("10", "5");
        graph.addConnection("7", "10");
        graph.addConnection("10", "7");
        Assertions.assertDoesNotThrow(() -> graph.deleteNode("5"));
        HashMap<Object, Object> tmpNodeList1 = new HashMap<>();
        HashMap<Object, Object> tmpNodeList2 = new HashMap<>();
        Object node1 = constructorNode.newInstance("7");
        Object node2 = constructorNode.newInstance("10");
        tmpNodeList1.put(node1, 0);
        tmpNodeList2.put(node2, 0);
        Assertions.assertTrue(graph.getConnectedNodes("7").containsKey(constructorNode.newInstance("10")));
        Assertions.assertTrue(graph.getConnectedNodes("10").containsKey(constructorNode.newInstance("7")));
    }

    // ТЕСТЫ ДЛЯ ДУГ

    // Тест на добавление петли (обычный)
    @Test
    void addArc_withLoop() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        graph.addNode("5");
        Assertions.assertDoesNotThrow(() -> graph.addConnection("5", "5"));
        Method getGraph = null;
        getGraph = graph.getClass().getDeclaredMethod("getGraph");
        getGraph.setAccessible(true);
        Object node = constructorNode.newInstance("5");
        HashMap<Object, HashMap<Object, Object>> localGraph = (HashMap<Object, HashMap<Object, Object>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.get(node).containsKey(node));
    }

    // Тест на проверку добавления петли, которая уже существует
    @Test
    void addArc_withLoopTwiceError() {
        graph.addNode("5");
        graph.addConnection("5", "5");
        Assertions.assertThrows(ConnectionAlreadyExistException.class, () -> graph.addConnection("5", "5"));
    }

    // Тест на удаление петли
    @Test
    void deleteArc_withLoop() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        graph.addNode("5");
        graph.addConnection("5", "5");
        Assertions.assertDoesNotThrow(() -> graph.deleteConnection("5", "5"));
        Method getGraph = null;
        getGraph = graph.getClass().getDeclaredMethod("getGraph");
        getGraph.setAccessible(true);
        Object node = constructorNode.newInstance("5");
        HashMap<Object, HashMap<Object, Object>> localGraph = (HashMap<Object, HashMap<Object, Object>>) getGraph.invoke(graph);
        Assertions.assertEquals(true, localGraph.get(node).isEmpty());
    }

    // Тест на удаление ноды с петлёй
    @Test
    void deleteNode_withLoop() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        graph.addNode("5");
        graph.addConnection("5", "5");
        Assertions.assertDoesNotThrow(() -> graph.deleteNode("5"));
        Method getGraph = null;
        getGraph = graph.getClass().getDeclaredMethod("getGraph");
        getGraph.setAccessible(true);
        Object node = constructorNode.newInstance("5");
        HashMap<Object, HashMap<Object, Object>> localGraph = (HashMap<Object, HashMap<Object, Object>>) getGraph.invoke(graph);
        Assertions.assertFalse(localGraph.containsKey(node));
    }

    // Обычное добавление дуги
    @Test
    void addArc_withEmptyHMAndTwoNodes() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        graph.addNode("5");
        graph.addNode("7");
        Assertions.assertDoesNotThrow(() -> graph.addConnection("5", "7"));
        Method getGraph = graph.getClass().getDeclaredMethod("getGraph");
        Object node1 = constructorNode.newInstance("5");
        Object node2 = constructorNode.newInstance("7");
        HashMap<Object, HashMap<Object, Object>> localGraph = (HashMap<Object, HashMap<Object, Object>>) getGraph.invoke(graph);
        Assertions.assertTrue(localGraph.get(node1).containsKey(node2));
    }

    // Добавление дуги, но ноды источника не существует
    @Test
    void addArc_withEmptyHMAndOneNodeSrc() {
        graph.addNode("5");
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection("5", "7"));
    }

    // Добавление дуги, но ноды назначения не существует
    @Test
    void addArc_withEmptyHMAndOneNodeDest() {
        graph.addNode("5");
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection("7", "5"));
    }

    // Тест на добавление дуги у несуществующих нод 1
    @Test
    void addArc_FirstNodeNotExist() {
        graph.addNode("5");
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection("7", "5"));
    }

    // Тест на добавление дуги у несуществующих нод 2
    @Test
    void addArc_SecondNodeNotExist() {
        graph.addNode("5");
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addConnection("5", "7"));
    }

    // Тест на добавление дуги, которая уже существует
    @Test
    void addArc_testTwice() {
        graph.addNode("5");
        graph.addNode("7");
        graph.addConnection("5", "7");
        Assertions.assertThrows(ConnectionAlreadyExistException.class, () -> graph.addConnection("5", "7"));
    }

    // Удаление дуги, которая не существует
    @Test
    void deleteArc_notExistArc() {
        graph.addNode("5");
        graph.addNode("7");
        Assertions.assertThrows(ConnectionNotExistException.class, () -> graph.deleteConnection("5", "7"));
    }

    // Обычное удаление дуги, которая соединяет две ноды
    @Test
    void deleteArc_betweenTwoNodes() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        graph.addNode("5");
        graph.addNode("7");
        graph.addConnection("5", "7");
        Assertions.assertDoesNotThrow(() -> graph.deleteConnection("5", "7"));
        Method getGraph = null;
        getGraph = graph.getClass().getDeclaredMethod("getGraph");
        getGraph.setAccessible(true);
        Object node = constructorNode.newInstance("5");
        HashMap<Object, HashMap<Object, Object>> localGraph = (HashMap<Object, HashMap<Object, Object>>) getGraph.invoke(graph);
        Assertions.assertEquals(false, localGraph.get(node).containsKey(node));
    }

    // Удаление дуги, которая существует в ту сторону, но не в обратную
    @Test
    void deleteArc_wrongDirectionTest() {
        graph.addNode("5");
        graph.addNode("7");
        graph.addConnection("5", "7");
        Assertions.assertThrows(ConnectionNotExistException.class, () -> graph.deleteConnection("7", "5"));
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // Проверка работоспособности метода существования ноды (нода существует)
    @Test
    void isExistNode_nodeExist() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        graph.addNode("5");
        Method isExistMethod = OrientedUnweightedGraph.class.getDeclaredMethod("isExistNodeByName", String.class);
        isExistMethod.setAccessible(true);
        Assertions.assertEquals(true, isExistMethod.invoke(graph, "5"));
    }

    // Проверка работоспособности метода существования ноды (нода не существует)
    @Test
    void isExistNode_nodeNotExist() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method isExistMethod = OrientedUnweightedGraph.class.getDeclaredMethod("isExistNodeByName", String.class);
        isExistMethod.setAccessible(true);
        Assertions.assertEquals(false, isExistMethod.invoke(graph, "5"));
    }

    // Тест для получения списка нод
    @Test
    void getConnectedNodes_test() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        graph.addNode("5");
        graph.addNode("7");
        graph.addConnection("5", "7");
        HashMap<Object, Object> connectedNodes = new HashMap<>();
        Object node = constructorNode.newInstance("7");
        connectedNodes.put(node, 0);
        Assertions.assertEquals(true, graph.getConnectedNodes("5").containsKey(constructorNode.newInstance("7")));
    }
}
