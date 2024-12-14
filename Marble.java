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

    // possible operators - moving up, down, left or right
    public Marble[][] moveUp(Marble[][] board) {
        Position prev = getPos();
        int newI = (this.pos.getI() - 1 + 3) % 3; // using % because it is a circular board
        if (canMove(newI, this.pos.getJ(), board)) {
            setPos(newI, this.pos.getJ()); // using setPos to update position
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return null;
    }

    public Marble[][] moveDown(Marble[][] board) {
        Position prev = getPos();
        int newI = (this.pos.getI() + 1) % 3;
        if (canMove(newI, this.pos.getJ(), board)) {
            setPos(newI, this.pos.getJ());
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return null;
    }

    public Marble[][] moveLeft(Marble[][] board) {
        Position prev = getPos();
        int newJ = (this.pos.getJ() - 1 + 3) % 3;
        if (canMove(this.pos.getI(), newJ, board)) {
            setPos(this.pos.getI(), newJ);
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return null;
    }

    public Marble[][] moveRight(Marble[][] board) {
        Position prev = getPos();
        int newJ = (this.pos.getJ() + 1) % 3;
        if (canMove(this.pos.getI(), newJ, board)) {
            setPos(this.pos.getI(), newJ);
            Position now = getPos();
            return updateBoard(board, prev, now);
        }
        return null;
    }

    private Marble[][] updateBoard(Marble[][] board, Position prev, Position now) {
        Marble[][] newBoard = new Marble[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newBoard[i][j] = new Marble(board[i][j]);
            }
        }
        newBoard[now.getI()][now.getJ()] = newBoard[prev.getI()][prev.getJ()];
        newBoard[prev.getI()][prev.getJ()] = new Marble("X", prev);
        return newBoard;
    }

    // checks that the wanted space is blank (no color or X) and that doesn't go right back to where it was
    private boolean canMove(int i, int j, Marble[][] board) {
        return board[i][j].getColor().equals("_") && (this.pathDid.getLast().getI() != i || this.pathDid.getLast().getJ() != j);
    }


    public boolean[] allowedOperators(Marble[][] board) {
        boolean[] allowed = new boolean[4]; // [up, down, left, right]
        int i = this.pos.getI();
        int j = this.pos.getJ();
        // Check up
        int upI = (i - 1 + 3) % 3;
        allowed[0] = board[upI][j].getColor().equals("_");

        // Check down
        int downI = (i + 1) % 3;
        allowed[1] = board[downI][j].getColor().equals("_");

        // Check left
        int leftJ = (j - 1 + 3) % 3;
        allowed[2] = board[i][leftJ].getColor().equals("_");

        // Check right
        int rightJ = (j + 1) % 3;
        allowed[3] = board[i][rightJ].getColor().equals("_");

        return allowed;  // 1=allowed , 0=forbidden
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
    public String toString(){
        return "("+ this.i+","+ this.j+")";
    }

}


