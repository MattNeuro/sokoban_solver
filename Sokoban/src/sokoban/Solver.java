
package sokoban;

import java.util.LinkedList;

import communication.Client;

import Map.Map;

/**
 *	Abstract solver base class.
 *
 *	This is the basis for each solver: a class which loads a history, a
 *	solution, and defines the maximum search depth for those solvers in
 *	our list that require it (such as BFS and DFS).
 *
 *	Note that all Solvers run in a separate thread; this means, that it
 *	is possible to run multiple solvers and whichever finishes first is
 *	able to send its solution to the server. So long as there are fewer
 *	solvers than there are CPU's in the client machine, this should not
 *	result in any degraded performance.
 *
 *	@author Matthijs
 */
abstract public class Solver extends Thread {

	protected 	LinkedList<Map>	states		= null;		// A history of visited states.
	protected	Map				solution	= null;		// Solution to the problem, used by most solvers.
	protected		   int		depth		=    0;		// Current depth for depth-based solvers.
	protected   static int		maxDepth 	=   200;		// Maximum depth for depth-based solvers.
	protected	static boolean	finished	= false;	// Are we done? If so, do not send other solutions.
	
	
	/**
	 *  Run the solver. Start with the current map in the MapHandler and
	 *  expand all the moves possible from that map (ie, state).
	 * @throws Exception 
	 */
	public Solver () {
		System.out.println("Starting solver " + this.getClass().getName() + ".");
		states 		= new LinkedList<Map>();
		this.start();
	}
	

	/**
	 * 	Whenever a thread starts, its run method is called. This method will
	 * 	in turn call the default solve() method, and when that has yielded a
	 * 	solution, set that solution and end all other running threads.
	 */
	public void run () {
		long start		= System.currentTimeMillis();		
		Map  solution 	= solve(Sokoban.getMapHandler().getMap());
		if (finished)
			System.err.println("\nError:\tsolution already send.");
		else
			this.sendSolution(solution);
		long end		= System.currentTimeMillis();
		finished 		= true;
		System.out.println("Solving with " + this.getClass().getName() + " took " + (int)(end - start) + " ms");
	}	
	
	
	/**
	 * 	The solution has been found by one of our solvers: set it here
	 * 	and send it on to the client.
	 * 
	 * 	@param solution
	 */
	synchronized protected void sendSolution (Map solution) {
		try {
			Client client = Sokoban.getClient();
			client.sendSolution(solution.getMoveString());
		} catch (Exception e) {
			System.err.println("Could not send solution to server.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 	Are we finished? If another Solver finished solving the puzzle,
	 * 	there is no need to continue looking. Iterative solvers should
	 * 	regularly call this method so our program can terminate sooner
	 * 	after we find a solution.
	 * 
	 * 	@return	Either true (solving is finished) or false (still busy).
	 */
	synchronized protected static boolean isFinished () {
		return finished;
	}
	
	
	/**
	 * 	Abstract solve method.
	 * 
	 * 	This is where the magic happens. Extending classes should implement
	 * 	their solution using this solve method.
	 * 
	 * 	@param 	map		The original map to work from.
	 * 	@return			The solution in the form of a Map object.
	 */
	abstract protected Map solve (Map map);

	
	/**
	 * 	Print the current state of the solver.
	 * 
	 * 	@param map	The map we just evaluated.
	 */
	protected void printCurrentState (Map map) {
		System.out.println("---------");
		map.printMap();
		System.out.println("+++++++++");
		System.out.println("Checked " + states.size() + " maps");
	}
}