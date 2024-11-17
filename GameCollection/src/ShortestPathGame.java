 import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;



public class ShortestPathGame extends JFrame {
    public int numCities = 10;
    public String[] cities = {"   A", "   B", "   C", "   D", "   E", "   F", "   G", "   H", "   I", "   J"};
    private final int[][] adjacencyMatrix = new int[numCities][numCities];
    private final Random random = new Random();
    public final JLabel selectedCityLabel = new JLabel("Selected City: ");
    public JTextField[] distanceFields = new JTextField[numCities];
    private final JTextField shortestPathField = new JTextField();
    
    private String gameType;
 
    private JTextField playerNameField;
    
    private long durationDij; 
    private long durationBel; 
    private Thread dijkstraThread;
    private Thread bellmanFordThread;


    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:8889/shortestPathGame";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    
    
    public ShortestPathGame() {
    	
    	MainMenuUI();
    	
    }
    
    public void MainMenuUI() {
        // Main menu frame
        JFrame mainMenuFrame = new JFrame("Welcome to the Knight's tour game - Main Menu");
        mainMenuFrame.setSize(500, 500);
        mainMenuFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainMenuFrame.setLocationRelativeTo(null);

        // JPanel to hold main menu items
        JPanel mainMenuPanel = new JPanel(new GridLayout(4, 1)); // Increase the number of rows to accommodate the Back button
        mainMenuPanel.setBackground(new Color(209, 234, 240));
        Border border = BorderFactory.createLineBorder(new Color(209, 234, 240), 3);
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
            	mainMenuFrame.dispose();
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
        UIManager.put("OptionPane.background", new Color(209, 234, 240));

        JTextField playerNameField = new JTextField(20);

        JPanel playerDetailsPanel = new JPanel(new BorderLayout()); // Use BorderLayout
        UIManager.put("Panel.background", new Color(209, 234, 240));

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

        JButton nextButton = new JButton("Next");
        nextButton.setPreferredSize(buttonSize);
        nextButton.setFont(boldFont);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Anonymous Player";
                    
                }

                playerDetailsFrame.setVisible(false);

                // Load the game UI directly
                gameUI(playerName);
            }
        });
        
        



        buttonPanel.add(backButton); // Add the back button
        buttonPanel.add(nextButton); // Add the next button
        playerDetailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        playerDetailsFrame.add(playerDetailsPanel);
        playerDetailsFrame.setVisible(true);
    }

    
    
    
    public void leaderboardUI() {
        // Create the leaderboard frame
        JFrame leaderboardFrame = new JFrame("Leaderboard");
        leaderboardFrame.setLocationRelativeTo(null);
        leaderboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        leaderboardFrame.setSize(800, 400);
        
        // Create a tabbed pane to hold the tables
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create the winners table
        JTable winnersTable = createWinnersTable();
        JScrollPane winnersScrollPane = new JScrollPane(winnersTable);
        tabbedPane.addTab("Winners", winnersScrollPane);
        
        // Create the algorithm performances table
        JTable performancesTable = createPerformancesTable();
        JScrollPane performancesScrollPane = new JScrollPane(performancesTable);
        tabbedPane.addTab("Algorithm Performances", performancesScrollPane);
        
        // Add the tabbed pane to the frame
        leaderboardFrame.add(tabbedPane);
        
        // Create a "Back" button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 11));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the leaderboard frame
            	leaderboardFrame.dispose();
                
                // Load the MainMenuUI
                
                MainMenuUI();
            }
        });
        
        // Add the "Back" button to the frame
        leaderboardFrame.add(backButton, BorderLayout.SOUTH);
        
        // Display the frame
        leaderboardFrame.setVisible(true);
    }


    private JTable createWinnersTable() {
        JTable winnersTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Player Name", "Game Type", "Shortest Path"}, 0);

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a SQL query to retrieve data from the 'winners', 'games', and 'gameResults' tables
            String query = "SELECT winners.playerName, games.gameType, gameResults.shortestPath " +
                           "FROM winners " +
                           "JOIN gameResults ON winners.playerID = gameResults.playerID " +
                           "JOIN games ON games.gameID = gameResults.gameID";
            
            // Execute the query
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Populate the table model with data from the database
            while (resultSet.next()) {
                String playerName = resultSet.getString("playerName");
                String gameType = resultSet.getString("gameType");
                String shortestPath = resultSet.getString("shortestPath");
                model.addRow(new Object[]{playerName, gameType, shortestPath});
            }
            
            // Close the database connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related exceptions here
        }

        winnersTable.setModel(model);
        return winnersTable;
    }

    private JTable createPerformancesTable() {
        JTable performancesTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Dijkstra's", "Bellman-Ford"}, 0);

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a SQL query to retrieve data from the 'Performance' table
            String query = "SELECT Dijkstras, BellmanFord FROM Performance";
            
            // Execute the query
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            // Populate the table model with data from the database
            while (resultSet.next()) {
                long dijkstras = resultSet.getLong("Dijkstras");
                long bellmanFord = resultSet.getLong("BellmanFord");
                model.addRow(new Object[]{dijkstras, bellmanFord});
            }
            
            // Close the database connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any database-related exceptions here
        }

        performancesTable.setModel(model);
        return performancesTable;
    }
    
    
    
    private void gameUI(String playerName) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the game frame
                JFrame gameFrame = new JFrame("Shortest Path Game");
                gameFrame.setSize(1920, 1080);
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                Font labelFont = new Font("Hiragino Mincho Pro", Font.PLAIN, 12);

                // Generate random adjacency matrix and create UI components
                generateRandomAdjacencyMatrix();

                JPanel matrixPanel = new JPanel(new GridLayout(numCities + 1, numCities + 1));

                JLabel selectedCityLabel = new JLabel("     Selected City :              ");
                selectedCityLabel.setFont(new Font("Hiragino Mincho Pro", Font.PLAIN, 16));
                selectedCityLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                JPanel topPanel = new JPanel();
                topPanel.add(selectedCityLabel);

                // Create labels for the cities
                matrixPanel.add(new JLabel("   Cities"));
                for (String city : cities) {
                    matrixPanel.add(new JLabel(city));
                }

                // Fill the matrix with values and display them
                for (int i = 0; i < numCities; i++) {
                    matrixPanel.add(new JLabel(cities[i]));

                    for (int j = 0; j < numCities; j++) {
                        int value = adjacencyMatrix[i][j];
                        String text = (value == 0) ? "X" : String.valueOf(value); // Display 'X' for unconnected cities
                        JLabel cellLabel = new JLabel(text, SwingConstants.CENTER);

                        cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        matrixPanel.add(cellLabel);
                    }
                }

                JScrollPane scrollPane = new JScrollPane(matrixPanel);

                // inputPanel which stores distance input fields and shortest path input field
                JPanel inputPanel = new JPanel(new GridLayout(numCities + 2, 2));
                for (int i = 0; i < numCities; i++) {
                    JLabel label = new JLabel("               Distance from " + cities[i] + " to selected city :   ");
                    label.setFont(labelFont);
                    distanceFields[i] = new JTextField();
                    inputPanel.add(label);
                    inputPanel.add(distanceFields[i]);
                }

                JLabel shortestPathLabel = new JLabel("             Enter Shortest Path :   ");
                shortestPathLabel.setFont(labelFont);
                shortestPathField.setEnabled(false);

                inputPanel.add(shortestPathLabel);
                inputPanel.add(shortestPathField);

                JPanel buttonPanel = new JPanel(new GridLayout(1, 7));

                JButton selectCityButton = new JButton("Select a City");
                selectCityButton.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 11));
                selectCityButton.setPreferredSize(new Dimension(200, 50));
                selectCityButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedCity = cities[random.nextInt(numCities)];
                        selectedCityLabel.setText("     Selected City : " + selectedCity + "              ");

                        // Enable distance input fields and shortest path input field
                        enableDistanceInputFields(true);
                        shortestPathField.setEnabled(true);

                        // Display distances from the selected city
                        displayDistancesFromSelectedCity(selectedCity);
                    }
                });

                JButton performance = new JButton("Performance");
                performance.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 11));
                performance.setPreferredSize(new Dimension(200, 50));
                performance.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedCityLabelContent = selectedCityLabel.getText().trim(); // Trim any leading/trailing whitespace

                        if (selectedCityLabelContent.startsWith("Selected City :")) {
                            String selectedCity = selectedCityLabelContent.replace("Selected City : ", "");

                            if (selectedCity.isEmpty()) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                                return;
                            }

                            int selectedCityIndex = getIndex(selectedCity);
                            if (selectedCityIndex == -1) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Invalid selected city.");
                                return;
                            }

                            // Create threads for Dijkstra's and Bellman-Ford algorithms
                            dijkstraThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long startTime = System.currentTimeMillis();
                                    Map<String, Integer> dijkstraDistances = dijkstra(adjacencyMatrix, selectedCityIndex);
                                    long endTime = System.currentTimeMillis();
                                    durationDij = endTime - startTime;

                                    if (dijkstraDistances != null) {
                                        List<String> dijkstraVisitedNodes = getVisitedNodesInOrder(dijkstraDistances);
                                        // You can use dijkstraDistances and dijkstraVisitedNodes as needed
                                    }
                                }
                            });

                            bellmanFordThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long startTime = System.currentTimeMillis();
                                    Map<String, Integer> bellmanFordDistances = bellmanFord(adjacencyMatrix, selectedCityIndex);
                                    long endTime = System.currentTimeMillis();
                                    durationBel = endTime - startTime;

                                    if (bellmanFordDistances != null) {
                                        List<String> bellmanFordVisitedNodes = getVisitedNodesInOrder(bellmanFordDistances);
                                        // You can use bellmanFordDistances and bellmanFordVisitedNodes as needed
                                    }
                                }
                            });

                            // Start both threads
                            dijkstraThread.start();
                            bellmanFordThread.start();

                            // Wait for both threads to finish
                            try {
                                dijkstraThread.join();
                                bellmanFordThread.join();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            // Display runtime results in a JOptionPane
                            String message = "Dijkstra's Runtime: " + durationDij + " ms\n" +
                                    "Bellman-Ford Runtime: " + durationBel + " ms";
                            JOptionPane.showMessageDialog(ShortestPathGame.this, message);

                            savePerformanceToDatabase(durationDij, durationBel);

                        }
                    }
                });

                JButton shortestPathButtonDij = new JButton("Shortest Path - Dijkstra");
                shortestPathButtonDij.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 11));
                shortestPathButtonDij.setPreferredSize(new Dimension(200, 50));
                shortestPathButtonDij.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedCityLabelContent = selectedCityLabel.getText().trim(); // Trim any leading/trailing whitespace

                        if (selectedCityLabelContent.startsWith("Selected City :")) {
                            String selectedCity = selectedCityLabelContent.replace("Selected City : ", "");

                            if (selectedCity.isEmpty()) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                                return;
                            }

                            int selectedCityIndex = getIndex(selectedCity);
                            if (selectedCityIndex == -1) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Invalid selected city.");
                                return;
                            }

                            // Use Dijkstra's algorithm to find the shortest path to all other cities
                            Map<String, Integer> shortestDistances = dijkstra(adjacencyMatrix, selectedCityIndex);

                            // Display the shortest distances and visited nodes in order
                            StringBuilder result = new StringBuilder("Shortest Distances from " + selectedCity + ":\n");
                            for (String city : cities) {
                                if (!city.equals(selectedCity)) {
                                    int distance = shortestDistances.get(city);
                                    result.append(city).append(": ").append(distance).append("\n");
                                }
                            }

                            // Display visited nodes in order
                            result.append("Visited Nodes in Order: ");
                            List<String> visitedNodes = getVisitedNodesInOrder(shortestDistances);
                            for (String node : visitedNodes) {
                                result.append(node).append(" -> ");
                            }
                            result.delete(result.length() - 4, result.length()); // Remove the last " -> "

                            // Enable distance input fields and shortest path input field
                            enableDistanceInputFields(true);
                            shortestPathField.setEnabled(true);

                            JOptionPane.showMessageDialog(ShortestPathGame.this, result.toString());
                        } else {
                            JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                        }
                    }
                });
                
                JButton checkButtonDij = new JButton("Check - Dijkstra");
                checkButtonDij.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 11));
                checkButtonDij.setPreferredSize(new Dimension(200, 50));
                checkButtonDij.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    	gameType = "Dijkstra";
                        String selectedCityLabelContent = selectedCityLabel.getText().trim(); // Trim any leading/trailing whitespace

                        if (selectedCityLabelContent.startsWith("Selected City :")) {
                            String selectedCity = selectedCityLabelContent.replace("Selected City : ", "");

                            if (selectedCity.isEmpty()) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                                return;
                            }

                            int selectedCityIndex = getIndex(selectedCity);
                            if (selectedCityIndex == -1) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Invalid selected city.");
                                return;
                            }

                            // Use Dijkstra's algorithm to find the correct shortest distances
                            Map<String, Integer> correctDistances = dijkstra(adjacencyMatrix, selectedCityIndex);

                            // Get user inputs from the distance input fields
                            Map<String, Integer> userDistances = getUserDistances();

                            // Compare user answers with correct answers
                            boolean allCorrect = true;
                            StringBuilder incorrectAnswers = new StringBuilder("Incorrect Answers:\n");

                            for (String city : cities) {
                                if (!city.equals(selectedCity)) {
                                    int userDistance = userDistances.get(city);
                                    int correctDistance = correctDistances.get(city);

                                    if (userDistance == -1) {
                                        allCorrect = false;
                                        incorrectAnswers.append(city).append(": Invalid input (not an integer)\n");
                                    } else if (userDistance != correctDistance) {
                                        allCorrect = false;
                                        incorrectAnswers.append(city).append(": ").append(userDistance)
                                                .append(" (Correct: ").append(correctDistance).append(")\n");
                                    }
                                }
                            }
                            
                            String shortestPath = shortestPathField.getText().trim();

                            // Display the shortest distances and visited nodes in order
                            StringBuilder result = new StringBuilder("Shortest Distances from " + selectedCity + ":\n");
                            for (String city : cities) {
                                if (!city.equals(selectedCity)) {
                                    int distance = correctDistances.get(city);
                                    result.append(city).append(": ").append(distance).append("\n");
                                }
                            }

                            // Display visited nodes in order
                            result.append("Visited Nodes in Order: ");
                            List<String> shortestDistances = getVisitedNodesInOrder(correctDistances);
                            for (String node : shortestDistances) {
                                result.append(node).append(" -> ");
                            }
                            result.delete(result.length() - 4, result.length()); // Remove the last " -> "

                            if (allCorrect) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "You are correct!");
                             // Call storePlayerData only when all answers are correct
                                storePlayerData(playerName, gameType, shortestDistances);
                            } else {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Some of your provided answers are incorrect.\n\n" + incorrectAnswers.toString() +
                                        "\n\n" + "Below are the correct answers" + "\n\n" + result.toString());
                                
                            }
                        } else {
                            JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                        }
                    }
                });

                
                JButton shortestPathBell = new JButton("Shortest Path - Bellman Ford");
                shortestPathBell.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 11));
                shortestPathBell.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedCityLabelContent = selectedCityLabel.getText().trim(); // Trim any leading/trailing whitespace

                        if (selectedCityLabelContent.startsWith("Selected City :")) {
                            String selectedCity = selectedCityLabelContent.replace("Selected City : ", "");

                            if (selectedCity.isEmpty()) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                                return;
                            }

                            int selectedCityIndex = getIndex(selectedCity);
                            if (selectedCityIndex == -1) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Invalid selected city.");
                                return;
                            }

                            // Use the Bellman-Ford algorithm to find the shortest path to all other cities
                            Map<String, Integer> shortestDistances = bellmanFord(adjacencyMatrix, selectedCityIndex);

                            if (shortestDistances != null) {
                                // Display the shortest distances and visited nodes in order
                                StringBuilder result = new StringBuilder("Shortest Distances from " + selectedCity + ":\n");
                                for (String city : cities) {
                                    if (!city.equals(selectedCity)) {
                                        int distance = shortestDistances.get(city);
                                        result.append(city).append(": ").append(distance).append("\n");
                                    }
                                }

                                // Display visited nodes in order
                                result.append("Visited Nodes in Order: ");
                                List<String> visitedNodes = getVisitedNodesInOrder(shortestDistances);
                                for (String node : visitedNodes) {
                                    result.append(node).append(" -> ");
                                }
                                result.delete(result.length() - 4, result.length()); // Remove the last " -> "

                                // Enable distance input fields and shortest path input field
                                enableDistanceInputFields(true);
                                shortestPathField.setEnabled(true);

                                JOptionPane.showMessageDialog(ShortestPathGame.this, result.toString());
                            }
                        } else {
                            JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                        }
                    }
                });

                
                
                JButton checkButtonBell = new JButton("Check - Bellman Ford");
                checkButtonBell.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 11));
                checkButtonBell.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    	playerNameField = new JTextField(20);
                    	gameType = "Bellman Ford";
                        String selectedCityLabelContent = selectedCityLabel.getText().trim(); // Trim any leading/trailing whitespace

                        if (selectedCityLabelContent.startsWith("Selected City :")) {
                            String selectedCity = selectedCityLabelContent.replace("Selected City : ", "");

                            if (selectedCity.isEmpty()) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                                return;
                            }

                            int selectedCityIndex = getIndex(selectedCity);
                            if (selectedCityIndex == -1) {
                                JOptionPane.showMessageDialog(ShortestPathGame.this, "Invalid selected city.");
                                return;
                            }

                            // Use the Bellman-Ford algorithm to find the correct shortest distances
                            Map<String, Integer> correctDistances = bellmanFord(adjacencyMatrix, selectedCityIndex);

                            if (correctDistances != null) {
                                // Get user inputs from the distance input fields
                                Map<String, Integer> userDistances = getUserDistances();

                                // Compare user answers with correct answers
                                boolean allCorrect = true;
                                StringBuilder incorrectAnswers = new StringBuilder("Incorrect Answers:\n");

                                for (String city : cities) {
                                    if (!city.equals(selectedCity)) {
                                        int userDistance = userDistances.get(city);
                                        int correctDistance = correctDistances.get(city);

                                        if (userDistance == -1) {
                                            allCorrect = false;
                                            incorrectAnswers.append(city).append(": Invalid input (not an integer)\n");
                                        } else if (userDistance != correctDistance) {
                                            allCorrect = false;
                                            incorrectAnswers.append(city).append(": ").append(userDistance)
                                                    .append(" (Correct: ").append(correctDistance).append(")\n");
                                        }
                                    }
                                }
                                
                                
                                // Display the shortest distances and visited nodes in order
                                StringBuilder result = new StringBuilder("Shortest Distances from " + selectedCity + ":\n");
                                for (String city : cities) {
                                    if (!city.equals(selectedCity)) {
                                        int distance = correctDistances.get(city);
                                        result.append(city).append(": ").append(distance).append("\n");
                                    }
                                }

                                // Display visited nodes in order
                                result.append("Visited Nodes in Order: ");
                                List<String> visitedNodes = getVisitedNodesInOrder(correctDistances);
                                for (String node : visitedNodes) {
                                    result.append(node).append(" -> ");
                                }
                                result.delete(result.length() - 4, result.length()); // Remove the last " -> "

                                if (allCorrect) {
                                    JOptionPane.showMessageDialog(ShortestPathGame.this, "You are correct!");
                                    storePlayerData(playerName, gameType, visitedNodes);
                                } else {
                                    JOptionPane.showMessageDialog(ShortestPathGame.this, "Some of your provided answers are incorrect.\n\n" + incorrectAnswers.toString() +
                                            "\n\n" + "Below are the correct answers" + "\n\n" + result.toString());
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(ShortestPathGame.this, "Please select a city first.");
                        }
                    }
                });

                
                JButton backButton = new JButton("Back");
                backButton.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 14));
                backButton.setPreferredSize(new Dimension(125, 50));

                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    	gameFrame.dispose();
                        playerDetailsUI();
                        
                    }
                });
                
                JButton exitButton = new JButton("Exit");
                exitButton.setFont(new Font("Hiragino Mincho Pro", Font.BOLD, 14));
                exitButton.setPreferredSize(new Dimension(125, 50));
                exitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Implement the action for the "Exit" button
                        System.exit(0); // Exit the application
                    }
                });

    

                buttonPanel.add(checkButtonDij);
                buttonPanel.add(selectCityButton);
                buttonPanel.add(shortestPathButtonDij);
                buttonPanel.add(shortestPathBell);
                buttonPanel.add(checkButtonDij);
                buttonPanel.add(checkButtonBell);
                buttonPanel.add(performance);
                buttonPanel.add(backButton);
                buttonPanel.add(exitButton);
                
                
                add(topPanel, BorderLayout.NORTH);
                add(scrollPane, BorderLayout.CENTER);
                add(inputPanel, BorderLayout.EAST);
                
                
                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.add(matrixPanel, BorderLayout.CENTER); // Add the matrix panel to the center
                mainPanel.add(buttonPanel, BorderLayout.PAGE_END); // Add the button panel to the bottom

                // Add the main panel to the JFrame
                add(mainPanel);
                
                add(buttonPanel, BorderLayout.SOUTH);

                inputPanel.setBackground(new Color(209, 234, 240));
                buttonPanel.setBackground(new Color(209, 234, 240));
                topPanel.setBackground(new Color(209, 234, 240));
                matrixPanel.setBackground(new Color(245, 245, 235));
                
                
                // Add components to the frame
                gameFrame.add(matrixPanel, BorderLayout.CENTER);
                gameFrame.add(inputPanel, BorderLayout.EAST);
                gameFrame.add(topPanel, BorderLayout.NORTH);
                gameFrame.add(buttonPanel, BorderLayout.SOUTH);
                

                // Set the frame visible
                gameFrame.setVisible(true);
           
            }
        
        });
        
    
    }
                
    

    private void displayDistancesFromSelectedCity(String selectedCity) {
        int selectedCityIndex = getIndex(selectedCity);
        if (selectedCityIndex == -1) {
            return;
        }

        for (int i = 0; i < numCities; i++) {
            if (i != selectedCityIndex) {
                int distance = adjacencyMatrix[selectedCityIndex][i];
                distanceFields[i].setText(String.valueOf(distance));
            }
        }
    }
    
    
  //method to get user input distances from the input fields
    public Map<String, Integer> getUserDistances() {
        Map<String, Integer> userDistances = new HashMap<>();
        for (int i = 0; i < numCities; i++) {
            if (i != getIndex(selectedCityLabel.getText().replace("Selected City: ", ""))) {
                String city = cities[i];
                try {
                    int distance = Integer.parseInt(distanceFields[i].getText());
                    userDistances.put(city, distance);
                } catch (NumberFormatException e) {
                    // Handle invalid input (non-integer)
                    userDistances.put(city, -1); // Use a sentinel value to indicate invalid input
                }
            }
        }
        return userDistances;
    }

    public Map<String, Integer> dijkstra(int[][] graph, int source) {
        int[] distance = new int[numCities];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source] = 0;

        boolean[] visited = new boolean[numCities];

        while (true) {
            int u = -1;
            int minDistance = Integer.MAX_VALUE;

            // Find the unvisited node with the smallest distance
            for (int i = 0; i < numCities; i++) {
                if (!visited[i] && distance[i] < minDistance) {
                    u = i;
                    minDistance = distance[i];
                }
            }

            if (u == -1) {
                break; // All nodes have been visited
            }

            visited[u] = true;

            for (int v = 0; v < numCities; v++) {
                if (!visited[v] && graph[u][v] != 0 && distance[u] != Integer.MAX_VALUE
                        && distance[u] + graph[u][v] < distance[v]) {
                    distance[v] = distance[u] + graph[u][v];
                }
            }
        }

        Map<String, Integer> shortestDistances = new HashMap<>();
        for (int i = 0; i < numCities; i++) {
            shortestDistances.put(cities[i], distance[i]);
        }

        return shortestDistances;
    }

    public List<String> getVisitedNodesInOrder(Map<String, Integer> shortestDistances) {
        List<String> visitedNodes = new ArrayList<>();
        String selectedCity = selectedCityLabel.getText().replace("Selected City: ", "");
        
        visitedNodes.add(selectedCity); // Add the selected city

        // Sort the cities by their shortest distances
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(shortestDistances.entrySet());
        entries.sort(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : entries) {
            String city = entry.getKey();
            if (!city.equals(selectedCity)) {
                visitedNodes.add(city);
            }
        }

        return visitedNodes;
    }

    
    public Map<String, Integer> bellmanFord(int[][] graph, int source) {
        int[] distance = new int[numCities];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source] = 0;

        // Relax edges repeatedly
        for (int i = 0; i < numCities - 1; i++) {
            for (int u = 0; u < numCities; u++) {
                for (int v = 0; v < numCities; v++) {
                    if (graph[u][v] != 0 && distance[u] != Integer.MAX_VALUE && distance[u] + graph[u][v] < distance[v]) {
                        distance[v] = distance[u] + graph[u][v];
                    }
                }
            }
        }

        // Check for negative cycles
        for (int u = 0; u < numCities; u++) {
            for (int v = 0; v < numCities; v++) {
                if (graph[u][v] != 0 && distance[u] != Integer.MAX_VALUE && distance[u] + graph[u][v] < distance[v]) {
                    // Negative cycle found
                    JOptionPane.showMessageDialog(ShortestPathGame.this, "Negative cycle detected. Bellman-Ford cannot find a solution.");
                    return null;
                }
            }
        }

        Map<String, Integer> shortestDistances = new HashMap<>();
        for (int i = 0; i < numCities; i++) {
            shortestDistances.put(cities[i], distance[i]);
        }

        return shortestDistances;
    }




    private void generateRandomAdjacencyMatrix() {
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i == j) {
                    adjacencyMatrix[i][j] = 0; // No distance from a city to itself
                } else {
                    // Generate a random distance between 5 and 50, or 0 if not connected
                    adjacencyMatrix[i][j] = random.nextInt(10) < 5 ? 0 : random.nextInt(46) + 5;
                }
            }
        }
    }

    private int getIndex(String city) {
        for (int i = 0; i < numCities; i++) {
            if (cities[i].equals(city)) {
                return i;
            }
        }
        return -1; // City not found
    }

    private void enableDistanceInputFields(boolean enable) {
        for (int i = 0; i < numCities - 1; i++) {
            distanceFields[i].setEnabled(enable);
        }
    }
    

    private void storePlayerData(String playerName, String gameType, List<String> visitedNodes) {
        Connection connection = null;
        try {
            // Establish a database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            
            String insertPlayerQuery = "INSERT INTO winners (playerName) VALUES (?)";
            PreparedStatement playerStatement = connection.prepareStatement(insertPlayerQuery);
            playerStatement.setString(1, playerName);
            playerStatement.executeUpdate();

            
            int playerID;
            try (PreparedStatement playerIDStatement = connection.prepareStatement(
                    "SELECT LAST_INSERT_ID()")) {
                var resultSet = playerIDStatement.executeQuery();
                resultSet.next();
                playerID = resultSet.getInt(1);
            }

            
            String insertGameQuery = "INSERT INTO games (gameType) VALUES (?)";
            PreparedStatement gameStatement = connection.prepareStatement(insertGameQuery);
            gameStatement.setString(1, gameType);
            gameStatement.executeUpdate();

            // Get the auto-generated gameID
            int gameID;
            try (PreparedStatement gameIDStatement = connection.prepareStatement(
                    "SELECT LAST_INSERT_ID()")) {
                var resultSet = gameIDStatement.executeQuery();
                resultSet.next();
                gameID = resultSet.getInt(1);
            }

            // Convert the visitedNodes list to a comma-separated string
            String visitedNodesString = String.join(",", visitedNodes);

            // Insert game result data into the 'gameResults' table
            String insertGameResultQuery = "INSERT INTO gameResults (playerID, gameID, shortestPath) VALUES (?, ?, ?)";
            PreparedStatement gameResultStatement = connection.prepareStatement(insertGameResultQuery);
            gameResultStatement.setInt(1, playerID);
            gameResultStatement.setInt(2, gameID);
            gameResultStatement.setString(3, visitedNodesString); // Store the visitedNodes as a string
            gameResultStatement.executeUpdate();

            
            JOptionPane.showMessageDialog(null, "Player data stored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null, "Failed to store player data.", "Error", JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    private void savePerformanceToDatabase(long durationDij, long durationBel) {
        Connection connection = null;
        
        try {
            
            String Con = "jdbc:mysql://localhost:8889/shortestPathGame";
            String User = "root";
            final String Pw = "root";

            
            connection = DriverManager.getConnection(Con, User, Pw);

            
            String insertQuery = "INSERT INTO Performance (Dijkstras, BellmanFord) VALUES (?, ?)";

            
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            
            preparedStatement.setLong(1, durationDij);
            preparedStatement.setLong(2, durationBel);

            
            preparedStatement.executeUpdate();

            
            JOptionPane.showMessageDialog(null, "Performance data stored successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            
            JOptionPane.showMessageDialog(null, "Failed to store performance data.", "Error", JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}