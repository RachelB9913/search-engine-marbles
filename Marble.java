import java.util.ArrayList;

public class Marble {

    private String color = "";
    private final int cost;
    private Position pos; //maybe to change just to i and j
    private boolean[] allowedOperators;
    private ArrayList<Position> pathDid;  //maybe to save only last move - not to repeat


    public Marble(String color, Position pos) {
//        System.out.println("Creating Marble with color: " + color + ", position: " + pos);
        this.color = color;
        this.pos = pos;
        switch (color) {
            case "R": this.cost = 10; break;
            case "G": this.cost = 3; break;
            case "B": this.cost = 1; break;
            default: this.cost = 0;
        }
        this.allowedOperators = new boolean[4];
        this.pathDid = new ArrayList<>();
        this.pathDid.add(pos);
    }

    //copy constructor
    public Marble(Marble marble) {
        this.color = marble.getColor();
        this.cost = marble.getCost();
        this.pos = marble.getPos();
        this.allowedOperators = new boolean[4];
        this.pathDid = new ArrayList<>();
        this.pathDid.add(marble.getPos());
    }

    public String getColor() {
        return color;
    }

    public Position getPos() {
        return this.pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public void setPos(int i, int j) {
        this.pos = new Position(i, j);
    }

    public int getCost() {
        return cost;
    }

    public boolean[] getAllowedOperators() {
        return allowedOperators;
    }


    public void setAllowedOperators(boolean[] allowedOperators) {
        this.allowedOperators = allowedOperators;
    }

    public Position getPathDid() {
        return pathDid.getLast();
    }

    public void setPathDid(ArrayList<Position> pathDid) {
        this.pathDid = pathDid;
    }

    // possible operators - moving up, down, left or right
    public Marble[][] moveUp(Marble[][] board) {
        Position prev = getPos();
        int newI = (this.pos.getI() - 1 + 3) % 3; // using % because it is a circular board
        if (canMove(newI, this.pos.getJ(), board)) {
            setPos(newI, this.pos.getJ()); // using setPos to update position
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return board;
    }

    public Marble[][] moveDown(Marble[][] board) {
        Position prev = getPos();
        int newI = (this.pos.getI() + 1) % 3;
        if (canMove(newI, this.pos.getJ(), board)) {
            setPos(newI, this.pos.getJ());
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return board;
    }

    public Marble[][] moveLeft(Marble[][] board) {
        Position prev = getPos();
        int newJ = (this.pos.getJ() - 1 + 3) % 3;
        if (canMove(this.pos.getI(), newJ, board)) {
            setPos(this.pos.getI(), newJ);
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return board;
    }

    public Marble[][] moveRight(Marble[][] board) {
        Position prev = getPos();
        int newJ = (this.pos.getJ() + 1) % 3;
        if (canMove(this.pos.getI(), newJ, board)) {
            setPos(this.pos.getI(), newJ);
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return board;
    }

    private Marble[][] updateBoard(Marble[][] board, Position prev, Position now) {
        Marble[][] newBoard = new Marble[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newBoard[i][j] = new Marble(board[i][j]);
            }
        }
        newBoard[now.getI()][now.getJ()] = newBoard[prev.getI()][prev.getJ()];
        newBoard[now.getI()][now.getJ()].setPos(now); // Update the marble's position
        newBoard[prev.getI()][prev.getJ()] = new Marble("_", prev);
        return newBoard;
    }

    // checks that the wanted space is blank (no color or X) and that doesn't go right back to where it was
    private boolean canMove(int i, int j, Marble[][] board) {
        if((this.getColor().equals("R") || this.getColor().equals("G") || this.getColor().equals("B"))
                && board[i][j].getColor().equals("_")
                && (this.pathDid.getLast().getI() != i || this.pathDid.getLast().getJ() != j)){
            return true;}
        else { return false; }
    }

    // this function ensures that the allowed operators for the marble will maintain that the marble
    // can only move to empty spaces and cannot immediately return to its previous position
    public boolean[] allowedOperators(Marble[][] board) {
        boolean[] allowed = new boolean[4]; // [up, down, left, right]
        int i = this.pos.getI();
        int j = this.pos.getJ();
        if (!board[i][j].getColor().equals("G") && !board[i][j].getColor().equals("R") && !board[i][j].getColor().equals("B")) {
            return new boolean[]{false, false, false, false};
        }
        // Check up
        int upI = (i - 1 + 3) % 3;
        allowed[0] = board[upI][j].getColor().equals("_") && !(this.pathDid.getLast().getI() == upI && this.pathDid.getLast().getJ() == j);

        // Check down
        int downI = (i + 1) % 3;
        allowed[1] = board[downI][j].getColor().equals("_") && !(this.pathDid.getLast().getI() == downI && this.pathDid.getLast().getJ() == j);

        // Check left
        int leftJ = (j - 1 + 3) % 3;
        allowed[2] = board[i][leftJ].getColor().equals("_") && !(this.pathDid.getLast().getI() == i && this.pathDid.getLast().getJ() == leftJ);

        // Check right
        int rightJ = (j + 1) % 3;
        allowed[3] = board[i][rightJ].getColor().equals("_") && !(this.pathDid.getLast().getI() == i && this.pathDid.getLast().getJ() == rightJ);

        return allowed;  // true = allowed, false = forbidden
    }
}

class Position {
    private int i;
    private int j;

    public Position(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    @Override
    //the function not only prints the position in the form of (i,j) but also change the indexing:
    // (0,0) (0,1) (0,2)        (1,1) (1,2) (1,3)
    // (1,0) (1,1) (1,2)  ->    (2,1) (2,2) (2,3)
    // (2,0) (2,1) (2,2)        (3,1) (3,2) (3,3)
    public String toString(){
        int realI = this.i + 1;
        int realJ = this.j + 1;
        return "("+ realI+","+ realJ+")";
    }

}


