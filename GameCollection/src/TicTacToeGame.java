import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class TicTacToeGame extends JFrame {
    private JButton[][] buttons; // declaration of buttons array
    private String[][] board = new String[3][3]; // declaratio of the board array
    private String playerName; 

    private boolean playerTurn = false;
    private boolean gameOver = false;
    private int moves = 0;
    private JFrame playerDetailsFrame;
    private JTextField playerNameField;

    private Random random = new Random();

    public JFrame mainMenuFrame;

    private JButton playerDetailsNextButton;
    public Object leaderboardFrame;
    


    public TicTacToeGame() {
        MainMenuUI();
    }

    private void MainMenuUI() {
        // Main menu frame
        JFrame mainMenuFrame = new JFrame("Welcome to the Knight's tour game - Main Menu");
        mainMenuFrame.setSize(500, 500);
        mainMenuFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainMenuFrame.setLocationRelativeTo(null);

        // JPanel to hold main menu items
        JPanel mainMenuPanel = new JPanel(new GridLayout(4, 1)); // Increase the number of rows to accommodate the Back button
        mainMenuPanel.setBackground(new Color(207, 239, 252));
        Border border = BorderFactory.createLineBorder(new Color(207, 239, 252), 3);
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


    public void leaderboardUI() {
        // leaderboard frame
        JFrame leaderboardFrame = new JFrame("Leaderboard - The Top 10 Players");
        leaderboardFrame.setSize(500, 400); // Adjust the frame size as needed
        leaderboardFrame.setBackground(new Color(207, 239, 252));
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
        
        JPanel buttonPanel = new JPanel(); // Create a panel for the back button
        buttonPanel.setBackground(new Color(233, 243, 233));
        buttonPanel.add(backButton);
        
        
        leaderboardFrame.add(buttonPanel, BorderLayout.SOUTH);
        

        // JTextArea to display leaderboard data
        JTextArea leaderboardTextArea = new JTextArea();
        leaderboardTextArea.setFont(new Font("Hiragino Mincho Pro", Font.PLAIN, 15));
        leaderboardTextArea.setEditable(false); // Make it read-only
        leaderboardTextArea.setLineWrap(true);
        leaderboardTextArea.setWrapStyleWord(true);

        // Database connection to retrieve data for the leaderboard
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Load/Register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection details
            String databaseUrl = "jdbc:mysql://localhost:8889/ticTacToeGame";
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
            StringBuilder leaderboardText = new StringBuilder("All time winners:\n\n");
            int rank = 1;

            while (resultSet.next()) {
                String playerName = resultSet.getString("PlayerName");
                leaderboardText.append(rank).append(". ").append(playerName).append("\n");
                rank++;
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

    public void playerDetailsUI() {
        // playerDetailsUI frame
        playerDetailsFrame = new JFrame("Player Details");
        playerDetailsFrame.setSize(410, 160);
        playerDetailsFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        playerDetailsFrame.setLocationRelativeTo(null);
        UIManager.put("OptionPane.background", new Color(207, 239, 252));

        playerNameField = new JTextField(20);

        JPanel playerDetailsPanel = new JPanel(new BorderLayout()); // Use BorderLayout
        UIManager.put("Panel.background", new Color(207, 239, 252));

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
        playerDetailsNextButton = new JButton("Next");
        playerDetailsNextButton.setPreferredSize(buttonSize);
        playerDetailsNextButton.setFont(boldFont);

        playerDetailsNextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = playerNameField.getText().trim(); // Assign playerName
                if (playerName.isEmpty()) {
                    playerName = "Anonymous Player";
                }
                playerDetailsFrame.setVisible(false);
                startGame(playerName); // Pass the playerName to startGame
            }
        });

        buttonPanel.add(backButton); // Add the back button
        buttonPanel.add(playerDetailsNextButton); // Add the next button
        playerDetailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        playerDetailsFrame.add(playerDetailsPanel);
        playerDetailsFrame.setVisible(true);
    }

    public void gameUI(String playerName) {
        setTitle("Tic Tac Toe");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3));
        buttons = new JButton[3][3];

        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(new Color(233, 243, 233));

        Font cellFont = new Font("Hiragino Mincho Pro", Font.BOLD, 50); // Font for matrix's cell text

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFont(cellFont); // Set font for cell text
                buttons[row][col].setFocusPainted(false);

                final int finalRow = row;
                final int finalCol = col;

                buttons[row][col].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        buttonClicked(finalRow, finalCol);
                    }
                });

                buttonPanel.add(buttons[row][col]);
            }
        }

        // Create a panel for control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 1)); // Use GridLayout for control buttons

        // Create a panel for back/exit buttons
        JPanel controlPanel2 = new JPanel();
        controlPanel2.setLayout(new GridLayout(1, 2));

        JButton restartButton = new JButton("Restart");
        JButton backButton = new JButton("Back");
        JButton exitButton = new JButton("Exit");

        // Increase the preferred height of control buttons
        Dimension buttonSize = new Dimension(60, 40); 
        restartButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);
        exitButton.setForeground(Color.RED);

        // Set the font for control buttons
        Font buttonFont = new Font("Hiragino Mincho Pro", Font.BOLD, 14);
        restartButton.setFont(buttonFont);
        backButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	resetGame();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainMenuUI(); // Go back to the main menu
                dispose();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        controlPanel.add(restartButton);
        controlPanel2.add(backButton);
        controlPanel2.add(exitButton);

        // Create a panel to hold both sets of buttons
        JPanel buttonContainerPanel = new JPanel();
        buttonContainerPanel.setLayout(new BorderLayout());
        buttonContainerPanel.add(controlPanel, BorderLayout.NORTH);
        buttonContainerPanel.add(controlPanel2, BorderLayout.SOUTH);

        add(buttonPanel, BorderLayout.CENTER);
        add(buttonContainerPanel, BorderLayout.SOUTH);

        setVisible(true);
        
        
    }

    public void buttonClicked(int row, int col) {
        if (!gameOver && buttons[row][col].getText().equals("") && playerTurn) {
            buttons[row][col].setText("X");
            board[row][col] = "X"; // Update the board state
            playerTurn = false;
            moves++;
            checkGameStatus(playerName); // Pass playerName to checkGameStatus
            if (!gameOver && !playerTurn) {
                computerMove();
            }
        }
    }

    public void computerMove() {
        if (!gameOver) {
            if (moves == 0) {
                // Make the first move random
                int row, col;
                do {
                    row = (int) (Math.random() * 3);
                    col = (int) (Math.random() * 3);
                } while (!buttons[row][col].getText().equals("") || !board[row][col].equals(""));

                buttons[row][col].setText("O");
                board[row][col] = "O"; // Update the board state
                playerTurn = true;
                moves++;
                checkGameStatus(playerName); // Pass playerName to checkGameStatus
            } else {
                // Find the best move using minimax
                int[] bestMove = findBestMove();
                int row = bestMove[0];
                int col = bestMove[1];

                buttons[row][col].setText("O");
                board[row][col] = "O"; // Update the board state
                playerTurn = true;
                moves++;
                checkGameStatus(playerName); // Pass playerName to checkGameStatus
            }
        }
    }

    public void startGame(String playerName) {
        // Clear the board and start the game
        gameUI(playerName);
        playerTurn = true; // Set player's turn to true
        gameOver = false;
        moves = 0;

        // Initialize the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }

        // Make the first move as O (computer's move)
        computerMove();
    }
    
    public void storeData(String playerName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Load/Register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection details
            String databaseUrl = "jdbc:mysql://localhost:8889/ticTacToeGame";
            String username = "root";
            String password = "root";

            // Create a database connection
            connection = DriverManager.getConnection(databaseUrl, username, password);

            // SQL query to insert the player's name into the Winners table
            String insertQuery = "INSERT INTO Winners (PlayerName) VALUES (?)";

            // Create a PreparedStatement
            preparedStatement = connection.prepareStatement(insertQuery);

            // Set the value for the placeholder
            preparedStatement.setString(1, playerName);

            // Execute the query to insert data
            preparedStatement.executeUpdate();

            // Close the PreparedStatement
            preparedStatement.close();

            // Optionally, you can display a success message here
            JOptionPane.showMessageDialog(
                    this,
                    "Record added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
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
    }


    public void checkGameStatus(String playerName) {
        if (checkWin("X")) {
            gameOver = true;
            showGameOverDialog("Congratulations, " + playerName + "! You win!", "Game Over");
            
            // Call the storeData method to store the player's data in the database
            storeData(playerName);
        } else if (checkWin("O")) {
            gameOver = true;
            showGameOverDialog("Computer wins!", "Game Over");
        } else if (moves == 9) {
            gameOver = true;
            showGameOverDialog("It's a draw!", "Game Over");
        }
    }    
    public void resetGame() {
        // Clear the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
                buttons[i][j].setText("");
            }
        }

        // Reset game variables
        playerTurn = true;
        gameOver = false;
        moves = 0;

        // Make the first move as O (computer's move)
        computerMove();
    }


    public void showGameOverDialog(String message, String title) {
        int option = JOptionPane.showOptionDialog(
                this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Restart", "Exit"},
                "Restart");

        if (option == 0) {
        	resetGame(); // Restart the game
        } else {
            System.exit(0); // Exit the application
        }
    }
    
    

    public boolean checkWin(String player) {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player)) {
                return true; // Row win
            }
            if (board[0][i].equals(player) && board[1][i].equals(player) && board[2][i].equals(player)) {
                return true; // Column win
            }
        }

        if (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player)) {
            return true; // Diagonal win (top-left to bottom-right)
        }
        if (board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player)) {
            return true; // Diagonal win (top-right to bottom-left)
        }

        return false;
    }

    public int[] findBestMove() {
        int[] bestMove = new int[]{-1, -1};
        int bestScore = Integer.MIN_VALUE;

        // Try all empty cells and select the one with the highest winning chance
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals("")) {
                    board[i][j] = "O"; // Make the move
                    int score = minimax(board, 0, false);
                    board[i][j] = ""; // Undo the move

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    public int minimax(String[][] board, int depth, boolean isMaximizing) {
        String currentPlayer = isMaximizing ? "O" : "X";

        if (checkWin("X")) {
            return -1;
        }
        if (checkWin("O")) {
            return 1;
        }
        if (moves == 9) {
            return 0;
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals("")) {
                    board[i][j] = currentPlayer;
                    int score = minimax(board, depth + 1, !isMaximizing);
                    board[i][j] = "";

                    if (isMaximizing) {
                        bestScore = Math.max(score, bestScore);
                    } else {
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
        }

        return bestScore;
    }

}
