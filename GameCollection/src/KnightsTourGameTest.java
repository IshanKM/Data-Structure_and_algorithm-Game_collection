import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KnightsTourGameTest {

    private KnightsTourGame game;

    @BeforeEach
    public void setUp() {
        // New KnightsTourGame instance before each test
        game = new KnightsTourGame();
    }

    @Test
    public void testIsValidMove() {
        /* Test valid moves
         * The Knight can move 2 cells in one direction and 1 cell in the other direction
         *
         * Below moves are valid, so the function should return true */
        assertTrue(game.isValidMove(2, 1)); 
        assertTrue(game.isValidMove(1, 2)); 

        // Test invalid moves
        assertFalse(game.isValidMove(0, 0)); // Invalid move - no movement
        assertFalse(game.isValidMove(3, 3)); // Invalid move - not an L-shape
        assertFalse(game.isValidMove(2, 2)); // Invalid move - not an L-shape
    }

    @Test
    public void testHandleMoveValid() {
        // Simulate a valid move
        game.initializeBoard();

        game.handleMoves(2, 1, "Player1"); // Attempt to move the Knight to position (2, 1) by Player1

        // Assert that the move was successful
        assertEquals(1, game.getMoveCount());
        assertEquals(1, game.getBoard()[2][1]);
    }

    @Test
    public void testHandleMoveInvalid() {
        // Simulate an invalid move
        game.initializeBoard();

        game.handleMoves(4, 4, "Player1"); // Attempt to move the Knight to an invalid position (4, 4) by Player1

        // Assert that the move was not successful
        assertEquals(0, game.getMoveCount());
        assertEquals(0, game.getBoard()[4][4]);
    }

    @Test
    public void testInitializeBoard() {
        // Test if the board is correctly initialized 
        game.initializeBoard();
        int[][] board = game.getBoard();

        // Ensure each cell on the board is initialized to 0
        for (int i = 0; i < game.boardSize; i++) {
            for (int j = 0; j < game.boardSize; j++) {
                assertEquals(0, board[i][j]); // Expecting each cell to be initialized to 0
            }
        }
    }

    @Test
    public void testStartGame() {
        // Test starting a new game
        game.startGame(); // Start a new game

        // After starting the game, there should be 0 moves, and 0 invalid moves
        assertEquals(0, game.getMoveCount());
        assertEquals(0, game.getInvalidMoveCount());
    }

    @Test
    public void testRestartGame() {
        // Test restarting the game
        game.initializeBoard();
        game.handleMoves(2, 1, "Player1"); // Make a move to (2, 1) by Player1
        game.restartGame();

        // After restarting the game, the move count and invalid move count should be reset to 0
        assertEquals(0, game.getMoveCount()); // Expect move count to be reset
        assertEquals(0, game.getInvalidMoveCount()); // Expect invalid move count to be reset
    }
}
