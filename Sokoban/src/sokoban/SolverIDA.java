package sokoban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Map.Heuristics;
import Map.Map;
import Map.Move;
import Map.NewNode;

/**
 * 	Iterative A* Search
 * 
 * 	Keep going deeper and deeper till we find a solution. Current
 *  depth increment is set to 2, which, combined with a quite low
 *  branching factor (max 4, but on average probably more like 2)
 *  means, that each new iteration is almost 4 times as extensive
 *  as the previous one. Ergo, we lose only about 20% performance
 *  by this. Sounds pretty bad, sure, but it is better than a max
 *  depth limit which may or may not ever be reached.
 * 
 * 	@author Mary
 */
class SolverIDA extends Solver {

	private static int		currentMaximum	=    4;
	private ArrayList<NewNode> nodes;
	private ArrayList<NewNode> closedSetNodes;
	private int height=0;
	
	
	

	public Map solve (Map map) {
		while (currentMaximum < maxDepth && solution == null && !Solver.isFinished()) {
			nodes = new ArrayList<NewNode>();
			closedSetNodes = new ArrayList<NewNode>();
			//System.out.println("Now searching at depth " + currentMaximum + ".");
			NewNode initialState = new NewNode(map, 0);
			this.nodes.add(initialState);
			while (this.nodes.size()>0)
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
	 * 
	 * 	@param map		Current map to expand.
	 * 	@param level	Current level at which we search. If this
	 * 					exceeds the current maximum search depth,
	 * 					return.
	 */
	private void search () {
		
		NewNode currentNode = this.nodes.remove(0);
		int level 	= currentNode.getMap().getDepth();
	
		if (depth%1000==0) {
			System.out.println("Depth is "+depth);
			System.out.println("We are at this map: ");
			currentNode.getMap().printMap();
			System.out.println("Which has a score of "+Heuristics.mapScore(currentNode.getMap()));
		}
		
		
		if (solution != null || states.contains(currentNode.getMap()) || level > currentMaximum )
			return;

		if (currentNode.isTerminal())
			solution = currentNode.getMap();
		states.add(currentNode.getMap());
		this.closedSetNodes.add(currentNode);		
		for (Move move : currentNode.getMap().findMoves()) {
			
			Map child = new Map(currentNode.getMap());
			
			if (!child.willBlock(move))
				child.performMove(move);
			NewNode kid = new NewNode(child, level);
			kid.calcTheScore(this.states.get(0),level);
			this.nodes.add(kid);
		}
		Collections.sort(this.nodes);
	}
}
