import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EightQueensPuzzleTest {

    private EightQueensPuzzle game;

    @BeforeEach
    public void setUp() {
        game = new EightQueensPuzzle("TestPlayer");
    }

    @Test
    public void testIsGameCompleted() {
        // Initially, no solutions are identified, so the game should not be completed
        assertFalse(game.isGameCompleted());

        // Add a solution to the identified solutions
        game.getIdentifiedSolutions().add("[0, 1, 2, 3, 4, 5, 6, 7]");
        assertTrue(game.isGameCompleted());
    }

    @Test
    public void testIsValid() {
        int[] queens = {0, 2, 4, 1, 7, 5, 3, 6};

        // Test an invalid placement in the same column
        assertFalse(game.isValid(queens, 7, 4));

        // Test an invalid placement in the same diagonal
        assertFalse(game.isValid(queens, 7, 1));

        // Test a valid placement
        assertTrue(game.isValid(queens, 7, 2));
    }

    @Test
    public void testSolveEightQueens() {
        game.setSolutions(game.solveEightQueens());

        // Ensure there are 92 solutions for the Eight Queens problem
        assertEquals(92, game.getSolutions().size());

        // Check if the first solution is valid
        assertTrue(game.isValidSolution(game.getSolutions().get(0)));
    }

    private boolean isValidSolution(List<Integer> solution) {
        // Helper method to check if a solution is valid (no two queens attacking each other)
        int n = solution.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (solution.get(i).equals(solution.get(j)) || Math.abs(solution.get(i) - solution.get(j)) == Math.abs(i - j)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Test
    public void testRestartGame() {
        // Simulate userAnswer and button states
        game.getUserAnswer().add(0);
        game.getUserAnswer().add(2);

        // Set buttons as disabled
        List<List<Integer>> solutions = game.solveEightQueens();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                game.getChessBoard().getComponent(i * 8 + j).setEnabled(false);
            }
        }

        // Call restartGame method
        game.restartGame();

        // Check if userAnswer is cleared
        assertTrue(game.getUserAnswer().isEmpty());

        // Check if buttons are enabled again
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertTrue(game.getChessBoard().getComponent(i * 8 + j).isEnabled());
            }
        }
    }

}
