import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.andrewsalygin.graph.Node;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;
import ru.andrewsalygin.graph.OrientedWeightedGraph;
import ru.andrewsalygin.graph.utils.ConnectionNotExistException;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.utils.NodeNotExistException;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author Andrew Salygin
 */

// HM = HashMap
class OrientedUnweightedGraphTest {
    private OrientedUnweightedGraph<Integer> graph;
    @BeforeEach
    void setUp() {
        graph = new OrientedWeightedGraph<>();
    }

    // Тесты для нод
    @Test
    void addNode_withoutExceptions() {
        Assertions.assertDoesNotThrow(() -> graph.addNode(5));
    }

    @Test
    void addNode_nodeAlreadyExist() {
        graph.addNode(5);
        Assertions.assertThrows(NodeAlreadyExistException.class, () -> graph.addNode(5));
    }

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

    @Test
    void deleteNode_nodeNotExist() {
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.deleteNode(5));
    }
    @Test
    void deleteNode_nodeExistWithoutOthers() {
        graph.addNode(5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
    }

    @Test
    void deleteNode_nodeExistWithOneNodeToDeleteNode() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addArc(7, 5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(7));
    }

    @Test
    void deleteNode_nodeExistWithSeveralNodesToDeleteNode() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addNode(10);
        graph.addArc(7, 5);
        graph.addArc(10, 5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(7));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(10));
    }

    @Test
    void deleteNode_nodeExistWithOneNodeToDeleteAndSeveralNodes() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addNode(10);
        graph.addArc(7, 5);
        graph.addArc(10, 5);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(5));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(7));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(10));
    }

    @Test
    void deleteNode_deleteTransitiveNode() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addNode(10);
        graph.addArc(5, 7);
        graph.addArc(7, 10);
        graph.addArc(10, 7);
        Assertions.assertDoesNotThrow(() -> graph.deleteNode(7));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(5));
        Assertions.assertEquals(new HashMap<>(), graph.getConnectedNodes(10));
    }

    // Тесты для дуг
    @Test
    void addArc_withEmptyHMAndTwoNodes() {
        graph.addNode(5);
        graph.addNode(7);
        Assertions.assertDoesNotThrow(() -> graph.addArc(5, 7));
    }

    @Test
    void addArc_withEmptyHMAndOneNodeSrc() {
        graph.addNode(5);
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addArc(5, 7));
    }

    @Test
    void addArc_withEmptyHMAndOneNodeDest() {
        graph.addNode(5);
        Assertions.assertThrows(NodeNotExistException.class, () -> graph.addArc(7, 5));
    }

    @Test
    void getConnectedNodes_test() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addArc(5, 7);
        HashMap<Node<Integer>, Integer> connectedNodes = new HashMap<>();
        connectedNodes.put(new Node<>(7), 0);
        Assertions.assertEquals(connectedNodes, graph.getConnectedNodes(5));
    }

    @Test
    void deleteArc_notExistArc() {
        graph.addNode(5);
        graph.addNode(7);
        Assertions.assertThrows(ConnectionNotExistException.class, () -> graph.deleteArc(5, 7));
    }

    @Test
    void deleteArc_betweenTwoNodes() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addArc(5, 7);
        Assertions.assertDoesNotThrow(() -> graph.deleteArc(5, 7));
    }

    @Test
    void deleteArc_wrongDirectionTest() {
        graph.addNode(5);
        graph.addNode(7);
        graph.addArc(5, 7);
        Assertions.assertThrows(ConnectionNotExistException.class, () -> graph.deleteArc(7, 5));
    }
}
