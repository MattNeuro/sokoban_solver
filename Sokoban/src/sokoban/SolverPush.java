package sokoban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import Map.Box;
import Map.Heuristics;
import Map.Map;
import Map.Move;
import Map.NewNode;


/**
 * This solver is push based, it doesn't consider player moves to update the current state
 * unless there is a box move involved. Since the non push movements of a player does not
 * affect the board configuration (box positions) it's more efficient to store states this way:
 * we don't have to worry about visiting previous as much as before.
 * 
 * the down side for this is that once the solution is found, for each box movement
 * an algorithm has to reconstructs the moves that the player would have had to perform
 * in order to reach the box and move it.
 * 
 * the other problem is that for each board configuration we have to determine which boxes can be moved
 * and in which direction (to implement this properly the map class needs to implement bit masks)
 * 
 * it's possible to implement this with the current performMove function in the map class, but the player
 * location has to be updated before calling it (jump the player beside the box once its been confirmed
 * that its a reachable position)
 * 
 * 
 * 
 * @author Alejandro
 *
 */

class SolverPush extends Solver {

	int cDepthLimit = 2;
	
	public Map solve ( Map map ) {
		System.out.println();
		
		while (cDepthLimit < maxDepth && solution == null) {
			expand( map , 0 );
/*			if ( solution == null )
				System.out.println( "No solution found at depth:" + cDepthLimit );
			else
				System.out.println( "Solution found at depth:" + cDepthLimit );*/
			cDepthLimit++;
		}
		
		if ( solution == null )
			System.out.println( "Solver Failed with depth:" + maxDepth );

		return solution;
	}


	private void expand ( Map map , int depth ) {
		
		int score = Heuristics.mapScore(map);
		
		if ( solution != null)
			return;
		if ( depth == cDepthLimit )
			return;
		if ( map.isSolved() )
			solution = map;

		Map contMap = new Map(map);
		
		contMap = Contaminate( contMap, map.getPlayer().x, map.getPlayer().y);
		
		for ( Box box : map.boxes ) {	
			int x = box.getLocation().x;
			int y = box.getLocation().y;
			LinkedList<Move> BoxMovement = findBoxMoves( contMap , box );
			for ( Move move : BoxMovement ) {
				
				if ( move.getDirection() == 'D' ) {
					Map child = new Map( map );
					child.Teleport(x-1,y);
					//System.out.printf("player after teleport: (%d,%d)\n",child.getPlayer().x,child.getPlayer().y);
					//child.printMap();
					child.performMove(move);
//					if( Heuristics.goalsScore(child) >= score -50 )
						expand ( child , depth+1 );
				}
				if ( move.getDirection() == 'U' ) {
					Map child = new Map( map );
					child.Teleport(x+1,y);
					//child.printMap();
					child.performMove(move);
//					if( Heuristics.goalsScore(child) >= score -50 )
						expand ( child , depth+1 );
				}
				if ( move.getDirection() == 'L' ) {
					Map child = new Map( map );
					child.Teleport(x,y+1);
					//System.out.printf("player after teleport: (%d,%d)\n",child.getPlayer().x,child.getPlayer().y);
					//child.printMap();
					child.performMove(move);
//					if( Heuristics.goalsScore(child) >= score -50 )
						expand ( child , depth+1 );
				}
				if ( move.getDirection() == 'R' ) {
					Map child = new Map( map );
					child.Teleport(x,y-1);
					//System.out.printf("player after teleport: (%d,%d)\n",child.getPlayer().x,child.getPlayer().y);
					//child.printMap();
					child.performMove(move);
//					if( Heuristics.goalsScore(child) >= score -50 )
						expand ( child , depth+1 );
				}	
			}
		}
	}

	private LinkedList<Move> findBoxMoves ( Map map , Box box ) {

		// for each box we have to fill a list of possible moves
		LinkedList<Move> boxmoves = new LinkedList<Move>();
		int x,y;

		x = box.getLocation().x;
		y = box.getLocation().y;

		//looking for box moves in the X axis
		if( map.isDCEmpty(x-1,y) && map.isDLEmpty(x+1,y) && map.isContaminated(x-1,y) ){
			boxmoves.add( new Move('D') );
			//System.out.printf("D Player could Reach (%d,%d)\n",x-1,y);	
		}

		if( map.isDLEmpty(x-1,y) && map.isDCEmpty(x+1,y) && map.isContaminated(x+1,y) ){
			boxmoves.add( new Move('U') );
			//System.out.printf("U Player could Reach (%d,%d)\n",x+1,y);
		}

		//looking for box moves in the Y axis
		if( map.isDCEmpty(x,y-1) && map.isDLEmpty(x,y+1) && map.isContaminated(x,y-1) ){
			boxmoves.add( new Move('R') );
			//System.out.printf("R Player could Reach (%d,%d)\n",x,y-1);
		}
		if( map.isDLEmpty(x,y-1) && map.isDCEmpty(x,y+1) && map.isContaminated(x,y+1) ){
			boxmoves.add( new Move('L') );
			//System.out.printf("L Player could Reach (%d,%d)\n",x,y+1);
		}

		return boxmoves;
	}
	
	Map Contaminate( Map map, int x, int y) {
		
		map.Contaminate(x,y);
		
		if ( !map.isContaminated(x+1, y) && map.isDCEmpty(x+1, y) )
			map = Contaminate (map, x+1, y);
		

		if ( !map.isContaminated(x-1, y) && map.isDCEmpty(x-1, y) )
			map = Contaminate (map, x-1, y);
		

		if ( !map.isContaminated(x, y+1) && map.isDCEmpty(x, y+1) )
			map = Contaminate (map, x, y+1);
		

		if ( !map.isContaminated(x, y-1) && map.isDCEmpty(x, y-1) )
			map = Contaminate (map, x, y-1);
		
		return map;
		
	}

}