import java.util.*;

public class AIPlayer {
    public enum AILevel {
        EASY("Suppandi", "Plays with basic strategy"),
        MEDIUM("Chacha Chaudhary", "Smart tactical player"), 
        HARD("Chanakya", "Master strategist");
        
        private final String name;
        private final String description;
        
        AILevel(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
    
    private AILevel level;
    private Random random;
    
    public AIPlayer(AILevel level) {
        this.level = level;
        this.random = new Random();
    }
    
    public AIMove chooseMove(Board board, Player aiPlayer, Player humanPlayer) {
        switch (level) {
            case EASY:
                return chooseSuppandiMove(board, aiPlayer, humanPlayer);
            case MEDIUM:
                return chooseChaChaudharyMove(board, aiPlayer, humanPlayer);
            case HARD:
                return chooseChankyaMove(board, aiPlayer, humanPlayer);
            default:
                return chooseSuppandiMove(board, aiPlayer, humanPlayer);
        }
    }
    
    // SUPPANDI (Easy) - Basic strategy with some smart moves
    private AIMove chooseSuppandiMove(Board board, Player aiPlayer, Player humanPlayer) {
        List<AIMove> allMoves = getAllPossibleMoves(board, aiPlayer);
        if (allMoves.isEmpty()) {
            return new AIMove(aiPlayer.getHand().get(0), -1, -1); // Discard
        }
        
        // 30% chance of making a smart move, 70% random
        if (random.nextInt(100) < 30) {
            return findSmartMove(board, aiPlayer, humanPlayer, allMoves);
        }
        
        // Otherwise random move
        return allMoves.get(random.nextInt(allMoves.size()));
    }
    
    // CHACHA CHAUDHARY (Medium) - Smart tactical player
    private AIMove chooseChaChaudharyMove(Board board, Player aiPlayer, Player humanPlayer) {
        List<AIMove> allMoves = getAllPossibleMoves(board, aiPlayer);
        if (allMoves.isEmpty()) {
            return new AIMove(aiPlayer.getHand().get(0), -1, -1); // Discard
        }
        
        AIMove bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (AIMove move : allMoves) {
            int score = evaluateMoveMedium(board, move, aiPlayer, humanPlayer);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove != null ? bestMove : allMoves.get(0);
    }
    
    // CHANAKYA (Hard) - Master strategist
    private AIMove chooseChankyaMove(Board board, Player aiPlayer, Player humanPlayer) {
        List<AIMove> allMoves = getAllPossibleMoves(board, aiPlayer);
        if (allMoves.isEmpty()) {
            return new AIMove(aiPlayer.getHand().get(0), -1, -1); // Discard
        }
        
        AIMove bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (AIMove move : allMoves) {
            int score = evaluateMoveHard(board, move, aiPlayer, humanPlayer);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove != null ? bestMove : allMoves.get(0);
    }
    
    // Get all possible moves for the AI
    private List<AIMove> getAllPossibleMoves(Board board, Player aiPlayer) {
        List<AIMove> moves = new ArrayList<>();
        
        for (Card card : aiPlayer.getHand()) {
            List<int[]> validPositions = board.getValidMoves(card, aiPlayer.getId());
            for (int[] pos : validPositions) {
                moves.add(new AIMove(card, pos[0], pos[1]));
            }
        }
        
        return moves;
    }
    
    // Find a smart move from available moves
    private AIMove findSmartMove(Board board, Player aiPlayer, Player humanPlayer, List<AIMove> allMoves) {
        // Look for winning moves first
        for (AIMove move : allMoves) {
            if (wouldWinGame(board, move, aiPlayer)) {
                return move;
            }
        }
        
        // Look for blocking moves
        for (AIMove move : allMoves) {
            if (wouldBlockHumanWin(board, move, humanPlayer)) {
                return move;
            }
        }
        
        // Look for sequence completing moves
        for (AIMove move : allMoves) {
            if (wouldCompleteSequence(board, move, aiPlayer)) {
                return move;
            }
        }
        
        // Random fallback
        return allMoves.get(random.nextInt(allMoves.size()));
    }
    
    // MEDIUM AI EVALUATION
    private int evaluateMoveMedium(Board board, AIMove move, Player aiPlayer, Player humanPlayer) {
        int score = 0;
        int row = move.getRow();
        int col = move.getCol();
        
        // Winning move - highest priority
        if (wouldWinGame(board, move, aiPlayer)) {
            return 100000;
        }
        
        // Block human from winning
        if (wouldBlockHumanWin(board, move, humanPlayer)) {
            score += 50000;
        }
        
        // Complete a sequence
        if (wouldCompleteSequence(board, move, aiPlayer)) {
            score += 25000;
        }
        
        // Block human sequence progress
        if (wouldBlockHumanSequence(board, move, humanPlayer)) {
            score += 10000;
        }
        
        // Progress towards own sequence
        score += countSequenceProgress(board, row, col, aiPlayer.getId()) * 1000;
        
        // Corner usage
        if (board.isCornerAt(row, col) || isNearCorner(row, col)) {
            score += 500;
        }
        
        // Central board positions are generally better
        score += evaluateBoardPosition(row, col) * 100;
        
        // Jack strategy
        if (move.getCard().isJack()) {
            score += evaluateJackMove(board, move, aiPlayer, humanPlayer);
        }
        
        // Add small randomness to avoid predictability
        score += random.nextInt(100);
        
        return score;
    }
    
    // HARD AI EVALUATION  
    private int evaluateMoveHard(Board board, AIMove move, Player aiPlayer, Player humanPlayer) {
        int score = evaluateMoveMedium(board, move, aiPlayer, humanPlayer);
        int row = move.getRow();
        int col = move.getCol();
        
        // Advanced strategic considerations
        
        // Multiple sequence threats
        score += countMultipleSequenceThreats(board, row, col, aiPlayer.getId()) * 2000;
        
        // Defensive positioning - block multiple human threats
        score += countMultipleSequenceThreats(board, row, col, humanPlayer.getId()) * -1500;
        
        // Future flexibility - positions that create multiple opportunities
        score += evaluateFutureFlexibility(board, row, col, aiPlayer.getId()) * 500;
        
        // Endgame strategy - if one player has 1 sequence, prioritize differently
        if (board.countSequences(aiPlayer.getId()) == 1) {
            score += evaluateEndgameStrategy(board, move, aiPlayer, humanPlayer);
        }
        
        if (board.countSequences(humanPlayer.getId()) == 1) {
            score += evaluateDefensiveEndgame(board, move, aiPlayer, humanPlayer);
        }
        
        // Tempo considerations - forcing opponent responses
        score += evaluateTempo(board, move, aiPlayer, humanPlayer);
        
        // Board control
        score += evaluateBoardControl(board, row, col, aiPlayer.getId()) * 200;
        
        return score;
    }
    
    // Check if move would win the game
    private boolean wouldWinGame(Board board, AIMove move, Player player) {
        if (move.isDiscard()) return false;
        
        // Simulate the move
        int currentSequences = board.countSequences(player.getId());
        
        // If player already has 1 sequence and this would create another line of 4+ chips
        if (currentSequences >= 1) {
            return wouldCreateWinningSequence(board, move.getRow(), move.getCol(), player.getId());
        }
        
        return false;
    }
    
    // Check if move would create a winning sequence
    private boolean wouldCreateWinningSequence(Board board, int row, int col, int playerId) {
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}};
        
        for (int[] dir : directions) {
            int count = 1; // The position we're placing
            
            // Count in positive direction
            for (int i = 1; i < 5; i++) {
                int newRow = row + i * dir[0];
                int newCol = col + i * dir[1];
                if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
                    Integer chipOwner = board.getChipAt(newRow, newCol);
                    if ((chipOwner != null && chipOwner == playerId) || board.isCornerAt(newRow, newCol)) {
                        count++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            
            // Count in negative direction
            for (int i = 1; i < 5; i++) {
                int newRow = row - i * dir[0];
                int newCol = col - i * dir[1];
                if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
                    Integer chipOwner = board.getChipAt(newRow, newCol);
                    if ((chipOwner != null && chipOwner == playerId) || board.isCornerAt(newRow, newCol)) {
                        count++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            
            if (count >= 5) return true;
        }
        
        return false;
    }
    
    // Check if move would block human from winning
    private boolean wouldBlockHumanWin(Board board, AIMove move, Player humanPlayer) {
        if (move.isDiscard()) return false;
        
        // If human has 1 sequence, check if this move blocks their potential 2nd sequence
        if (board.countSequences(humanPlayer.getId()) >= 1) {
            return wouldBlockSequenceCompletion(board, move.getRow(), move.getCol(), humanPlayer.getId());
        }
        
        return false;
    }
    
    // Check if move would block a sequence completion
    private boolean wouldBlockSequenceCompletion(Board board, int row, int col, int opponentId) {
        // Check if opponent could complete a sequence using this position
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}};
        
        for (int[] dir : directions) {
            // Check all possible 5-chip sequences that would include this position
            for (int start = -4; start <= 0; start++) {
                int count = 0;
                boolean includesThisPosition = false;
                
                for (int i = 0; i < 5; i++) {
                    int checkRow = row + (start + i) * dir[0];
                    int checkCol = col + (start + i) * dir[1];
                    
                    if (checkRow >= 0 && checkRow < 10 && checkCol >= 0 && checkCol < 10) {
                        if (checkRow == row && checkCol == col) {
                            includesThisPosition = true;
                            count++; // This position would help complete sequence
                        } else {
                            Integer chipOwner = board.getChipAt(checkRow, checkCol);
                            if ((chipOwner != null && chipOwner == opponentId) || board.isCornerAt(checkRow, checkCol)) {
                                count++;
                            }
                        }
                    }
                }
                
                // If this position would complete a sequence of 5
                if (count >= 5 && includesThisPosition) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    // Check if move would complete a sequence
    private boolean wouldCompleteSequence(Board board, AIMove move, Player player) {
        if (move.isDiscard()) return false;
        return wouldCreateWinningSequence(board, move.getRow(), move.getCol(), player.getId());
    }
    
    // Check if move would block human sequence progress
    private boolean wouldBlockHumanSequence(Board board, AIMove move, Player humanPlayer) {
        if (move.isDiscard()) return false;
        
        int row = move.getRow();
        int col = move.getCol();
        
        // Count adjacent human chips - if 2+, this is blocking progress
        return countAdjacentChips(board, row, col, humanPlayer.getId()) >= 2;
    }
    
    // Count sequence progress for a position
    private int countSequenceProgress(Board board, int row, int col, int playerId) {
        int maxProgress = 0;
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}};
        
        for (int[] dir : directions) {
            int progress = 1; // This position
            
            // Count in both directions
            for (int i = 1; i < 5; i++) {
                int newRow = row + i * dir[0];
                int newCol = col + i * dir[1];
                if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
                    Integer chipOwner = board.getChipAt(newRow, newCol);
                    if ((chipOwner != null && chipOwner == playerId) || board.isCornerAt(newRow, newCol)) {
                        progress++;
                    } else {
                        break;
                    }
                }
            }
            
            for (int i = 1; i < 5; i++) {
                int newRow = row - i * dir[0];
                int newCol = col - i * dir[1];
                if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
                    Integer chipOwner = board.getChipAt(newRow, newCol);
                    if ((chipOwner != null && chipOwner == playerId) || board.isCornerAt(newRow, newCol)) {
                        progress++;
                    } else {
                        break;
                    }
                }
            }
            
            maxProgress = Math.max(maxProgress, progress);
        }
        
        return maxProgress;
    }
    
    // Check if position is near corner
    private boolean isNearCorner(int row, int col) {
        return (row <= 1 && col <= 1) || (row <= 1 && col >= 8) ||
               (row >= 8 && col <= 1) || (row >= 8 && col >= 8);
    }
    
    // Evaluate board position (center is generally better)
    private int evaluateBoardPosition(int row, int col) {
        int distanceFromCenter = Math.abs(row - 4) + Math.abs(col - 4);
        return 8 - distanceFromCenter; // Higher score for central positions
    }
    
    // Evaluate Jack moves
    private int evaluateJackMove(Board board, AIMove move, Player aiPlayer, Player humanPlayer) {
        if (move.isDiscard()) return 0;
        
        Card card = move.getCard();
        int row = move.getRow();
        int col = move.getCol();
        
        // Two-eyed Jack (wild)
        if (card.getSuit().equals("Hearts") || card.getSuit().equals("Diamonds")) {
            // Prefer using wild Jacks for sequence completion or key defensive positions
            return countSequenceProgress(board, row, col, aiPlayer.getId()) * 500;
        }
        
        // One-eyed Jack (removal)
        if (card.getSuit().equals("Spades") || card.getSuit().equals("Clubs")) {
            Integer chipOwner = board.getChipAt(row, col);
            if (chipOwner != null && chipOwner == humanPlayer.getId()) {
                // Higher value for removing chips that block our sequences
                return countSequenceProgress(board, row, col, aiPlayer.getId()) * 300;
            }
        }
        
        return 0;
    }
    
    // Count adjacent chips of a player
    private int countAdjacentChips(Board board, int row, int col, int playerId) {
        int count = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < 10 && c >= 0 && c < 10 && !(r == row && c == col)) {
                    Integer chipOwner = board.getChipAt(r, c);
                    if ((chipOwner != null && chipOwner == playerId) || board.isCornerAt(r, c)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    // ADVANCED METHODS FOR HARD AI
    
    // Count multiple sequence threats
    private int countMultipleSequenceThreats(Board board, int row, int col, int playerId) {
        int threats = 0;
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}};
        
        for (int[] dir : directions) {
            if (countSequenceProgress(board, row, col, playerId) >= 3) {
                threats++;
            }
        }
        
        return threats;
    }
    
    // Evaluate future flexibility
    private int evaluateFutureFlexibility(Board board, int row, int col, int playerId) {
        int flexibility = 0;
        
        // Count empty adjacent spaces for future development
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < 10 && c >= 0 && c < 10) {
                    if (board.getChipAt(r, c) == null) {
                        flexibility++;
                    }
                }
            }
        }
        
        return flexibility;
    }
    
    // Endgame strategy when AI has 1 sequence
    private int evaluateEndgameStrategy(Board board, AIMove move, Player aiPlayer, Player humanPlayer) {
        if (move.isDiscard()) return 0;
        
        // Focus on completing 2nd sequence quickly
        int score = 0;
        
        if (wouldCompleteSequence(board, move, aiPlayer)) {
            score += 30000; // Very high priority
        }
        
        // Prefer moves that create multiple threats
        score += countMultipleSequenceThreats(board, move.getRow(), move.getCol(), aiPlayer.getId()) * 5000;
        
        return score;
    }
    
    // Defensive endgame when human has 1 sequence
    private int evaluateDefensiveEndgame(Board board, AIMove move, Player aiPlayer, Player humanPlayer) {
        if (move.isDiscard()) return 0;
        
        int score = 0;
        
        // Block human completion attempts
        if (wouldBlockHumanWin(board, move, humanPlayer)) {
            score += 40000;
        }
        
        // Block human sequence building
        if (wouldBlockHumanSequence(board, move, humanPlayer)) {
            score += 15000;
        }
        
        return score;
    }
    
    // Evaluate tempo (forcing opponent responses)
    private int evaluateTempo(Board board, AIMove move, Player aiPlayer, Player humanPlayer) {
        if (move.isDiscard()) return 0;
        
        int tempo = 0;
        int row = move.getRow();
        int col = move.getCol();
        
        // Moves that create immediate threats force responses
        if (countSequenceProgress(board, row, col, aiPlayer.getId()) >= 4) {
            tempo += 1000; // Forces defensive response
        }
        
        // Moves that threaten multiple sequences
        if (countMultipleSequenceThreats(board, row, col, aiPlayer.getId()) >= 2) {
            tempo += 2000; // Very hard to defend against
        }
        
        return tempo;
    }
    
    // Evaluate board control
    private int evaluateBoardControl(Board board, int row, int col, int playerId) {
        int control = 0;
        
        // Count controlled area around this position
        for (int r = row - 2; r <= row + 2; r++) {
            for (int c = col - 2; c <= col + 2; c++) {
                if (r >= 0 && r < 10 && c >= 0 && c < 10) {
                    Integer chipOwner = board.getChipAt(r, c);
                    if (chipOwner != null && chipOwner == playerId) {
                        control++;
                    }
                }
            }
        }
        
        return control;
    }
    
    public String getName() {
        return level.getName();
    }
    
    public String getDescription() {
        return level.getDescription();
    }
}