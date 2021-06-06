package Map;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;
/**
 * Class that contains the map and all its features
 * @author Radu
 *
 */
public class Map {


	/**
	 * Alternative method with bit mask: all the possible states of each cell are defined in such a way
	 * that they are mutually exclusive, this leads to several implementation problems because in reality
	 * some of the conditions are not mutually exclusive, for example: an empty space can be a boxgoal
	 * or a deadlock, a box can be in a deadlock position or in a goal position or in neither of those,
	 * same with the player, luckily for us the walls are just walls.
	 * 
	 * to implement a bit mask I propose:
	 * 
	 * bit1: 1 means there is a wall 		0 means there is not a wall
	 * bit2: 1 means there is a player	 	0 means there is not a player
	 * bit3: 1 means there is a box 		0 means there is not a box
	 * bit4: 1 means there is a deadlock 	0 means there is not a deadlock
	 * bit5: 1 means there is a goal 		0 means there is not a goal
	 * 
	 * note: the first 3 bits are mutually exclusive.
	 * 
	 * In the current implementation to ask if i can move a player or a box to a certain position
	 * I use the function isEmpty(x,y), which will test negative for everything but EMPTY, including
	 * the situations where the cell contains a GOAL & DEADLOCK in which cases the cell is empty tells
	 * otherwise.
	 * 
	 * In the bit mask implementation it's possible to ask for specific situations using the bits:
	 * is the spot empty? is 0b00000 | coord[x][y] == 0 ? we would then define 0b00000 as EMPTY_MASK
	 * is there a box in a goal? is 0b10100 & coord[x][y] == 0b10100 ? and define 0b10100 as BOXGOAL_MASK
	 * 
	 * other useful masks could be:
	 * g=goal, d=deadlock, b=box, p=player, w=wall.
	 * 
	 * MASKS			0b c g d b p w	
	 * WALL_MASK 		0b 0 0 0 0 0 1
	 * PLAYER_MASK 		0b 0 0 0 0 1 0
	 * BOX_MASK			0b 0 0 0 1 0 0
	 * PLAYERLOCK_MASK  0b 0 0 1 0 1 0
	 * PLAYERGOAL_MASK  0b 0 1 0 0 1 0
	 * BOXLOCK_MASK		0b 0 0 1 1 0 0
	 * BOXGOAL_MASK		0b 0 1 0 1 0 0
	 * EMPTY_MASK		0b 0 0 0 0 0 0
	 * LOCK_MASK		0b 0 0 1 0 0 0
	 * GOAL_MASK		0b 0 1 0 0 0 0
	 * DCEMPTY_MASK		0b 1 1 1 0 1 0
	 * DLEMPTY_MASK		0b 1 1 0 0 1 0
	 * CONT_MASK		0b 1 0 0 0 0 0
	 * 
	 * ****NOTE**** may need to check the other masks after adding the contaminated property
	 * 
	 * how to test for conditions easily:
	 * 
	 * ¤¤ To test for the positive condition (there is a WALL/PLAYER/BOX)
	 * we always evaluate this condition : if ( XXXXXX_MASK & coord[x][y] == XXXXXX_MASK )
	 * 
	 * ¤¤ To test for the negative condition (there is NOT a WALL/PLAYER/etc)
	 * we always evaluate this condition : if ( XXXXXX_MASK & coord[x][y] == 0 )
	 * 
	 * ¤¤ To test if a certain place is empty (totally empty: no deadlock or goal) or 
	 * empty in the sence of there is not an object there, we use different MASKs
	 * to test for the latest we use: if ( DCEMPTY_MASK | coord[x][y] == DCEMPTY_MASK )
	 * 
	 * more specific masks can be created for specific situations so we don't have to use
	 * many conditions to evaluate a single statement.
	 * 
	 */

	//MASK definitions
	static final byte		WALL 			= (byte)0x01;
	static final byte		PLAYER 			= (byte)0x02;
	static final byte		BOX				= (byte)0x04;
	static final byte		PLAYERDEADLOCK 	= (byte)0x0A;
	static final byte		PLAYERGOAL 		= (byte)0x12;
	static final byte		BOXLOCK			= (byte)0x0C;
	static final byte		BOXGOAL			= (byte)0x14;
	static final byte		EMPTY			= (byte)0x00;
	static final byte		DEADLOCK		= (byte)0x08;
	static final byte		GOAL			= (byte)0x10;
	static final byte		DCEMPTY			= (byte)0x3A;
	static final byte		DLEMPTY			= (byte)0x32;
	static final byte		CONTAM          = (byte)0x20;



	public byte coord[][]; // matrix representation of the map
	int maxRow; // number of rows 
	int maxCol; // number of columns
	public Vector<Box> boxes			= new Vector<Box>(); //vector that contains the boxes
	public Vector<Deadlock> deadlocks	= new Vector<Deadlock>(); // vector that contains deadlocks
	private char[] tracker 				= {};



	public int getMaxRow() {
		return maxRow;
	}
	public int getMaxCol() {
		return maxCol;
	}

	/**
	 * checks to see if a move will lead to a blocked map
	 * @param move: the move
	 * @return
	 */

	public boolean willBlock(Move move) {
		Map map=new Map(this);
		map.performMove(move);
		if (map.isSolvable())
			return false;
		else
			return true;


	}

	/**
	 * determines if a map is solvable by checking if adjacent boxes are blocked
	 * @return
	 */
	public boolean isSolvable() {
		int x, y;
		for (Box box : boxes) {
			x = box.getLocation().x;
			y = box.getLocation().y;
			if (coord[x-1][y] == BOX || (coord[x-1][y] == BOXGOAL && coord[x][y] !=BOXGOAL)) { // there is a box above this box and only 1 may be on goal
				if (coord[x-1][y+1] == WALL && coord[x][y+1] == WALL) // there is a wall to the right of the 2 boxes
					return false;
				if (coord[x-1][y-1] == WALL && coord[x][y-1] == WALL) // there is a wall to the left of the boxes
					return false;
			}
			if (coord[x+1][y] == BOX || (coord[x+1][y] == BOXGOAL && coord[x][y] !=BOXGOAL)) { // there is a box below this box and only 1 may be on goal
				if (coord[x+1][y+1] == WALL && coord[x][y+1] == WALL) // there is a wall to the right of the 2 boxes
					return false;
				if (coord[x+1][y-1] == WALL && coord[x][y-1] == WALL) // there is a wall to the left of the boxes
					return false;
			}
			if (coord[x][y+1] == BOX || (coord[x][y+1] == BOXGOAL && coord[x][y] !=BOXGOAL)) { // there is a box to the right of this box and only 1 may be on goal
				if (coord[x-1][y] == WALL && coord[x-1][y+1] == WALL) // there is a wall above the 2 boxes
					return false;
				if (coord[x+1][y] == WALL && coord[x+1][y+1] == WALL) // there is a wall below the 2 boxes
					return false;
			}
			if (coord[x][y-1] == BOX || (coord[x][y-1] == BOXGOAL && coord[x][y] !=BOXGOAL)) { // there is a box to the right of this box and only 1 may be on goal
				if (coord[x+1][y] == WALL && coord[x+1][y-1] == WALL) // there is a wall above the 2 boxes
					return false;
				if (coord[x+1][y] == WALL && coord[x+1][y-1] == WALL) // there is a wall below the 2 boxes
					return false;
			}

		}
		return true;

	}
	/**
	 *  Returns the vector of moves a box can make
	 */
	public Vector<Move> boxMovements (Box box) {
		Vector<Move> possibleBoxMoves = new Vector<Move>();
		int x = box.getLocation().x;
		int y = box.getLocation().y;

		if ((coord[x-1][y] != WALL && coord[x-1][y] !=DEADLOCK && coord[x-1][y] != PLAYERDEADLOCK && coord[x-1][y] !=BOX) && (coord[x+1][y] !=WALL && coord[x+1][y] != BOX))
			possibleBoxMoves.add(new Move('U'));
		if ((coord[x+1][y] != WALL && coord[x+1][y] !=DEADLOCK && coord[x+1][y] != PLAYERDEADLOCK && coord[x+1][y] !=BOX) && (coord[x-1][y] !=WALL && coord[x-1][y] != BOX))
			possibleBoxMoves.add(new Move('D'));
		if ((coord[x][y+1] != WALL && coord[x][y+1] !=DEADLOCK && coord[x][y+1] != PLAYERDEADLOCK && coord[x][y+1] !=BOX) && (coord[x][y-1] !=WALL && coord[x][y-1] != BOX))
			possibleBoxMoves.add(new Move('R'));
		if ((coord[x][y-1] != WALL && coord[x][y-1] !=DEADLOCK && coord[x][y-1] != PLAYERDEADLOCK && coord[x][y-1] !=BOX) && (coord[x][y+1] !=WALL && coord[x][y+1] != BOX))
			possibleBoxMoves.add(new Move('L'));
		return possibleBoxMoves;


	}



	/**
	 * returns the adjacent boxes to a particular box
	 * @param box: the box in question
	 * @return
	 */
	public Vector<Box> adjacentBoxes(Box box) {
		Vector<Box> adjacent=new Vector<Box>();
		adjacent.add(box);
		int x=box.getLocation().x;
		int y=box.getLocation().y;
		for (Box box2:boxes) {
			if ((box2.getLocation().x==x+1 && box2.getLocation().y==y) || (box2.getLocation().x==x-1 && box2.getLocation().y==y) || (box2.getLocation().x==x && box2.getLocation().y==y-1) || (box2.getLocation().x==x && box2.getLocation().y==y+1))
				adjacent.add(box2);
		}
		return adjacent;
	}






	/**
	 * returns true if the map in question is solved
	 * @return 
	 */
	public boolean isSolved() {
		int nrBoxes=boxes.size();
		for (int i=0;i<maxRow;i++)
			for (int j=0;j<maxCol;j++) {
				if (coord[i][j]==BOXGOAL)
					nrBoxes--;
			}

		if (nrBoxes==0)
			return true;
		else
			return false;
	}


	public int getDepth () {
		return tracker.length;
	}



	/**
	 * Print the current map contents
	 * @param map: matrix representation of the map
	 */
	public void printMap() {
		for (int i=0;i<coord.length;i++) { 
			for (int j=0;j<coord[0].length;j++)
				switch (coord[i][j]) {
				case WALL:
					System.out.print("#");
					break;
				case PLAYER:
					System.out.print("@");
					break;
				case BOX:
					System.out.print("$");
					break;
				case GOAL:
					System.out.print(".");
					break;
				case BOXGOAL:
					System.out.print("*");
					break;
				case PLAYERGOAL:
					System.out.print("+");
					break;
				case EMPTY:
					System.out.print(" ");
					break;
				case DEADLOCK:
					System.out.print("d");
					break;
				case PLAYERDEADLOCK:
					System.out.print("D");
					break;
				}
			System.out.println();
		}



	}

	/** 
	 * Parses a map string creating a matrix representing the map
	 * @param inputString: the string received from the server
	 * containing the positions of the walls,boxes,player, etc.
	 */
	public Map(String inputString) {
		int col = 0;
		int row = 1;
		int maxRow = 0;
		int maxCol = 0;

		for (int i=0;i<inputString.length();i++) {
			if (inputString.charAt(i) == '\n') {
				if (col != 0)
					row++;
				if (col > maxCol)
					maxCol = col;
				this.maxCol=col;
				col = 0;
			} 
			else {
				col++;
			}
		}
		maxRow = row;
		this.maxRow = maxRow;



		coord=new byte[maxRow][maxCol];

		col = 0;
		row = 0;

		for (int i=0;i<inputString.length();i++) {
			switch(inputString.charAt(i)) {
			case '*':
				coord[row][col] = BOXGOAL;
				boxes.add(new Box(row,col));
				break;
			case '.':
				coord[row][col] = GOAL;
				break;
			case '#':
				coord[row][col] = WALL;
				break;
			case '$':
				coord[row][col] = BOX;
				boxes.add(new Box(row,col));
				break;
			case '+':
				coord[row][col] = PLAYERGOAL;
				break;
			case '@':
				coord[row][col] = PLAYER;
				break;
			case ' ':
				coord[row][col] = EMPTY;
				break;
			case '\n':
				row++;
				col = 0;
				break;
			}

			if (inputString.charAt(i)!= '\n')
				col++;
		}
		// bound the map
		for (int x = 0; x < maxCol; x++)
			coord[0][x] = coord[maxRow-1][x] = WALL;
		for (int y = 0; y < maxRow; y++)
			coord[y][0] = coord[y][maxCol-1] = WALL;
		System.out.println("Parsed the map. It has " + coord.length + " rows and " + coord[0].length + " columns");

	}


	/**
	 * 	Copy-constructor. Create a new Map object from an old one.
	 * 
	 * 	@param map
	 */
	public Map(Map map) {

		this.coord=new byte[map.coord.length][map.coord[0].length];
		for(int i=0;i<map.coord.length;i++){
			for(int j=0;j<map.coord[0].length;j++){
				this.coord[i][j]=map.coord[i][j];
			}
		}

		//this.coord 	= map.coord.clone(); // matrix representation of the map
		this.maxRow = map.maxRow; // number of rows 
		this.maxCol = map.maxCol; // number of columns

		this.boxes=new Vector<Box>();
		for(int i=0;i<map.boxes.size();i++){

			this.boxes.add(map.boxes.get(i));
		}
		this.tracker = map.tracker.clone();
		//   this.boxes 	= map.boxes; //vector that contains the boxes
	}


	/**
	 * Add initial deadlocks to a map (corners and lines along the walls connecting the corners)
	 * @param map: the matrix representation of the map
	 */
	public void addDeadlocks() {
		int x,y;

		for (int i=0;i<coord.length;i++)
			for (int j=0;j<coord[0].length;j++) {

				if (coord[i][j]==EMPTY && (coord[i+1][j]==WALL && coord[i][j+1]==WALL)) {coord[i][j]=DEADLOCK; deadlocks.add(new Deadlock(i,j));}
				if (coord[i][j]==EMPTY && (coord[i-1][j]==WALL && coord[i][j+1]==WALL)) {coord[i][j]=DEADLOCK; deadlocks.add(new Deadlock(i,j));}
				if (coord[i][j]==EMPTY && (coord[i-1][j]==WALL && coord[i][j-1]==WALL)) {coord[i][j]=DEADLOCK; deadlocks.add(new Deadlock(i,j));}
				if (coord[i][j]==EMPTY && (coord[i][j-1]==WALL && coord[i+1][j]==WALL)) {coord[i][j]=DEADLOCK; deadlocks.add(new Deadlock(i,j));}

				if (coord[i][j]==PLAYER && (coord[i+1][j]==WALL && coord[i][j+1]==WALL)) {coord[i][j]=PLAYERDEADLOCK; deadlocks.add(new Deadlock(i,j));}
				if (coord[i][j]==PLAYER && (coord[i-1][j]==WALL && coord[i][j+1]==WALL)) {coord[i][j]=PLAYERDEADLOCK; deadlocks.add(new Deadlock(i,j));}
				if (coord[i][j]==PLAYER && (coord[i-1][j]==WALL && coord[i][j-1]==WALL)) {coord[i][j]=PLAYERDEADLOCK; deadlocks.add(new Deadlock(i,j));}
				if (coord[i][j]==PLAYER && (coord[i][j-1]==WALL && coord[i+1][j]==WALL)) {coord[i][j]=PLAYERDEADLOCK; deadlocks.add(new Deadlock(i,j));}

			}

		Vector<Deadlock> leftTopCorner=new Vector<Deadlock>();
		Vector<Deadlock> rightBottomCorner=new Vector<Deadlock>();

		for (Deadlock deadlock:deadlocks) {
			x=deadlock.getLocation().x;
			y=deadlock.getLocation().y;
			if (coord[x][y-1]==WALL && coord[x-1][y]==WALL)
				leftTopCorner.add(deadlock);

			if (coord[x][y+1]==WALL && coord[x+1][y]==WALL)
				rightBottomCorner.add(deadlock);

		}

		for (Deadlock deadlock:leftTopCorner) {
			wallDownLeft(deadlock); // check below
			wallRightUp(deadlock); // check to the right

		}

		for (Deadlock deadlock:rightBottomCorner) {

			wallUpRight(deadlock); // check above
			wallLeftDown(deadlock); // check to the left
		}
		System.out.println("Exiting deadlock adder");

	}
	/** 
	 * goes down from a deadlock and checks if the left column is all WALL until a deadlock; if so, makes the column deadlocks; it's for top left deadlocks
	 * @param deadlock
	 */
	public void wallDownLeft(Deadlock deadlock) {
		System.out.println("Studying deadlock at " + deadlock.getLocation().x + " " + deadlock.getLocation().y);
		boolean makeDeadlocks=false;
		boolean brokeLine=false;
		int initialX=deadlock.getLocation().x;
		int initialY=deadlock.getLocation().y;
		int x,y,endingX=0,endingY=0;
	
		x=initialX + 1; // start with the row below
		y=initialY;
		if (coord[x][y]==WALL) // the deadlock is alone
			brokeLine=true;
		
		while (!makeDeadlocks && !brokeLine) {
			
			if (coord[x][y-1] != WALL || coord[x][y] == GOAL) // if we have a goal on the way, we can't add deadlocks
				brokeLine=true; // the wall line is broken, we can't add deadlocks
			if (coord[x][y] == DEADLOCK || coord[x][y] == PLAYERDEADLOCK) { 
				makeDeadlocks=true; // we went all the way to a new deadlock without having anything else than walls on the way
				endingX=x; // so we know when to stop adding deadlocks

			}
			if (coord[x][y-1]==WALL)
				x += 1; // go to the next row


		}
		if (!brokeLine && makeDeadlocks) { // if we only encountered walls and we went all the way to another deadlock 
			for (int i=initialX+1;i<endingX;i++) {
				
				if (coord[i][initialY]==EMPTY) {
					coord[i][initialY]=DEADLOCK;
				
				}
				
				if (coord[i][initialY] == PLAYER) {
					coord[i][initialY] = PLAYERDEADLOCK;
				
				}
			}

		}
		
	}




	/** 
	 * goes right from a deadlock and checks if the above row is all WALL until a deadlock; if so, makes the line deadlocks; it's for top left deadlocks
	 * @param deadlock
	 */
	public void wallRightUp(Deadlock deadlock) {
		System.out.println("Studying deadlock at " + deadlock.getLocation().x + " " + deadlock.getLocation().y);
		int maxIter = 100;
		int currentIter = 0;
		boolean makeDeadlocks=false;
		boolean brokeLine=false;
		int initialX=deadlock.getLocation().x;
		int initialY=deadlock.getLocation().y;
		int x,y,endingX=0,endingY=0;

		x=initialX;
		y=initialY+1; // start with the column to the right
		if (coord[x][y]==WALL) // the deadlock is alone
			brokeLine=true;

		while (!makeDeadlocks && !brokeLine) {
			if (coord[x-1][y]!=WALL || coord[x][y]==GOAL) // if we have a goal on the way, we can't add deadlocks
				brokeLine=true; // the wall line is broken, we can't add deadlocks
			if (coord[x][y]==DEADLOCK || coord[x][y] == PLAYERDEADLOCK) { 
				makeDeadlocks=true; // we went all the way to a new deadlock without having anything else than walls on the way
				endingY=y; // so we know when to stop adding deadlocks

			}
			if (coord[x-1][y]==WALL)
				y+=1; // go to the next column


		}
		if (!brokeLine && makeDeadlocks) { // if we only encountered walls and we went all the way to another deadlock 
			for (int j=initialY+1;j<endingY;j++) { 
				if (coord[initialX][j]==EMPTY)
					coord[initialX][j]=DEADLOCK;
				if (coord[initialX][j] == PLAYER)
					coord[initialX][j] = PLAYERDEADLOCK;

			}

		}
	}



	/** 
	 * goes up from a deadlock and checks if the right column is all WALL until a deadlock; if so, makes the column deadlocks; it's for bottom right deadlocks
	 * @param deadlock
	 */
	public void wallUpRight(Deadlock deadlock) {
		System.out.println("Studying deadlock at " + deadlock.getLocation().x + " " + deadlock.getLocation().y);
		boolean makeDeadlocks=false;
		boolean brokeLine=false;
		int initialX=deadlock.getLocation().x;
		int initialY=deadlock.getLocation().y;
		int x,y,endingX=0,endingY=0;

		x=initialX-1;
		y=initialY; // start with the row below
		if (coord[x][y]==WALL) // the deadlock is alone
			brokeLine=true;

		while (!makeDeadlocks && !brokeLine) {
			if (coord[x][y+1]!=WALL || coord[x][y]==GOAL) // if we have a goal on the way, we can't add deadlocks
				brokeLine=true; // the wall line is broken, we can't add deadlocks
			if (coord[x][y]==DEADLOCK || coord[x][y] == PLAYERDEADLOCK) { 
				makeDeadlocks=true; // we went all the way to a new deadlock without having anything else than walls on the way
				endingX=x; // so we know when to stop adding deadlocks

			}
			if (coord[x][y+1]==WALL)
				x+=-1; // go to the next row


		}
		if (!brokeLine && makeDeadlocks) { // if we only encountered walls and we went all the way to another deadlock 
			for (int i=initialX-1;i>endingX;i--) {
				if (coord[i][y]==EMPTY)
					coord[i][y]=DEADLOCK;
				if (coord[i][y] == PLAYER)
					coord[i][y] = PLAYERDEADLOCK;
			}

		}
	}



	/** 
	 * goes left from a deadlock and checks if the below row is all WALL until a deadlock; if so, makes the line deadlocks; it's for bottom right deadlocks
	 * @param deadlock
	 */
	public void wallLeftDown(Deadlock deadlock) {
		System.out.println("Studying deadlock at " + deadlock.getLocation().x + " " + deadlock.getLocation().y);
		boolean makeDeadlocks=false;
		boolean brokeLine=false;
		int initialX=deadlock.getLocation().x;
		int initialY=deadlock.getLocation().y;
		int x,y,endingX=0,endingY=0;

		x=initialX;
		y=initialY-1; // start with the row below
		if (coord[x][y]==WALL) // the deadlock is alone
			brokeLine=true;

		while (!makeDeadlocks && !brokeLine) {
			if (coord[x+1][y]!=WALL || coord[x][y]==GOAL) // if we have a goal on the way, we can't add deadlocks
				brokeLine=true; // the wall line is broken, we can't add deadlocks
			if (coord[x][y]==DEADLOCK || coord[x][y] == PLAYERDEADLOCK) { 
				makeDeadlocks=true; // we went all the way to a new deadlock without having anything else than walls on the way
				endingY=y; // so we know when to stop adding deadlocks

			}
			if (coord[x+1][y]==WALL)
				y+=-1; // go to the next column


		}
		if (!brokeLine && makeDeadlocks) { // if we only encountered walls and we went all the way to another deadlock 
			for (int j=initialY-1;j>endingY;j--) {
				if (coord[initialX][j]==EMPTY)
					coord[initialX][j]=DEADLOCK;
				if (coord[initialX][j] == PLAYER)
					coord[initialX][j] = PLAYERDEADLOCK;
			}
		}



	}






	/**
	 * returns true if there is a player at row x and column y
	 * @param x: the row in question
	 * @param y: the column in question
	 * @return
	 */
	public boolean isPlayer(int x, int y) {
		if (coord[x][y]==PLAYER || coord[x][y]==PLAYERGOAL || coord[x][y]==PLAYERDEADLOCK)
			return true;
		else 
			return false;
	}

	/**
	 * returns true if the map is empty at row x and column y
	 * @param x: the row in question
	 * @param y: the column in question
	 * @return
	 */
	public boolean isEmpty (int x, int y) {
		if (coord[x][y]==EMPTY)
			return true;
		else 
			return false;
	}

	/**
	 * return true if the map is empty at row x and column y (can be a goal or a deadlock or even
	 * a player and it will return true), it will return false if there is a box or a wall there.
	 * 
	 */
	public boolean isDCEmpty (int x, int y) {
		if ( (DCEMPTY | coord[x][y]) == DCEMPTY  )
			return true;
		else
			return false;
	}


	/**
	 * return true if the map is empty at row x and column y (can be a goal or even
	 * a player and it will return true), it will return false if there is a box, a wall
	 * or a deadlock there.
	 * 
	 */
	public boolean isDLEmpty (int x, int y) {
		if ( (DLEMPTY | coord[x][y]) == DLEMPTY  )
			return true;
		else
			return false;
	}

	/**
	 * return true if the cell is contaminated meaning that the player can reach that spot
	 * @param x		x axis
	 * @param y		y axis
	 * @return
	 */
	public boolean isContaminated (int x, int y) {
		if ( (coord[x][y] & CONTAM) == CONTAM )
			return true;
		else
			return false;
	}

	/**
	 * returns true if there is a wall at row x and column y
	 * @param x: the row in question
	 * @param y: the column in question
	 * @return
	 */
	public boolean isWall (int x,int y) {
		if (coord[x][y]==WALL)
			return true;
		else 
			return false;
	}



	/**
	 * returns true if there is a goal at position x,y
	 * @param x: row
	 * @param y: column
	 * @return
	 */
	public boolean isGoal(int x,int y) {
		if (coord[x][y]==GOAL || coord[x][y]==PLAYERGOAL || coord[x][y]==BOXGOAL)
			return true;
		else 
			return false;
	}



	/**
	 * returns true if there is a deadlock at position x,y
	 * @param x: row
	 * @param y: column
	 * @return
	 */
	public boolean isDeadlock(int x,int y) {
		if (coord[x][y]==DEADLOCK || coord[x][y]==PLAYERDEADLOCK)
			return true;
		else
			return false;
	}




	/** 
	 * returns the player position as a Point
	 * @return
	 */
	public Point getPlayer () {

		for (int x = 0; x < coord.length; x++)
			for (int y = 0; y < coord[0].length; y++)
				if (coord[x][y] == PLAYER || coord[x][y] == PLAYERGOAL || coord[x][y] == PLAYERDEADLOCK)
					return new Point(x,y);
		System.out.println("Can't find player on map: ");
		for (int x = 0; x < maxRow; x++) {
			for (int y = 0; y < maxCol; y++)
				System.out.print(coord[x][y] + " ");
			System.out.println();
		}
		System.out.println();

		printMap();
		return null;
	}


	/**
	 * returns true if there is a box at position x,y
	 * @param x: row
	 * @param y: column
	 * @return
	 */
	public boolean isBox(int x,int y){
		if (coord[x][y]==BOX || coord[x][y]==BOXGOAL)
			return true;
		else
			return false;

	}


	/**
	 * returns the index of a box in the boxes Vector of the box at position x,y
	 * @param x: row
	 * @param y: column
	 * @return
	 */
	public int boxIndex (int x, int y) {
		for (int i = 0; i < boxes.size(); i++) {
			Box box = boxes.elementAt(i);
			Point location = box.getLocation();

			if (location.x == x && location.y == y) 
				return i; // returns the box index in the boxes vector
		}
		return -1; // there isn't a box at x,y
	}


	/**
	 * determines the possible moves in a particular board
	 * @return
	 */
	public LinkedList<Move> findMoves () {
		LinkedList<Move> moves = new LinkedList<Move>();
		int i, j;
		i = getPlayer().x;
		j = getPlayer().y;

		if ( coord[i-1][j] == EMPTY || coord[i-1][j] == DEADLOCK || coord[i-1][j] == GOAL || ( (coord[i-1][j] == BOX || coord[i-1][j] == BOXGOAL) && (coord[i-2][j] == EMPTY || coord[i-2][j] == GOAL) ) )
			moves.add(new Move('U'));
		if ( coord[i+1][j] == EMPTY || coord[i+1][j] == DEADLOCK || coord[i+1][j] == GOAL || ( (coord[i+1][j] == BOX || coord[i+1][j] == BOXGOAL) && (coord[i+2][j] == EMPTY || coord[i+2][j] == GOAL) ) )
			moves.add(new Move('D'));
		if ( coord[i][j-1] == EMPTY || coord[i][j-1] == DEADLOCK || coord[i][j-1] == GOAL || ( (coord[i][j-1] == BOX || coord[i][j-1] == BOXGOAL) && (coord[i][j-2] == EMPTY || coord[i][j-2] == GOAL) ) )
			moves.add(new Move('L'));
		if ( coord[i][j+1] == EMPTY || coord[i][j+1] == DEADLOCK || coord[i][j+1] == GOAL || ( (coord[i][j+1] == BOX || coord[i][j+1] == BOXGOAL) && (coord[i][j+2] == EMPTY || coord[i][j+2] == GOAL) ) )
			moves.add(new Move('R'));

		return moves;
	}
	/**
	 * this function telepors the player to the position he has to be in, to be able to perform
	 * the box move in the push solver, once this function is called it's taken for granted
	 * that it has been checked the player can actually reach the destination.
	 * @param xf		destination x
	 * @param yf		destination y
	 */
	public void Teleport (int xf, int yf) {
		Point playerPosition = getPlayer();
		int xi = playerPosition.x;
		int yi = playerPosition.y;
		coord[xi][yi] = (byte) (coord[xi][yi] & ~PLAYER); 
		coord[xf][yf] = (byte) (coord[xf][yf] |  PLAYER); 
	}

	/**
	 * contaminates square x , y meaning that the player can reach that place
	 * @param x		x parameter of the contamination
	 * @param y		y parameter of the contamination
	 */
	public void Contaminate (int x, int y) {
		coord[x][y] = (byte) (coord[x][y] | CONTAM);
		//System.out.printf("contaminated square (%d,%d)\n",x,y);
	}


	/**
	 * Executes a move and returns the resulting map	
	 * @param move: the executed move
	 * @return: resulting map
	 */
	public Map doMove(Move move) {
		Map map = new Map(this);
		map.performMove(move);
		return map;
	}


	public String getMoveString () {
		String solution = "";
		for (char move : tracker) 
			solution += move;
				System.out.println("Solution: " + solution);
				return solution;
	}


	private void addMoveToTracker (Map map, char move) {
		char[] moves 			= new char[map.tracker.length + 1];
		System.arraycopy(map.tracker, 0, moves, 0, map.tracker.length);
		moves[map.tracker.length] = move;
		map.tracker = moves;
	}


	public void performMove (Move move) {
		addMoveToTracker(this, move.getDirection());
		int index;
		Point playerPosition=getPlayer();
		int x=playerPosition.x;
		int y=playerPosition.y;
		char direction=move.getDirection();

		if (direction=='U') {

			index=boxIndex(x-1,y);

			if (index<0) { // there is no box above
				if (coord[x-1][y]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
					coord[x-1][y]=PLAYER;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x-1][y]==EMPTY && coord[x][y]==PLAYER) {
					coord[x-1][y]=PLAYER;
					coord[x][y]=EMPTY;
				}
				if (coord[x-1][y]==EMPTY && coord[x][y]==PLAYERGOAL) {
					coord[x-1][y]=PLAYER;
					coord[x][y]=GOAL;
				}
				if (coord[x-1][y]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
					coord[x-1][y]=PLAYERGOAL;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x-1][y]==GOAL && coord[x][y]==PLAYER) {
					coord[x-1][y]=PLAYERGOAL;
					coord[x][y]=EMPTY;
				}
				if (coord[x-1][y]==GOAL && coord[x][y]==PLAYERGOAL) {
					coord[x-1][y]=PLAYERGOAL;
					coord[x][y]=GOAL;
				}
				if (coord[x-1][y]==DEADLOCK && coord[x][y]==PLAYERDEADLOCK) {
					coord[x-1][y]=PLAYERDEADLOCK;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x-1][y]==DEADLOCK && coord[x][y]==PLAYER) {
					coord[x-1][y]=PLAYERDEADLOCK;
					coord[x][y]=EMPTY;
				}
				if (coord[x-1][y]==DEADLOCK && coord[x][y]==PLAYERGOAL) {
					coord[x-1][y]=PLAYERDEADLOCK;
					coord[x][y]=GOAL;
				}
			}

			else { //there is a box above

				boxes.set(index, new Box(boxes.get(index).getLocation().x-1,boxes.get(index).getLocation().y));


				if (coord[x-1][y]==BOX) { //not on goal

					if (coord[x-2][y]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
						coord[x-2][y]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x-1][y]=PLAYER;
					}
					if (coord[x-2][y]==EMPTY && coord[x][y]==PLAYER) {
						coord[x-2][y]=BOX;
						coord[x][y]=EMPTY;
						coord[x-1][y]=PLAYER;
					}
					if (coord[x-2][y]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x-2][y]=BOX;
						coord[x][y]=GOAL;
						coord[x-1][y]=PLAYER;
					}
					if (coord[x-2][y]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x-2][y]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x-1][y]=PLAYER;
					}
					if (coord[x-2][y]==GOAL && coord[x][y]==PLAYER) {
						coord[x-2][y]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x-1][y]=PLAYER;
					}
					if (coord[x-2][y]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x-2][y]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x-1][y]=PLAYER;
					}
				}

				else if (coord[x-1][y]==BOXGOAL) { //on goal
					if (coord[x-2][y]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
						coord[x-2][y]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x-1][y]=PLAYERGOAL;
					}
					if (coord[x-2][y]==EMPTY && coord[x][y]==PLAYER) {
						coord[x-2][y]=BOX;
						coord[x][y]=EMPTY;
						coord[x-1][y]=PLAYERGOAL;
					}
					if (coord[x-2][y]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x-2][y]=BOX;
						coord[x][y]=GOAL;
						coord[x-1][y]=PLAYERGOAL;
					}
					if (coord[x-2][y]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x-2][y]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x-1][y]=PLAYERGOAL;
					}
					if (coord[x-2][y]==GOAL && coord[x][y]==PLAYER) {
						coord[x-2][y]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x-1][y]=PLAYERGOAL;
					}
					if (coord[x-2][y]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x-2][y]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x-1][y]=PLAYERGOAL;
					}
				} // else if box on goal

			} // else if index>0
		} // if direction






		if (direction=='D') {


			index=boxIndex(x+1,y); // returns the index of the box that's below the player

			if (index<0) { //there is no box below
				if (coord[x+1][y]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
					coord[x+1][y]=PLAYER;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x+1][y]==EMPTY && coord[x][y]==PLAYER) {
					coord[x+1][y]=PLAYER;
					coord[x][y]=EMPTY;
				}
				if (coord[x+1][y]==EMPTY && coord[x][y]==PLAYERGOAL) {
					coord[x+1][y]=PLAYER;
					coord[x][y]=GOAL;
				}
				if (coord[x+1][y]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
					coord[x+1][y]=PLAYERGOAL;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x+1][y]==GOAL && coord[x][y]==PLAYER) {
					coord[x+1][y]=PLAYERGOAL;
					coord[x][y]=EMPTY;
				}
				if (coord[x+1][y]==GOAL && coord[x][y]==PLAYERGOAL) {
					coord[x+1][y]=PLAYERGOAL;
					coord[x][y]=GOAL;
				}
				if (coord[x+1][y]==DEADLOCK && coord[x][y]==PLAYERDEADLOCK) {
					coord[x+1][y]=PLAYERDEADLOCK;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x+1][y]==DEADLOCK && coord[x][y]==PLAYER) {
					coord[x+1][y]=PLAYERDEADLOCK;
					coord[x][y]=EMPTY;
				}
				if (coord[x+1][y]==DEADLOCK && coord[x][y]==PLAYERGOAL) {
					coord[x+1][y]=PLAYERDEADLOCK;
					coord[x][y]=GOAL;
				}
			}

			else { //there is a box below

				boxes.set(index, new Box(boxes.get(index).getLocation().x+1,boxes.get(index).getLocation().y));


				if (coord[x+1][y]==BOX) { //not on goal

					if (coord[x+2][y]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
						coord[x+2][y]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x+1][y]=PLAYER;
					}
					if (coord[x+2][y]==EMPTY && coord[x][y]==PLAYER) {
						coord[x+2][y]=BOX;
						coord[x][y]=EMPTY;
						coord[x+1][y]=PLAYER;
					}
					if (coord[x+2][y]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x+2][y]=BOX;
						coord[x][y]=GOAL;
						coord[x+1][y]=PLAYER;
					}
					if (coord[x+2][y]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x+2][y]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x+1][y]=PLAYER;
					}
					if (coord[x+2][y]==GOAL && coord[x][y]==PLAYER) {
						coord[x+2][y]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x+1][y]=PLAYER;
					}
					if (coord[x+2][y]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x+2][y]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x+1][y]=PLAYER;
					}
				}

				else if (coord[x+1][y]==BOXGOAL) { //on goal
					if (coord[x+2][y]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
						coord[x+2][y]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x+1][y]=PLAYERGOAL;
					}
					if (coord[x+2][y]==EMPTY && coord[x][y]==PLAYER) {
						coord[x+2][y]=BOX;
						coord[x][y]=EMPTY;
						coord[x+1][y]=PLAYERGOAL;
					}
					if (coord[x+2][y]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x+2][y]=BOX;
						coord[x][y]=GOAL;
						coord[x+1][y]=PLAYERGOAL;
					}
					if (coord[x+2][y]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x+2][y]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x+1][y]=PLAYERGOAL;
					}
					if (coord[x+2][y]==GOAL && coord[x][y]==PLAYER) {
						coord[x+2][y]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x+1][y]=PLAYERGOAL;
					}
					if (coord[x+2][y]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x+2][y]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x+1][y]=PLAYERGOAL;
					}
				} // else if box on goal

			} // else if index>0




		} // if direction




		if (direction=='L') {

			index=boxIndex(x,y-1); // returns the index of the box that's left to the player

			if (index<0) { //there is no box to the left
				if (coord[x][y-1]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
					coord[x][y-1]=PLAYER;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x][y-1]==EMPTY && coord[x][y]==PLAYER) {
					coord[x][y-1]=PLAYER;
					coord[x][y]=EMPTY;
				}
				if (coord[x][y-1]==EMPTY && coord[x][y]==PLAYERGOAL) {
					coord[x][y-1]=PLAYER;
					coord[x][y]=GOAL;
				}
				if (coord[x][y-1]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
					coord[x][y-1]=PLAYERGOAL;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x][y-1]==GOAL && coord[x][y]==PLAYER) {
					coord[x][y-1]=PLAYERGOAL;
					coord[x][y]=EMPTY;
				}
				if (coord[x][y-1]==GOAL && coord[x][y]==PLAYERGOAL) {
					coord[x][y-1]=PLAYERGOAL;
					coord[x][y]=GOAL;
				}
				if (coord[x][y-1]==DEADLOCK && coord[x][y]==PLAYERDEADLOCK) {
					coord[x][y-1]=PLAYERDEADLOCK;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x][y-1]==DEADLOCK && coord[x][y]==PLAYER) {
					coord[x][y-1]=PLAYERDEADLOCK;
					coord[x][y]=EMPTY;
				}
				if (coord[x][y-1]==DEADLOCK && coord[x][y]==PLAYERGOAL) {
					coord[x][y-1]=PLAYERDEADLOCK;
					coord[x][y]=GOAL;
				}
			}

			else { //there is a box to the left
				boxes.set(index, new Box(boxes.get(index).getLocation().x,boxes.get(index).getLocation().y-1));



				if (coord[x][y-1]==BOX) { //not on goal

					if (coord[x][y-2]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y-2]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x][y-1]=PLAYER;
					}
					if (coord[x][y-2]==EMPTY && coord[x][y]==PLAYER) {
						coord[x][y-2]=BOX;
						coord[x][y]=EMPTY;
						coord[x][y-1]=PLAYER;
					}
					if (coord[x][y-2]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x][y-2]=BOX;
						coord[x][y]=GOAL;
						coord[x][y-1]=PLAYER;
					}
					if (coord[x][y-2]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y-2]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x][y-1]=PLAYER;
					}
					if (coord[x][y-2]==GOAL && coord[x][y]==PLAYER) {
						coord[x][y-2]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x][y-1]=PLAYER;
					}
					if (coord[x][y-2]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x][y-2]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x][y-1]=PLAYER;
					}
				}

				else if (coord[x][y-1]==BOXGOAL) { //on goal
					if (coord[x][y-2]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y-2]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x][y-1]=PLAYERGOAL;
					}
					if (coord[x][y-2]==EMPTY && coord[x][y]==PLAYER) {
						coord[x][y-2]=BOX;
						coord[x][y]=EMPTY;
						coord[x][y-1]=PLAYERGOAL;
					}
					if (coord[x][y-2]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x][y-2]=BOX;
						coord[x][y]=GOAL;
						coord[x][y-1]=PLAYERGOAL;
					}
					if (coord[x][y-2]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y-2]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x][y-1]=PLAYERGOAL;
					}
					if (coord[x][y-2]==GOAL && coord[x][y]==PLAYER) {
						coord[x][y-2]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x][y-1]=PLAYERGOAL;
					}
					if (coord[x][y-2]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x][y-2]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x][y-1]=PLAYERGOAL;
					}
				} // else if box on goal

			} // else if index>0



		} // if direction





		if (direction=='R') {


			index=boxIndex(x,y+1); // returns the index of the box that's right to the player

			if (index<0) { //there is no box to the right
				if (coord[x][y+1]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
					coord[x][y+1]=PLAYER;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x][y+1]==EMPTY && coord[x][y]==PLAYER) {
					coord[x][y+1]=PLAYER;
					coord[x][y]=EMPTY;
				}
				if (coord[x][y+1]==EMPTY && coord[x][y]==PLAYERGOAL) {
					coord[x][y+1]=PLAYER;
					coord[x][y]=GOAL;
				}
				if (coord[x][y+1]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
					coord[x][y+1]=PLAYERGOAL;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x][y+1]==GOAL && coord[x][y]==PLAYER) {
					coord[x][y+1]=PLAYERGOAL;
					coord[x][y]=EMPTY;
				}
				if (coord[x][y+1]==GOAL && coord[x][y]==PLAYERGOAL) {
					coord[x][y+1]=PLAYERGOAL;
					coord[x][y]=GOAL;
				}
				if (coord[x][y+1]==DEADLOCK && coord[x][y]==PLAYERDEADLOCK) {
					coord[x][y+1]=PLAYERDEADLOCK;
					coord[x][y]=DEADLOCK;
				}
				if (coord[x][y+1]==DEADLOCK && coord[x][y]==PLAYER) {
					coord[x][y+1]=PLAYERDEADLOCK;
					coord[x][y]=EMPTY;
				}
				if (coord[x][y+1]==DEADLOCK && coord[x][y]==PLAYERGOAL) {
					coord[x][y+1]=PLAYERDEADLOCK;
					coord[x][y]=GOAL;
				}
			}

			else { //there is a box to the right

				boxes.set(index, new Box(boxes.get(index).getLocation().x,boxes.get(index).getLocation().y+1));


				if (coord[x][y+1]==BOX) { //not on goal

					if (coord[x][y+2]==EMPTY && coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y+2]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x][y+1]=PLAYER;
					}
					if (coord[x][y+2]==EMPTY && coord[x][y]==PLAYER) {
						coord[x][y+2]=BOX;
						coord[x][y]=EMPTY;
						coord[x][y+1]=PLAYER;
					}
					if (coord[x][y+2]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x][y+2]=BOX;
						coord[x][y]=GOAL;
						coord[x][y+1]=PLAYER;
					}
					if (coord[x][y+2]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y+2]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x][y+1]=PLAYER;
					}
					if (coord[x][y+2]==GOAL && coord[x][y]==PLAYER) {
						coord[x][y+2]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x][y+1]=PLAYER;
					}
					if (coord[x][y+2]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x][y+2]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x][y+1]=PLAYER;
					}
				}

				else if (coord[x][y+1]==BOXGOAL) { //on goal
					if (coord[x][y+2]==EMPTY &coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y+2]=BOX;
						coord[x][y]=DEADLOCK;
						coord[x][y+1]=PLAYERGOAL;
					}
					if (coord[x][y+2]==EMPTY && coord[x][y]==PLAYER) {
						coord[x][y+2]=BOX;
						coord[x][y]=EMPTY;
						coord[x][y+1]=PLAYERGOAL;
					}
					if (coord[x][y+2]==EMPTY && coord[x][y]==PLAYERGOAL) {
						coord[x][y+2]=BOX;
						coord[x][y]=GOAL;
						coord[x][y+1]=PLAYERGOAL;
					}
					if (coord[x][y+2]==GOAL && coord[x][y]==PLAYERDEADLOCK) {
						coord[x][y+2]=BOXGOAL;
						coord[x][y]=DEADLOCK;
						coord[x][y+1]=PLAYERGOAL;
					}
					if (coord[x][y+2]==GOAL && coord[x][y]==PLAYER) {
						coord[x][y+2]=BOXGOAL;
						coord[x][y]=EMPTY;
						coord[x][y+1]=PLAYERGOAL;
					}
					if (coord[x][y+2]==GOAL && coord[x][y]==PLAYERGOAL) {
						coord[x][y+2]=BOXGOAL;
						coord[x][y]=GOAL;
						coord[x][y+1]=PLAYERGOAL;
					}
				} // else if box on goal

			} // else if index>0




		} // if direction




	} // performMove with Map





	/***
	 * 
	 * @return an ArrayList with the Goal positions useful for the manhattan distances
	 */

	public ArrayList<Point> getGoalPositions(){    

		ArrayList<Point> goalpositions=new ArrayList<Point>();


		for(int i=0;i<coord.length;i++){


			for(int j=0;j<coord[i].length;j++){

				if ((coord[i][j] & GOAL) == GOAL) {
					goalpositions.add(new Point(i,j));

				}


			}
		}

		return goalpositions;


	}//getGoalPositions end





	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coord);
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Map other = (Map) obj;

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		for(int i = 0; i < this.coord.length; i++)
			for(int j = 0; j < this.coord[0].length; j++) 
				if (this.coord[i][j] != other.coord[i][j])
					return false;
		return true;
	}
}