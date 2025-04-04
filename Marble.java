import java.util.ArrayList;

public class Marble {

    private String color = "";
    private final int cost;
    private Position pos;
    private ArrayList<Position> pathDid;


    public Marble(String color, Position pos) {
        this.color = color;
        this.pos = pos;
        switch (color) {
            case "R": this.cost = 10; break;
            case "G": this.cost = 3; break;
            case "B": this.cost = 1; break;
            default: this.cost = 0;
        }
        this.pathDid = new ArrayList<>();
        this.pathDid.add(pos);
    }

    //copy constructor
    public Marble(Marble marble) {
        this.color = marble.getColor();
        this.cost = marble.getCost();
        this.pos = marble.getPos();
        this.pathDid = new ArrayList<>(marble.getPathDid()); // deep copy
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

    public void setPos(int i, int j) { this.pos = new Position(i, j); }

    public int getCost() {
        return cost;
    }

    public ArrayList<Position> getPathDid() { return pathDid; }

    // possible operators - moving up, down, left or right
    public Marble[][] moveUp(Marble[][] board) {
        Position prev = getPos();
        int newI = (this.pos.getI() - 1 + 3) % 3; // using % because it is a circular board
        if (canMove(newI, this.pos.getJ(), board)) {
            setPos(newI, this.pos.getJ()); // using setPos to update position
            Position now = getPos();
            if (!pathDid.contains(now)) { // Add the new position to pathDid
                pathDid.add(now);
            }
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
            if (!pathDid.contains(now)) { // Add the new position to pathDid
                pathDid.add(now);
            }
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
            if (!pathDid.contains(now)) { // Add the new position to pathDid
                pathDid.add(now);
            }
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
            if (!pathDid.contains(now)) { // Add the new position to pathDid
                pathDid.add(now);
            }
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
        if (!board[i][j].getColor().equals("_")) {
            return false; // Can only move to empty spaces
        }

        Position newPos = new Position(i, j);
        // Check for immediate backtracking
        if (!pathDid.isEmpty() && newPos.equals(pathDid.getLast())) {
            return false; // Prevent it
        }

        return true;
    }

    public boolean[] allowedOperators(Marble[][] board) {
        boolean[] allowed = new boolean[4]; // [up, down, left, right]
        int i = this.pos.getI();
        int j = this.pos.getJ();

        // Check up
        int upI = (i - 1 + 3) % 3;
        if (canMove(upI, j, board)) {
            allowed[0] = true;
        }

        // Check down
        int downI = (i + 1) % 3;
        if (canMove(downI, j, board)) {
            allowed[1] = true;
        }

        // Check left
        int leftJ = (j - 1 + 3) % 3;
        if (canMove(i, leftJ, board)) {
            allowed[2] = true;
        }

        // Check right
        int rightJ = (j + 1) % 3;
        if (canMove(i, rightJ, board)) {
            allowed[3] = true;
        }

        return allowed;
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

    public int getJ() {
        return j;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same reference
        if (obj == null || getClass() != obj.getClass()) return false; // Null or different class
        Position other = (Position) obj;
        return this.i == other.getI() && this.j == other.getJ(); // Compare coordinates
    }

    @Override //with the help of chatGPT
    public int hashCode() {
        return 31 * i + j; // Generate a simple hash code for consistency
    }
}