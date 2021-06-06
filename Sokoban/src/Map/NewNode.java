package Map;
/**
 * Class that encapsulates a Node representation for a state in order to traverse the shortest path
 * @author Mary
 *
 */
import java.io.IOException;
import Map.Heuristics;
import java.util.ArrayList;
import java.util.Comparator;

public class NewNode implements Comparable<NewNode> {

	private int depth;
	private int nboxes;
	private Point player;
	private long score;
	private ArrayList<Box> boxes; // where the boxes located
	private ArrayList<Point> goalpositions;
	private int[][] coords;
	private Map map;

	public NewNode(Map map, int depth)  {
		//	System.err.println("I am here");
		this.map = map;

		coords=new int[map.coord.length][map.coord[0].length]; // map is rectangular, all the columns have the same length => map.coord[0] can be used for all columns
		for (int i = 0; i < map.coord.length; i++) {
			for (int j = 0; j < map.coord[i].length; j++) 
				coords[i][j] = map.coord[i][j];

		}



		this.depth 	= depth;
		this.nboxes = map.boxes.size();
		this.player = map.getPlayer();
		this.score 	= -1;
		this.boxes 	= new ArrayList<Box>();
		for (Box bx : map.boxes) {

			boxes.add(bx);

		}
		goalpositions=new ArrayList<Point>(); 
		for (Point p : map.getGoalPositions()) {

			goalpositions.add(p);

		}

	}// constructor

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public Point getPlayer() {
		return player;
	}

	public void setPlayer(Point player) {
		this.player = player;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public ArrayList<Box> getBoxes() {
		return boxes;
	}

	public void setBoxes(ArrayList<Box> boxes) {
		this.boxes = boxes;
	}

	
	
	/**
	 * We calculate the manhattan distances between the player and the box, 
	 * and between the box and goal positions. 
	 * stores the score from the calculated Manhattan distances
	 * 
	 */
	
	public void calcTheScore(Map initMap,int depth){
		for (int i = 0; i < boxes.size(); i++) {          // we assume that #boxes = #goal slots
		//	System.out.println("Goal positions: "+ goalpositions.get(i).toString());
						int goalmanhattan = Math.abs(boxes.get(i).getLocation().x
								- goalpositions.get(i).x)
								+ Math.abs(boxes.get(i).getLocation().y
										- goalpositions.get(i).y);
						int playerboxes = Math.abs(boxes.get(i).getLocation().x
								- player.x)
								+ Math.abs(boxes.get(i).getLocation().y
										- player.y);
						score += (goalmanhattan+playerboxes)+depth+Heuristics.boxesOutFromGoals(map);
					}
	
		
		
		
	
		
	}
	
	
	
	
	public void calculateTheScore(Map initMap) {

		/**	for (int i = 0; i < boxes.size(); i++) {          // we assume that #boxes = #goal slots
System.out.println("Goal positions: "+ goalpositions.get(i).toString());
			int goalmanhattan = Math.abs(boxes.get(i).getLocation().x
					- goalpositions.get(i).x)
					+ Math.abs(boxes.get(i).getLocation().y
							- goalpositions.get(i).y);
			int playerboxes = Math.abs(boxes.get(i).getLocation().x
					- player.x)
					+ Math.abs(boxes.get(i).getLocation().y
							- player.y);
			score += (goalmanhattan+playerboxes);
		}
		 */
		score+=Heuristics.mapScore(initMap);
	}

	public void scorePush (int x, int y, Map map) {
		score += Heuristics.playerToPos(x, y, map);

	}


	public boolean isTerminal(){

		return this.map.isSolved();

	}
	public void printNode(){

		this.map.printMap();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewNode other = (NewNode) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}



	@Override
	public int compareTo(NewNode arg0) {
		// TODO Auto-generated method stub
		return  (int) arg0.score-(int) this.score;
	}

}
