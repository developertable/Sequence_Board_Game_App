import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int SIZE = 10;
    private final Cell[][] grid;

    // Nested class to represent each cell on the board
    private static class Cell {
        String card;   // e.g., "2H" for 2 of Hearts, "JC" for Jack of Clubs
        Integer chip;  // null if empty, otherwise player ID who placed chip
        boolean isCorner;

        Cell(String card, boolean isCorner) {
            this.card = card;
            this.chip = null;
            this.isCorner = isCorner;
        }
    }

    // Add these methods to your Board.java class:

    public String getCardAt(int row, int col) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            return grid[row][col].card;
        }
        return "";
    }

    public Integer getChipAt(int row, int col) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            return grid[row][col].chip;
        }
        return null;
    }

    public boolean isCornerAt(int row, int col) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            return grid[row][col].isCorner;
        }
        return false;
    }

    public Board() {
        grid = new Cell[SIZE][SIZE];
        initializeBoard();
    }

    // Initialize the board with correct Sequence layout
    private void initializeBoard() {
        // Correct Sequence board layout (10x10) - Jacks are not on the board
        String[][] layout = {
            {"*",  "6D", "7D", "8D", "9D", "10D","QD", "KD", "AD", "*"},
            {"5D", "3H", "2H", "2S", "3S", "4S", "5S", "6S", "7S", "AC"},
            {"4D", "4H", "KD", "AD", "AC", "KC", "QC", "10C", "8S", "KC"},
            {"3D", "5H", "QD", "QH", "10H", "9H", "8H", "9C", "9S", "QC"},
            {"2D", "6H", "10D", "KH", "3H", "2H", "7H", "8C", "10S", "10C"},
            {"AS", "7H", "9D", "AH", "4H", "5H", "6H", "7C", "QS", "9C"},
            {"KS", "8H", "8D", "2C", "3C", "4C", "5C", "6C", "KS", "8C"},
            {"QS", "9H", "7D", "6D", "5D", "4D", "3D", "2D", "AS", "7C"},
            {"10S","10H", "QH", "KH", "AH", "2C", "3C", "4C", "5C", "6C"},
            {"*",  "9S", "8S", "7S", "6S", "5S", "4S", "3S", "2S", "*"}
        };

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                boolean corner = layout[r][c].equals("*");
                grid[r][c] = new Cell(layout[r][c], corner);
            }
        }
    }

    // Place a chip for playerId at row, col if allowed
    public boolean placeChip(int row, int col, int playerId) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;

        Cell cell = grid[row][col];
        if (cell.chip != null) return false; // cell already occupied

        // Players can always place on corner "joker" cells or empty cells
        if (cell.isCorner || cell.chip == null) {
            cell.chip = playerId;
            return true;
        }
        return false;
    }

    // Play a card according to Sequence rules
    public boolean playCard(Player player, Card card, int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;

        // Two-eyed Jack (wild placement) - Hearts or Diamonds
        boolean isTwoEyedJack = card.isJack() &&
                (card.getSuit().equals("Hearts") || card.getSuit().equals("Diamonds"));

        // One-eyed Jack (remove opponent's chip) - Spades or Clubs
        boolean isOneEyedJack = card.isJack() &&
                (card.getSuit().equals("Spades") || card.getSuit().equals("Clubs"));

        if (isTwoEyedJack) {
            // Can place anywhere that's not already occupied by a chip
            Cell cell = grid[row][col];
            if (cell.chip == null) {
                return placeChip(row, col, player.getId());
            }
            return false;
        } else if (isOneEyedJack) {
            return removeOpponentChip(row, col, player.getId());
        } else {
            // Normal card play - must match the board card exactly
            Cell cell = grid[row][col];
            String cardString = card.getRank() + card.getSuit().charAt(0); // e.g., "2H"
            
            if (cell.card.equals(cardString) && cell.chip == null && !cell.isCorner) {
                return placeChip(row, col, player.getId());
            }
        }
        return false;
    }

    private boolean removeOpponentChip(int row, int col, int playerId) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;
        Cell cell = grid[row][col];
        
        // Can't remove from corners or your own chips
        if (cell.chip == null || cell.chip == playerId || cell.isCorner) {
            return false;
        }
        
        // Check if this chip is part of a completed sequence - if so, protect it
        if (isChipPartOfSequence(row, col, cell.chip)) {
            return false; // Cannot remove chips that are part of completed sequences
        }
        
        // Can remove opponent's chip that's not part of a sequence
        cell.chip = null;
        return true;
    }

    // Add this new helper method to Board.java
    private boolean isChipPartOfSequence(int row, int col, int playerId) {
        // Check all four directions from this cell
        int[][] directions = {
            {0, 1},   // horizontal right
            {1, 0},   // vertical down
            {1, 1},   // diagonal down-right
            {1, -1}   // diagonal down-left
        };
        
        for (int[] dir : directions) {
            // Check forward and backward in this direction to find sequences of 5
            for (int startOffset = -4; startOffset <= 0; startOffset++) {
                int sequenceCount = 0;
                boolean thisPositionInSequence = false;
                
                for (int i = 0; i < 5; i++) {
                    int checkRow = row + (startOffset + i) * dir[0];
                    int checkCol = col + (startOffset + i) * dir[1];
                    
                    if (checkRow >= 0 && checkRow < SIZE && 
                        checkCol >= 0 && checkCol < SIZE) {
                        
                        Cell checkCell = grid[checkRow][checkCol];
                        
                        if ((checkCell.chip != null && checkCell.chip == playerId) || checkCell.isCorner) {
                            sequenceCount++;
                            if (checkRow == row && checkCol == col) {
                                thisPositionInSequence = true;
                            }
                        } else {
                            break; // Sequence broken
                        }
                    } else {
                        break; // Out of bounds
                    }
                }
                
                // If we found a sequence of 5 and this position is part of it
                if (sequenceCount == 5 && thisPositionInSequence) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // Check if player has won (needs 2 sequences of 5)
    public boolean hasPlayerWon(int playerId) {
        return countSequences(playerId) >= 2;
    }

    // Count the number of sequences of 5 for a player
    public int countSequences(int playerId) {
        List<List<int[]>> allSequences = findAllValidSequences(playerId);
        return allSequences.size();
    }

    // Find all valid sequences considering the sharing rule
    private List<List<int[]>> findAllValidSequences(int playerId) {
        List<List<int[]>> validSequences = new ArrayList<>();
        List<List<int[]>> allPossibleSequences = findAllPossibleSequences(playerId);
        
        // Use a greedy approach to select non-overlapping sequences (max 1 shared chip)
        boolean[] used = new boolean[allPossibleSequences.size()];
        
        for (int i = 0; i < allPossibleSequences.size(); i++) {
            if (used[i]) continue;
            
            List<int[]> currentSeq = allPossibleSequences.get(i);
            validSequences.add(currentSeq);
            used[i] = true;
            
            // Mark sequences that share more than 1 chip as unusable
            for (int j = i + 1; j < allPossibleSequences.size(); j++) {
                if (used[j]) continue;
                
                List<int[]> otherSeq = allPossibleSequences.get(j);
                if (countSharedChips(currentSeq, otherSeq) > 1) {
                    used[j] = true; // Can't use this sequence
                }
            }
        }
        
        return validSequences;
    }

    // Find all possible sequences of exactly 5 chips
    private List<List<int[]>> findAllPossibleSequences(int playerId) {
        List<List<int[]>> allSequences = new ArrayList<>();
        
        int[][] directions = {
            {0, 1},   // horizontal right
            {1, 0},   // vertical down
            {1, 1},   // diagonal down-right
            {1, -1}   // diagonal down-left
        };
        
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                for (int[] dir : directions) {
                    List<int[]> sequence = getSequenceFromPosition(r, c, dir[0], dir[1], playerId);
                    if (sequence != null && sequence.size() == 5) {
                        // Check if this exact sequence is already found
                        if (!isDuplicateSequence(allSequences, sequence)) {
                            allSequences.add(sequence);
                        }
                    }
                }
            }
        }
        
        return allSequences;
    }

    // Get a sequence of exactly 5 chips starting from a position
    private List<int[]> getSequenceFromPosition(int startRow, int startCol, int deltaR, int deltaC, int playerId) {
        List<int[]> sequence = new ArrayList<>();
        
        // Check if we can form exactly 5 consecutive chips
        for (int i = 0; i < 5; i++) {
            int r = startRow + i * deltaR;
            int c = startCol + i * deltaC;
            
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) return null;
            
            Cell cell = grid[r][c];
            if (!((cell.chip != null && cell.chip == playerId) || cell.isCorner)) {
                return null;
            }
            
            sequence.add(new int[]{r, c});
        }
        
        // Check that there's no valid chip before (to ensure we start at the beginning)
        int beforeR = startRow - deltaR;
        int beforeC = startCol - deltaC;
        if (beforeR >= 0 && beforeR < SIZE && beforeC >= 0 && beforeC < SIZE) {
            Cell beforeCell = grid[beforeR][beforeC];
            if ((beforeCell.chip != null && beforeCell.chip == playerId) || beforeCell.isCorner) {
                return null; // This is not the start of the sequence
            }
        }
        
        return sequence;
    }

    // Count how many chips are shared between two sequences
    private int countSharedChips(List<int[]> seq1, List<int[]> seq2) {
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

    // Check if this sequence already exists in the list
    private boolean isDuplicateSequence(List<List<int[]>> existingSequences, List<int[]> newSequence) {
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

    private boolean hasPlayerChip(int row, int col, int playerId) {
        return grid[row][col].chip != null && grid[row][col].chip == playerId;
    }

    // Find a sequence of 5 starting from a cell, marking used cells
    private boolean findSequenceFromCell(int row, int col, int playerId, boolean[][] used) {
        int[][] directions = {
                {0, 1},   // horizontal right
                {1, 0},   // vertical down
                {1, 1},   // diagonal down-right
                {1, -1}   // diagonal down-left
        };

        for (int[] dir : directions) {
            if (isValidSequence(row, col, dir[0], dir[1], playerId, 5)) {
                // Mark these cells as used
                for (int i = 0; i < 5; i++) {
                    int r = row + i * dir[0];
                    int c = col + i * dir[1];
                    used[r][c] = true;
                }
                return true;
            }
        }
        return false;
    }

    private boolean isValidSequence(int startRow, int startCol, int deltaR, int deltaC, int playerId, int length) {
        for (int i = 0; i < length; i++) {
            int r = startRow + i * deltaR;
            int c = startCol + i * deltaC;
            
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) return false;
            
            Cell cell = grid[r][c];
            // Valid if it's the player's chip or a corner
            if (!((cell.chip != null && cell.chip == playerId) || cell.isCorner)) {
                return false;
            }
        }
        return true;
    }

    // Check if a card is "dead" (both board positions occupied by opponent)
    public boolean isDeadCard(Card card, int playerId) {
        if (card.isJack()) return false; // Jacks are never dead
        
        String cardString = card.getRank() + card.getSuit().charAt(0);
        int blockedCount = 0;
        
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c].card.equals(cardString)) {
                    if (grid[r][c].chip != null && grid[r][c].chip != playerId) {
                        blockedCount++;
                    }
                }
            }
        }
        return blockedCount >= 2; // Both positions blocked
    }

    // Get valid moves for a card
    public java.util.List<int[]> getValidMoves(Card card, int playerId) {
        java.util.List<int[]> validMoves = new java.util.ArrayList<>();
        
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (isValidMove(card, r, c, playerId)) {
                    validMoves.add(new int[]{r, c});
                }
            }
        }
        return validMoves;
    }

    private boolean isValidMove(Card card, int row, int col, int playerId) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;
        
        Cell cell = grid[row][col];
        
        boolean isTwoEyedJack = card.isJack() && 
                (card.getSuit().equals("Hearts") || card.getSuit().equals("Diamonds"));
        boolean isOneEyedJack = card.isJack() && 
                (card.getSuit().equals("Spades") || card.getSuit().equals("Clubs"));
        
        if (isTwoEyedJack) {
            return cell.chip == null;
        } else if (isOneEyedJack) {
            return cell.chip != null && cell.chip != playerId && !cell.isCorner;
        } else {
            String cardString = card.getRank() + card.getSuit().charAt(0);
            return cell.card.equals(cardString) && cell.chip == null && !cell.isCorner;
        }
    }

    // For debugging: print board state showing chip owners and corners
    public void printBoard() {
        System.out.print("   ");
        for (int c = 0; c < SIZE; c++) {
            System.out.printf("%3d", c);
        }
        System.out.println();
        
        for (int r = 0; r < SIZE; r++) {
            System.out.printf("%2d ", r);
            for (int c = 0; c < SIZE; c++) {
                Cell cell = grid[r][c];
                if (cell.isCorner) {
                    System.out.print(" * ");
                } else if (cell.chip == null) {
                    System.out.print(" . ");
                } else {
                    System.out.print(" " + cell.chip + " ");
                }
            }
            System.out.println();
        }
    }

    // Print board with card names for debugging
    public void printBoardWithCards() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                System.out.printf("%4s", grid[r][c].card);
            }
            System.out.println();
        }
    }
}