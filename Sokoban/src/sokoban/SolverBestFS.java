package sokoban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import Map.Box;
import Map.Map;
import Map.Move;
import Map.NewNode;
import Map.Heuristics;

class SolverBestFS extends Solver {

	protected HashSet<Map> closedSet = null;
	private ArrayList<NewNode> nodes;
	private ArrayList<NewNode> closedSetNode;
	private int currentMaximum = 2;
	/**
	 * runs the BFS algorithm with a closedSet--> history
	 * 
	 * @param initialState
	 * @return the solved Map
	 */
	protected Map solve(Map map) {
			
		
		int k = 0;
		this.closedSetNode = new ArrayList<NewNode>();
		this.nodes = new ArrayList<NewNode>();
		NewNode initialState = new NewNode(map, 0);
		this.nodes.add(initialState);
				
		while (this.nodes.size() > 0) {
			depth++;
			NewNode currentNode = this.nodes.remove(0);
			if (depth%1000==0) {
				System.out.println("Depth is "+depth);
				System.out.println("We are at this map: ");
				currentNode.getMap().printMap();
				System.out.println("Which has a score of "+Heuristics.mapScore(currentNode.getMap()));
			}
		
			if (currentNode.isTerminal()) {
				
				return currentNode.getMap();

			} else {
				

				if (!this.closedSetNode.contains(currentNode)) {
				
					this.closedSetNode.add(currentNode);

					for (Move mv : currentNode.getMap().findMoves()) {
								
						
						Map mp = new Map(currentNode.getMap());
						if (!mp.willBlock(mv)) 
								mp.performMove(mv);
						
						NewNode child = new NewNode(mp, depth);
						child.calculateTheScore(child.getMap());
						
					
						this.nodes.add(child);

					}// expandMoves

					Collections.sort(this.nodes);

				}

			}

		}
		System.out.println("Returning null");
		return null;

	}

	
}