import java.util.Random;
import java.lang.StringBuilder;

class Maze {
    private static final int EAST = 0;
    private static final int SOUTH = 1;
    private static final int VISITED = 2;
    private static final int VIRGIN = 0;
    private static final int TOUCHED = 1;
    private static final int EXPLORED = 2;
    final int SIZE;
    final int FULL_SIZE;
    int[][] maze;

    void debug(String s) {
        System.out.print(s);
        try {
            Thread.sleep(500);
        }
        catch (Exception ignored) {}
    }

    Maze(int size) {
        SIZE = size;
        FULL_SIZE = SIZE*SIZE;
        maze = new int[4][FULL_SIZE];
    }

    void randomise() {
        Random rand = new Random(System.nanoTime());
        for (int col = 0; col < FULL_SIZE; col++) {
            // Bound on EAST
            if ((col + 1) % SIZE != 0)
                maze[EAST][col] = rand.nextInt(2);
            // Bound on SOUTH
            if (col < FULL_SIZE - SIZE)
                maze[SOUTH][col] = rand.nextInt(2);
        }
    }

    void mazify() {
        // Clear any visited data.
        for (int i = 0; i < maze[EAST].length; i++)
            maze[VISITED][i] = VIRGIN;

        int[] cell_stack = new int[FULL_SIZE];
        int stack_pointer = 1;
        int visited_cells = 1;
        int current_cell = 0;
        maze[VISITED][current_cell] = EXPLORED;

        while (visited_cells < FULL_SIZE) {
            // Pick a destination where to go.
            int destination = pick_destination(current_cell);

            // If no good destination, pop.
            if (destination == -1) 
                current_cell = cell_stack[stack_pointer--];

            // Call the builders, knock the wall.
            else {
                if (destination < current_cell) {
                    if (current_cell - destination == 1)
                        maze[EAST][destination] = 1; // Cell to the WEST.
                    else 
                        maze[SOUTH][destination] = 1; // Cell to the NORTH.
                }
                else {
                    if (destination - current_cell == 1)
                        maze[EAST][current_cell] = 1; // Cell to the EAST.
                    else 
                        maze[SOUTH][current_cell] = 1; // Cell to the SOUTH.
                }
                
                // Move to that cell, mark as explored, push on the stack, increment visited cells.
                current_cell = destination;
                maze[VISITED][destination] = EXPLORED;
                cell_stack[stack_pointer++] = current_cell;
                visited_cells++;
            }

            // Uncomment the following four lines if you want to see the generation in action.
//            try {
//                this.print();
//                Thread.sleep(100);
//            } catch (Exception ignored) {}
        }
    }

    int pick_destination(int current) {
        // Walls will be an array, holding four 1s or 0s, depending on the existence of those walls.
        // After that, for each existing wall, we check if we have a path.
        // If a path exists, we mark it as 0 (no wall).
        // Note that this is the reverse of how the original array is encoded.
        int[] walls = get_walls(current);
        if ((walls[0] + walls[1] + walls[2] + walls[3]) < 1)
            return -1;

        // Randomly pick one of four destinations.
        Random rand = new Random(System.nanoTime());
        while (true) {
            int result = rand.nextInt(4);
            switch (result) {
                // EAST
                case 0 : if (walls[0] == 1) 
                            return current + 1;

                // SOUTH
                case 1 : if (walls[1] == 1) 
                            return current + SIZE;

                // WEST
                case 2 : if (walls[2] == 1) 
                            return current - 1;

                // NORTH
                case 3 : if (walls[3] == 1) 
                            return current - SIZE;
            }
        }
    }

    int[] get_walls(int current) {
        int[] result = new int[4];

        // EAST.
        if ((current + 1) % SIZE != 0)
            if (maze[VISITED][current + 1] == VIRGIN)
                result[0] = 1;

        // SOUTH.
        if (current + SIZE < FULL_SIZE)
            if (maze[VISITED][current + SIZE] == VIRGIN)
                result[1] = 1;

        // WEST.
        if (current % SIZE != 0)
            if (maze[VISITED][current - 1] == VIRGIN)
                result[2] = 1;

        // NORTH.
        if (current - SIZE >= 0)
            if (maze[VISITED][current - SIZE] == VIRGIN)
                result[3] = 1;

        return result;
    }

    void bfs_solve() {
        int[] queue = new int[FULL_SIZE];
        int[] order = new int[FULL_SIZE];
        int head = 1;
        int tail = 1;
        int current = 0;
        int total_visited = 0;
        int destination;

        for (int i = 0; i < FULL_SIZE; i++)
            maze[VISITED][i] = VIRGIN;

        while (current != (FULL_SIZE - 1)) {
            if (total_visited == (FULL_SIZE - 1)) {
                System.out.println("Could not find a solution :(");
                break;
            }

            // EAST.
            destination = current + 1;
            if (destination % SIZE != 0)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[EAST][current] == 1) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                    }

            // SOUTH.
            destination = current + SIZE;
            if (destination < FULL_SIZE)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[SOUTH][current] == 1) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                    }

            // WEST.
            destination = current - 1;
            if (current % SIZE != 0)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[EAST][destination] == 1) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                    }

            // NORTH.
            destination = current - SIZE;
            if (destination >= 0)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[SOUTH][destination] == 1) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                    }

            if (tail - head == 0) {
                System.out.println("Queue empty. Couldn't find a solution.");
                break;
            }

            order[current] = total_visited;
            total_visited++;
            maze[VISITED][current] = EXPLORED;
            current = queue[tail++];
            try {
                this.print(order, "%3d");
                Thread.sleep(1000);
            } catch (Exception ignored) {}
        }

        print(order, "%3d");
    }

//    void print_debug() {
//        StringBuilder output = new StringBuilder(SIZE*10);
//
//        output.append("cell: ");
//        for (int col = 0; col < FULL_SIZE; col++)
//            output.append(String.format("%3d", col%10 + 1));
//        output.append("\neast: ");
//        for (int col = 0; col < FULL_SIZE; col++)
//            output.append(String.format("%3d", maze[0][col]));
//        output.append("\nsout: ");
//        for (int col = 0; col < FULL_SIZE; col++)
//            output.append(String.format("%3d", maze[1][col]));
//        output.append("\nvisi: ");
//        for (int col = 0; col < FULL_SIZE; col++)
//            output.append(String.format("%3d", maze[2][col]));
//        output.append("\n");
//
//        System.out.println(output.toString());
//    }

    void print() {
        print(new int[FULL_SIZE], "   ");
    }

    void print(int[] data, String format) {
        StringBuilder output = new StringBuilder(FULL_SIZE*10);

        output.append("+   ");
        for (int col = 1; col < SIZE; col++)
            output.append("+---");
        output.append("+\n");

        for (int row = 0; row < SIZE; row++) {
            output.append("|");
            for (int col = 0; col < SIZE - 1; col++) {
                output.append(String.format(format, data[row * SIZE + col]));
                if (maze[EAST][row * SIZE + col] == 1)
                    output.append(" ");
                else
                    output.append("|");
            }
            output.append(String.format(format, data[(row + 1) * SIZE - 1]));
            output.append("|\n");

            if (row == SIZE - 1)
                break;

            output.append("+");
            for (int col = 0; col < SIZE; col++) {
                if (maze[SOUTH][row * SIZE + col] == 1)
                    output.append("   +");
                else
                    output.append("---+");
            }
            output.append("\n");
        }

        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+   +\n");

        System.out.println(output.toString());
    }
}

class MazeSolve {
    public static void main(String[] args) {
        Maze maze = new Maze(5);
        //maze.randomise();
        maze.print();
        maze.mazify();
        maze.print();
        maze.bfs_solve();
    }
}
