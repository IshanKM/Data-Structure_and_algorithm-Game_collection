import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

public class LongestCommonSequenceGame extends JFrame {
	
    // Declaration of class variables and components
	
    public JTextField userInputField;
    
    private JButton generateBtn ;
    private JButton checkBtn;

    
    private JTextArea string1TextArea; 
    private JTextArea string2TextArea;
    
    public String string1;
    public String string2;
    
    private JLabel scoreLable;
    public int score = 0;
    
    private Random random = new Random();
    
    private boolean generatedStrings = false;
    
    public JFrame mainMenuFrame;
    
    public JFrame playerDetailsFrame;
    private JTextField playerNameField;
    private JButton playerDetailsNextButton;
    public Object leaderboardFrame;

    public LongestCommonSequenceGame() {
    	
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
        mainMenuPanel.setBackground(new Color(222,250,222));
        Border border = BorderFactory.createLineBorder(new Color(222, 250, 222), 3);
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
            	mainMenuFrame.dispose();
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


    
    private void leaderboardUI() {
    	
    	
        // leaderboard frame
        JFrame leaderboardFrame = new JFrame("Leaderboard - The to 10 players");
        leaderboardFrame.setSize(1000, 420); // Adjust the frame size as needed
        leaderboardFrame.setBackground(new Color(233, 243, 233));
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        leaderboardFrame.setLocationRelativeTo(null);
        

        // Jpanel to hold leaderboard items
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(233, 243, 233));
        

        // Set up a JTable for displaying leaderboard
        String[] columnHeaders = {"Player Name", "Score","First String", "Second String",  "Provided Answer", "Correct Answer"};
        DefaultTableModel tableModel = new DefaultTableModel(columnHeaders, 1);
        JTable leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFont(new Font("Hiragino Mincho Pro", Font.PLAIN, 13));
        leaderboardTable.getTableHeader().setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 15));
        leaderboardTable.setRowHeight(30);
        

        // Database connection for retrieve data to leaderboard
        try {
        	
            String databaseUrl = "jdbc:mysql://localhost:8889/LongestCommonSequenceGame";
            String username = "root";
            String password = "root";
            

            Connection connection = DriverManager.getConnection(databaseUrl, username, password);

            
            String selectQuery = "SELECT Players.PlayerName, GameResults.Score, GameResults.ProvidedAnswer, " +
                    "GameResults.CorrectAnswer, Games.FirstString, Games.SecondString " +
                    "FROM GameResults " +
                    "LEFT JOIN Players ON GameResults.PlayerID = Players.PlayerID " +
                    "LEFT JOIN Games ON GameResults.GameID = Games.GameID " +
                    "ORDER BY GameResults.Score DESC " +
                    "LIMIT 10";
            
            
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            

            ResultSet resultSet = preparedStatement.executeQuery();
            

            // Populate the table with data
            while (resultSet.next()) {
                String playerName = resultSet.getString("PlayerName");
                int score = resultSet.getInt("Score");
                String firstString = resultSet.getString("FirstString");
                String secondString = resultSet.getString("SecondString");
                String providedAnswer = resultSet.getString("ProvidedAnswer");
                String correctAnswer = resultSet.getString("CorrectAnswer");

                Object[] rowData = {playerName, score, firstString, secondString,  providedAnswer, correctAnswer,};
                tableModel.addRow(rowData);
            }

            
            resultSet.close();
            preparedStatement.close();
            connection.close();
            
        } 
        
        catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching leaderboard from the database.");
        }

        
        // Create a scroll pane for the leaderboard table
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        
        // Create a back button 
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
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Add the button panel to the bottom of the main panel

        // Add the main panel to the leaderboard frame
        leaderboardFrame.add(mainPanel);
        leaderboardFrame.setVisible(true);
        
    }


    private void playerDetailsUI() {
    	    	
    	// playerDetailsUI frame
        playerDetailsFrame = new JFrame("Player Details");
        playerDetailsFrame.setSize(410, 160);
        playerDetailsFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        playerDetailsFrame.setLocationRelativeTo(null); 
        UIManager.put("OptionPane.background", new Color(233, 243, 233)); 
        
        playerNameField = new JTextField(20);
        
        
        JPanel playerDetailsPanel = new JPanel(new BorderLayout()); // Use BorderLayout
        UIManager.put("Panel.background", new Color(233, 243, 233));
        
        
        //Name lable
        Font hiraginoFont = new Font("Hiragino Mincho Pro", Font.PLAIN, 14); 
        Font boldFont = hiraginoFont.deriveFont(Font.BOLD, 14); 
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        JLabel nameLabel = new JLabel(" Enter your name :    ");
        nameLabel.setPreferredSize(new Dimension(150, 30));

        nameLabel.setFont(hiraginoFont); 
        namePanel.add(nameLabel);
        namePanel.add(playerNameField);
        playerDetailsPanel.add(namePanel, BorderLayout.NORTH);
        
        
        Dimension buttonSize = new Dimension(80, 35); //preferred size for the next button using a dimension object
        

        //Back button configuration
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        JButton backButton = new JButton("Back"); 
        backButton.setFont(boldFont);
        backButton.setPreferredSize(buttonSize);
        
        backButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
            	playerDetailsFrame.dispose();
                MainMenuUI(); 
            }
        });
        
        
        //Next button configuration
        playerDetailsNextButton = new JButton("Next");
        playerDetailsNextButton.setPreferredSize(buttonSize);
        playerDetailsNextButton.setFont(boldFont);
      
        playerDetailsNextButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Anonymous Player";
                }
                playerDetailsFrame.setVisible(false);
                startGame(); 
            }
            
        });

        
        buttonPanel.add(backButton); // Add the back button
        buttonPanel.add(playerDetailsNextButton); // Add the next button
        playerDetailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        playerDetailsFrame.add(playerDetailsPanel);
        playerDetailsFrame.setVisible(true);
        
    }


    public void startGame() {
    	
        generateRandomStrings();
        GameUI(); 
    }
    
    private void generateRandomStrings() {
    	
    	string1 = generateRandomString();
    	string2 = generateRandomString();
    	
    }

    private JButton createStyledButton(String text) {
    	
        JButton button = new JButton(text);
        button.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(80, 35));
        return button;
        
    }
    
    private void GameUI() {
    	
        setTitle("Longest Common Sequence Game");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        userInputField = new JTextField(20);
       

        generateBtn = new JButton("Generate Random Strings");
        checkBtn = new JButton("Check");

        // Resize the s1 and s2 text areas
        string1TextArea = new JTextArea(2, 20);
        string2TextArea = new JTextArea(2, 20);

        scoreLable = new JLabel(" Score: 0 ");

        
        // Create a custom border with the desired dimensions
        Border resizedBorder = BorderFactory.createLineBorder(Color.BLACK, 1, true);
        Border finalBorder = BorderFactory.createCompoundBorder(null,BorderFactory.createCompoundBorder(resizedBorder,BorderFactory.createEmptyBorder(30, 100, 30, 100)));
        scoreLable.setBorder(BorderFactory.createCompoundBorder(finalBorder, null));

        
        generateBtn.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!generatedStrings) {
                    generateRandomStrings();
                    string1TextArea.setText(string1);
                    string2TextArea.setText(string2);
                    generatedStrings = true;
                    generateBtn.setEnabled(false);
                }
            }
        });

        
        checkBtn.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        
        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(new Color(233, 243, 233));

        GridBagConstraints gridContains = new GridBagConstraints();
        gridContains.gridx = 0;
        gridContains.gridy = 0;
        gridContains.anchor = GridBagConstraints.WEST;
        gridContains.insets = new Insets(5, 5, 5, 5);

        gamePanel.add(new JLabel("First string : "), gridContains);
        gridContains.gridx = 1;
        gamePanel.add(string1TextArea, gridContains);
        gridContains.gridx = 0;
        
        gridContains.gridy = 1;
        gamePanel.add(new JLabel("Second string : "), gridContains);
        gridContains.gridx = 1;
        gamePanel.add(string2TextArea, gridContains);

        gridContains.gridy = 2;
        gridContains.gridx = 1;
        gamePanel.add(generateBtn, gridContains);
        gridContains.gridy = 3;

        gridContains.gridx = 0;
        gamePanel.add(new JLabel("Your guess: "), gridContains);
        gridContains.gridx = 1;
        gamePanel.add(userInputField, gridContains);

        gridContains.gridy = 4;
        gridContains.gridx = 1;
        gamePanel.add(checkBtn, gridContains);

        gridContains.gridy = 5;
        gridContains.gridwidth = 2;
        gamePanel.add(new JSeparator(), gridContains);

        gridContains.gridy = 6;
        gridContains.gridwidth = 1;
        gamePanel.add(scoreLable, gridContains);

        gridContains.gridy = 7;
        gridContains.gridwidth = 2;
        gamePanel.add(new JSeparator(), gridContains);
        

        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	dispose();
            	playerDetailsUI();
                
            }
        });

        JButton exitButton = createStyledButton("Exit");
        exitButton.setForeground(Color.RED);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());

        buttonPanel.add(backButton, BorderLayout.WEST);
        buttonPanel.add(exitButton, BorderLayout.EAST);

        gridContains.gridy = 8;
        gridContains.gridx = 1;
        gamePanel.add(buttonPanel, gridContains);

        add(gamePanel);
        setVisible(true);
    }


    public void checkAnswer() {
    	
       
        String providedAnswer = userInputField.getText().trim();  // Get the user's answer from the input field
        String correctAnswer = calculateLongestCommonSequence(string1, string2).toLowerCase(); // Calculate the correct answer using the longest common subsequence algorithm

        
        // Check if the player's answer matches the correct answer
        if (providedAnswer.equals(correctAnswer)) {
        	
            // If user's answer is correct, increment the score, display the new score, and generate new strings
            score++;
            scoreLable.setText("Score: " + score);
            generateRandomStrings();
            string1TextArea.setText(string1);
            string2TextArea.setText(string2);
            userInputField.setText(""); 
            
        } 
        
        else {
        	
            // If user's answer is incorrect, show a message dialog with the correct answer and reset the game
            int option = JOptionPane.showOptionDialog( this,"You are wrong!\nCorrect answer: " + correctAnswer + "\nYour score: " + score,"Game Over",
                JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE,null,new String[]{"Restart", "Exit"},"Exit");

            if (option == 0) { 
            	
            	// Restart the game
                LongestCommonSequenceGame newGame = new LongestCommonSequenceGame(); 
                newGame.startGame();
                dispose(); 

            } 
            
            else { 
            	// Exit the application
                System.exit(0);
            }

            
            storePlayerData(playerNameField.getText().trim(), score, string1, string2, providedAnswer, correctAnswer); // Update the database
           
            dispose();
        }
    }

    
    public void storePlayerData(String playerName, int score, String firstString, String secondString, String providedAnswer, String correctAnswer) {
        
    	try {
    		
            // Database connection details
            String databaseUrl = "jdbc:mysql://localhost:8889/LongestCommonSequenceGame";
            String username = "root";
            String password = "root";

            Connection connection = DriverManager.getConnection(databaseUrl, username, password); //Database Connection

            // Prepare an SQL query to insert player data
            String insertPlayerQuery = "INSERT INTO Players (PlayerName) VALUES (?)";
            PreparedStatement playerStatement = connection.prepareStatement(insertPlayerQuery, Statement.RETURN_GENERATED_KEYS);
            playerStatement.setString(1, playerName);
            
            // Execute the insert query for players
            int rowsInserted = playerStatement.executeUpdate();
            int playerID = -1;

            if (rowsInserted > 0) {
                
                ResultSet generatedKeys = playerStatement.getGeneratedKeys(); // Retrieve the auto-generated PlayerID
                if (generatedKeys.next()) {
                    playerID = generatedKeys.getInt(1);
                }
            }

            // Prepare an SQL query to insert game data
            String insertGameQuery = "INSERT INTO Games (FirstString, SecondString) VALUES (?, ?)";
            PreparedStatement gameStatement = connection.prepareStatement(insertGameQuery, Statement.RETURN_GENERATED_KEYS);
            gameStatement.setString(1, firstString);
            gameStatement.setString(2, secondString);
  
            // Execute the insert query for games
            rowsInserted = gameStatement.executeUpdate();
            int gameID = -1;

            if (rowsInserted > 0) {
            	
                // Retrieve the auto-generated GameID
                ResultSet generatedKeys = gameStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    gameID = generatedKeys.getInt(1);
                }
                
            }
            
            // Prepare an SQL query to insert game resultි
            String insertResultQuery = "INSERT INTO GameResults (PlayerID, GameID, Score, ProvidedAnswer, CorrectAnswer) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement resultStatement = connection.prepareStatement(insertResultQuery);
            resultStatement.setInt(1, playerID);
            resultStatement.setInt(2, gameID);
            resultStatement.setInt(3, score);
            resultStatement.setString(4, providedAnswer);
            resultStatement.setString(5, correctAnswer);

            // Execute the insert query for game results
            rowsInserted = resultStatement.executeUpdate();

            if (rowsInserted > 0) {
            	
                JOptionPane.showMessageDialog(this, "Record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } 
            
            else {
            	
                JOptionPane.showMessageDialog(this, "Failed to add record to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Close database resources
            playerStatement.close();
            gameStatement.close();
            resultStatement.close();
            connection.close();
            
        } 
    	
    	catch (SQLException e) {
    		
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving player data to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        
    	}
    }


    public String generateRandomString() {
    	
        StringBuilder StringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            
            char randomChar = (char) (random.nextInt(26) + 'a'); // Generate a random lowercase characters
            StringBuilder.append(randomChar);
        }
        
        return StringBuilder.toString(); // Return the generated random string
    }
    

    public String calculateLongestCommonSequence(String str1, String str2) {
       
        int len1 = str1.length();
        int len2 = str2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        // Dynamic programming table code
        for (int i = 0; i <= len1; i++) {
        	
            for (int j = 0; j <= len2; j++) {
            	
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                    
                } 
                
                else if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    
                } 
                
                else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        
        // Reconstruct the longest common subsequence
        int index = dp[len1][len2];
        char[] lcs = new char[index];
        int i = len1, j = len2;

        while (i > 0 && j > 0) {
        	
            if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                lcs[index - 1] = str1.charAt(i - 1);
                i--;
                j--;
                index--;
                
            } 
            
            else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } 
            
            else {
                j--;
            }
        }

        return new String(lcs); // Return the longest common subsequence as a string
    
    }
}