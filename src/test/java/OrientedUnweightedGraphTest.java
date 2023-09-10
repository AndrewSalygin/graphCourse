import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.andrewsalygin.graph.Node;
import ru.andrewsalygin.graph.OrientedUnweightedGraph;
import ru.andrewsalygin.graph.OrientedWeightedGraph;
import ru.andrewsalygin.graph.utils.NodeAlreadyExistException;

/**
 * @author Andrew Salygin
 */
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
}
