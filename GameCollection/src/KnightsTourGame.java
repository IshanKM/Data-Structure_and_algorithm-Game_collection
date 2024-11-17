import javax.swing.*;
import javax.swing.border.Border; 
import com.mysql.cj.xdevapi.Statement; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;



public class KnightsTourGame extends JFrame {
	
    final int boardSize = 8; 
    private int[][] board; // 2D array to represent the game board
    private int knightMoveCount; 
    private JButton[][] cellButtons; // 2D array of buttons to represent board cells

    private int[] knightMovesX = {2, 1, -1, -2, -2, -1, 1, 2}; // Possible moves for the knight - x axis
    private int[] knightMovesY = {1, 2, 2, 1, -1, -2, -2, -1}; // Possible moves for the knight - y axis

    private JButton startButton; 
    private JButton restartButton; 
    
    private boolean gameStarted; // Flag to track if the game is started or not

    private JPanel mainMenuPanel; 
    private JTextField playerNameField; 

    private JButton playerDetailsNextButton; 
    private JButton playerDetailsBackButton; 
    private JFrame mainMenuFrame;
    private JLabel nameLabel; 
    private Font boldFont; 
    private JFrame playerDetailsFrame; 
 

    private JButton backButton; 
    private JButton exitButton; 
    
    private int invalidknightMoveCount = 0; 
    private JLabel invalidMoveLabel;
    private JLabel movesLabel; 

    public KnightsTourGame() {
    	
        // Constructor
        MainMenuUI(); // Create the main menu panel
       // mainMenuFrame.setVisible(true); // Display the main menu
    }

    private void MainMenuUI() {
        // Main menu frame
        JFrame mainMenuFrame = new JFrame("Welcome to the Knight's tour game - Main Menu");
        mainMenuFrame.setSize(500, 500);
        mainMenuFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainMenuFrame.setLocationRelativeTo(null);

        // JPanel to hold main menu items
        JPanel mainMenuPanel = new JPanel(new GridLayout(4, 1)); // Increase the number of rows to accommodate the Back button
        mainMenuPanel.setBackground(new Color(245,245,220));
        Border border = BorderFactory.createLineBorder(new Color(245, 245, 220), 3);
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
                PlayerDetailsInterface();
            }
        });

        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent btnClick) {
                mainMenuFrame.setVisible(false);
                LeaderboardUI();
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
    
    
    private void PlayerDetailsInterface() {
    	
        //Player details frame
        playerDetailsFrame = new JFrame("Player Details");
        playerDetailsFrame.setSize(385, 160);
        playerDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        playerDetailsFrame.setLocationRelativeTo(null);
        
        
        // Update the UI appearance
        UIManager.put("OptionPane.background", new Color(245, 245, 220));
        UIManager.put("Panel.background", new Color(245, 245, 220));

        
        // Create a label for player name input
        nameLabel = new JLabel("Enter your name :  ");
        nameLabel.setPreferredSize(new Dimension(120, 30));
        
        
        // Player name input field
        playerNameField = new JTextField(20);
        

        // Create and configure the "Next" and "Back" buttons using action listener for the "Next" button
        playerDetailsNextButton = new JButton("Next");
        playerDetailsBackButton = new JButton("Back");

        Dimension buttonSize = new Dimension(80, 35);
        playerDetailsNextButton.setPreferredSize(buttonSize);
        playerDetailsBackButton.setPreferredSize(buttonSize);
   
        playerDetailsNextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText().trim();

                
                if (playerName.isEmpty()) {
                    playerName = "Anonymous Player"; // Set a default name if the input is empty
                }

                playerDetailsFrame.dispose();
                initializeKnightTourUI(playerName); 
            }
        });
        

        playerDetailsBackButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                
                playerDetailsFrame.dispose();
                MainMenuUI(); // Close the player details frame and show the main menu
            }
        });
        
        
        // Create panels for player details input and buttons
        JPanel playerDetailsPanel = new JPanel(new BorderLayout());
        JPanel namePanel = new JPanel();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        
        // Add components to the panels
        namePanel.add(nameLabel);
        namePanel.add(playerNameField);
        buttonPanel.add(playerDetailsBackButton);
        buttonPanel.add(playerDetailsNextButton);

        
        // Add panels to the player details frame
        playerDetailsPanel.add(namePanel, BorderLayout.NORTH);
        playerDetailsPanel.add(buttonPanel, BorderLayout.SOUTH);
        playerDetailsFrame.add(playerDetailsPanel);
        
        
        // Make the player details frame visible
        playerDetailsFrame.setVisible(true);
    }

      
    private void initializeKnightTourUI(String playerName) {
	  	
    	setTitle("Knight's Tour Game!!");
        setSize(850, 850); 
        board = new int[boardSize][boardSize]; 
        cellButtons = new JButton[boardSize][boardSize]; 
       
        
        initializeBoard(); // Initialization of the chessboard
        setupUI(playerName); // Set up UI

        
        // Enable and disable of the "Start" and "restart" buttons
        startButton.setEnabled(true);
        restartButton.setEnabled(false);

    
        updateUI(); // Update the user interface

   
        setVisible(true);  // Make the player details frame visible
    }
    



    public void initializeBoard() {
    	
        // Initialize the chessboard with zeros (empty)
        for (int i = 0; i < boardSize; i++) {
        	
            for (int j = 0; j < boardSize; j++) {
            	
                board[i][j] = 0;
                
            }
        }
    }

    private void setupUI(String playerName) {

    	//info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        invalidMoveLabel = new JLabel(" Invalid Moves: 0");
        invalidMoveLabel.setFont(new Font("Hiragino Mincho Pro", Font.PLAIN, 16));
        invalidMoveLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        infoPanel.add(invalidMoveLabel); // Add invalidMoveLabel to the infoPanel
   
        movesLabel = new JLabel("Move: " );
        movesLabel.setFont(new Font("Hiragino Mincho Pro", Font.PLAIN, 16));
        Border movesLabelBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        movesLabel.setBorder(movesLabelBorder);
        infoPanel.add(movesLabel); // Add movesLabel to the infoPanel
        
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoPanel, BorderLayout.NORTH);

        
        //Separator between info panel and matrix
        JPanel separatorPanel = new JPanel();
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setPreferredSize(new Dimension(getWidth(), 2));
        separatorPanel.add(separator);

        
        // control Panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 4));
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	
                if (!gameStarted) {
                	
                    startGame();
                }
            }
            
        });
        
        restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	
                restartGame();
                
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	
                dispose();
                PlayerDetailsInterface();
                
            }
        });

        exitButton = new JButton("Exit");
        exitButton.setForeground(Color.RED);
        exitButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	
                System.exit(0);
                
            }
        });
        
   
        int buttonHeight = 40;
        int buttonWidth = 80;
        startButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        restartButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        backButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        exitButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        controlPanel.add(startButton);
        controlPanel.add(restartButton);
        controlPanel.add(backButton);
        controlPanel.add(exitButton);

        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(controlPanel, BorderLayout.SOUTH);

        
        //Board pane;
        JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        Border boardPanelBorder = BorderFactory.createLineBorder(Color.BLACK, 1); // Add a border
        boardPanel.setBorder(boardPanelBorder); // Set the border for boardPanel
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        
        for (int i = 0; i < boardSize; i++) { //Board
        	
            for (int j = 0; j < boardSize; j++) {
            	
                final int x = i, y = j;
                cellButtons[i][j] = new JButton();
                cellButtons[i][j].setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                cellButtons[i][j].setFont(new Font("Hiragino Mincho Pro", Font.PLAIN, 20));
                
                if ((i + j) % 2 == 0) {
                	
                    cellButtons[i][j].setBackground(new Color(211, 211, 211));
                    
                }
                
                else {
                	
                    cellButtons[i][j].setBackground(new Color(2, 138, 15));
                    
                }
                
                cellButtons[i][j].setOpaque(true);
                
                cellButtons[i][j].addActionListener(new ActionListener() {
                	
                    public void actionPerformed(ActionEvent e) {
                    	
                        if (gameStarted) {
                            int invalidKnightMoveCountBeforeMove = invalidknightMoveCount; // Store the count before the move
                            handleMoves(x, y, playerName);
                            movesLabel.setText("Move: " + knightMoveCount);

                            // Check whether the invalid move count has changed
                            if (invalidknightMoveCount != invalidKnightMoveCountBeforeMove) {
                            	
                            	invalidMoveLabel.setText("Invalid Move Count: " + invalidknightMoveCount);
                            }
                            
                        }
                    }
                });
                boardPanel.add(cellButtons[i][j]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    
 
    public void handleMoves(int x, int y, String playerName) {
    	
        if (isValidMove(x, y)) {
        	
        	knightMoveCount++ ;
            board[x][y] = knightMoveCount;
            updateUI();

            if (knightMoveCount == boardSize * boardSize) {
            	
                JOptionPane.showMessageDialog(this, "Congratulations! You've completed the Knight's Tour!\nYour Score: " + knightMoveCount);
                storePlayerData(playerName, invalidknightMoveCount, knightMoveCount);
                gameStarted = false;
                
            }

            invalidMoveLabel.setText(" Invalid Moves: " + invalidknightMoveCount); // Update the invalidMoveLabel
        
        } 
        
        else {
        	
            JOptionPane.showMessageDialog(this, "Invalid move!");
            invalidknightMoveCount++;
         
            invalidMoveLabel.setText(" Invalid Moves: " + invalidknightMoveCount); // Update the invalidMoveLabel

            if (invalidknightMoveCount >= 5) {
            	
                displayGameOverOptions(); // Display restart and exit options
            }
        }
    }

    
    private void displayGameOverOptions() {
    	
        // Message dialog when the game is over due to too many invalid moves
        int choice = JOptionPane.showOptionDialog( this, "You've made 5 invalid moves. Game over!","Game Over",
        		JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Restart", "Exit"},"Restart");

        if (choice == JOptionPane.YES_OPTION) {
            
            restartGame();
        
        } 
        
        else {
        	
            System.exit(0);
        }
    }

    
    private void displayGameCompletionMessage() {
    	
        // Message dialog when the player completes the Knight's Tour game
        int choice = JOptionPane.showOptionDialog(this, "Congratulations! You've completed the Knight's Tour!\nYour Score: " + knightMoveCount,"Game Completed",
        		JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,new String[]{"Restart", "Exit"},"Restart");

        if (choice == JOptionPane.YES_OPTION) {
        	
            restartGame(); 
            
        } 
        
        else {
        	
            System.exit(0); 

        }
    }
    

    private void updateUI() {
    	
        // Update the display of the chessboard based on the current state
        for (int i = 0; i < boardSize; i++) {
        	
            for (int j = 0; j < boardSize; j++) {
            	
                if (board[i][j] == 0) {
                	
                    cellButtons[i][j].setText(""); 
                    
                } 
                
                else {
                    cellButtons[i][j].setText(String.valueOf(board[i][j])); // Display the move count
                }
            }
        }
    }

    
    public void restartGame() {
    	
        initializeBoard();
        knightMoveCount = 1;
        gameStarted = false;
        startButton.setEnabled(true);
        restartButton.setEnabled(false);
        backButton.setEnabled(true);
        invalidknightMoveCount = 0; 
        updateUI();

        // Reset the move and invalid move count labels
        movesLabel.setText("Move: 0");
        invalidMoveLabel.setText("Invalid Moves: 0");
    }

    
    public void startGame() {
    	
        // Start the game by initializing the board and starting position
        initializeBoard();
        
        Random random = new Random(); //Random knight starting point
        int startX = random.nextInt(boardSize);
        int startY = random.nextInt(boardSize);

        board[startX][startY] = 1;
        knightMoveCount = 1;
        gameStarted = true;

        startButton.setEnabled(false);
        restartButton.setEnabled(true);
        updateUI();
        
    }

    
    public boolean isValidMove(int x, int y) {
    	
        // Check whether the move to is valid for the knight (L shape law)
        if (x >= 0 && x < boardSize && y >= 0 && y < boardSize && board[x][y] == 0) {
        	
            for (int i = 0; i < 8; i++) {
            	
                int newX = x + knightMovesX[i];
                int newY = y + knightMovesY[i];
                if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize && board[newX][newY] == knightMoveCount) {
                    return true; // for valid move
                
                }
            }
        }
        
        return false; // for invalid move
    }

    
    
    
    private void storePlayerData(String playerName, int invalidMoveCount, int score) {
    	
        try {
        	
            String databaseUrl = "jdbc:mysql://localhost:8889/KnightsTourGame"; 
            String username = "root";
            String password = "root"; 

            
            Connection connection = DriverManager.getConnection(databaseUrl, username, password);  // Database connection

            
            String insertQuery = "INSERT INTO Winners (PlayerName, InvalidMoves, ValidMoves) VALUES (?, ?, ?)"; //SQL query to insert player data
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);

            
            // Parameter values for the query
            preparedStatement.setString(1, playerName); 
            preparedStatement.setInt(2, invalidMoveCount);
            preparedStatement.setInt(3, score); 


            int rowsInserted = preparedStatement.executeUpdate(); // Execute the query to insert the player's data into database

            
            if (rowsInserted > 0) {
            	
                JOptionPane.showMessageDialog(this, "Record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } 
            
            else {
            	
                JOptionPane.showMessageDialog(this, "Failed to add record to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            
            }

            
            preparedStatement.close(); 
            connection.close(); 
        } 
        
        catch (SQLException e) {
        	
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving player data to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        
        }
    }

    
    private class PlayerData {
    	
        // Inner class to represent player data
        private String playerNameForLeaderboard;
        private int invalidKnightMoveCountForleaderboard;
        private int scoreForLeaderboard;

        public PlayerData(String playerName, int invalidMoveCount, int score) {
            this.playerNameForLeaderboard = playerName;
            this.invalidKnightMoveCountForleaderboard = invalidMoveCount;
            this.scoreForLeaderboard = score;
        }

        public String getPlayerName() {
            return playerNameForLeaderboard;
        }

        public int getInvalidMoveCount() {
            return invalidKnightMoveCountForleaderboard;
        }

        public int getScore() {
            return scoreForLeaderboard;
        }
    }

    
    private List<PlayerData> retrievePlayerData() {
    	
        // Retrieve player data from the database
        List<PlayerData> playerDataList = new ArrayList<>();
        
        try {
            String databaseUrl = "jdbc:mysql://localhost:8889/KnightsTourGame";
            String username = "root"; 
            String password = "root"; 
            
            Connection connection = DriverManager.getConnection(databaseUrl, username, password); // Database connection
            
            String selectQueryToRetrieveData = "SELECT PlayerName, InvalidMoves, ValidMoves FROM Winners ORDER BY ValidMoves DESC LIMIT 10"; 
            PreparedStatement preparedStatement = connection.prepareStatement(selectQueryToRetrieveData); // SQL statement
            ResultSet resultSet = preparedStatement.executeQuery(); 

            
            // Data retrieving
            while (resultSet.next()) {
                String playerNameForLeaderboard = resultSet.getString("PlayerName"); 
                int invalidKnightMoveCountForleaderboard = resultSet.getInt("InvalidMoves");
                int scoreForLeaderboard = resultSet.getInt("ValidMoves");
                PlayerData playerData = new PlayerData(playerNameForLeaderboard, invalidKnightMoveCountForleaderboard, scoreForLeaderboard); // Create a PlayerData object
                playerDataList.add(playerData); // Add the player data to the list
           
            }

            
            // Close database resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } 
        
        catch (SQLException e) {
        	
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving player data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        
        }
        
        return playerDataList;
        
    }
    
 
    
    private void LeaderboardUI() {
    	
        
        // Retrieve player data from the database
        List<PlayerData> playerDataList = retrievePlayerData();

        
        // Check whether the playerDataList is not empty
        if (!playerDataList.isEmpty()) {
            
            StringBuilder leaderboardMessage = new StringBuilder("Leaderboard:\n"); // Construct the leaderboard message

            // Traverse through player data and add those data to the leaderboard
            for (int i = 0; i < playerDataList.size(); i++) {
                PlayerData playerData = playerDataList.get(i);
                leaderboardMessage.append(i + 1).append(". ")
                        .append("Player: ").append(playerData.getPlayerName()).append(", ")
                        .append("Invalid Moves: ").append(playerData.getInvalidMoveCount()).append(", ")
                        .append("Moves: ").append(playerData.getScore()).append("\n");
            }

            
            //Change the leaderboardUI color
            UIManager.put("OptionPane.background", new Color(245, 245, 220));
            UIManager.put("Panel.background", new Color(245, 245, 220));
            UIManager.put("Label.background", new Color(245, 245, 220));
            

            // Back button
            Object[] options = { "Back" };
            int choice = JOptionPane.showOptionDialog(this, leaderboardMessage.toString(), "Leaderboard",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

           
            if (choice == 0) {
            	
            	MainMenuUI();
            }
        } 
        
        else {
        	
            JOptionPane.showMessageDialog(this, "Leaderboard is empty.", "Leaderboard", JOptionPane.INFORMATION_MESSAGE);  // Display a message if the leaderboard is empty
            
        }
    }   

    
    public int getMoveCount() {
        return knightMoveCount;
    }
    
    
    public int[][] getBoard() {
        return board;
    }
    
    public int getInvalidMoveCount() {
    	return invalidknightMoveCount;
    }
 

 
}