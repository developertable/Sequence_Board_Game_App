import java.util.ArrayList;
import java.util.List;

public class Player {
    private final int id;
    private final String name;
    private final List<Card> hand = new ArrayList<>();

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    public void removeCard(Card card) {
        hand.remove(card);
    }

    public List<Card> getHand() {
        return hand;
    }

    public void showHand() {
        System.out.println(name + "'s hand: " + hand);
    }
}
