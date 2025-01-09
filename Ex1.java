import java.io.*;
import java.util.*;


public class Ex1 {

    private static final int MAX_NODES = 362880; //factorial of 9 for a 3x3 board;
    private static int numOfNodes;  // Num:
    private static int costOfPath;  // Cost:
    private static double timeTook;

    public static void main(String[] args) throws IOException {

        parsedData data = InputParser.parseInput("input.txt");

        String filePath = "output.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        assert data != null;
        String algoName = data.getAlgoName().toUpperCase(); // just to make sure that it will handle lower and upper case names.
        boolean time = data.getTime();
        boolean openList = data.getOpenList();
        Board start = data.getStartBoard();
        Board goal = data.getGoalBoard();

        reset();
        long startTime = System.nanoTime(); // start measuring the time
        switch (algoName) {
            case "BFS": {
                runBFS(start, goal, writer, openList);
                break;
            }
            case "DFID": {
                runDFID(start, goal, writer, openList);
                break;
            }
            case "A*": {
                runAstar(start, goal, writer, openList);
                break;
            }
            case "IDA*": {
                runIDAstar(start, goal, writer, openList);
                break;
            }
            case "DFBNB": {
                runDFBnB(start, goal, writer, openList);
                break;
            }
        }

        long endTime = System.nanoTime(); // stop the time measurement
        timeTook = (endTime - startTime) / 1_000_000_000.0; //duration and paring to seconds
        if (time) {
            String theTime = String.format("%.3f", timeTook);
            writer.newLine();
            writer.write(theTime + " seconds");
        }
        writer.flush();
    }


//// FUNCTIONS!!! ////

    //BFS function - uses closedList and a hashSet for both open and closed list
    private static void runBFS(Board startBoard, Board goalBoard, BufferedWriter writer, boolean isOpenList) throws IOException {
        boolean goal;
        ArrayList<Board> queue = new ArrayList<>();  // frontier
        HashSet<Board> openList = new HashSet<>();
        HashSet<Board> closedList = new HashSet<>(); // closedList list

        queue.add(startBoard);
        openList.add(startBoard);
        while (!queue.isEmpty()) {
            if (isOpenList) {
                System.out.println("The open List now is:");
                for (Board board : queue) {
                    InputParser.printBoard(board.getMarbleBoard());
                    System.out.println();
                }
            }
            Board currentBoard = queue.removeFirst(); //take the first added board
            openList.remove(currentBoard);
            if (closedList.contains(currentBoard)) {
                continue; // Skip processing if already in closed list.
            }
            ArrayList<Operator> allOperators = generateAllOperators(currentBoard); //find all the possible operators for this state
            closedList.add(currentBoard); // adding to the closed list after finding all its "children"
            for (Operator op : allOperators) {
                Board curr = boardFromOperator(currentBoard, op); // the state we get when applying the given operator on the current state
                numOfNodes++; // will count each time a new state (board - node) was created
                if (!closedList.contains(curr) && !openList.contains(curr)) {
                    goal = curr.checkIsGoal(goalBoard.getMarbleBoard());
                    if (goal) {
                        ArrayList<Operator> path = retrievePath(curr); // function to retrieve the path
                        writer.write(pathToString(path));
                        writer.write("\nNum: " + numOfNodes + "\nCost: " + costOfPath);
                        return;
                    } else {
                        queue.add(curr);
                        openList.add(curr);
                    }
                }
            }
        }
        writer.write("no path\nNum: " + numOfNodes + "\nCost: inf");
    }

//    DFID function - implemented in a recursive way without closed list but with loop avoidance
    private static void runDFID(Board startBoard, Board goalBoard, BufferedWriter writer, boolean isOpenList) throws IOException {
        int depth = 1;
        while (true) {
            HashSet<Board> pathStates = new HashSet<>();
            ArrayList<Operator> result = runLimitedDFS(startBoard, goalBoard, depth, pathStates, isOpenList);
            if (result != null) {
                writer.write(pathToString(result));
                writer.newLine();
                writer.write("Num: " + numOfNodes);
                writer.newLine();
                writer.write("Cost: " + costOfPath);
                return;
            }
            if (numOfNodes >= MAX_NODES*3) {
                // Factorial limit for a 3x3 board * 3 to make the limit bigger and not miss the path if there is one
                writer.write("no path\nNum: " + numOfNodes + "\nCost: inf");
                return;
            }
            depth++;
        }
    }

    // an auxiliary function to the DFID algo
    private static ArrayList<Operator> runLimitedDFS(Board currBoard, Board goalBoard, int limit, HashSet<Board> pathStates, boolean isOpenList) {
        if (currBoard.checkIsGoal(goalBoard.getMarbleBoard())) {
            // Directly return the path when goal is found
            return retrievePath(currBoard);
        }
        else if (limit == 0) { // if depth limit is reached and no solution found
            return null;
        }
        else {
            pathStates.add(currBoard);
            if(isOpenList){
                System.out.println("The open List now is:");
                for (Board board : pathStates) {
                    InputParser.printBoard(board.getMarbleBoard());
                    System.out.println("---------");
                }
            }
            ArrayList<Operator> result = null;
            boolean isCutoff = false;
            ArrayList<Operator> allOperators = generateAllOperators(currBoard); //find all the possible operators for this state
            for (Operator op : allOperators) {
                Board nextBoard = boardFromOperator(currBoard, op);
                numOfNodes++;
                if (pathStates.contains(nextBoard)) {
                    continue;  // continue to the next operator - loop avoidance??????
                }
                result = runLimitedDFS(nextBoard, goalBoard, limit - 1, pathStates ,isOpenList);

                if (result != null) {
                    return result;
                } else {
                    isCutoff = true;
                }
            }
            pathStates.remove(currBoard);  // checked all the children - can be released
            if (isCutoff) {  // ==true - current depth has been reached and no solution was found at this depth
                return null;
            }
            return result;
        }
    }

    /**
     * the heuristic function that is used in the A* algorithm
     * this is a weighted Manhattan distance.
     * It calculates the estimated cost to reach the goal board by considering both the Manhattan distance of each marble and its movement cost.
     */
    private static int heuristic(Board currentBoard, Board goalBoard) {
        Marble[][] current = currentBoard.getMarbleBoard();
        Marble[][] goal = goalBoard.getMarbleBoard();
        int boardSize = 3;
        int heuristicDist = 0;

        // Precompute goal positions for each color
        Hashtable<String, ArrayList<Position>> goalPositions = new Hashtable<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String color = goal[i][j].getColor();
                if (!color.equals("_") && !color.equals("X")) {
                    goalPositions.putIfAbsent(color, new ArrayList<>());
                    goalPositions.get(color).add(new Position(i, j));
                }
            }
        }

        // Calculate distances with costs
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String currentColor = current[i][j].getColor();
                if (!currentColor.equals("_") && !currentColor.equals("X") && goalPositions.containsKey(currentColor)) {
                    int minCost = Integer.MAX_VALUE;
                    for (Position goalPos : goalPositions.get(currentColor)) {
                        int rowDist = Math.min(Math.abs(i - goalPos.getI()), boardSize - Math.abs(i - goalPos.getI()));
                        int colDist = Math.min(Math.abs(j - goalPos.getJ()), boardSize - Math.abs(j - goalPos.getJ()));
                        int distance = rowDist + colDist;
                        int movementCost = current[i][j].getCost(); // cost of the marble
                        minCost = Math.min(minCost, distance * movementCost);
                    }
                    heuristicDist += minCost;
                }
            }
        }
        return heuristicDist;
    }

    /**
     * A* function - with closed list and hash for both open and closed list.
     * searches for the shortest path from a starting Board to a goal Board by:
     * minimizing the F(n) = g(n) + h(n) each step
     * where g(n) is the path cost and h(n) is the heuristic estimate of the cost to the goal
     */
    private static void runAstar(Board startBoard, Board goalBoard, BufferedWriter writer, boolean isOpenList) throws IOException {
        int iteration = 0;
        PriorityQueue<Board> openList = new PriorityQueue<>(); // use the comparator based on the f of the board
        Hashtable<Board, Integer> closedList = new Hashtable<>();
        Hashtable<Board, Integer> processSet = new Hashtable<>(); // tracks boards in openList with their g(n)

        startBoard.setG(0);  // the cost to reach the start board is always 0
        startBoard.setH(heuristic(startBoard, goalBoard));
        startBoard.setF(startBoard.getG() + startBoard.getH());
        startBoard.setCreationTime(iteration);

        openList.add(startBoard);
        processSet.put(startBoard, startBoard.getG()); // add to openSet for tracking

        while (!openList.isEmpty()) {
            if (isOpenList) {
                System.out.println("The open List now is:");
                for (Board board : openList) {
                    InputParser.printBoard(board.getMarbleBoard());
                    System.out.println();
                }
            }

            iteration++;
            Board current = openList.poll();
            processSet.remove(current);
            if (current.checkIsGoal(goalBoard.getMarbleBoard())) {  // Goal reached, output the path
                ArrayList<Operator> path = retrievePath(current);
                writer.write(pathToString(path));
                writer.write("\nNum: " + numOfNodes + "\nCost: " + costOfPath);
                return;
            }
            closedList.put(current, current.getG()); // add to closedList as fully processed

            ArrayList<Operator> possibleOperators = generateAllOperators(current);
            for (Operator op : possibleOperators) {
                Board curr = boardFromOperator(current, op);
                numOfNodes++;
                // the cost of reaching the current board plus the cost of the move that was made
                int temporalG = current.getG() + curr.getHowGotTo().getMarble().getCost();
                if (closedList.containsKey(curr) && closedList.get(curr) <= temporalG) {
                    continue; // skip if already processed with a better or equal cost
                }
                if (!processSet.containsKey(curr) || temporalG < processSet.get(curr)) {  //if not in the open list or the new path is better
                    curr.setParent(current);
                    curr.setG(temporalG);
                    curr.setH(heuristic(curr, goalBoard));
                    curr.setF(curr.getG() + curr.getH());
                    curr.setCreationTime(iteration);

                    if (processSet.containsKey(curr)) {
                        openList.remove(curr); // remove the outdated version
                    }

                    openList.add(curr); // add updated board to openList
                    processSet.put(curr, curr.getG()); // update processSet with the new g(n)
                }
            }
        }
        writer.write("no path\nNum: " + numOfNodes + "\nCost: inf");
    }

    // IDA* algo with a stack, without a closed-list, with loop-avoidance
    private static void runIDAstar(Board startBoard, Board goalBoard, BufferedWriter writer, boolean isOpenList) throws IOException {
        int threshold = heuristic(startBoard, goalBoard);
        Stack<Board> stack = new Stack<>();

        // initialize
        startBoard.setG(0);
        startBoard.setH(threshold);
        startBoard.setF(threshold);

        while (true) {
            stack.clear(); // Reset visited states for each threshold
            stack.push(startBoard);
            int result = runDFS(stack, threshold, goalBoard, isOpenList);
            if (result == -1) { // goal found
                Board goalNode = stack.peek();
                ArrayList<Operator> path = retrievePath(goalNode);
                writer.write(pathToString(path));
                writer.write("\nNum: " + numOfNodes + "\nCost: " + costOfPath);
                return;
            }
            if (result == Integer.MAX_VALUE || numOfNodes >= MAX_NODES) {
                writer.write("no path\nNum: " + numOfNodes + "\nCost: inf");
                return;
            }
            threshold = result; // update the threshold for the next iteration
        }
    }

    // an auxiliary function to IDA* algo
    private static int runDFS(Stack<Board> stack, int threshold, Board goalBoard, boolean isOpenList) throws IOException {
        Board current = stack.peek();
        int f = current.getF();

        if (f > threshold) return f;
        if (current.checkIsGoal(goalBoard.getMarbleBoard())) return -1; // goal found

        int minThreshold = Integer.MAX_VALUE;
        ArrayList<Operator> operators = generateAllOperators(current);
        ArrayList<Board> possibleBoards = new ArrayList<>();
        for (Operator op : operators) {
            Board board = boardFromOperator(current, op);
            numOfNodes++;
            if (stack.contains(board)){
                continue; // Skip visited states
            }
            board.setG(current.getG() + op.getMarble().getCost());
            board.setH(heuristic(board, goalBoard));
            board.setF(board.getG() + board.getH());
            board.setParent(current);
            board.setHowGotTo(op);

            possibleBoards.add(board);
        }
        for (Board nextBoard : possibleBoards) {
            if (isInStack(stack, nextBoard)) { // Loop avoidance: check if the board is already in the stack
                continue;
            }

            stack.push(nextBoard);

            int result = runDFS(stack, threshold, goalBoard, isOpenList);
            if (result == -1) {
                return -1; // solution found
            }
            minThreshold = Math.min(minThreshold, result);
            if (isOpenList) {
                System.out.println("The open List now is:");
                for (Board board : stack) {
                    InputParser.printBoard(board.getMarbleBoard());
                    System.out.println();
                }
            }
            stack.pop(); // Backtrack - didnt help to get to goal
        }
        return minThreshold;
    }


    /**
     * DFBnB function. uses explicitly most of the functions it needs.
     * uses a stack, without closed list but with loop avoidance
     */
    private static void runDFBnB(Board startBoard, Board goalBoard, BufferedWriter writer, boolean isOpenList) throws IOException {
        Stack<Board> stack = new Stack<>();
        HashSet<Board> visitedHash = new HashSet<>();
        ArrayList<Operator> result = new ArrayList<>();

//        int t = Integer.MAX_VALUE; // initial threshold
        int t = 100 * heuristic(startBoard, goalBoard); // initial threshold - does it in half the time of Integer.MAX_VALUE but still run through all the options

        startBoard.setG(0);
        startBoard.setH(heuristic(startBoard, goalBoard));
        startBoard.setF(startBoard.getG() + startBoard.getH());
        stack.push(startBoard);
        visitedHash.add(startBoard);

        while (!stack.isEmpty()) {
            if (numOfNodes >= MAX_NODES) {
                writer.write("no path\nNum: " + numOfNodes + "\nCost: inf");
                return;
            }

            if (isOpenList) {
                System.out.println("The open List now is:");
                for (Board board : stack) {
                    InputParser.printBoard(board.getMarbleBoard());
                    System.out.println();
                }
            }

            Board current = stack.pop();
            if (current.isOut()) {
                visitedHash.remove(current);
            } else {
                current.setOut(true);
                stack.push(current);

                ArrayList<Board> successors = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        Marble marble = new Marble(current.getMarbleBoard()[i][j]);
                        if (!marble.getColor().matches("[RGB]")) {
                            continue;
                        }
                        Position prevPos = new Position(i, j);
                        boolean[] possibleMoves = marble.allowedOperators(current.getMarbleBoard());

                        if (possibleMoves[0]) {
                            Position nextPos = new Position((i - 1 + 3) % 3, j);
                            Operator op = new Operator(marble, "up", prevPos, nextPos);
                            Board successor = boardFromOperator(current, op);
                            successors.add(updateBoardDetails(successor, current, op, goalBoard));
                        }
                        if (possibleMoves[1]) {
                            Position nextPos = new Position((i + 1) % 3, j);
                            Operator op = new Operator(marble, "down", prevPos, nextPos);
                            Board successor = boardFromOperator(current, op);
                            successors.add(updateBoardDetails(successor, current, op, goalBoard));
                        }
                        if (possibleMoves[2]) {
                            Position nextPos = new Position(i, (j - 1 + 3) % 3);
                            Operator op = new Operator(marble, "left", prevPos, nextPos);
                            Board successor = boardFromOperator(current, op);
                            successors.add(updateBoardDetails(successor, current, op, goalBoard));
                        }
                        if (possibleMoves[3]) {
                            Position nextPos = new Position(i, (j + 1) % 3);
                            Operator op = new Operator(marble, "right", prevPos, nextPos);
                            Board successor = boardFromOperator(current, op);
                            successors.add(updateBoardDetails(successor, current, op, goalBoard));
                        }
                    }
                }
                Collections.sort(successors);

                ArrayList<Board> validBoards = new ArrayList<>();
                for (Board successor : successors) {
                    int fValue = successor.getF();
                    if (fValue >= t) {
                        continue;
                    } else if (visitedHash.contains(successor)) {
                        Board existing = null;
                        for (Board b : visitedHash) {
                            if (b.equals(successor)) {
                                existing = b;
                                break;
                            }
                        }
                        if (existing != null) {
                            int existingF = existing.getF();
                            if (existing.isOut() || existingF <= fValue) {
                                continue;
                            } else {
                                stack.remove(existing);
                                visitedHash.remove(existing);
                                validBoards.add(successor);
                            }
                        }
                    } else if (successor.checkIsGoal(goalBoard.getMarbleBoard())) {
                        t = fValue;
                        result = retrievePath(successor);
                        break;
                    } else {
                        validBoards.add(successor);
                    }
                }
                // insert in reverse order all the validBoards to the stack and hash
                for (int i = validBoards.size() - 1; i >= 0; i--) {
                    Board validBoard = validBoards.get(i);
                    if (!stack.contains(validBoard)) {
                        stack.push(validBoard);
                        visitedHash.add(validBoard);
                    }
                }
            }
        }
        // analyze the result and write it to the output file
        if (!result.isEmpty()) {
            writer.write(pathToString(result));
            writer.write("\nNum: " + numOfNodes + "\nCost: " + costOfPath);
        } else {
            writer.write("no path\nNum: " + numOfNodes + "\nCost: inf");
        }
    }

    //Updates the details of a successor board, including its g(n), h(n), and f(n) values.
    private static Board updateBoardDetails(Board successor, Board current, Operator op, Board goalBoard) {
        numOfNodes++;
        int moveCost = op.getMarble().getCost();
        successor.setG(current.getG() + moveCost);
        successor.setH(heuristic(successor, goalBoard));
        successor.setF(successor.getG() + successor.getH());
        return successor;
    }


    /**
     * this function gets the current board and gets a list of all the possible operators for this state.
     * operator is like a pair of marble and an allowed movement of it
     * for each marble on the board checks what movements are allowed for it to do and returns a list
     */
    private static ArrayList<Operator> generateAllOperators(Board currentBoard) {
        ArrayList<Operator> allOperators = new ArrayList<>();
        Marble[][] currentMarbleBoard = currentBoard.getMarbleBoard();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Marble marble = currentMarbleBoard[i][j];
                if (!marble.getColor().equals("R") && !marble.getColor().equals("G") && !marble.getColor().equals("B")) {
                    continue; // Skip empty or invalid marbles
                }

                boolean[] possibleMoves = marble.allowedOperators(currentMarbleBoard);
                Position currentPosition = new Position(i, j);

                if (possibleMoves[0]) { // up
                    Position newPosition = new Position((i - 1 + 3) % 3, j);
                    allOperators.add(new Operator(marble, "up", currentPosition, newPosition));
                }
                if (possibleMoves[1]) { // down
                    Position newPosition = new Position((i + 1) % 3, j);
                    allOperators.add(new Operator(marble, "down", currentPosition, newPosition));
                }
                if (possibleMoves[2]) { // left
                    Position newPosition = new Position(i, (j - 1 + 3) % 3);
                    allOperators.add(new Operator(marble, "left", currentPosition, newPosition));
                }
                if (possibleMoves[3]) { // right
                    Position newPosition = new Position(i, (j + 1) % 3);
                    allOperators.add(new Operator(marble, "right", currentPosition, newPosition));
                }
            }
        }

        return allOperators;
    }

    public static void reset() {
        numOfNodes = 0;  // Num:
        costOfPath = 0;  // Cost:
        timeTook = 0;
    }

    // function that receives a board and the operator it wants to fulfill and returns the new board.
    public static Board boardFromOperator(Board prevBoard, Operator op) {
        Marble[][] map = new Marble[3][3];
        Marble[][] originalBoard = prevBoard.getMarbleBoard();

        // create a deep copy of the original board first
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                map[i][j] = new Marble(originalBoard[i][j]);
            }
        }
        op.marble = map[op.prevPos.getI()][op.prevPos.getJ()];

        // then apply the move
        if (op.move.equals("up")) {
            map = op.marble.moveUp(map);
        }
        if (op.move.equals("down")) {
            map = op.marble.moveDown(map);
        }
        if (op.move.equals("left")) {
            map = op.marble.moveLeft(map);
        }
        if (op.move.equals("right")) {
            map = op.marble.moveRight(map);
        }

        // Create the new board with correct parent and operator
        Board newBoard = new Board(map);
        newBoard.setParent(prevBoard);
        newBoard.setHowGotTo(op);

        return newBoard;
    }

    // function to retrieve the path by getting the last board
    // and using the howGotTo and the parent of the boards to find the way back to the start board
    public static ArrayList<Operator> retrievePath(Board lastOne) {
        ArrayList<Operator> path = new ArrayList<>();
        Board current = lastOne;  // maybe need to copy

        while (current.getParent() != null) {
            path.addFirst(current.getHowGotTo());  // add the operator that got to this board to the path
            current = current.getParent();  // move to the parent board to the previous one
        }
        return path;
    }


    // converts the path to the form: (2,2):B:(2,3)--(2,3):B:(1,3)--(3,1):G:(3,3)--(3,2):G:(2,2)
    public static String pathToString(ArrayList<Operator> path) {
        if (path.isEmpty()) {
            costOfPath = -1;
            return "no path";
        }
        StringBuilder thePath = new StringBuilder((path.getFirst().getPrevPos().toString() + ":"
                + path.getFirst().getMarble().getColor() + ":"
                + path.getFirst().getNextPos().toString()));
        costOfPath += path.getFirst().getMarble().getCost(); // will add the cost of the marble that we moved
        String xTOy = "";
        for (int i = 1; i < path.size(); i++) {
            thePath.append("--");
            xTOy = (path.get(i).getPrevPos() + ":" + path.get(i).getMarble().getColor() + ":" + path.get(i).getNextPos());
            costOfPath += path.get(i).getMarble().getCost(); // will add the cost of the marble that we moved
            thePath.append(xTOy);
        }
        return thePath.toString();
    }

    // a helper method to check if a given board is in the stack given or not
    private static boolean isInStack(Stack<Board> stack, Board board) {
        for (Board b : stack) {
            if (b.checkEquals(board.getMarbleBoard())) return true;
        }
        return false;
    }
}
