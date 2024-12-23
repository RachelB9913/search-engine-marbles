public class Operator {
    Marble marble;
    Position prevPos;
    String move; // [up, down, left, right]
    Position nextPos;

    public Operator(Marble marble , String move) {
        this.marble = marble;
        this.move = move;
    }

    public Operator(Marble marble , String move,Position prevPos, Position nextPos) {
        this.marble = marble;
        this.move = move;
        this.prevPos = prevPos;
        this.nextPos = nextPos;
    }

    public Marble getMarble() {
        return new Marble(marble);
    }


    public void setMarble(Marble marble) {
        this.marble = marble;
    }

    public Position getPrevPos() { return new Position(this.prevPos.getI(), this.prevPos.getJ()); }

    public void setPrevPos(Position prevPos) {
        this.prevPos = prevPos;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public Position getNextPos() {
        return new Position(this.nextPos.getI(), this.nextPos.getJ());
    }

    public void setNextPos(Position nextPos) {
        this.nextPos = nextPos;
    }
}
