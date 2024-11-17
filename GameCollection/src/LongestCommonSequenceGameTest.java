import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LongestCommonSequenceGameTest {
	
	
    private LongestCommonSequenceGame game;

    @Before
    public void setUp() {
    	
        // Create the game instance within the Event Dispatch Thread (EDT) to ensure proper initialization
        SwingUtilities.invokeLater(() -> {
            game = new LongestCommonSequenceGame();
            
        });
    }

    @After
    public void dispose() {
    	
        // Dispose of the main menu after each test
        SwingUtilities.invokeLater(() -> {
            game.mainMenuFrame.dispose();
        });
    }
    

    @Test
    public void testMainMenuButtonActions() {
    	
        // Find and validate the main menu buttons
        JButton startButtonEx = findButtonUsingName("Start New Game");
        JButton highScoresButtonEx = findButtonUsingName("High Scores");
        JButton exitButtonEx = findButtonUsingName("Exit");

        assertNotNull(startButtonEx); // Check whether the button is exist or not
        assertNotNull(highScoresButtonEx);
        assertNotNull(exitButtonEx);

        // Test Start New Game button
        clickButton(startButtonEx);
        assertNotNull(game.playerDetailsFrame); // Ensure the working

        // Test High Scores button
        clickButton(highScoresButtonEx);
        assertNotNull(game.leaderboardFrame);

        // Test Exit button
        clickButton(exitButtonEx);
        assertFalse(game.mainMenuFrame.isVisible());
        
    }

    
    @Test
    public void testScore() {
    	
        // Simulate a game round
        SwingUtilities.invokeLater(() -> {
        	
            game.startGame();
            game.string1 = "abc";
            game.string2 = "ac";
            game.userInputField.setText("a");
            game.checkAnswer();
            
        });

        assertEquals("Score should be updated after a correct answer.", 1, game.score); //Check the score update or not

        SwingUtilities.invokeLater(() -> {
        	
            game.userInputField.setText("b");
            game.checkAnswer();
            
        });

        assertEquals("Score should be updated after a correct answer.", 2, game.score);
    }

    
    @Test
    public void testGeneratedRandomStrings() {
    	
        String randomString = game.generateRandomString();

        assertNotNull(randomString); // Check whether the generated string is not null
        assertEquals(10, randomString.length()); // Check whether the generated string has the expected length or not
        assertTrue(randomString.matches("[a-z]+")); // Check whether the generated string contains only lowercase or not
    }

    @Test
    public void testCalculateLongestCommonSequence() {
    	
        String str1 = "abcdefg";
        String str2 = "acdfg";

        String lcs = game.calculateLongestCommonSequence(str1, str2);

        assertEquals("acfg", lcs); // Check whether the calculated LCS is correct or not
    }
    
    
    
    
    

    @Test
    public void testStorePlayerData() {
    	
        // Simulate storing player 
        SwingUtilities.invokeLater(() -> {
            game.storePlayerData("TestPlayer", 5, "test1", "test2", "test_answer", "correct_answer");
        });
    }

    private JButton findButtonUsingName(String buttonName) {
    	
        // Method to find a button using its name within the main menu (Helper method)
        return (JButton) findComponentUsingName(game.mainMenuFrame, buttonName);
        
    }

    private void clickButton(final JButton button) {
    	
        // Method to simulate clicking a button (Helper method)
        SwingUtilities.invokeLater(() -> {
        	
            for (ActionListener listener : button.getActionListeners()) {
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                
            }
        });
    }

    private Component findComponentUsingName(Container container, String componentName) {
        
    	// Method to find a component by its name within a container (Helper method)
        for (Component component : container.getComponents()) {
        	
            if (component instanceof JButton) {
            	
                if (((JButton) component).getText().equals(componentName)) {
                	
                    return component;
                    
                }
            }
        }
        
        return null;
        
    }
}
