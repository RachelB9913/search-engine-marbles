import java.io.*;
import java.util.Objects;

class InputParser {
    public static parsedData parseInput(String fileName) {
        String algoName = "";
        boolean time = false;
        boolean openList = false;
        Marble[][] startBoard = new Marble[3][3];
        Marble[][] goalBoard = new Marble[3][3];
        Board start = null;
        Board goal = null;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            System.out.println("Reading file " + fileName);
            algoName = br.readLine();  // Read algo_name

            String timeLine = br.readLine();  // Read time
            time = Objects.equals(timeLine, "with time");

            String openListLine = br.readLine();  // Read open_list
            openList = !Objects.equals(openListLine, "no open");

            // Read start_board
            for (int i = 0; i < 3; i++) {
                String[] row = br.readLine().split(",");
                for (int j = 0; j < 3; j++) {
                    startBoard[i][j] = new Marble(row[j], new Position(i, j));
                }
                start = new Board(startBoard, false, null);
            }

            br.readLine();  // Skip "Goal state:" line

            // Read after-goal board
            for (int i = 0; i < 3; i++) {
                String[] row = br.readLine().split(",");
                for (int j = 0; j < 3; j++) {
                    goalBoard[i][j] = new Marble(row[j], new Position(i, j));
                }
                goal = new Board(goalBoard, false);
            }

            // Ensure boards are always initialized
            if (startBoard[0][0] == null || goalBoard[0][0] == null) {
                System.out.println("Error: Boards not properly initialized");
                return null;
            }
            start = new Board(startBoard, false);  // Ensure this constructor sets board correctly
            goal = new Board(goalBoard, true);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
        return new parsedData(algoName, time, openList, start, goal);
    }

    public static void printBoard(Marble[][] board) {
        for (Marble[] row : board) {
            for (Marble cell : row) {
                System.out.print(cell.getColor() + " ");
            }
            System.out.println();
        }
    }
}

class parsedData {
    private final String algoName;
    final boolean time;
    private final boolean openList;
    private final Board startBoard;
    private final Board goalBoard;

    // deep coping in order for it to work correctly
    public parsedData(String algoName, boolean time, boolean openList, Board startBoard, Board goalBoard) {
        this.algoName = algoName;
        this.time = time;
        this.openList = openList;

        // Create deep copies of boards
        Marble[][] startBoardCopy = new Marble[3][3];
        Marble[][] goalBoardCopy = new Marble[3][3];

        Marble[][] originalStartBoard = startBoard.getMarbleBoard();
        Marble[][] originalGoalBoard = goalBoard.getMarbleBoard();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                startBoardCopy[i][j] = new Marble(originalStartBoard[i][j]);
                goalBoardCopy[i][j] = new Marble(originalGoalBoard[i][j]);
            }
        }
        this.startBoard = new Board(startBoardCopy, false);
        this.goalBoard = new Board(goalBoardCopy, true);
    }

    public String getAlgoName() {
        return algoName;
    }

    public boolean getTime() {
        return time;
    }

    public boolean getOpenList() {
        return openList;
    }

    public Board getStartBoard() {
        return startBoard;
    }

    public Marble[][] getGoalMarbleBoard() {
        Marble[][] goalBoard = new Marble[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                goalBoard[i][j] = this.goalBoard.getMarbleBoard()[i][j];
            }
        }
        return goalBoard;
    }

    public Board getGoalBoard() {
        return goalBoard;
    }

    public  Marble getMarble(Marble[][] board, int i, int j) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) {
            throw new IndexOutOfBoundsException("Position out of board bounds.");
        }
        return board[i][j];
    }

}