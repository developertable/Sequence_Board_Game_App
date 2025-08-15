import java.util.*;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private int currentIndex = 0;

    public Deck() {
        initializeDeck();
        shuffle();
    }


    private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        // Very explicit 2-deck creation
        System.out.println("Creating deck...");
        
        // First deck
        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
                if (rank.equals("J") && suit.equals("Diamonds")) {
                    System.out.println("Added first Jack of Diamonds");
                }
            }
        }
        
        // Second deck
        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
                if (rank.equals("J") && suit.equals("Diamonds")) {
                    System.out.println("Added second Jack of Diamonds");
                }
            }
        }
        
        System.out.println("Total cards created: " + cards.size());
    }
    /*private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        // Sequence boards often use 2 decks — we can do that here
        for (int d = 0; d < 2; d++) {
            for (String suit : suits) {
                for (String rank : ranks) {
                    cards.add(new Card(rank, suit));
                }
            }
        }
    }*/

    public void shuffle() {
        Collections.shuffle(cards);
        currentIndex = 0;
    }

    public Card drawCard() {
        if (currentIndex < cards.size()) {
            return cards.get(currentIndex++);
        }
        return null; // deck empty
    }

    public boolean isEmpty() {
        return currentIndex >= cards.size();
    }


}
