import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameMenu extends JFrame {

    private KnightsTourGame knightsTourGame;
    private EightQueensPuzzle eightQueensPuzzle;
    private TicTacToeGame ticTacToeGame;
    private LongestCommonSequenceGame longestCommonSequenceGame;
    private ShortestPathGame shortestPathGame;
    
    public GameMenu() {
        setTitle("Game Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 700);

        // Create a separator panel with reduced height
        JPanel separatorPanelTop = new JPanel();
        separatorPanelTop.setPreferredSize(new Dimension(1, 3)); // Adjust the height as needed
        separatorPanelTop.setBackground(new Color(25, 25, 112)); // Set the background color

        JPanel separatorPanelBottom = new JPanel();
        separatorPanelBottom.setPreferredSize(new Dimension(1, 3)); // Adjust the height as needed
        separatorPanelBottom.setBackground(new Color(25, 25, 112)); // Set the background color
        
        JPanel separatorPanelLeft = new JPanel();
        separatorPanelLeft.setPreferredSize(new Dimension(3, 1)); // Adjust the height as needed
        separatorPanelLeft.setBackground(new Color(25, 25, 112)); // Set the background color

        JPanel separatorPanelRight = new JPanel();
        separatorPanelRight.setPreferredSize(new Dimension(3, 1)); // Adjust the height as needed
        separatorPanelRight.setBackground(new Color(25, 25, 112)); // Set the background color

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 0, 0));
        buttonPanel.setBackground(new Color(25, 25, 112));

        JButton knightsTourButton = createGameButton("Knight's Tour Game");
        JButton queensPuzzleButton = createGameButton("Eight Queens Puzzle Game");
        JButton ticTacToeButton = createGameButton("Tic Tac Toe Game");
        JButton longestSequenceButton = createGameButton("Identify Longest Common Sequence Game");
        JButton shortestPathButton = createGameButton("Shortest Path Calculation Game");
        JButton exitButton = createGameButton("Exit");
        exitButton.setForeground(Color.RED);

        knightsTourButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (knightsTourGame == null) {
                    dispose();
                    knightsTourGame = new KnightsTourGame();
                }
            }
        });
        queensPuzzleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (eightQueensPuzzle == null) {
                	dispose();
                    eightQueensPuzzle = new EightQueensPuzzle(null);
                    eightQueensPuzzle.MainMenuUI();
                }
               // eightQueensPuzzle.setVisible(true); // Make sure to set the frame as visible
            }
        });

        
        ticTacToeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ticTacToeGame == null) {
                    dispose();
                    ticTacToeGame = new TicTacToeGame();
                }
            }
        });
        
        longestSequenceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (longestCommonSequenceGame == null) {
                    dispose();
                    longestCommonSequenceGame = new LongestCommonSequenceGame();
                }
            }
        });
        
        shortestPathButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
                if (shortestPathGame == null) {
                    dispose();
                    shortestPathGame = new ShortestPathGame();
               }
          }
       });
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.exit(0);
                
            }
        });

        buttonPanel.add(knightsTourButton);
        buttonPanel.add(queensPuzzleButton);
        buttonPanel.add(ticTacToeButton);
        buttonPanel.add(longestSequenceButton);
        buttonPanel.add(shortestPathButton);
        buttonPanel.add(exitButton);

        // Use BorderLayout to arrange the panels
        setLayout(new BorderLayout());
        add(separatorPanelTop, BorderLayout.NORTH); // Add separator panel at the top
        add(separatorPanelBottom, BorderLayout.SOUTH);
        add(separatorPanelLeft, BorderLayout.WEST);
        add(separatorPanelRight, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.CENTER); // Add button panel in the center
    }

    private JButton createGameButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 20));
        button.setBackground(new Color(245, 245, 220)); // Set the button background color to beige
       // button.setForeground(Color.BLACK); // Set the text color to black
        button.setPreferredSize(new Dimension(200, 60)); // Increase the button height
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMenu gameMenu = new GameMenu();
            gameMenu.setVisible(true);
        });
    }
}
