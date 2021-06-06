package Map;

import java.util.LinkedList;

/**
 * This class is responsible for returning the succession of moves that led to
 * the correct solution to the Client and discarding the others.
 * 
 * NOTES: Increasing depth limit may be good idea to implement, start with low depth search limit
 * and increase as needed according to time left. 
 * I tested this with checkers yesterday, before i used to keep depth search at lvl 12 always causing
 * the algorithm to not complete the three when branching factor is too high, or wasting time when it's not.
 * 
 * now using increasing depth search, i start at depth 5, and rebuild the whole three for increased depth limits
 * the results are much better, and its in fact true that the sum of the times spent on rebuilding the three n-1
 * times is less than the amount of time spent on building the three the n-th time.
 * 
 * in checkers this leads to be always able to search the whole three, adjusting the depth to the branching factor
 * at the expense of a few seconds, if the branching factor allows it, the depth can reach like 19 in checkers.
 * maybe the same principle can be applied here.
 * @author Alejandro
 *
 */

public class MoveTracker {
	
	private static LinkedList<Move> solution;
	
	/**
	 * should only be casted once to initialize the solution builder, according to how we move
	 * in the three, elements should be added and removed accordingly to keep always in the
	 * linked list the movements that led us this far (hopefully to the solution).
	 */
	public MoveTracker( ) {
		solution = new LinkedList<Move>();
	}
	
	/**
	 * add element should be casted everytime we go deeper in the three
	 * because we can that once a solution is found, the solution (linked list)
	 * contains the full succession of movements that led to it
	 * @param movement
	 */
	public void add( Move movement ) {
		solution.add( movement );
	}
	
	/**
	 * remove element should be casted as we go back in the three to remove
	 * moves that tested negative for solution of the maze, because we want
	 * to discard movements that didnt led to a solution
	 * @param movement
	 */
	public void remove( Move movement ) {
		solution.remove( movement );
	}
	
	/**
	 * to clear the list each time we move to the next "depth limit"
	 */
	public void delete( ) {
		solution.clear();
	}
	
	
	/**
	 * this method builds the solution by parsing the content on the linked list
	 * @return		the string of characters that will be sent to the server
	 */
	public String buildSolution( ) {
		StringBuffer S = new StringBuffer();
		S = new StringBuffer();
		for ( int i=0 ; i < solution.size() ; i++ ) {
			S.append( solution.get(i).getDirection() );
		}
		String Sol = S.toString();
		return Sol;
	}
	

}
