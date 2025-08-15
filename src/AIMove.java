public class AIMove {
    private Card card;
    private int row;
    private int col;
    private boolean isDiscard;
    
    public AIMove(Card card, int row, int col) {
        this.card = card;
        this.row = row;
        this.col = col;
        this.isDiscard = (row == -1 && col == -1);
    }
    
    public Card getCard() { return card; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isDiscard() { return isDiscard; }
}