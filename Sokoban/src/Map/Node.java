package Map;

import java.util.Arrays;


/**
 * 
 * @author Alejandro
 *
 */
public class Node {
	
	private int[][] nodes;		/* Store the basic information to define a state
								 * Matrix information distributed as follows
								 * for the first 'nboxes' columns: the first row
								 * contains the X position of the box and the second
								 * row contains the Y position of the box.
								 * for the following column: the first row contains
								 * the X position of the player and the second row
								 * contains the Y position of the player.
								 * for the final column: the first row contains the
								 * depth of the node itself and the second row contains
								 * the heuristic value of the node. See Below for graph.
								 */
	
	static int nboxes;			// there is only one number of boxes per puzzle
	
	
	
	/**
	 * Constructor Node, creates a new matrix with the basic information of a state
	 * still missing to add the heuristic value to the node (its commented)
	 * @param map		the map associated with this node
	 */
	public Node ( Map map, int depth ){
		
		nboxes = map.boxes.size();
		System.out.println(nboxes);
		nodes = new int[2][nboxes+2];
		
		
		for(int i = 0 ; i < nboxes ; i++ ){
			nodes[0][i] = map.boxes.elementAt(i).getLocation().x;
			nodes[1][i] = map.boxes.elementAt(i).getLocation().y;
		}
		
		nodes[0][nboxes] = map.getPlayer().x; //perhaps casting the function twice
		nodes[1][nboxes] = map.getPlayer().y; //is not the most efficient way to do this
		
		nodes[0][nboxes+1] = depth;
		//nodes[1][nboxes+1]=heuristic;
	}
	
	/**
	 * print the information in the matrix array
	 */
	public void DisplayNode ( ){
		System.out.println(Arrays.deepToString(nodes));
	}
	
	/**
	 * Get the depth of the node
	 * @param b		1 means print, 0 means don't print
	 * @return		depth
	 */
	public int getDepthNode ( boolean b ){
		if( b )
			System.out.println( nodes[1][nboxes+2] );
		return( nodes[1][nboxes+2] );
	}
	
	/**
	 * Gets the position of any box or the player
	 * @param i		to refer to a box use i:1->nboxes, to refer to the player use i:nboxes;
	 * @return		returns the x,y position of the box/player specified.
	 */
	public int[] getPosition ( int i ){
		i--;
		int[] coord;
		coord = new int[2];
		if(i < nboxes){
			coord[0] = nodes[1][i];
			coord[1] = nodes[2][i];
			System.out.println( "the box number: " + i + "is located at:"+ coord );
		}
		else if(i == nboxes){
			coord[0] = nodes[1][i];
			coord[1] = nodes[2][i];
			System.out.println( "the player is located at:"+ coord );
		}
		else {
			coord[0] = 0;
			coord[1] = 0;
			System.out.println( "ERROR: Enter valid parameter" );
		}
		return( coord );
	}
	
	/**
	 * returns the heuristic value of the node
	 * @return		return heuristic value previously associated with the node
	 */
	public int Score (){
		return nodes[1][nboxes+1];
	}
	
	/* Graphical representation of the Node
	 * --------------------------------------------------------------------------
	 * | Xfirst box | Xsec box  | Xthird box| Xfour box | X player  | Depth node|
	 * --------------------------------------------------------------------------
	 * | Yfirst box | Ysec box  | Ythird box| Yfour box	| Y Player  | Heuristic |
	 * --------------------------------------------------------------------------
	 */
}
