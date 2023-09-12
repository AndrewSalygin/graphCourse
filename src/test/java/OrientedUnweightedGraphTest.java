import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.andrewsalygin.graph.Graph;
import ru.andrewsalygin.graph.Node;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;
import ru.andrewsalygin.graph.OrientedWeightedGraph;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;
import ru.andrewsalygin.graph.utils.NodeNotExistException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}
