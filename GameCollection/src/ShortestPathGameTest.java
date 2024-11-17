import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

import javax.swing.JTextField;

public class ShortestPathGameTest {

    @Test
    public void testGetUserDistances() {
        ShortestPathGame game = new ShortestPathGame();
        
        // Set up necessary state
        game.numCities = 4;
        game.distanceFields = new JTextField[4];
        game.distanceFields[0] = new JTextField("10");
        game.distanceFields[1] = new JTextField("20");
        game.distanceFields[2] = new JTextField("30");
        game.distanceFields[3] = new JTextField("40");

        Map<String, Integer> userDistances = game.getUserDistances();

        assertNotNull(userDistances);
        assertEquals(3, userDistances.size());
        assertEquals(20, userDistances.get("City1"));
        assertEquals(30, userDistances.get("City2"));
        assertEquals(40, userDistances.get("City3"));
    }

    @Test
    public void testDijkstra() {
        ShortestPathGame game = new ShortestPathGame();
        
        // Set up necessary state
        game.numCities = 3;
        game.cities = new String[]{"City1", "City2", "City3"};
        int[][] graph = new int[][]{
            {0, 10, 20},
            {10, 0, 30},
            {20, 30, 0}
        };

        Map<String, Integer> distances = game.dijkstra(graph, 0);

        assertNotNull(distances);
        assertEquals(3, distances.size());
        assertEquals(0, distances.get("City1"));
        assertEquals(10, distances.get("City2"));
        assertEquals(20, distances.get("City3"));
    }

    @Test
    public void testBellmanFord() {
        ShortestPathGame game = new ShortestPathGame();
        
        // Set up necessary state
        game.numCities = 3;
        game.cities = new String[]{"City1", "City2", "City3"};
        int[][] graph = new int[][]{
            {0, 10, 20},
            {10, 0, 30},
            {20, 30, 0}
        };

        Map<String, Integer> distances = game.bellmanFord(graph, 0);

        assertNotNull(distances);
        assertEquals(3, distances.size());
        assertEquals(0, distances.get("City1"));
        assertEquals(10, distances.get("City2"));
        assertEquals(20, distances.get("City3"));
    }

    @Test
    public void testGetVisitedNodesInOrder() {
        ShortestPathGame game = new ShortestPathGame();
        
        // Set up necessary state
        game.selectedCityLabel.setText("Selected City: City2");
        Map<String, Integer> distances = new HashMap<>();
        distances.put("City1", 10);
        distances.put("City2", 0);
        distances.put("City3", 20);

        List<String> visitedNodes = game.getVisitedNodesInOrder(distances);

        assertNotNull(visitedNodes);
        assertEquals(3, visitedNodes.size());
        assertEquals("City2", visitedNodes.get(0));
        assertEquals("City1", visitedNodes.get(1));
        assertEquals("City3", visitedNodes.get(2));
    }
}