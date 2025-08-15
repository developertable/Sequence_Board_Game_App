import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SequenceGameGUI extends JFrame {
    private static final int CELL_SIZE = 80;
    private static final int BOARD_SIZE = 10;

    private static final Color PLAYER1_COLOR = new Color(0xf7b173);                    // Custom orange/peach
    private static final Color PLAYER2_COLOR = new Color(0x90d9f4);                   // Custom light blue
    private static final Color PLAYER1_SEQUENCE_COLOR = new Color(0xfc7d56);  // Custom coral/red-orange
    private static final Color PLAYER2_SEQUENCE_COLOR = new Color(0x1fbbf5);  // Custom bright blue
    private static final Color CORNER_COLOR = Color.YELLOW;
    private static final Color EMPTY_COLOR = Color.WHITE;
    private static final Color SELECTED_COLOR = Color.CYAN;
    
    private Board board;
    private Deck deck;
    private Player[] players;
    private int currentPlayerIndex;
    private boolean isAIGame;
    private AIPlayer aiPlayer;
    private boolean isAIThinking;
    
    // GUI Components
    private JPanel boardPanel;
    private JPanel[][] cellPanels;
    private JPanel handPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel sequenceLabel;
    private JButton[] handButtons;
    private Card selectedCard;
    private boolean gameOver;

    private Timer turnTimer;
    private int timeLeft;
    private static final int TURN_TIME_SECONDS = 15;
    private JLabel timerLabel;

    public SequenceGameGUI() {

        initializeGame();
        setupGUI();
        updateDisplay();

    }

    private String[] getPlayerNames() {
        String[] names = new String[2];
        
        // Get Player 1 name
        String player1Name = JOptionPane.showInputDialog(this, 
            "Enter name for Player 1 (Orange):", 
            "Player Setup", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (player1Name == null || player1Name.trim().isEmpty()) {
            player1Name = "Player 1";
        }
        names[0] = player1Name.trim();
        
        // Get Player 2 name
        String player2Name = JOptionPane.showInputDialog(this, 
            "Enter name for Player 2 (Blue):", 
            "Player Setup", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (player2Name == null || player2Name.trim().isEmpty()) {
            player2Name = "Player 2";
        }
        names[1] = player2Name.trim();
        
        return names;
    }

    private void initializeGame() {
        board = new Board();
        deck = new Deck();
        
        // Select game mode first
        GameMode gameMode = selectGameMode();
        isAIGame = gameMode.isAI;
        
        if (isAIGame) {
            aiPlayer = new AIPlayer(gameMode.aiLevel);
            
            // Get human player name
            String humanName = JOptionPane.showInputDialog(this, 
                "Enter your name:", 
                "Player Setup", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (humanName == null || humanName.trim().isEmpty()) {
                humanName = "Human";
            }
            
            players = new Player[2];
            players[0] = new Player(1, humanName.trim() + " (Orange)");
            players[1] = new Player(2, aiPlayer.getName() + " (Blue)");
            
        } else {
            // Get player names for human vs human
            String[] playerNames = getPlayerNames();
            
            players = new Player[2];
            players[0] = new Player(1, playerNames[0] + " (Orange)");
            players[1] = new Player(2, playerNames[1] + " (Blue)");
        }

        // Deal initial hands
        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    p.addCard(card);
                }
            }
        }

        currentPlayerIndex = 0;
        gameOver = false;
        isAIThinking = false;
    }

    private void setupGUI() {
        setTitle("Sequence Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create board panel
        createBoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        // Create hand panel
        createHandPanel();
        add(handPanel, BorderLayout.SOUTH);

        // Create status panel
        createStatusPanel();
        add(statusPanel, BorderLayout.NORTH);

        // Create legend panel - ADD THIS LINE
        createLegendPanel();

        // Create menu
        createMenuBar();

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    private void createBoardPanel() {
        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 1, 1));
        boardPanel.setBackground(Color.BLACK);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        cellPanels = new JPanel[BOARD_SIZE][BOARD_SIZE];
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JPanel cellPanel = createCellPanel(row, col);
                cellPanels[row][col] = cellPanel;
                boardPanel.add(cellPanel);
            }
        }
    }

    private JPanel createCellPanel(int row, int col) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        panel.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Add click listener
        final int finalRow = row;
        final int finalCol = col;
      panel.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
              if (!gameOver && selectedCard != null) {
                  handleCellClick(finalRow, finalCol);
              }
          }
          
          @Override
          public void mouseEntered(MouseEvent e) {
              if (!gameOver && selectedCard != null && isValidMove(selectedCard, finalRow, finalCol)) {
                  panel.setBackground(SELECTED_COLOR);
              }
          }
          
          @Override
          public void mouseExited(MouseEvent e) {
              updateCellDisplay(finalRow, finalCol);
          }
      });
        
        return panel;
    }

    private void createHandPanel() {
        handPanel = new JPanel(new BorderLayout());
        
        // Create player info panel
        JPanel playerInfoPanel = new JPanel(new FlowLayout());
        playerInfoPanel.setBorder(BorderFactory.createTitledBorder("Current Player"));
        
        // This will be updated in updateHandDisplay()
        
        // Create cards panel
        JPanel cardsPanel = new JPanel(new FlowLayout());
        cardsPanel.setBorder(BorderFactory.createTitledBorder("Your Hand - Click a card to select"));
        
        handButtons = new JButton[7]; // Max hand size
        for (int i = 0; i < handButtons.length; i++) {
            handButtons[i] = new JButton();
            handButtons[i].setPreferredSize(new Dimension(80, 120));
            handButtons[i].setVisible(false);
            handButtons[i].setBackground(Color.WHITE);
            handButtons[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            
            final int cardIndex = i;
            handButtons[i].addActionListener(event -> handleCardSelection(cardIndex));
            
            cardsPanel.add(handButtons[i]);
        }
        
        // Add both panels to handPanel
        handPanel.add(playerInfoPanel, BorderLayout.NORTH);
        handPanel.add(cardsPanel, BorderLayout.CENTER);
    }

    private void createStatusPanel() {
        statusPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel();
        sequenceLabel = new JLabel();
        
        // Add timer label
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(sequenceLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(timerLabel);
    }

    private void createLegendPanel() {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legend"));
        
        // Regular chips
        JPanel regularPanel = new JPanel(new FlowLayout());
        JLabel p1Legend = new JLabel("● " + players[0].getName());
        p1Legend.setForeground(PLAYER1_COLOR);
        p1Legend.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel p2Legend = new JLabel("● " + players[1].getName());
        p2Legend.setForeground(PLAYER2_COLOR);
        p2Legend.setFont(new Font("Arial", Font.BOLD, 12));
        
        regularPanel.add(p1Legend);
        regularPanel.add(Box.createHorizontalStrut(15));
        regularPanel.add(p2Legend);
        
        // Sequence chips
        JPanel sequencePanel = new JPanel(new FlowLayout());
        JLabel p1SeqLegend = new JLabel("★ " + players[0].getName() + " Sequence");
        p1SeqLegend.setForeground(PLAYER1_SEQUENCE_COLOR);
        p1SeqLegend.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel p2SeqLegend = new JLabel("★ " + players[1].getName() + " Sequence");
        p2SeqLegend.setForeground(PLAYER2_SEQUENCE_COLOR);
        p2SeqLegend.setFont(new Font("Arial", Font.BOLD, 12));
        
        sequencePanel.add(p1SeqLegend);
        sequencePanel.add(Box.createHorizontalStrut(10));
        sequencePanel.add(p2SeqLegend);
        
        // Other legend items
        JPanel otherPanel = new JPanel(new FlowLayout());
        JLabel cornerLegend = new JLabel("FREE = Corner (Wild)");
        cornerLegend.setForeground(Color.BLACK);
        
        JLabel validLegend = new JLabel("Green Border = Valid Move");
        validLegend.setForeground(Color.GREEN);
        
        otherPanel.add(cornerLegend);
        otherPanel.add(Box.createHorizontalStrut(15));
        otherPanel.add(validLegend);
        
        // Add all panels to legend
        legendPanel.add(regularPanel);
        legendPanel.add(sequencePanel);
        legendPanel.add(otherPanel);
        
        // Add legend to the right side
        add(legendPanel, BorderLayout.EAST);
    }
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem rules = new JMenuItem("Rules");
        JMenuItem exit = new JMenuItem("Exit");
        
        newGame.addActionListener(e -> startNewGame());
        rules.addActionListener(e -> showRules());
        exit.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGame);
        gameMenu.add(rules);
        gameMenu.addSeparator();
        gameMenu.add(exit);
        
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void handleCardSelection(int cardIndex) {
        if (gameOver || isAIThinking) return;
        
        // Prevent card selection during AI turn
        if (isAIGame && currentPlayerIndex == 1) {
            return;
        }
        
        Player currentPlayer = players[currentPlayerIndex];
        if (cardIndex >= currentPlayer.getHand().size()) return;
        
        Card chosenCard = currentPlayer.getHand().get(cardIndex);
        
        // Check if card is dead
        boolean isDead = board.isDeadCard(chosenCard, currentPlayer.getId());
        List<int[]> validMoves = board.getValidMoves(chosenCard, currentPlayer.getId());
        
        if (isDead || validMoves.isEmpty()) {
            // Handle dead card - discard and end turn
            handleDeadCardDiscard(chosenCard, currentPlayer);
            return;
        }
        
        // Normal card selection
        // Deselect previous card
        for (JButton button : handButtons) {
            if (button.getBorder() instanceof javax.swing.border.LineBorder) {
                javax.swing.border.LineBorder border = (javax.swing.border.LineBorder) button.getBorder();
                if (border.getLineColor().equals(Color.GREEN)) {
                    button.setBorder(UIManager.getBorder("Button.border"));
                }
            }
        }
        
        // Select new card
        selectedCard = chosenCard;
        handButtons[cardIndex].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        
        statusLabel.setText(currentPlayer.getName() + " - Selected: " + selectedCard + " - Click on board to play");
        
        // Update board to show valid moves
        updateBoardDisplay();
    }

    private void handleDeadCardDiscard(Card deadCard, Player currentPlayer) {
        // Show confirmation dialog
        int choice = JOptionPane.showConfirmDialog(this, 
            "This card (" + deadCard + ") is DEAD and has no valid moves.\n" +
            "Do you want to discard it and end your turn?",
            "Discard Dead Card", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Stop current timer
            stopTurnTimer();
            
            // Remove the dead card from hand
            currentPlayer.removeCard(deadCard);
            
            // Draw a new card if deck has cards
            Card newCard = deck.drawCard();
            if (newCard != null) {
                currentPlayer.addCard(newCard);
            }
            
            // Clear selection
            selectedCard = null;
            
            // Show message about what happened
            statusLabel.setText(currentPlayer.getName() + " discarded dead card: " + deadCard);
            
            // End turn - switch to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            
            // Update display
            updateDisplay();
            
            // Brief pause to show the message, then start appropriate turn
            Timer timer = new Timer(1500, e -> {
                if (isAIGame && currentPlayerIndex == 1) {
                    // AI's turn
                    scheduleAIMove();
                } else {
                    // Human's turn
                    startTurnTimer();
                    updateStatusDisplay();
                }
                ((Timer)e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        }
}
    private void handleCellClick(int row, int col) {
        if (selectedCard == null || gameOver || isAIThinking) return;
        
        // Only allow human interaction on human turns
        if (isAIGame && currentPlayerIndex == 1) {
            return; // AI's turn, ignore human clicks
        }
        
        Player currentPlayer = players[currentPlayerIndex];
        
        // Special handling for one-eyed Jacks trying to remove sequence chips
        boolean isOneEyedJack = selectedCard.isJack() && 
                (selectedCard.getSuit().equals("Spades") || selectedCard.getSuit().equals("Clubs"));
        
        if (isOneEyedJack) {
            Integer chipOwner = board.getChipAt(row, col);
            if (chipOwner != null && chipOwner != currentPlayer.getId()) {
                if (isCellPartOfSequence(row, col, chipOwner)) {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot remove this chip!\nIt's part of a completed sequence.", 
                        "Protected Sequence Chip", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
        
        if (!isValidMove(selectedCard, row, col)) {
            JOptionPane.showMessageDialog(this, "Invalid move! Try another position.", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Play the card
        boolean success = board.playCard(currentPlayer, selectedCard, row, col);
        if (success) {
            executeSuccessfulMove(currentPlayer, selectedCard);
        }
    }

    private void executeSuccessfulMove(Player currentPlayer, Card playedCard) {
        // Stop the timer for successful move
        stopTurnTimer();
        
        // Remove card from hand and draw new one
        currentPlayer.removeCard(playedCard);
        Card newCard = deck.drawCard();
        if (newCard != null) {
            currentPlayer.addCard(newCard);
        }
        
        selectedCard = null;
        
        // Refresh the entire board to update sequence highlighting
        updateBoardDisplay();
        
        // Check for win
        if (board.hasPlayerWon(currentPlayer.getId())) {
            gameOver = true;
            stopTurnTimer();
            JOptionPane.showMessageDialog(this, 
                currentPlayer.getName() + " WINS!\nSequences: " + board.countSequences(currentPlayer.getId()),
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Next player's turn
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            
            // Start next turn
            if (isAIGame && currentPlayerIndex == 1) {
                // AI's turn
                scheduleAIMove();
            } else {
                // Human's turn
                startTurnTimer();
            }
        }
        
        updateDisplay();
    }

    private void scheduleAIMove() {
        if (gameOver) return;
        
        isAIThinking = true;
        statusLabel.setText(players[1].getName() + " is thinking...");
        
        // Add thinking delay based on AI level
        int thinkingTime = getAIThinkingTime();
        
        Timer aiTimer = new Timer(thinkingTime, e -> {
            executeAIMove();
            ((Timer)e.getSource()).stop();
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    private int getAIThinkingTime() {
        if (aiPlayer == null) return 1000;
        
        switch (aiPlayer.getName()) {
            case "Suppandi": return 500;          // Quick random moves
            case "Chacha Chaudhary": return 1500; // Medium thinking
            case "Chanakya": return 2500;         // Deep thinking
            default: return 1000;
        }
    }

    private void executeAIMove() {
        if (gameOver) return;
        
        Player currentAIPlayer = players[1];
        Player humanPlayer = players[0];
        
        // Get AI's move choice
        AIMove aiMove = aiPlayer.chooseMove(board, currentAIPlayer, humanPlayer);
        
        if (aiMove.isDiscard()) {
            // AI is discarding a dead card
            executeAIDiscard(currentAIPlayer, aiMove.getCard());
        } else {
            // AI is playing a card
            executeAICardPlay(currentAIPlayer, aiMove);
        }
        
        isAIThinking = false;
    }

    private void executeAICardPlay(Player aiPlayerObj, AIMove aiMove) {
        Card playedCard = aiMove.getCard();
        int row = aiMove.getRow();
        int col = aiMove.getCol();
        
        // Highlight AI's move briefly
        highlightAIMove(playedCard, row, col);
        
        // Execute the move
        boolean success = board.playCard(aiPlayerObj, playedCard, row, col);
        if (success) {
            executeSuccessfulMove(aiPlayerObj, playedCard);
        } else {
            // Fallback if AI move fails - discard the card
            executeAIDiscard(aiPlayerObj, playedCard);
        }
    }

    private void executeAIDiscard(Player aiPlayerObj, Card deadCard) {
        // Remove the dead card from hand
        aiPlayerObj.removeCard(deadCard);
        
        // Draw a new card if deck has cards
        Card newCard = deck.drawCard();
        if (newCard != null) {
            aiPlayerObj.addCard(newCard);
        }
        
        // Show what AI did
        statusLabel.setText(aiPlayerObj.getName() + " discarded: " + deadCard);
        
        // End turn - switch to human
        currentPlayerIndex = 0;
        
        // Update display
        updateDisplay();
        
        // Brief pause then start human turn
        Timer timer = new Timer(1500, e -> {
            startTurnTimer();
            updateStatusDisplay();
            ((Timer)e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void highlightAIMove(Card playedCard, int row, int col) {
        // Briefly highlight the AI's move
        statusLabel.setText(players[1].getName() + " played " + playedCard + " at (" + row + "," + col + ")");
        
        // You could add visual highlighting of the cell here if desired
        JPanel cell = cellPanels[row][col];
        Color originalColor = cell.getBackground();
        cell.setBackground(Color.YELLOW);
        
        Timer highlightTimer = new Timer(800, e -> {
            cell.setBackground(originalColor);
            updateCellDisplay(row, col);
            ((Timer)e.getSource()).stop();
        });
        highlightTimer.setRepeats(false);
        highlightTimer.start();
    }

    private boolean isValidMove(Card card, int row, int col) {
        List<int[]> validMoves = board.getValidMoves(card, players[currentPlayerIndex].getId());
        for (int[] move : validMoves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }

    private void updateDisplay() {
        updateBoardDisplay();
        updateHandDisplay();
        updateStatusDisplay();
        
        // Start timer for the first player if game just started
        if (turnTimer == null && !gameOver) {
            if (isAIGame && currentPlayerIndex == 1) {
                scheduleAIMove();
            } else {
                startTurnTimer();
            }
        }
    }
    
    private void updateBoardDisplay() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                updateCellDisplay(row, col);
            }
        }
    }

    private void updateCellDisplay(int row, int col) {
        JPanel panel = cellPanels[row][col];
        panel.removeAll();
        panel.setLayout(new BorderLayout());
        
        // Get actual board state
        String cardName = board.getCardAt(row, col);
        Integer chipOwner = board.getChipAt(row, col);
        boolean isCorner = board.isCornerAt(row, col);
        
        // Determine background color based on chip ownership and sequence status
        Color bgColor = EMPTY_COLOR;
        Color textColor = Color.BLACK;
        
        if (chipOwner != null) {
            // Check if this chip is part of a completed sequence
            boolean isPartOfSequence = isCellPartOfSequence(row, col, chipOwner);
            
            if (isPartOfSequence) {
                // Use special sequence colors
                bgColor = (chipOwner == 1) ? PLAYER1_SEQUENCE_COLOR : PLAYER2_SEQUENCE_COLOR;
                textColor = Color.WHITE; // White text on sequence background
            } else {
                // Use regular chip colors
                bgColor = (chipOwner == 1) ? PLAYER1_COLOR : PLAYER2_COLOR;
                textColor = Color.WHITE;
            }
        } else if (isCorner) {
            // Corner cell
            bgColor = CORNER_COLOR;
            textColor = Color.BLACK;
        }
        
        // Highlight valid moves with green border
        if (selectedCard != null && isValidMove(selectedCard, row, col)) {
            panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        } else {
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }
        
        panel.setBackground(bgColor);
        
        // Create the display text
        String displayText;
        if (isCorner) {
            displayText = "FREE";
        } else {
            displayText = formatCardName(cardName);
        }
        
        // Add the card name label
        JLabel cardLabel = new JLabel(displayText, SwingConstants.CENTER);
        cardLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Smaller font to make room for bigger chip
        
        // If there's a chip, add chip indicator
        if (chipOwner != null) {
            // Check if it's part of a sequence for special styling
            boolean isPartOfSequence = isCellPartOfSequence(row, col, chipOwner);
            
            String chipSymbol = isPartOfSequence ? "★" : "●"; // Star for sequence, circle for regular
            JLabel chipLabel = new JLabel(chipSymbol, SwingConstants.CENTER);
            chipLabel.setFont(new Font("Arial", Font.BOLD, isPartOfSequence ? 28 : 32)); // BIGGER CHIPS
            chipLabel.setForeground(Color.WHITE);
            
            // Use a layered approach - card name on top, BIG chip symbol in center
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            textPanel.add(cardLabel, BorderLayout.NORTH);
            textPanel.add(chipLabel, BorderLayout.CENTER);
            
            panel.add(textPanel, BorderLayout.CENTER);
        } else {
            // No chip - just show card name
            cardLabel.setForeground(textColor);
            panel.add(cardLabel, BorderLayout.CENTER);
        }
        
        panel.revalidate();
        panel.repaint();
    }

    private String formatCardName(String cardName) {
        if (cardName == null || cardName.isEmpty() || cardName.equals("*")) {
            return "";
        }
        
        // Convert card format like "2H" to "2♥" with proper colors
        if (cardName.length() >= 2) {
            String rank = cardName.substring(0, cardName.length() - 1);
            String suit = cardName.substring(cardName.length() - 1);
            
            String suitSymbol;
            String color;
            switch (suit) {
                case "H": 
                    suitSymbol = "♥"; 
                    color = "red";
                    break;
                case "D": 
                    suitSymbol = "♦"; 
                    color = "red";
                    break;
                case "C": 
                    suitSymbol = "♣"; 
                    color = "black";
                    break;
                case "S": 
                    suitSymbol = "♠"; 
                    color = "black";
                    break;
                default: 
                    suitSymbol = suit;
                    color = "black";
            }
            
            return "<html><font color='" + color + "'>" + rank + suitSymbol + "</font></html>";
        }
        
        return cardName;
    }

    private void updateHandDisplay() {
        Player currentPlayer = players[currentPlayerIndex];
        
        // Update player info panel
        JPanel playerInfoPanel = (JPanel) handPanel.getComponent(0);
        playerInfoPanel.removeAll();
        
        // Create player indicator
        JLabel playerLabel = new JLabel("● " + currentPlayer.getName() + "'s Turn");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerLabel.setForeground(currentPlayer.getId() == 1 ? PLAYER1_COLOR : PLAYER2_COLOR);
        
        // Add sequence count
        JLabel seqLabel = new JLabel("Sequences: " + board.countSequences(currentPlayer.getId()) + "/2");
        seqLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        playerInfoPanel.add(playerLabel);
        playerInfoPanel.add(Box.createHorizontalStrut(20));
        playerInfoPanel.add(seqLabel);
        playerInfoPanel.revalidate();
        
        // Show cards based on game type and current player
        if (isAIGame && currentPlayerIndex == 1) {
            // AI turn - show card backs only
            showAIHand(currentPlayer);
        } else {
            // Human turn - show actual cards
            showHumanHand(currentPlayer);
        }
        
        handPanel.revalidate();
    }

    private void showHumanHand(Player player) {
        List<Card> hand = player.getHand();
        
        for (int i = 0; i < handButtons.length; i++) {
            if (i < hand.size()) {
                Card card = hand.get(i);
                
                handButtons[i].setBackground(Color.WHITE);
                handButtons[i].setVisible(true);
                handButtons[i].setEnabled(true);
                
                // Check if card is dead or has valid moves
                List<int[]> validMoves = board.getValidMoves(card, player.getId());
                boolean isDead = board.isDeadCard(card, player.getId());
                
                if (isDead || validMoves.isEmpty()) {
                    handButtons[i].setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    handButtons[i].setToolTipText("DEAD CARD - Click to discard and end turn");
                    
                    String suitColor = getSuitColor(card.getSuit()).equals(Color.RED) ? "red" : "black";
                    handButtons[i].setText("<html><center><font color='" + suitColor + "'>" + 
                        card.getRank() + "<br>" + getSuitSymbol(card.getSuit()) + 
                        "</font><br><font color='red'><b>DEAD</b></font></center></html>");
                } else {
                    handButtons[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    handButtons[i].setToolTipText("Click to select - " + validMoves.size() + " valid moves");
                    
                    String suitColor = getSuitColor(card.getSuit()).equals(Color.RED) ? "red" : "black";
                    handButtons[i].setText("<html><center><font color='" + suitColor + "'>" + 
                        card.getRank() + "<br>" + getSuitSymbol(card.getSuit()) + "</font></center></html>");
                }
            } else {
                handButtons[i].setVisible(false);
            }
        }
    }

    private void showAIHand(Player aiPlayerObj) {
        List<Card> hand = aiPlayerObj.getHand();
        
        for (int i = 0; i < handButtons.length; i++) {
            if (i < hand.size()) {
                handButtons[i].setBackground(new Color(100, 100, 200));
                handButtons[i].setVisible(true);
                handButtons[i].setEnabled(false); // Can't click AI cards
                handButtons[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                handButtons[i].setText("<html><center><font color='white'>?<br>?</font></center></html>");
                handButtons[i].setToolTipText("AI Card (Hidden)");
            } else {
                handButtons[i].setVisible(false);
            }
        }
    }

    private void updateStatusDisplay() {
        if (gameOver) {
            statusLabel.setText("Game Over!");
        } else {
            Player currentPlayer = players[currentPlayerIndex];
            if (selectedCard != null) {
                statusLabel.setText(currentPlayer.getName() + "'s Turn - Selected: " + selectedCard + " - Click on board to play");
            } else {
                statusLabel.setText(currentPlayer.getName() + "'s Turn - Select a card from your hand");
            }
        }
        
        // Update sequence counts
        String seqText = "Sequences - " + players[0].getName() + ": " + 
                        board.countSequences(1) + "/2, " + 
                        players[1].getName() + ": " + 
                        board.countSequences(2) + "/2";
        sequenceLabel.setText(seqText);
    }


    private String getSuitSymbol(String suit) {
        switch (suit) {
            case "Hearts": return "♥";
            case "Diamonds": return "♦";
            case "Clubs": return "♣";
            case "Spades": return "♠";
            default: return suit.substring(0, 1);
        }
    }

        private Color getSuitColor(String suit) {
        switch (suit) {
            case "Hearts":
            case "Diamonds":
                return Color.RED;   // Hearts and Diamonds are red
            case "Clubs":
            case "Spades":
                return Color.BLACK; // Clubs and Spades are black
            default:
                return Color.BLACK;
        }
    }


    private boolean isCellPartOfSequence(int row, int col, int playerId) {
        // Get all valid sequences for this player
        List<List<int[]>> validSequences = getValidSequencesForPlayer(playerId);
        
        // Check if this cell is part of any valid sequence
        for (List<int[]> sequence : validSequences) {
            for (int[] pos : sequence) {
                if (pos[0] == row && pos[1] == col) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // Add this helper method to SequenceGameGUI.java
    private List<List<int[]>> getValidSequencesForPlayer(int playerId) {
        // This mimics the logic from Board.java but returns the actual sequences
        List<List<int[]>> allSequences = new ArrayList<>();
        
        int[][] directions = {
            {0, 1}, {1, 0}, {1, 1}, {1, -1}
        };
        
        // Find all possible sequences
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                for (int[] dir : directions) {
                    List<int[]> sequence = getSequenceFromGUI(r, c, dir[0], dir[1], playerId);
                    if (sequence != null && sequence.size() == 5) {
                        if (!isDuplicateSequenceGUI(allSequences, sequence)) {
                            allSequences.add(sequence);
                        }
                    }
                }
            }
        }
        
        // Filter to get valid sequences (max 1 shared chip)
        List<List<int[]>> validSequences = new ArrayList<>();
        boolean[] used = new boolean[allSequences.size()];
        
        for (int i = 0; i < allSequences.size(); i++) {
            if (used[i]) continue;
            
            List<int[]> currentSeq = allSequences.get(i);
            validSequences.add(currentSeq);
            used[i] = true;
            
            for (int j = i + 1; j < allSequences.size(); j++) {
                if (used[j]) continue;
                
                List<int[]> otherSeq = allSequences.get(j);
                if (countSharedChipsGUI(currentSeq, otherSeq) > 1) {
                    used[j] = true;
                }
            }
        }
        
        return validSequences;
    }

    // Helper methods for GUI
    private List<int[]> getSequenceFromGUI(int startRow, int startCol, int deltaR, int deltaC, int playerId) {
        List<int[]> sequence = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            int r = startRow + i * deltaR;
            int c = startCol + i * deltaC;
            
            if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) return null;
            
            Integer chipOwner = board.getChipAt(r, c);
            boolean isCorner = board.isCornerAt(r, c);
            
            if (!((chipOwner != null && chipOwner == playerId) || isCorner)) {
                return null;
            }
            
            sequence.add(new int[]{r, c});
        }
        
        // Check no chip before
        int beforeR = startRow - deltaR;
        int beforeC = startCol - deltaC;
        if (beforeR >= 0 && beforeR < BOARD_SIZE && beforeC >= 0 && beforeC < BOARD_SIZE) {
            Integer beforeChip = board.getChipAt(beforeR, beforeC);
            boolean beforeCorner = board.isCornerAt(beforeR, beforeC);
            if ((beforeChip != null && beforeChip == playerId) || beforeCorner) {
                return null;
            }
        }
        
        return sequence;
    }

    private int countSharedChipsGUI(List<int[]> seq1, List<int[]> seq2) {
        int sharedCount = 0;
        for (int[] pos1 : seq1) {
            for (int[] pos2 : seq2) {
                if (pos1[0] == pos2[0] && pos1[1] == pos2[1]) {
                    sharedCount++;
                }
            }
        }
        return sharedCount;
    }

    private void startTurnTimer() {
        if (gameOver) return;
        
        timeLeft = TURN_TIME_SECONDS;
        updateTimerDisplay();
        
        // Stop any existing timer
        if (turnTimer != null) {
            turnTimer.stop();
        }
        
        // Create new timer that ticks every second
        turnTimer = new Timer(1000, e -> {
            timeLeft--;
            updateTimerDisplay();
            
            if (timeLeft <= 0) {
                handleTimeOut();
            }
        });
        
        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
        timerLabel.setText("");
    }

    private void updateTimerDisplay() {
        if (timeLeft <= 5) {
            timerLabel.setForeground(Color.RED);
            timerLabel.setText("⏰ TIME: " + timeLeft + "s");
        } else {
            timerLabel.setForeground(Color.ORANGE);
            timerLabel.setText("⏰ Time: " + timeLeft + "s");
        }
    }

    private void handleTimeOut() {
        stopTurnTimer();
        
        Player currentPlayer = players[currentPlayerIndex];
        
        // Clear any selected card
        selectedCard = null;
        
        // Clear card selection borders
        for (JButton button : handButtons) {
            if (button.getBorder() instanceof javax.swing.border.LineBorder) {
                javax.swing.border.LineBorder border = (javax.swing.border.LineBorder) button.getBorder();
                if (border.getLineColor().equals(Color.GREEN)) {
                    button.setBorder(UIManager.getBorder("Button.border"));
                }
            }
        }
        
        // Show timeout message
        JOptionPane.showMessageDialog(this, 
            currentPlayer.getName() + "'s time is up!\nTurn skipped.", 
            "Time Out!", 
            JOptionPane.WARNING_MESSAGE);
        
        // Move to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        
        // Update display and start appropriate turn
        updateDisplay();
        
        if (isAIGame && currentPlayerIndex == 1) {
            // AI's turn
            scheduleAIMove();
        } else {
            // Human's turn
            startTurnTimer();
        }
    }
    private boolean isDuplicateSequenceGUI(List<List<int[]>> existingSequences, List<int[]> newSequence) {
        for (List<int[]> existing : existingSequences) {
            if (existing.size() != newSequence.size()) continue;
            
            boolean allMatch = true;
            for (int i = 0; i < existing.size(); i++) {
                int[] existingPos = existing.get(i);
                int[] newPos = newSequence.get(i);
                if (existingPos[0] != newPos[0] || existingPos[1] != newPos[1]) {
                    allMatch = false;
                    break;
                }
            }
            
            if (allMatch) return true;
        }
        return false;
    }

    private GameMode selectGameMode() {
        String[] options = {
            "Human vs Human",
            "Human vs Suppandi (Easy)",
            "Human vs Chacha Chaudhary (Medium)", 
            "Human vs Chanakya (Hard)"
        };
        
        int choice = JOptionPane.showOptionDialog(
            this,
            "Select Game Mode:",
            "Game Mode",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        switch (choice) {
            case 1: return new GameMode(true, AIPlayer.AILevel.EASY);
            case 2: return new GameMode(true, AIPlayer.AILevel.MEDIUM);
            case 3: return new GameMode(true, AIPlayer.AILevel.HARD);
            default: return new GameMode(false, null);
        }
        }

    // Helper class for game mode
    private static class GameMode {
        boolean isAI;
        AIPlayer.AILevel aiLevel;
        
        GameMode(boolean isAI, AIPlayer.AILevel aiLevel) {
            this.isAI = isAI;
            this.aiLevel = aiLevel;
        }
    }


    private void startNewGame() {
        stopTurnTimer();
        dispose();
        new SequenceGameGUI().setVisible(true);
    }

    private void showRules() {
        String rules = """
        SEQUENCE GAME RULES:
        
        Goal: Be the first to get 2 sequences of 5 chips in a row!
        
        How to Play:
        1. You have 15 seconds per turn to make a move
        2. Click a card from your hand to select it
        3. Click on the board where you want to play it
        4. Normal cards must match the board position
        5. Jacks are special:
        • Hearts/Diamonds Jacks = Wild (place anywhere)
        • Spades/Clubs Jacks = Remove opponent's chip
            (EXCEPT chips that are part of completed sequences)
        6. Corners count as free spaces for sequences
        7. Completed sequence chips are PROTECTED and cannot be removed
        8. If time runs out, your turn is skipped!
        
        Winning: Get 2 sequences of 5 chips in a row (horizontal, vertical, or diagonal)
        """;
        
        JOptionPane.showMessageDialog(this, rules, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        try {
            // FIXED LINE
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        new SequenceGameGUI().setVisible(true);
    });
}
}