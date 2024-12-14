import java.util.Objects;

public class Board {
    private Marble[][] board;
    private final boolean isGoal;
    private Board parent;
    private Operator howGotTo;

    public Board(Marble[][] board, boolean isGoal) {
        this.board = board;
        this.isGoal = isGoal;
    }

    public Board(Marble[][] board, boolean isGoal, Board parent) {
        this.board = board;
        this.isGoal = isGoal;
        this.parent = parent;
        this.howGotTo = null;
    }

    public Board(Marble[][] board, boolean isGoal, Board parent, Operator howGotTo) {
        this.board = board;
        this.isGoal = isGoal;
        this.parent = parent;
        this.howGotTo = howGotTo;
    }

    // uses deep copy to return the marble board
    public Marble[][] getMarbleBoard() {
        Marble[][] currBoard = new Marble[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                currBoard[i][j] = new Marble(this.board[i][j]);  // Use copy constructor
            }
        }
        return currBoard;
    }

    // a function that checks whether the board is the goal board to understand if we reached the goal.
    public boolean checkIsGoal(Marble[][] goal){
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                String color1 = this.board[i][j].getColor();
                String color2 = goal[i][j].getColor();
                if (!color1.equals(color2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Board getParent() { // deep copy?
        return parent;
    }

    public void setParent(Board parent) {
        this.parent = parent;
    }

    public Operator getHowGotTo() {
        return howGotTo;
    }

    public void setHowGotTo(Operator howGotTo) {
        this.howGotTo = howGotTo;
    }
}
