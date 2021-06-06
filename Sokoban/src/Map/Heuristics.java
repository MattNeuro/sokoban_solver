package Map;





import java.util.ArrayList;

import java.util.LinkedList;

import java.util.Queue;
import java.util.Vector;

/**
 * Set of heuristics that help solve puzzles more efficiently
 * 
 * @author Radu
 * 
 */
public class Heuristics {

	/**
	 * Returns the total score of a map, using all heuristics
	 */

	public static int mapScore(Map map) {
		//	return  boxesToGoals(map) + playerToBoxes(map);	
		return boxesOnGoal(map) + boxesToGoals(map) + goalsScore(map);
	}
	public static int mapScoreAstar(Map initMap, Map map) {
		return playerToBoxes(map) + getPlayersStartPoint(initMap, map);
	}


	
	
	/**
	 * Returns a score based on what goals are occupied by boxes. The harder they are to reach, the better the score
	 * @param map
	 * @return
	 */
	public static int goalsScore (Map map) {
		int score = 0;
		for (int i = 0 ; i < map.maxRow ; i++)
			for (int j = 0 ; j < map.maxCol ; j++)
				if (map.coord[i][j] == Map.BOXGOAL) { // for every direction that is closed points are added
					if (map.coord[i-1][j] == Map.WALL || map.coord[i-1][j] == Map.BOX || map.coord[i-1][j] == Map.BOXGOAL)
						score += 30;
					if (map.coord[i-1][j] == Map.WALL || map.coord[i-1][j] == Map.BOX || map.coord[i-1][j] == Map.BOXGOAL)
						score += 30;
					if (map.coord[i-1][j] == Map.WALL || map.coord[i-1][j] == Map.BOX || map.coord[i-1][j] == Map.BOXGOAL)
						score += 30;
					if (map.coord[i-1][j] == Map.WALL || map.coord[i-1][j] == Map.BOX || map.coord[i-1][j] == Map.BOXGOAL)
						score += 30;
				
				}
		return score;
					
		
	}
	
	
	/** 
	 * Heuristic that tells how much movement the boxes have available
	 */
	
	public static int degreesOfFreedom (Map map) {
		int score = 0;
		for (Box box : map.boxes)
			if (map.coord[box.getLocation().x][box.getLocation().y] != Map.BOXGOAL)
				score += map.boxMovements(box).size();
		return score*20;
	}

	/** 
	 * Heuristic that gives the total number of places the player can reach
	 * @param map
	 * @return
	 */

	public static int numberOfReachablePositions(Map map) {
		Queue<Point> queue = new LinkedList<Point>();
		Vector<Point> reachablePositions = new Vector<Point>();
		queue.add(map.getPlayer());
		reachablePositions.add(map.getPlayer());
		int reachable = 1;
		while (!queue.isEmpty()) {
			Point current = queue.poll();
			for (Move move : Move.allMoves()) {
				Point p = null;
				if (move.getDirection() == 'U')
					p = new Point(current.x - 1, current.y);
				if (move.getDirection() == 'D')
					p = new Point(current.x + 1, current.y);
				if (move.getDirection() == 'L')
					p = new Point(current.x, current.y - 1);
				if (move.getDirection() == 'R')
					p =new Point(current.x, current.y + 1);
				if (!map.isWall(p.x, p.y) && !map.isBox(p.x, p.y) && !reachablePositions.contains(p)) {
					queue.add(p);
					reachablePositions.add(p);
					reachable++;
				}
			}
		}

		//	System.out.println("The player can reach " + reachable + " locations on map: ");
		//	map.printMap();
		return reachable;
	}




	/**
	 * Heuristic for the push algorithm in order to help the player reach the position to push the box
	 * @param x: x coord of the box
	 * @param y: y coord of the box
	 * @param map: the map
	 * @return
	 */
	public static int playerToPos (int x, int y, Map map) {
		int distance = 0;
		int playerX = map.getPlayer().x;
		int playerY = map.getPlayer().y;
		distance += Math.abs(x-playerX) + Math.abs(y-playerY);
		return -distance;
	}



	/**
	 * A heuristic that gives the number of boxes on goal
	 */

	public static int boxesOnGoal(Map map) {
		int score = 0;
		int x, y;

		for (Box box : map.boxes) {
			x = box.getLocation().x;
			y = box.getLocation().y;
			if (map.coord[x][y] == Map.BOXGOAL) 
				score += 200;
		}

		return score;

	}

	/**
	 * A heuristic that determines the number of the boxes out of the
	 * goal positions
	 * 
	 */

	public static int boxesOutFromGoals(Map map){
		int x=0;
		int y=0;
		int numOutBox=0;

		for (Box box : map.boxes) {
			x = box.getLocation().x;
			y = box.getLocation().y;
			if (map.coord[x][y] != Map.BOXGOAL) {
				numOutBox++;
			}

		}
		return numOutBox;
	}

	/**
	 * A heuristic that determines a score based on the distance from boxes to
	 * goals
	 */
	public static int boxesToGoals(Map map) {
		int totalDistance = 0;
		ArrayList<Point> goals = new ArrayList<Point>();
		goals = map.getGoalPositions();
		Vector<Box> boxes = new Vector<Box>();
		boxes = map.boxes;
		for (int i = 0; i < boxes.size(); i++) { // there is the same number of
			// goals as boxes so we can
			// use the same index for
			// both
			totalDistance += Math.abs(boxes.get(i).getLocation().x
					- goals.get(i).x)
					+ Math.abs(boxes.get(i).getLocation().y
							- goals.get(i).y);

		}

		return -totalDistance*10; // the distance needs to be short, therefore we
		// return -totalDistance

	}

	/**
	 * A heuristic that determines how close a player is to a box
	 */
	public static int playerToBoxes(Map map) {
		int x = map.getPlayer().x;
		int y = map.getPlayer().y;
		Vector<Box> boxes = new Vector<Box>();
		boxes = map.boxes;
		int minDistance = 1000;
		for (Box box : boxes) {
			if (Math.abs(x - box.getLocation().x)
					+ Math.abs(y - box.getLocation().y) < minDistance)
				minDistance = Math.abs(x - box.getLocation().x)
				+ Math.abs(y - box.getLocation().y);

		}
		return -minDistance; // the farther the player is, the worse the map
		// would be
	}

	/**
	 * a method that returns the distance between the initial position of the
	 * player and the current position useful for A* algorithm
	 * 
	 * @return
	 */
	public static int getPlayersStartPoint(Map initMap, Map currentMap) {

		int xPast = initMap.getPlayer().x;
		int yPast = initMap.getPlayer().y;
		int xCurrent = currentMap.getPlayer().x;
		int yCurrent = currentMap.getPlayer().y;

		return (-1)*(Math.abs(xPast - xCurrent) + Math.abs(yPast - yCurrent));
	}



}
