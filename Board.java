public class Board {
    private Marble[][] board;
    private final boolean isGoal;
    private Board parent;
    private Operator howGotTo;

    public Board(Marble[][] board, boolean isGoal) { //decide what to erase
        if (board == null) {
            this.board = new Marble[3][3];
            // initialize with blank marbles or throw an exception
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.board[i][j] = new Marble("_", new Position(i, j));
                }
            }
        } else {
            this.board = board;
        }
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
        if (this.board == null) {
            System.out.println("Error: Board is null in checkIsGoal");
            return false;
        }
        return this.checkEquals(goal);
    }

    //checks checkEquals only based on the colors
    public boolean checkEquals(Marble[][] other) {
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                String color1 = this.board[i][j].getColor();
                String color2 = other[i][j].getColor();
                if (!color1.equals(color2)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Board other = (Board) obj;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!this.board[i][j].getColor().equals(other.board[i][j].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    // suggested by chatGPT
    @Override
    public int hashCode() {
        int result = 17;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result = 31 * result + this.board[i][j].getColor().hashCode();
            }
        }
        return result;
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
