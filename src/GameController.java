import java.util.Scanner;
import java.util.List;

public class GameController {
    private Board board;
    private Deck deck;
    private Player[] players;
    private int currentPlayerIndex;
    private Scanner scanner;

    public GameController() {
        board = new Board();
        deck = new Deck();
        scanner = new Scanner(System.in);

        // Initialize players
        players = new Player[2];
        players[0] = new Player(1, "Alice");
        players[1] = new Player(2, "Bob");

        // Deal initial hands (7 cards each)
        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    p.addCard(card);
                }
            }
        }

        currentPlayerIndex = 0;
    }

    public void startGame() {
        boolean gameOver = false;

        System.out.println("Welcome to Sequence!");
        System.out.println("Goal: Get 2 sequences of 5 chips in a row to win!");
        System.out.println("Jacks: Hearts/Diamonds = Wild, Spades/Clubs = Remove opponent chip");
        
        while (!gameOver && !deck.isEmpty()) {
            Player currentPlayer = players[currentPlayerIndex];
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Current Board:");
            board.printBoard();

            System.out.println("\n" + currentPlayer.getName() + "'s turn");
            System.out.println("Sequences: " + board.countSequences(currentPlayer.getId()) + "/2");
            
            // Show current player's hand with valid moves
            displayPlayerOptions(currentPlayer);

            // Get player's card choice
            int cardIndex = getCardChoice(currentPlayer);
            if (cardIndex == -1) {
                nextTurn();
                continue;
            }

            Card chosenCard = currentPlayer.getHand().get(cardIndex);
            
            // Show valid moves for chosen card
            List<int[]> validMoves = board.getValidMoves(chosenCard, currentPlayer.getId());
            if (validMoves.isEmpty()) {
                System.out.println("No valid moves for this card! Choose a different card.");
                continue;
            }

            System.out.println("\nValid moves for " + chosenCard + ":");
            for (int i = 0; i < validMoves.size(); i++) {
                int[] move = validMoves.get(i);
                System.out.println(i + ": Row " + move[0] + ", Col " + move[1]);
            }

            // Get move choice
            int[] targetPosition = getMoveChoice(validMoves);
            if (targetPosition == null) {
                continue;
            }

            // Try to play the card
            boolean success = board.playCard(currentPlayer, chosenCard, targetPosition[0], targetPosition[1]);
            if (!success) {
                System.out.println("Invalid move! Try again.");
                continue;
            }

            System.out.println("Played " + chosenCard + " at (" + targetPosition[0] + "," + targetPosition[1] + ")");

            // Remove the card from hand and draw a new one
            currentPlayer.removeCard(chosenCard);
            Card newCard = deck.drawCard();
            if (newCard != null) {
                currentPlayer.addCard(newCard);
            }

            // Check for win
            if (board.hasPlayerWon(currentPlayer.getId())) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println(currentPlayer.getName() + " WINS!");
                System.out.println("Final sequences: " + board.countSequences(currentPlayer.getId()));
                board.printBoard();
                gameOver = true;
            } else {
                nextTurn();
            }
        }

        if (!gameOver) {
            System.out.println("Game over - no more cards in deck!");
        }
        
        scanner.close();
    }

    private void displayPlayerOptions(Player player) {
        System.out.println("\nYour hand:");
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            int validMoveCount = board.getValidMoves(card, player.getId()).size();
            String status = validMoveCount > 0 ? "(" + validMoveCount + " moves)" : "(DEAD)";
            System.out.println(i + ": " + card + " " + status);
        }
    }

    private int getCardChoice(Player player) {
        while (true) {
            try {
                System.out.print("\nChoose card index (0-" + (player.getHand().size() - 1) + ") or -1 to see board: ");
                int choice = scanner.nextInt();
                
                if (choice == -1) {
                    board.printBoard();
                    continue;
                }
                
                if (choice >= 0 && choice < player.getHand().size()) {
                    Card chosenCard = player.getHand().get(choice);
                    if (board.getValidMoves(chosenCard, player.getId()).isEmpty()) {
                        System.out.println("That card has no valid moves! Choose another.");
                        continue;
                    }
                    return choice;
                }
                System.out.println("Invalid choice! Try again.");
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private int[] getMoveChoice(List<int[]> validMoves) {
        while (true) {
            try {
                System.out.print("Choose move (0-" + (validMoves.size() - 1) + "): ");
                int choice = scanner.nextInt();
                
                if (choice >= 0 && choice < validMoves.size()) {
                    return validMoves.get(choice);
                }
                System.out.println("Invalid choice! Try again.");
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }

    public static void main(String[] args) {
        GameController game = new GameController();
        game.startGame();
    }
}