public class Operator {
    /** Operator is the class that represents a movement action for a marble.
     * with the help of this class we can pass from one state (board) to another */
    Marble marble;
    Position prevPos;
    String move; // [up, down, left, right]
    Position nextPos;


    public Operator(Marble marble, String move, Position prevPos, Position nextPos) {
        this.marble = this.marble;
        this.move = move;
        this.prevPos = prevPos;
        this.nextPos = nextPos;
    }

    public Marble getMarble() {
        return new Marble(marble);
    }

    public Position getPrevPos() { return new Position(this.prevPos.getI(), this.prevPos.getJ()); }

    public Position getNextPos() {
        return new Position(this.nextPos.getI(), this.nextPos.getJ());
    }
}
