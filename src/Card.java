public class Card {
    private final String rank; // 2-10, J, Q, K, A
    private final String suit; // Hearts, Diamonds, Clubs, Spades

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() { return rank; }
    public String getSuit() { return suit; }

    public boolean isJack() {
        return rank.equals("J");
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
