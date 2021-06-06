package Map;
/**
 * Class that encapsulates a move
 * @author Radu
 *
 */
public class Move implements Comparable<Move>  {
	
	private static final Move[] possibleMoves = new Move[4];
	private char direction;
	private Map map;
	
	public static Move[] allMoves() {
			possibleMoves[0] = new Move('U');
			possibleMoves[1] = new Move('D');
			possibleMoves[2] = new Move('L');
			possibleMoves[3] = new Move('R');
	
		return possibleMoves;
	
	}
	public Move (char direction) {
		this.direction = direction;
	}
	public Move (char direction, Map map) {
		this.direction = direction;
		this.map = map;
	}
	
	
	public char getDirection () {
		return direction;
	}
	
	public String toString(){
		
		return direction+" ";
		
	}
	
	public int compareTo(Move arg0) {
		return Heuristics.boxesOnGoal(this.map) + Heuristics.boxesToGoals(this.map) - Heuristics.boxesOnGoal(arg0.map) - Heuristics.boxesToGoals(arg0.map);
	}
	
		
	}
	

