package sokoban;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import Map.Map;
import Map.Move;


class SolverBFS extends Solver {

	protected 	HashSet<Map> 	closedSet	= null;	
	private ArrayList<Map> statesBFS;
	/**
	 * runs the BFS algorithm with a closedSet--> history
	 * @param initialState
	 * @return the solved Map
	 */	
	
	
	public Map solve (Map initialState) {
		
		statesBFS = new ArrayList<Map>();
		statesBFS.add(initialState);
		while (!statesBFS.isEmpty()) {

			Map currentState = statesBFS.remove(0);

			if (currentState.isSolved())
				return currentState;

			if (this.states.contains(currentState))
				continue;
				
			this.states.add(currentState);
			if (states.size() % 1000 == 0)
				printCurrentState(currentState);

			for (Move mv : currentState.findMoves()) {
			
				Map childMap = new Map(currentState);

				if (!childMap.willBlock(mv))
					childMap.performMove(mv);
				this.statesBFS.add(childMap);
			}
		}
		System.out.println("Returned null");
		return null;
	}
}


	
	
	


