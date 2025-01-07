public class Operator {
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
