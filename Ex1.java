import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

// size of the boards is 3x3  (if not to change and use the length func.)

public class Ex1 {

    static ArrayList<Operator> path = new ArrayList<>();
    static int numOfNodes;  // Num:
    static int costOfPath;  // Cost:
    static double timeTook;

    public static void main(String[] args) throws IOException {

        parsedData data = InputParser.parseInput("C:/Users/rache/IdeaProjects/search_engine_marbles/src/input.txt");

        String filePath = "output1.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

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

        // check that allowed operators function works
        Marble m = data.getMarble(goalBoard, 1, 0);

        boolean[] list = m.allowedOperators(goalBoard);
        System.out.println("Allowed operators: " + Arrays.toString(list) + " for: " + m.getColor() + m.getPos().toString());


        long startTime = System.nanoTime(); // start measuring the time
        switch (algoName) {
            case "BFS": {
                reset();
                runBFS(start, goal, writer);
                break;
            }
            case "DFID": {
                reset();
                runDFID(startBoard, goalBoard);
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
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0; //duration and paring to seconds
        if (time) {
            timeTook = durationInSeconds;  // and write it to the output file
            String theTime = String.valueOf(timeTook);
            writer.newLine();
            writer.write(theTime + "seconds");
        }
    }

//// FUNCTIONS!!! ////

    private static void runBFS(Board startBoard, Board goalBoard, BufferedWriter writer) throws IOException {
        // add code!!!!
        boolean goal = false;
        ArrayList<Board> queue = new ArrayList<>();  // to think maybe to add a class for boards as nodes
        queue.add(startBoard);
        while (!queue.isEmpty()) {
            Board currentBoard = queue.removeLast(); //take the last added board
            ArrayList<Operator> allOperators = generateAllOperators(currentBoard); //find all the possible operators for this state
            for (Operator op : allOperators) {
                Board curr = boardFromOperator(currentBoard, op); // the state we get when applying the given operator on the current state
                numOfNodes ++; // will count each time a new state (board - node) was created
                costOfPath += op.getMarble().getCost(); // will add the cost of the marble that we moved
                goal = curr.checkIsGoal(goalBoard.getMarbleBoard());
                if (goal) {
                    ArrayList<Operator> path = retrievePath(curr, startBoard); // function to retrieve the path
                    String pathToString = pathToString(path);//  a to-string to the path
                    writer.write(pathToString); // write path to output.txt
                    writer.newLine();
                    writer.write("Num: " + numOfNodes);
                    writer.newLine();
                    writer.write("Cost: " + costOfPath);
                    return;
                }
                else{
                    queue.add(curr);
                }
            }
        }
        System.out.println("BFS didn't succeed!");
    }


    private static void runDFID(Marble[][] startBoard, Marble[][] goalBoard) {

        // add code!!!! and understand what are the parameters needed for it

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
                boolean[] possible = m.allowedOperators(currentBoard.getMarbleBoard());
                Position prev = new Position(i, j);
                if (possible[0]) { // up
                    Position curr = new Position((i - 1 + 3) % 3, j);
                    Operator op = new Operator(m,"up",prev,curr);
                    allOperators.add(op);
                }if (possible[1]) { // down
                    Position curr = new Position((i + 1) % 3, j);
                    Operator op = new Operator(m,"down",prev,curr);
                    allOperators.add(op);
                }if (possible[2]) { // left
                    Position curr = new Position(i, (j - 1 + 3) % 3);
                    Operator op = new Operator(m,"left",prev,curr);
                    allOperators.add(op);
                }if (possible[3]) { // right
                    Position curr = new Position(i, (j + 1) % 3);
                    Operator op = new Operator(m,"right",prev,curr);
                    allOperators.add(op);
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
        StringBuilder thePath = new StringBuilder((path.getFirst().getPrevPos().toString() + ":"
                                                    + path.getFirst().getMarble().getColor() + ":"
                                                    + path.getFirst().getNextPos().toString()));
        String xTOy = "";
        for (int i=1;i<path.size();i++) {
            thePath.append("--");
            xTOy = (path.get(i).getPrevPos() + ":" + path.get(i).getMarble().getColor() + ":" + path.get(i).getNextPos());
            thePath.append(xTOy);
        }
        return thePath.toString();
    }




}

