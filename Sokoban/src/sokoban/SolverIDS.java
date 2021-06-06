package sokoban;

import java.util.LinkedList;

import Map.Map;
import Map.Move;

/**
 * 	Iterative Deepening Search
 * 
 * 	Keep going deeper and deeper till we find a solution. Current
 *  depth increment is set to 2, which, combined with a quite low
 *  branching factor (max 4, but on average probably more like 2)
 *  means, that each new iteration is almost 4 times as extensive
 *  as the previous one. Ergo, we lose only about 20% performance
 *  by this. Sounds pretty bad, sure, but it is better than a max
 *  depth limit which may or may not ever be reached.
 * 
 * 	@author Matthijs
 */
class SolverIDS extends Solver {

	private static int		currentMaximum	=    4;
	private	LinkedList<Map>	queue 			= null;	
	

	public Map solve (Map map) {
		while (currentMaximum < maxDepth && solution == null && !Solver.isFinished()) {
			queue = new LinkedList<Map>();
			//System.out.println("Now searching at depth " + currentMaximum + ".");
			queue.push(map);
			while (!queue.isEmpty())
				search();
			currentMaximum += 2;
			states.clear();
		}
		return solution;
	}
	
	
	/**
	 * 	Search a specific map. If we've found a solution, returns
	 * 	so we exit our search tree. If not, keeps expanding child
	 * 	nodes till we do find a solution.
	 */
	private void search () {
		Map map 	= queue.pop();
		int level 	= map.getDepth();
		
		if (solution != null || states.contains(map) || level > currentMaximum)
			return;

		if (map.isSolved())
			solution = map;
		states.add(map);
				
		for (Move move : map.findMoves()) {
			Map child = new Map(map);
			if (!child.willBlock(move))
				child.performMove(move);
			queue.push(child);
		}
	}
}
