package sjsu.nikolov.cs146.project3;
import static org.junit.Assert.*;
import org.junit.Test;


public class MazeSolveTester
{
	@Test
	public void maze04()
	{
		Maze maze = new Maze(4);
		maze.mazify();
		System.out.println("Maze of size " + 4);
		System.out.println("Solving using DFS.");
		int dfs = maze.dfs_solve();
		System.out.println("Solving using BFS.");
		int bfs = maze.bfs_solve();
		System.out.println("DFS moves: " + dfs + "\nBFS moves: " + bfs);
		System.out.println("--------------------------------------------------");
	}
	
	@Test
	public void maze05()
	{
		Maze maze = new Maze(5);
		maze.mazify();
		System.out.println("Maze of size " + 5);
		System.out.println("Solving using DFS.");
		int dfs = maze.dfs_solve();
		System.out.println("Solving using BFS.");
		int bfs = maze.bfs_solve();
		System.out.println("DFS moves: " + dfs + "\nBFS moves: " + bfs);
		System.out.println("--------------------------------------------------");
	}

	@Test
	public void maze06()
	{
		Maze maze = new Maze(6);
		maze.mazify();
		System.out.println("Maze of size " + 6);
		System.out.println("Solving using DFS.");
		int dfs = maze.dfs_solve();
		System.out.println("Solving using BFS.");
		int bfs = maze.bfs_solve();
		System.out.println("DFS moves: " + dfs + "\nBFS moves: " + bfs);
		System.out.println("--------------------------------------------------");
	}

	@Test
	public void maze07()
	{
		Maze maze = new Maze(7);
		maze.mazify();
		System.out.println("Maze of size " + 7);
		System.out.println("Solving using DFS.");
		int dfs = maze.dfs_solve();
		System.out.println("Solving using BFS.");
		int bfs = maze.bfs_solve();
		System.out.println("DFS moves: " + dfs + "\nBFS moves: " + bfs);
		System.out.println("--------------------------------------------------");
	}

	@Test
	public void maze08()
	{
		Maze maze = new Maze(8);
		maze.mazify();
		System.out.println("Maze of size " + 8);
		System.out.println("Solving using DFS.");
		int dfs = maze.dfs_solve();
		System.out.println("Solving using BFS.");
		int bfs = maze.bfs_solve();
		System.out.println("DFS moves: " + dfs + "\nBFS moves: " + bfs);
		System.out.println("--------------------------------------------------");
	}

	@Test
	public void maze10()
	{
		Maze maze = new Maze(10);
		maze.mazify();
		System.out.println("Maze of size " + 10);
		System.out.println("Solving using DFS.");
		int dfs = maze.dfs_solve();
		System.out.println("Solving using BFS.");
		int bfs = maze.bfs_solve();
		System.out.println("DFS moves: " + dfs + "\nBFS moves: " + bfs);
		System.out.println("--------------------------------------------------");
	}
	
	@Test
	public void maze_unsolvable() {
		Maze maze = new Maze(10);
		maze.mazify();
		System.out.println("Maze of size " + 10);
		System.out.println("Making maze unsolvable.");
		maze.render_unsolvable();
		System.out.println("Solving using DFS.");
		int dfs = maze.dfs_solve();
		System.out.println("Solving using BFS.");
		int bfs = maze.bfs_solve();
		System.out.println("DFS moves: " + dfs + "\nBFS moves: " + bfs);
		System.out.println("--------------------------------------------------");
	}
}

