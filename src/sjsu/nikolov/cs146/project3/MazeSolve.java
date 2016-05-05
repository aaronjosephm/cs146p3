package sjsu.nikolov.cs146.project3;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The Maze class implements square grids representing mazes.
 * It has the capability to turn a grid into a perfect maze,
 * as well as solve it using a breadth-first or depth-first algorithm.
 * @author Aaron Mednick
 * @author Todor Nikolov
 *
 */
class Maze {
    /**
     * Solution is a helper class, resembling a tree, but in the wrong direction.
     * Nodes know their parents, but not their children. All nodes are in a HashMap,
     * so they can be retrieved fast. The goal here is to add nodes to this structure
     * as we solve a maze and quickly find the correct path of the solution.
     */
	class Solution {
        /**
         * Node class represents a node in the custom tree.
         * It contains only a pointer to its parent and a number for the cell it represents.
         */
		class Node {
            // cell stands for the cell number in the maze grid.
			int cell;
			Node parent;
			
            /**
             * Node constructor creates a node for the tree.
             * @param cell - The number of the cell we are representing.
             * @param parent - The node for the parent of this cell.
             */
			Node(int cell, Node parent) {
				this.cell = cell;
                this.parent = parent;
			}
		}
		
        // This map contains tree nodes with cell number as a key.
		Map<Integer, Node> paths = new HashMap<Integer, Node>();
		
        /**
         * Default constructor adds starting node.
         */
		Solution() {
			Node first_node = new Node(0, null);
			paths.put(0, first_node);
		}
		
        /**
         * Adds a node to the tree given a cell and it's parent.
         * @param parent - The previous cell we have traversed.
         * @param child - The current cell we are adding.
         */
		void add(int parent, int child) {
			Node parent_node = paths.get(parent);
			if (parent_node != null) { 
				Node new_node = new Node(child, parent_node);
				paths.put(child, new_node);
			}
		}
		
        /**
         * Once we have solved the maze, we invoke this method which 
         * locates the node representing the maze exit and it bubbles
         * up the tree through the parents, revealing the correct path.
         * @returns An array of size of the whole maze, containing '#' 
         * in cells where the path is happening.
         */
		char[] find_solution() {
			char[] result = new char[FULL_SIZE];
			
            // Put spaces everywhere, but the first and last position.
			result[0] = '#';
			for (int i = 1; i < FULL_SIZE - 1; i++) 
				result[i] = ' ';
			result[FULL_SIZE - 1] = '#';
			
            // Place '#' on the path.
			Node current_node = paths.get(FULL_SIZE - 1);
			while (current_node.parent.cell != 0) {
				result[current_node.cell] = '#';
				current_node = current_node.parent;
			}
			result[current_node.cell] = '#';
			
			return result;
		}
	}

	// Each cell has only two walls and EAST and SOUTH represent the right and bottom walls, respectively.
    private static final int EAST = 0;
    private static final int SOUTH = 1;
    
    // Each cell's VISITED state can be either VIRGIN, TOUCHED, or EXPLORED.
    private static final int VISITED = 2;
    private static final int VIRGIN = 0;
    private static final int TOUCHED = 1;
    private static final int EXPLORED = 2;
    private static final int CONNECTED = 1;
    
    // SIZE holds the row length of the maze, FULL_LENGTH holds the total number of cells.
    private final int SIZE;
    private final int FULL_SIZE;
    
    // Where the maze is stored.
    int[][] maze;

    /**
     * Constructor creating a blank maze of dimensions size*size
     * @param size - The row length of the maze.
     */
    Maze(int size) {
        SIZE = size;
        FULL_SIZE = SIZE*SIZE;
        maze = new int[4][FULL_SIZE];
    }
    
    /**
     * Constructor being fed a maze manually.
     * @param maze - The maze we want in this object.
     */
    Maze(int[][] maze) {
    	this.maze = maze;
    	SIZE = (int)Math.sqrt(maze[0].length);
    	FULL_SIZE = maze[0].length;
    	
    }

    /**
     * Randomly knock walls around. It does not follow any rules of perfect maze.
     */
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

    /**
     * Generates a perfect maze using a depth-first algorithm.
     */
    void mazify() {
        // Clear any visited data.
        maze[VISITED] = new int[FULL_SIZE];

        // Create a stack and initialize number of visited cells, starting cell, and stack pointer.
        // The stack is the part that makes it depth-first method.
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
                        maze[EAST][destination] = CONNECTED; // Cell to the WEST.
                    else 
                        maze[SOUTH][destination] = CONNECTED; // Cell to the NORTH.
                }
                else {
                    if (destination - current_cell == 1)
                        maze[EAST][current_cell] = CONNECTED; // Cell to the EAST.
                    else 
                        maze[SOUTH][current_cell] = CONNECTED; // Cell to the SOUTH.
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

    /**
     * From a given cell, pick a destination where to go.
     * @param current - The current cell (position in the maze array)
     * @return The cell number of where we should go.
     */
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
        while (true) 
            switch (rand.nextInt(4)) {
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

    /**
     * From a given cell, report on the walls around it.
     * We look in all four directions and report a 1 for each existing wall,
     * if the cell on the other side has not been visited.
     * @param current - The current cell (position in the maze array)
     * @return An array of four containing the "usability" of each surrounding wall.
     */
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

    /**
     * Solves a maze using a breadth-first algorithm.
     * @returns The length of the solution path.
     */
    int bfs_solve() {
    	Solution solution = new Solution();
    	
    	// Initialise a queue. Head and tail for it.
        int[] queue = new int[FULL_SIZE];
        int head = 1;
        int tail = 1;
        
        // Initialise current cell, nodes visited, and destination.
        int current = 0;
        int total_visited = 0;

        // Initialise an array that will hold the traversal order.
        // It gets passed to the print method.
        char[] order = new char[FULL_SIZE];
        for (int i = 0; i < FULL_SIZE; i++)
            order[i] = ' ';
        
        // Clear any visited data.
        maze[VISITED] = new int[FULL_SIZE];

        // At each cell, we enqueue its neighbors.
        // Then we dequeue and repeat until we reach the end.
        while (current != (FULL_SIZE - 1)) {
            // EAST.
            int destination = current + 1;
            if (destination % SIZE != 0)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[EAST][current] == CONNECTED) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                        solution.add(current, destination);
                    }

            // SOUTH.
            destination = current + SIZE;
            if (destination < FULL_SIZE)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[SOUTH][current] == CONNECTED) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                        solution.add(current, destination);
                    }

            // WEST.
            destination = current - 1;
            if (current % SIZE != 0)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[EAST][destination] == CONNECTED) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                        solution.add(current, destination);
                    }

            // NORTH.
            destination = current - SIZE;
            if (destination >= 0)
                if (maze[VISITED][destination] == VIRGIN) 
                    if (maze[SOUTH][destination] == CONNECTED) {
                        maze[VISITED][destination] = TOUCHED;
                        queue[head++] = destination;
                        solution.add(current, destination);
                    }

            // Couldn't reach the end.
            if (tail - head == 0) {
                System.out.println("Queue empty. Couldn't find a solution.");
                return -1;
            }

            // Mark step number in the order traversal array, increase visited,
            // mark current as explored, and dequeue.
            order[current] = (char)(total_visited % 10 + '0');
            total_visited++;
            maze[VISITED][current] = EXPLORED;
            current = queue[tail++];
            
            // Uncomment the following four lines if you want to see the maze being solved.
//            try {
//                this.print(order, " %c ");
//                Thread.sleep(100);
//            } catch (Exception ignored) {}
        }
        
        // Add the final move.
        order[current] = (char)(total_visited % 10 + '0');

        // Print the resulting maze and solution.
        print(order, " %c ");
        print(solution.find_solution(), " %c ");
        
        return head;
    }

    /**
     * This method will wall off the last cell's NORTH and WEST,
     * rendering the maze unsolvable. Good for tests.
     */
    void render_unsolvable() {
        maze[EAST][FULL_SIZE - 2] = 0;
        maze[SOUTH][FULL_SIZE - SIZE - 1] = 0;
    }
    
    /**
     * Print method. Front end.
     */
    void print() {
        print(new char[FULL_SIZE], "   ");
    }

    /**
     * Print method.
     * @param filler - An array of length FULL_SIZE containing the information we want printed in the centre of each cell.
     * @param format - The C's printf style formatting string parameter. e.g. %3c
     */
    void print(char[] filler, String format) {
    	// Start a new output dump. StringBuilder is faster than multiple System.out.println
        StringBuilder output = new StringBuilder(FULL_SIZE*10);

        // Top edge of the maze.
        output.append("+   ");
        for (int col = 1; col < SIZE; col++)
            output.append("+---");
        output.append("+\n");

        // Middle of the maze.
        for (int row = 0; row < SIZE; row++) {
        	// Do a row.
            output.append("|");
            for (int col = 0; col < SIZE - 1; col++) {
                output.append(String.format(format, filler[row * SIZE + col]));
                if (maze[EAST][row * SIZE + col] == CONNECTED)
                    output.append(" ");
                else
                    output.append("|");
            }
            output.append(String.format(format, filler[(row + 1) * SIZE - 1]));
            output.append("|\n");

            if (row == SIZE - 1)
                break;

            // Edge below the row we just did.
            output.append("+");
            for (int col = 0; col < SIZE; col++) {
                if (maze[SOUTH][row * SIZE + col] == CONNECTED)
                    output.append("   +");
                else
                    output.append("---+");
            }
            output.append("\n");
        }

        // Bottom edge.
        for (int col = 0; col < SIZE - 1; col++)
            output.append("+---");
        output.append("+   +\n");

        // Print, finally.
        System.out.println(output.toString());
    } 
    
    /**
     * Solves a maze using a depth-first algorithm.
     */
    int dfs_solve()
    {
        // This tree will contain the solution of the maze,
        // from which we can traverse backwards.
    	Solution solution = new Solution();

        // Initialize a stack and a pointer for it.
        int[] stack = new int[FULL_SIZE];
        int top = 0;

        // Initialize current cell, cells visited, and destination.
        int current = 0;
        int total_visited = 1;
        maze[VISITED][current] = EXPLORED;

        // Initialize an array that will hold the traversal order.
        // It gets passed to the print method.
        char[] order = new char[FULL_SIZE];
        for (int i = 0; i < FULL_SIZE; i++)
            order[i] = ' ';
        order[0] = '0';

        // Clear any visited data.
    	maze[VISITED] = new int[FULL_SIZE];

        while (current < FULL_SIZE - 1) {
            // Find which way we want to go.
            int destination = dfs_pick_destination(current);

            // If we dead end, pop the stack and check if we've run out of options.
            if (destination == -1) {
                current = stack[top--];
                if (top == -1) {
                    System.out.println("Stack empty. Couldn't find a solution.");
                    return -1;
                }
                continue;
            }
            else {
                // Store the traversal order and solution tree.
                order[destination] = (char)(total_visited % 10 + '0');
                solution.add(current, destination);

                // 
                current = destination;
                maze[VISITED][current] = EXPLORED;
                stack[top++] = current;
                total_visited++;
            }
            // Uncomment the following four lines to see the dfs in action.
//            try {
//                print(order, " %c ");
//                Thread.sleep(100);
//            }
//            catch (Exception e) {}
                
        }

        print(order, " %c ");
        print(solution.find_solution(), " %c ");
        
        return total_visited;
    }

    /**
     * Similar to the pick_destination method, but here we are interested
     * in cells that *are* connected to us and have not been traversed.
     * @param current - The current cell number.
     * @returns A direction in which to go. Priority: EAST, SOUTH, WEST, NORTH.
     */
    int dfs_pick_destination(int current) {
        // EAST.
        int destination = current + 1;
        if (destination % SIZE != 0) 
            if (maze[EAST][current] == CONNECTED)
                if (maze[VISITED][destination] == VIRGIN)
                    return destination;
        
        // SOUTH.
        destination = current + SIZE;
        if (destination < FULL_SIZE) 
            if (maze[SOUTH][current] == CONNECTED)
                if (maze[VISITED][destination] == VIRGIN)
                    return destination;

        // WEST.
        destination = current - 1;
        if (current % SIZE != 0) 
            if (maze[EAST][destination] == CONNECTED)
                if (maze[VISITED][destination] == VIRGIN)
                    return destination;

        // NORTH.
        destination = current - SIZE;
        if (destination >= 0) 
            if (maze[SOUTH][destination] == CONNECTED)
                if (maze[VISITED][destination] == VIRGIN)
                    return destination;
        
        return -1;
    }
}

/**
 * MazeSolve class contains a main method we use for testing.
 */
class MazeSolve {
    /**
     * Pretty straightforward. We generate a maze grid, print it, turn int into a maze, print it,
     * solve it DFS and BFS, and report the number of steps it took.
     */ 
    public static void main(String[] args) {
    	int size = 10;

    	Scanner scanner = new Scanner(System.in);
    	System.out.print("Enter a maze size (10 would be a good choice): ");
    	try {
    		size = scanner.nextInt();
    		if (size < 1 || size > 100) 
    			throw new Exception();
    	}
    	catch (Exception e) {
    		System.out.println("Bad size. Going with 10.\n");
    		size = 10;
    	}
    	finally {
    		scanner.close();
    	}
    	
        Maze maze = new Maze(size);
        System.out.println("Plain grid.");
        maze.print();
        System.out.println("Maze generated.");
        maze.mazify();
        maze.print();
        System.out.println("Solving using DFS.");
        int dfs = maze.dfs_solve();
        System.out.println("Solving using BFS.");
        int bfs = maze.bfs_solve();
        System.out.println("\nDFS moves: " + dfs + "\nBFS moves: " + bfs);
    }
}
