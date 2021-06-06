package sokoban;

import java.util.Stack;

import Map.Map;
import Map.Move;


class SolverDFS extends Solver {

	
	private Stack<Map> statesDFS;
	
	
	/**
	 * runs the DFS algorithm with a closedSet--> history
	 * we use the stack in order to exploit the fifo queue which provides
	 * @param initialState
	 * @return the solved Map
	 */
	public Map solve (Map initialState) {
		
		statesDFS = new Stack<Map>();
		statesDFS.push(initialState);
		while (!statesDFS.isEmpty()) {

			Map currentState = statesDFS.pop();

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
				this.statesDFS.push(childMap);
			}
		}
		System.out.println("Returned null");
		return null;
	}
}


