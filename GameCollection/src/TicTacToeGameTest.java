import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

public class TicTacToeGameTest {

    private TicTacToeGame game;

    @BeforeEach
    public void setUp() {
        game = new TicTacToeGame();
    }

    // Helper method to set the board state using reflection
    private void setBoardState(TicTacToeGame game, String[][] boardState) {
        try {
            Field boardField = TicTacToeGame.class.getDeclaredField("board");
            boardField.setAccessible(true);
            boardField.set(game, boardState);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheckWin_RowWin() {
        String[][] boardState = {
            {"X", "X", "X"},
            {"O", "O", ""},
            {"", "", ""}
        };
        setBoardState(game, boardState);

        assertTrue(game.checkWin("X"));
        assertFalse(game.checkWin("O"));
    }

    @Test
    public void testCheckWin_ColumnWin() {
        String[][] boardState = {
            {"X", "O", "X"},
            {"X", "O", "O"},
            {"X", "", ""}
        };
        setBoardState(game, boardState);

        assertTrue(game.checkWin("X"));
        assertFalse(game.checkWin("O"));
    }

    @Test
    public void testCheckWin_DiagonalWin() {
        String[][] boardState = {
            {"X", "O", "X"},
            {"O", "X", "O"},
            {"O", "", "X"}
        };
        setBoardState(game, boardState);

        assertTrue(game.checkWin("X"));
        assertFalse(game.checkWin("O"));
    }

    @Test
    public void testCheckWin_NoWin() {
        String[][] boardState = {
            {"X", "O", "X"},
            {"O", "X", "O"},
            {"O", "X", "O"}
        };
        setBoardState(game, boardState);

        assertFalse(game.checkWin("X"));
        assertFalse(game.checkWin("O"));
    }

}
