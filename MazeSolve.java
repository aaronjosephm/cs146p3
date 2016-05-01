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
    private static final String[] d = {"East", "South", "West", "North"};  // debug var

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
        maze = new int[3][FULL_SIZE];
    }

    void randomise() {
        Random rand = new Random(System.nanoTime());
        for (int col = 0; col < FULL_SIZE; col++) {
            // Bound on EAST
            if ((col + 1)%SIZE != 0)
                maze[EAST][col] = rand.nextInt(2);
            // Bound on SOUTH
            if (col < FULL_SIZE - SIZE)
                maze[SOUTH][col] = rand.nextInt(2);
        }
    }

    void mazify() {
        // Clear any visited data.
        for (int i = 0; i < maze.length; i++)
            maze[VISITED][i] = VIRGIN;

        int[] cell_stack = new int[FULL_SIZE];
        int stack_pointer = 0;
        int visited_cells = 1;
        int current_cell = 0;
        maze[VISITED][current_cell] = EXPLORED;

        while (visited_cells < FULL_SIZE) {
            int direction = pick_direction(current_cell);

            // If no good direction, pop.
            if (direction == -1) 
                current_cell = cell_stack[stack_pointer--];

            // Call the builders, knock the wall.
            else {
                if (direction < current_cell) {
                    // Cell to the WEST.
                    if (current_cell - direction == 1)
                        maze[EAST][direction] = 1;
                    else // NORTH.
                        maze[SOUTH][direction] = 1;
                }
                else {
                    // Cell to the EAST.
                    if (direction - current_cell == 1)
                        maze[EAST][current_cell] = 1;
                    else
                        maze[SOUTH][current_cell] = 1;
                }
                
                current_cell = direction;
                maze[VISITED][direction] = EXPLORED;
                cell_stack[++stack_pointer] = current_cell;
                visited_cells++;
            }
//            this.print(VISITED);
            try {
                Thread.sleep(3);
            }
            catch (Exception e) {}
        }
    }

    int pick_direction(int current) {
        Random rand = new Random(System.nanoTime());

        // Walls will be an array, holding four 1s or 0s, depending on the existence of those walls.
        // After that, for each existing wall, we check if we have a path.
        // If a path exists, we mark it as 0 (no wall).
        // Note that this is the reverse of how the original array is encoded.
        int[] walls = count_walls(current);
        for (int i = 0; i < walls.length; i++)
            if (walls[i] == 1)
                // Here i is direction. 0 = EAST, 1 = SOUTH, 2 = WEST, 3 = NORTH
                if (is_connected(current, i))
                    walls[i] = 0;

        if ((walls[0] + walls[1] + walls[2] + walls[3]) < 1)
            return -1;

        // Randomly pick one of four directions.
        while (true) {
            int result = rand.nextInt(4);
            switch (result) {
                // EAST, make sure we're not at the edge.
                case 0 : if (walls[0] == 1) {
//                            debug("going: " + d[0] + "\n\n");
                            return current + 1;
                         }
                         break;
                // SOUTH
                case 1 : if (walls[1] == 1) {
//                            debug("going: " + d[1] + "\n\n");
                            return current + SIZE;
                         }
                         break;
                // WEST
                case 2 : if (walls[2] == 1) {
//                            debug("going: " + d[2] + "\n\n");
                            return current - 1;
                         }
                         break;
                // NORTH
                case 3 : if (walls[3] == 1) {
//                            debug("going: " + d[3] + "\n\n");
                            return current - SIZE;
                         }
                         break;
            }
            
        }
    }

    int[] count_walls(int current) {
        int[] result = new int[4];
        // EAST.
        if ((current + 1) % SIZE != 0)
            if (maze[EAST][current] == 0)
                result[0] = 1;
        // SOUTH.
        if (current + SIZE < FULL_SIZE)
            if (maze[SOUTH][current] == 0)
                result[1] = 1;
        // WEST.
        if (current % SIZE != 0)
            if (maze[EAST][current - 1] == 0)
                result[2] = 1;
        // NORTH.
        if (current - SIZE >= 0)
            if (maze[SOUTH][current - SIZE] == 0)
                result[3] = 1;
        return result;
    }

    boolean is_connected(int cell_a, int direction) {
//        debug("is_connected(" + cell_a + ", " + d[direction] + ") ");
        int cell_b = -1;
        if (direction == 0)
            cell_b = cell_a + 1;
        else if (direction == 1)
            cell_b = cell_a + SIZE;
        else if (direction == 2)
            cell_b = cell_a - 1;
        else if (direction == 3)
            cell_b = cell_a - SIZE;
        else {
            System.out.println("Error in is_connected. Direction was " + direction);
            System.exit(1);
        }
       
        boolean result = maze[VISITED][cell_b] == EXPLORED;
//        debug(result?"yes\n":"no\n");
        return result;
    }

//    boolean bfs_is_connected(int cell_a, int cell_b) {
////        debug("bfs_is_connected(" + cell_a + ", " + cell_b + ")");
//        for (int i = 0; i < FULL_SIZE; i++) 
//            maze[VISITED][i] = VIRGIN;
//        maze[VISITED][cell_a] = VISITED;
//
//        int[] queue = new int[FULL_SIZE];
//        queue[0] = cell_a;
//        int head = 1;
//        int tail = 1;
//        int current = cell_a;
//
//        while (tail - head > 0) {
////            print_debug();
////            for (int i = head; i < tail; i++)
////                System.out.print(queue[i]);
////            System.out.println();
//            // If EAST is connected and not visited, enqueue.
//            // Unless it's the destination, in which case we return true;
//            if (maze[EAST][current] == 1) { 
//                debug(current + " is connected to " + (current + 1));
//                if (current + 1 == cell_b) {
//                    debug("arrived at destination");
//                    return true;
//                }
//                if (maze[VISITED][current + 1] == VIRGIN) {
//                    debug("enqueing " + (current + 1));
//                    maze[VISITED][current + 1] = TOUCHED;
//                    queue[tail++] = current + 1;
//                }
//            }
//            // If SOUTH is connected and not visited, enqueue.
//            // Unless it's the destination, in which case we return true.
//            if (maze[SOUTH][current] == 1) {
//                debug(current + " is connected to " + (current + SIZE));
//                if (current + SIZE == cell_b)
//                    return true;
//                if (maze[VISITED][current + SIZE] == VIRGIN) {
//                    debug("enqueing " + (current + SIZE));
//                    maze[VISITED][current + SIZE] = TOUCHED;
//                    queue[tail++] = current + SIZE;
//                }
//            }
//            // If WEST is connected and not visited, enqueue.
//            // Unless it's the destination, in which case we return true.
//            if (current % SIZE != 0) {
//                if (maze[EAST][current - 1] == 1) {
//                    debug(current + " is connected to " + (current - 1));
//                    if (current - 1 == cell_b)
//                        return true;
//                    if (maze[VISITED][current - 1] == VIRGIN) {
//                        debug("enqueing " + (current - 1));
//                        maze[VISITED][current - 1] = TOUCHED;
//                        queue[tail++] = current - 1;
//                    }
//                }
//            }
//            // If NORTH is connected and not visited, enqueue.
//            // Unless it's the destination, in which case we return true.
//            if (current - SIZE >= 0) {
//                if (maze[SOUTH][current - SIZE] == 1) {
//                    debug(current + " is connected to " + (current - SIZE));
//                    if (current - SIZE == cell_b)
//                        return true;
//                    if (maze[VISITED][current - SIZE] == VIRGIN) {
//                        debug("enqueing " + (current - SIZE));
//                        maze[VISITED][current - SIZE] = TOUCHED;
//                        queue[tail++] = current - 1;
//                    }
//                }
//            }
//            
//            // Mark visited and pop.
//            debug(current + " is now VISITED and we pop " + queue[head]);
//            maze[VISITED][current] = EXPLORED;
//            current = queue[head++];
//        }
//        return false;
//    }
    
    void print_debug() {
        StringBuilder output = new StringBuilder(SIZE*10);

        output.append("cell: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", col%10 + 1));
        output.append("\neast: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", maze[0][col]));
        output.append("\nsout: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", maze[1][col]));
        output.append("\nvisi: ");
        for (int col = 0; col < FULL_SIZE; col++)
            output.append(String.format("%3d", maze[2][col]));
        output.append("\n");

        System.out.println(output.toString());
    }

    void print(int mode) {
        StringBuilder output = new StringBuilder(SIZE*10);

        output.append("+   ");
        for (int col = 1; col < SIZE; col++)
            output.append("+---");
        output.append("+\n");

        for (int row = 0; row < SIZE; row++) {
            output.append("|");
            for (int col = 0; col < SIZE - 1; col++) {
                if (maze[EAST][row * SIZE + col] == 1)
                    output.append("    ");
                else
                    output.append("   |");
            }
            output.append("   |\n");

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

    void print_count() {
        StringBuilder output = new StringBuilder(SIZE*10);

        output.append("+   ");
        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+\n");

        for (int row = 0; row < SIZE; row++) {
            output.append("|");
            for (int col = 0; col < SIZE - 1; col++) {
                int[] walls = count_walls(row * SIZE + col);
                int count = walls[0] + walls[1] + walls[2] + walls[3];
                if (maze[EAST][row * SIZE + col] == 1)
                    output.append(" " + count + "  ");
                else
                    output.append(" " + count + " |");
            }
            int[] walls = count_walls((row + 1) * SIZE - 1);
            int count = walls[0] + walls[1] + walls[2] + walls[3];
            output.append(" " + count + " |\n");

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
        Maze maze = new Maze(18);
        //maze.randomise();
        maze.print(2);
        maze.mazify();
        maze.print(2);
    }
}
