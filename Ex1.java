import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

// size of the boards is 3x3  (if not to change and use the length func.)

public class Ex1 {

    static ArrayList<Operator> path = new ArrayList<>();
    static int numOfNodes;  // Num:
    static int costOfPath;  // Cost:
    static double timeTook;

    public static void main(String[] args) throws IOException {

        parsedData data = InputParser.parseInput("input.txt");

        String filePath = "output.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        String algoName = data.getAlgoName();
        boolean time = data.getTime();
        boolean openList = data.getOpenList();
        Board start = data.getStartBoard();
        Board goal = data.getGoalBoard();
        Marble[][] startBoard = data.getStartMarbleBoard();
        Marble[][] goalBoard = data.getGoalMarbleBoard();

        // Print parsed data to verify that works
        System.out.println("Algorithm Name: " + algoName);
        System.out.println("Time: " + time);
        System.out.println("Open List: " + openList);

        System.out.println("Start Board:");
        InputParser.printBoard(startBoard);

        System.out.println("Goal Board:");
        InputParser.printBoard(goalBoard);

        long startTime = System.nanoTime(); // start measuring the time
        switch (algoName) {
            case "BFS": {
                reset();
                runBFS(start, goal, writer);
                break;
            }
            case "DFID": {
                reset();
                runDFID(start, goal, writer);
                break;
            }
            case "AStar": {
                reset();
                runAstar(startBoard, goalBoard);
                break;
            }
            case "IDAstar": {
                reset();
                runIDAstar(startBoard, goalBoard);
                break;
            }
            case "DFBnB": {
                reset();
                runDFBnB(startBoard, goalBoard);
                break;
            }
        }

        long endTime = System.nanoTime(); // stop the time measurement
        timeTook = (endTime - startTime) / 1_000_000_000.0; //duration and paring to seconds
        if (time) {
            String theTime = String.format("%.3f", timeTook);
            writer.newLine();
            writer.write(theTime + " seconds");
            System.out.println(theTime + " seconds");
        }
        writer.flush();
    }


//// FUNCTIONS!!! ////

    private static void runBFS(Board startBoard, Board goalBoard, BufferedWriter writer) throws IOException {
        boolean goal;
        ArrayList<Board> openList = new ArrayList<>();  // frontier
        Hashtable<Board, Integer> closedList = new Hashtable<>(); // closedList list 0 for not visited 1 for visited

        openList.add(startBoard);
        while (!openList.isEmpty()) {
            Board currentBoard = openList.removeFirst(); //take the first added board
            System.out.println("the current operator board is: ");
            InputParser.printBoard(currentBoard.getMarbleBoard());
            if (closedList.containsKey(currentBoard)) {
                continue; // Skip processing if already in closed list.
            }
            ArrayList<Operator> allOperators = generateAllOperators(currentBoard); //find all the possible operators for this state
            closedList.put(currentBoard, 1); // adding to the closed list after finding all its "children"
            for (Operator op : allOperators) {
                Board curr = boardFromOperator(currentBoard, op); // the state we get when applying the given operator on the current state
                numOfNodes ++; // will count each time a new state (board - node) was created
                if(!closedList.containsKey(curr) && !openList.contains(curr)) {
                    goal = curr.checkIsGoal(goalBoard.getMarbleBoard());
                    if (goal) {
                        System.out.println("reached the GOAL board: ");
                        InputParser.printBoard(curr.getMarbleBoard());
                        ArrayList<Operator> path = retrievePath(curr, startBoard); // function to retrieve the path
                        String pathToString = pathToString(path);//  a to-string to the path
                        writer.write(pathToString); // write path to output.txt
                        System.out.println(pathToString);
                        writer.newLine();
                        writer.write("Num: " + numOfNodes);
                        System.out.println("Num: " + numOfNodes);
                        writer.newLine();
                        if (costOfPath == -1) {
                            writer.write("Cost: inf");
                        } else {
                            writer.write("Cost: " + costOfPath);
                            System.out.println("Cost: " + costOfPath);
                        }
                        return;
                    } else {
                        openList.add(curr);
                    }
                }
            }
        }
        System.out.println("BFS didn't succeed!");
    }


    private static void runDFID(Board startBoard, Board goalBoard, BufferedWriter writer) throws IOException {
        ArrayList<Operator> result;
        for (int depth = 1; depth <= 100; depth++) { // understand what the limit and not 100
            Hashtable<Board, Integer> h = new Hashtable<>();
            result = runLimitedDFS(startBoard, startBoard, goalBoard, depth, h);
            if (result != null) {
                // Use the path returned by runLimitedDFS instead of the global path
                String pathToString = pathToString(result);
                writer.write(pathToString);
                System.out.println(pathToString);
                writer.newLine();
                writer.write("Num: " + numOfNodes);
                System.out.println("Num: " + numOfNodes);
                writer.newLine();
                writer.write("Cost: " + costOfPath);
                System.out.println("Cost: " + costOfPath);
                break; // Exit the loop once a path is found
            }
        }
    }

    private static ArrayList<Operator> runLimitedDFS(Board startBoard, Board currBoard, Board goalBoard, int limit, Hashtable<Board, Integer> h) {
        if (currBoard.checkIsGoal(goalBoard.getMarbleBoard())) {
            // Directly return the path when goal is found
            return retrievePath(currBoard, startBoard);
        }
        else if (limit == 0) { // if depth limit is reached and no solution found
            return null;
        }
        else {
            h.put(currBoard, 1);
            ArrayList<Operator> result = null;
            boolean isCutoff = false;
            ArrayList<Operator> allOperators = generateAllOperators(currBoard); //find all the possible operators for this state
            for (Operator op : allOperators) {
                Board nextBoard = boardFromOperator(currBoard, op);
                numOfNodes++;
                if (h.get(nextBoard) != null && h.get(nextBoard).equals(1)) {
                    continue;  // continue to the next operator - loop avoidance??????
                }
                result = runLimitedDFS(startBoard, nextBoard, goalBoard, limit - 1, h);

                if (result != null) {
                    return result;
                } else {
                    isCutoff = true;
                }
            }

            h.remove(currBoard);  // checked all the children - can be released
            if(isCutoff){  // ==true - current depth has been reached and no solution was found at this depth
                return null;
            }
            return result;
        }
    }


    private static void runAstar(Marble[][] startBoard, Marble[][] goalBoard) {

        // add code!!!! and understand what are the parameters needed for it

    }

    private static void runIDAstar(Marble[][] startBoard, Marble[][] goalBoard) {

        // add code!!!! and understand what are the parameters needed for it

    }

    private static void runDFBnB(Marble[][] startBoard, Marble[][] goalBoard) {

        // add code!!!! and understand what are the parameters needed for it

    }


    // this function gets the current board and gets a list of all the possible operators for this state.
    // operator is like a pair of marble and an allowed movement of it
    // for each marble on the board checks what movements are allowed for it to do and returns a list
    private static ArrayList<Operator> generateAllOperators(Board currentBoard) {
        ArrayList<Operator> allOperators = new ArrayList<>();
        for (int i=0;i<3;i++) {
            for (int j=0;j<3;j++) {
                Marble m = new Marble(currentBoard.getMarbleBoard()[i][j]);
                if(m.getColor().equals("R") || m.getColor().equals("B") || m.getColor().equals("G")) {
                    boolean[] possible = m.allowedOperators(currentBoard.getMarbleBoard());
                    Position prev = new Position(i, j);
                    if (possible[0]) { // up
                        Position curr = new Position((i - 1 + 3) % 3, j);
                        Operator op = new Operator(m, "up", prev, curr);
                        allOperators.add(op);
                    }
                    if (possible[1]) { // down
                        Position curr = new Position((i + 1) % 3, j);
                        Operator op = new Operator(m, "down", prev, curr);
                        allOperators.add(op);
                    }
                    if (possible[2]) { // left
                        Position curr = new Position(i, (j - 1 + 3) % 3);
                        Operator op = new Operator(m, "left", prev, curr);
                        allOperators.add(op);
                    }
                    if (possible[3]) { // right
                        Position curr = new Position(i, (j + 1) % 3);
                        Operator op = new Operator(m, "right", prev, curr);
                        allOperators.add(op);
                    }
                }
            }
        }
        return allOperators;
    }

    public static void reset(){
        path = new ArrayList<>();
        numOfNodes = 0;  // Num:
        costOfPath = 0;  // Cost:
        timeTook = 0;
    }

    public static Board boardFromOperator(Board prevBoard, Operator op) {
        Marble[][] map = new Marble[3][3];
        Marble[][] originalBoard = prevBoard.getMarbleBoard();

        // create a deep copy of the original board first
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                map[i][j] = new Marble(originalBoard[i][j]);
            }
        }
        // then apply the move
        if(op.move.equals("up")){ map = op.marble.moveUp(map); }
        if(op.move.equals("down")){ map = op.marble.moveDown(map); }
        if(op.move.equals("left")){ map = op.marble.moveLeft(map); }
        if(op.move.equals("right")){ map = op.marble.moveRight(map); }

        return new Board(map, false, prevBoard, op);
    }

    // function to retrieve the path
    public static ArrayList<Operator> retrievePath(Board lastOne, Board startBoard) {
        ArrayList<Operator> path = new ArrayList<>();
        Board current = lastOne;  // maybe need to copy

        while (current.getParent() != null) {
            path.addFirst(current.getHowGotTo());  // add the operator that got to this board to the path
            current = current.getParent();  // move to the parent board to the previous one
        }
        return path;
    }


    // to be of the form: (2,2):B:(2,3)--(2,3):B:(1,3)--(3,1):G:(3,3)--(3,2):G:(2,2)
    public static String pathToString(ArrayList<Operator> path){
        if(path.isEmpty()){
            costOfPath = -1;
            return "no path";
        }
        StringBuilder thePath = new StringBuilder((path.getFirst().getPrevPos().toString() + ":"
                                                    + path.getFirst().getMarble().getColor() + ":"
                                                    + path.getFirst().getNextPos().toString()));
        costOfPath += path.getFirst().getMarble().getCost(); // will add the cost of the marble that we moved
        String xTOy = "";
        for (int i=1;i<path.size();i++) {
            thePath.append("--");
            xTOy = (path.get(i).getPrevPos() + ":" + path.get(i).getMarble().getColor() + ":" + path.get(i).getNextPos());
            costOfPath += path.get(i).getMarble().getCost(); // will add the cost of the marble that we moved
            thePath.append(xTOy);
        }
        return thePath.toString();
    }




}

