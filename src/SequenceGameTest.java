public class SequenceGameTest {
    public static void main(String[] args) {
        // Create the board
        Board board = new Board();

        // Create two players
        Player player1 = new Player(1, "Alice");
        Player player2 = new Player(2, "Bob");

        // Place some chips for Player 1 to create a sequence
        board.placeChip(0, 0, player1.getId()); // Corner
        board.placeChip(0, 1, player1.getId());
        board.placeChip(0, 2, player1.getId());
        board.placeChip(0, 3, player1.getId());
        board.placeChip(0, 4, player1.getId());

        // Place some chips for Player 2
        board.placeChip(5, 5, player2.getId());
        board.placeChip(6, 5, player2.getId());
        board.placeChip(7, 5, player2.getId());
        board.placeChip(8, 5, player2.getId());
        board.placeChip(9, 5, player2.getId());

        // Print board state
        System.out.println("Current Board:");
        board.printBoard();

        // Check sequences using the new method names
        int p1Sequences = board.countSequences(player1.getId());
        System.out.println(player1.getName() + " has " + p1Sequences + " sequence(s)");
        System.out.println(player1.getName() + " has won? " + board.hasPlayerWon(player1.getId()));

        int p2Sequences = board.countSequences(player2.getId());
        System.out.println(player2.getName() + " has " + p2Sequences + " sequence(s)");
        System.out.println(player2.getName() + " has won? " + board.hasPlayerWon(player2.getId()));

        // Test card playing
        System.out.println("\n--- Testing Card Play ---");
        
        // Create some test cards
        Card testCard = new Card("6", "Diamonds"); // Should match 6D on the board
        Card jackHearts = new Card("J", "Hearts");  // Two-eyed jack (wild)
        Card jackSpades = new Card("J", "Spades");  // One-eyed jack (removal)

        System.out.println("\nTesting normal card play:");
        boolean success1 = board.playCard(player1, testCard, 0, 1);
        System.out.println("Playing " + testCard + " at (0,1): " + success1);

        System.out.println("\nTesting wild jack:");
        boolean success2 = board.playCard(player2, jackHearts, 1, 1);
        System.out.println("Playing " + jackHearts + " (wild) at (1,1): " + success2);

        System.out.println("\nTesting removal jack:");
        boolean success3 = board.playCard(player1, jackSpades, 1, 1);
        System.out.println("Playing " + jackSpades + " (removal) at (1,1): " + success3);

        System.out.println("\nBoard after card plays:");
        board.printBoard();

        // Test dead card detection
        System.out.println("\n--- Testing Dead Card Detection ---");
        Card testCard2 = new Card("2", "Hearts");
        boolean isDead = board.isDeadCard(testCard2, player1.getId());
        System.out.println("Is " + testCard2 + " dead for player 1? " + isDead);

        // Show valid moves for a card
        System.out.println("\n--- Testing Valid Moves ---");
        java.util.List<int[]> validMoves = board.getValidMoves(testCard, player1.getId());
        System.out.println("Valid moves for " + testCard + ":");
        for (int[] move : validMoves) {
            System.out.println("  Row " + move[0] + ", Col " + move[1]);
        }
    }
}