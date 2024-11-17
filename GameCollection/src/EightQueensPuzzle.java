import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EightQueensPuzzle extends JFrame {
    private List<List<Integer>> solutions;
    private List<Integer> userAnswer;
    private JPanel chessBoard;
    private JButton checkButton;
    private boolean allSolutionsRecognized;
    private Set<String> identifiedSolutions;
    private String playerName;

    
    public EightQueensPuzzle(String playerName) {
        // Initialize the game with the player's name
        this.playerName = playerName;

        solutions = solveEightQueens();
        userAnswer = new ArrayList<>();
        allSolutionsRecognized = false;
        identifiedSolutions = new HashSet<>();
        retrieveIdentifiedSolutions(); // Retrieve identified solutions from the database
        
        checkGameCompletion();
    }

    
    public void MainMenuUI() {
        // Main menu frame
        JFrame mainMenuFrame = new JFrame("Welcome to the Knight's tour game - Main Menu");
        mainMenuFrame.setSize(500, 500);
        mainMenuFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainMenuFrame.setLocationRelativeTo(null);

        // JPanel to hold main menu items
        JPanel mainMenuPanel = new JPanel(new GridLayout(4, 1)); // Increase the number of rows to accommodate the Back button
        mainMenuPanel.setBackground(new Color(254,224,241));
        Border border = BorderFactory.createLineBorder(new Color(254, 224, 241), 3);
        mainMenuPanel.setBorder(border);

        // Create main menu buttons
        JButton startNewGameButton = new JButton("Start New Game");
        JButton highScoresButton = new JButton("Leaderboard");
        JButton mainMenuExitButton = new JButton("Exit");
        mainMenuExitButton.setForeground(Color.RED);
        JButton backToGameMenuButton = new JButton("Back to Game Menu");
        backToGameMenuButton.setForeground(Color.BLUE);

        Font buttonFont = new Font("Hiragino Mincho Pro", Font.BOLD, 20); // Set font for the buttons
        mainMenuExitButton.setFont(buttonFont);
        startNewGameButton.setFont(buttonFont);
        highScoresButton.setFont(buttonFont);
        backToGameMenuButton.setFont(buttonFont); // Set font for the Back button

        // Button configuration using action listeners
        startNewGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent btnClick) {
                mainMenuFrame.setVisible(false);
                playerDetailsUI();
            }
        });

        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent btnClick) {
                mainMenuFrame.setVisible(false);
                leaderboardUI();
            }
        });

        mainMenuExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent btnClick) {
                System.exit(0);
            }
        });

        backToGameMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent btnClick) {
                mainMenuFrame.setVisible(false);
                dispose();
                GameMenu gameMenu = new GameMenu();
                gameMenu.setVisible(true);
            }
        });

        // Add buttons to the JPanel
        mainMenuPanel.add(startNewGameButton);
        mainMenuPanel.add(highScoresButton);
        mainMenuPanel.add(backToGameMenuButton);
        mainMenuPanel.add(mainMenuExitButton);
        

        // Set the JPanel for JFrame
        mainMenuFrame.setContentPane(mainMenuPanel);
        mainMenuFrame.setVisible(true);
    }


    
    private void playerDetailsUI() {
        // Player details frame
        JFrame playerDetailsFrame = new JFrame("Player Details");
        playerDetailsFrame.setSize(410, 160);
        playerDetailsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playerDetailsFrame.setLocationRelativeTo(null);
        UIManager.put("OptionPane.background", new Color(254,224,241));

        JTextField playerNameField = new JTextField(20);

        JPanel playerDetailsPanel = new JPanel(new BorderLayout()); // Use BorderLayout
        UIManager.put("Panel.background", new Color(254,224,241));

        // Name label
        Font hiraginoFont = new Font("Hiragino Mincho Pro", Font.PLAIN, 14);
        Font boldFont = hiraginoFont.deriveFont(Font.BOLD, 14);
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel(" Enter your name :    ");
        nameLabel.setPreferredSize(new Dimension(150, 30));
        nameLabel.setFont(hiraginoFont);
        namePanel.add(nameLabel);
        namePanel.add(playerNameField);
        playerDetailsPanel.add(namePanel, BorderLayout.NORTH);

        Dimension buttonSize = new Dimension(80, 35); // preferred size for the next button using a dimension object

        // Back button configuration
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.setFont(boldFont);
        backButton.setPreferredSize(buttonSize);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerDetailsFrame.setVisible(false);
                MainMenuUI();
            }
        });

        // Next button configuration
        JButton nextButton = new JButton("Next");
        nextButton.setPreferredSize(buttonSize);
        nextButton.setFont(boldFont);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText().trim(); // Get player name
                if (playerName.isEmpty()) {
                    playerName = "Anonymous Player";
                }
                playerDetailsFrame.dispose(); // Dispose of the player details UI

                // Create an instance of EightQueensPuzzle with the player's name
                EightQueensPuzzle game = new EightQueensPuzzle(playerName);
                game.initializeUI(); // Display the game board
            }
        });


        buttonPanel.add(backButton); // Add the back button
        buttonPanel.add(nextButton); // Add the next button
        playerDetailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        playerDetailsFrame.add(playerDetailsPanel);
        playerDetailsFrame.setVisible(true);
    }

    
    
    
    
    public void leaderboardUI() {
        // leaderboard frame
        JFrame leaderboardFrame = new JFrame("Leaderboard - The Top Players");
        leaderboardFrame.setSize(500, 400); // Adjust the frame size as needed
        leaderboardFrame.getContentPane().setBackground(new Color(254, 224, 241)); // Set background color
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        leaderboardFrame.setLocationRelativeTo(null);
        
        
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leaderboardFrame.dispose();
                MainMenuUI();
            }
        });

        // JTextArea to display leaderboard data
        JTextArea leaderboardTextArea = new JTextArea();
        leaderboardTextArea.setFont(new Font("Hiragino Mincho Pro", Font.PLAIN, 15));
        leaderboardTextArea.setEditable(false); // Make it read-only
        leaderboardTextArea.setLineWrap(true);
        leaderboardTextArea.setWrapStyleWord(true);
        leaderboardTextArea.setBackground(new Color(254, 224, 241)); // Set background color

        JPanel buttonPanel = new JPanel(); // Create a panel for the back button
        buttonPanel.setBackground(new Color(233, 243, 233));
        buttonPanel.add(backButton);
        
        leaderboardFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        // Database connection to retrieve data for the leaderboard
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Load/Register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection details
            String databaseUrl = "jdbc:mysql://localhost:8889/EightQueensPuzzle";
            String username = "root";
            String password = "root";

            // Create a database connection
            connection = DriverManager.getConnection(databaseUrl, username, password);

            // SQL query to retrieve player names from the Winners table
            String selectQuery = "SELECT PlayerName FROM Winners";

            // Create a PreparedStatement
            preparedStatement = connection.prepareStatement(selectQuery);

            // Execute the query to retrieve data
            ResultSet resultSet = preparedStatement.executeQuery();

            // Populate the leaderboardTextArea with retrieved data
            StringBuilder leaderboardText = new StringBuilder("Winners : \n\n");

            while (resultSet.next()) {
                String playerName = resultSet.getString("PlayerName");
                leaderboardText.append(playerName).append("\n");
            }

            leaderboardTextArea.setText(leaderboardText.toString());

            // Close the ResultSet
            resultSet.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Handle the ClassNotFoundException (e.g., show an error message)
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception (e.g., show an error message)
        } finally {
            try {
                // Close the connection and statement in a finally block
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle any SQLException that may occur during closing
            }
        }

        // Create a scroll pane for the leaderboard text area
        JScrollPane scrollPane = new JScrollPane(leaderboardTextArea);

        // Add the scroll pane to the leaderboard frame
        leaderboardFrame.add(scrollPane);
        leaderboardFrame.setVisible(true);
    }




    public List<List<Integer>> solveEightQueens() {
        List<List<Integer>> solutions = new ArrayList<>();
        int[] queens = new int[8]; // queens[i] represents the column position of the queen in row i
        solve(queens, 0, solutions);
        return solutions;
    }

    public void solve(int[] queens, int row, List<List<Integer>> solutions) {
        if (row == 8) {
            // Found a valid solution, add it to the list
            List<Integer> solution = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                solution.add(queens[i]);
            }
            solutions.add(solution);
        } else {
            for (int col = 0; col < 8; col++) {
                if (isValid(queens, row, col)) {
                    queens[row] = col; // Place queen in this column
                    solve(queens, row + 1, solutions);
                }
            }
        }
    }

    public boolean isValid(int[] queens, int row, int col) {
        // Check if a queen can be placed in the given row and column
        for (int i = 0; i < row; i++) {
            if (queens[i] == col || queens[i] - i == col - row || queens[i] + i == col + row) {
                return false;
            }
        }
        return true;
    }
    
    boolean isValidSolution(List<Integer> solution) {
        if (solution.size() != 8) {
            return false; // A valid solution should have exactly 8 elements
        }

        // Check if there are no duplicate values in the solution
        Set<Integer> uniqueValues = new HashSet<>(solution);
        return uniqueValues.size() == 8;
    }


    // Retrieve identified solutions from the database
    public void retrieveIdentifiedSolutions() {
        try {
        	
        	String databaseUrl = "jdbc:mysql://localhost:8889/EightQueensPuzzle";
            String username = "root";
            String password = "root"; 

        	
            Connection connection = DriverManager.getConnection(databaseUrl, username, password);
            String selectQuery = "SELECT Solution FROM IdentifiedSolutions";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String solutionStr = resultSet.getString("Solution");
                identifiedSolutions.add(solutionStr);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeUI() {
        setTitle("Eight Queens Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel leftSeparator = new JPanel();
        leftSeparator.setPreferredSize(new Dimension(10, 1));
        leftSeparator.setOpaque(false);

        JPanel rightSeparator = new JPanel();
        rightSeparator.setPreferredSize(new Dimension(10, 1));
        rightSeparator.setOpaque(false);

        JPanel topSeparator = new JPanel();
        topSeparator.setPreferredSize(new Dimension(1, 10));
        topSeparator.setOpaque(false);

        JPanel bottomSeparator = new JPanel();
        bottomSeparator.setPreferredSize(new Dimension(1, 10));
        bottomSeparator.setOpaque(false);

        JPanel chessboardContainer = new JPanel(new BorderLayout());
        chessBoard = new JPanel(new GridLayout(8, 8));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton cell = new JButton("");
                cell.setPreferredSize(new Dimension(100, 100));
                int finalCol = col; // Required for ActionListener
                cell.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        userAnswer.add(finalCol);
                        cell.setText("Queen");
                        cell.setEnabled(false);
                    }
                });

                // Set cell background color to create a chessboard pattern
                if ((row + col) % 2 == 0) {
                    cell.setBackground(new Color(254,224,241)); // Light color for even cells
                } else {
                    cell.setBackground(new Color(118, 150, 86)); // Dark color for odd cells
                }
                cell.setOpaque(true);

                chessBoard.add(cell);
            }
        }

        chessboardContainer.add(chessBoard, BorderLayout.CENTER);
        chessboardContainer.add(topSeparator, BorderLayout.NORTH);
        chessboardContainer.add(bottomSeparator, BorderLayout.SOUTH);

        mainPanel.add(leftSeparator, BorderLayout.WEST);
        mainPanel.add(chessboardContainer, BorderLayout.CENTER);
        mainPanel.add(rightSeparator, BorderLayout.EAST);

        // Define and create the controlPanel
        JPanel controlPanel = new JPanel(new GridLayout(1, 4)); // Grid layout for 4 buttons

        // Add the check button to the controlPanel
        checkButton = new JButton("Check Answer");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkUserAnswerFinal();
            }
        });
        controlPanel.add(checkButton);

        
        // Add the restart button to the controlPanel
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	restartGame();
            }
        });
        controlPanel.add(restartButton);

        // Add the back button to the controlPanel
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	dispose();
            	playerDetailsUI();
            }
        });
        controlPanel.add(backButton);

        // Add the exit button to the controlPanel
        JButton exitButton = new JButton("Exit");
        exitButton.setForeground(Color.RED);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 System.exit(0);

            }
        });
        controlPanel.add(exitButton);
        
        

        // Increase the height of the text buttons
        int buttonHeight = 40; // Adjust the height as needed
        int buttonWidth = 200; // Adjust the width as needed
        checkButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        restartButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        backButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        exitButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        // Add the control panel to the SOUTH position of the main container
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }




    public void checkGameCompletion() {
        if (isGameCompleted()) {
            allSolutionsRecognized = true;
            int option = JOptionPane.showOptionDialog(this,
                    "You have already completed the game.",
                    "Game Completed",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Exit", "Reset Game"},
                    "Exit");

            if (option == JOptionPane.YES_OPTION) {
                System.exit(0); // Exit the game
            } else if (option == JOptionPane.NO_OPTION) {
                resetGame(); // Reset the game
            }
        }
    }

    // Check if all solutions are identified
    public boolean isGameCompleted() {
        return identifiedSolutions.size() == solutions.size();
    }
    


    
private void storePlayerData(String playerName) {
    	
        try {
        	
            String databaseUrl = "jdbc:mysql://localhost:8889/EightQueensPuzzle";
            String username = "root";
            String password = "root"; 

            
            Connection connection = DriverManager.getConnection(databaseUrl, username, password);  // Database connection

            
            String insertQuery = "INSERT INTO Winners (PlayerName) VALUES (?)"; //SQL query to insert player data
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);

            
            // Parameter values for the query
            preparedStatement.setString(1, playerName); 
            

            int rowsInserted = preparedStatement.executeUpdate(); // Execute the query to insert the player's data into database

            
            if (rowsInserted > 0) {
            	
                JOptionPane.showMessageDialog(this, "Record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } 
            
            else {
            	
                JOptionPane.showMessageDialog(this, "Failed to add record to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            
            }

            
            preparedStatement.close(); // Close the prepared statement
            connection.close(); // Close the database connection
        } 
        
        catch (SQLException e) {
        	
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving player data to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        
        }
    }
    
    
    
    public void checkUserAnswerFinal() {
        if (!allSolutionsRecognized) {
            if (identifiedSolutions.contains(userAnswer.toString())) {
                int option = JOptionPane.showOptionDialog(chessBoard,
                        "This solution is already identified.",
                        "Solution Already Identified",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Restart", "Exit"},
                        "Exit");

                if (option == JOptionPane.YES_OPTION) {
                    // Restart the game
                    restartGame();
                } else if (option == JOptionPane.NO_OPTION) {
                    System.exit(0); // Exit the game
                }
            } else {
                boolean isCorrect = false;
                for (List<Integer> solution : solutions) {
                    if (userAnswer.equals(solution)) {
                        isCorrect = true;
                        break;
                    }
                }

                if (isCorrect) {
                    identifiedSolutions.add(userAnswer.toString()); // Add the user's answer to the identified solutions
                    saveUserAnswerToDatabase(userAnswer); // Save the user's answer to the database
                    int option = JOptionPane.showOptionDialog(chessBoard,"Congratulations! Your answer is correct.","Correct Answer",
                            JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Restart", "Exit"},"Exit");
                   
                    storePlayerData(playerName);

                    if (option == JOptionPane.YES_OPTION) {
                        // Restart the game
                        restartGame();
                    } else if (option == JOptionPane.NO_OPTION) {
                        System.exit(0); // Exit the game
                    }
                } else {
                    int option = JOptionPane.showOptionDialog(chessBoard,
                            "Sorry, your answer is incorrect.","Incorrect Answer",JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Restart", "Exit"},"Exit");

                    if (option == JOptionPane.YES_OPTION) {
                        // Restart the game
                        restartGame();
                    } else if (option == JOptionPane.NO_OPTION) {
                        System.exit(0); // Exit the game
                    }
                }
            }
        } else {
            int option = JOptionPane.showOptionDialog(chessBoard,
                    "All solutions have been recognized. Try again later.","Game Completed",
                    JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Reset", "Exit"},"Exit");

            if (option == JOptionPane.YES_OPTION) {
                // Restart the game
            	resetGame();
            } else if (option == JOptionPane.NO_OPTION) {
                System.exit(0); // Exit the game
            }
        }
    }



    
    // Save the user's answer to the database
    public void saveUserAnswerToDatabase(List<Integer> userAnswer) {
        try {
        	
        	String databaseUrl = "jdbc:mysql://localhost:8889/EightQueensPuzzle";
            String username = "root";
            String password = "root"; 

        	
            Connection connection = DriverManager.getConnection(databaseUrl, username, password);
            String insertQuery = "INSERT INTO IdentifiedSolutions (Solution) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, userAnswer.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void resetGame() {
        try {
        	
        	String databaseUrl = "jdbc:mysql://localhost:8889/EightQueensPuzzle";
            String username = "root";
            String password = "root"; 

        	
            Connection connection = DriverManager.getConnection(databaseUrl, username, password);
            String deleteQuery = "DELETE * FROM IdentifiedSolutions";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            identifiedSolutions.clear(); // Clear the identified solutions set
            allSolutionsRecognized = false;

            String[] options = {"Exit", "Play Game"};
            int choice = JOptionPane.showOptionDialog(chessBoard, "Game reset. You can start again.", "Game Reset", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);

            if (choice == 0) {
                System.exit(0); // Exit the game
            } else if (choice == 1) {
                // Start a new game
                dispose(); // Close the current game window
                new EightQueensPuzzle(playerName); // Create a new game instance
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Set<String> getIdentifiedSolutions() {
        return identifiedSolutions;
    }

    public void setSolutions(List<List<Integer>> solutions) {
        this.solutions = solutions;
    }
    
    public List<List<Integer>> getSolutions() {
        return solutions;
    }
    
    public void restartGame() {
        // Clear the user's answer and re-enable all buttons on the chessboard
        userAnswer.clear();
        Component[] components = chessBoard.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setText("");
                button.setEnabled(true);
            }
        }
    }

 // Add these getter methods to the EightQueensPuzzle class
    public List<Integer> getUserAnswer() {
        return userAnswer;
    }

    public JPanel getChessBoard() {
        return chessBoard;
    }

}