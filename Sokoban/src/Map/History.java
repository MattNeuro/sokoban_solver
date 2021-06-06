package Map;

import java.util.LinkedList;
/**
 * 
 * @author Alejandro
 *
 */
public class History {
	
	//the History as a linked list of Nodes
	private LinkedList<Node> hist;
	
	/**
	 * Constructor for the history, casted once with the initial state as parameter
	 * @param pNode		Initial state to create history, must not be null
	 */
	public History ( Node pNode ) {
	hist=new LinkedList<Node>();
		System.out.println( "History created" );
		this.hist.add( pNode );
	}
	
	/**
	 * Adds a node to the history.
	 * @param pNode		Node to be added
	 */
	public void Add ( Node pNode ){
		hist.add( pNode );
	}
	
	/**
	 * returns a boolean that indicates if the Node was contained in the history
	 * @param pNode		the node we want to look for
	 * @param b			1 for printing on screen, 0 for not printing.
	 * @return			boolean 1 if we found it, or 0 otherwise
	 */
	public boolean Contains ( Node pNode , boolean b ){
		boolean c = false;
		c = hist.contains( pNode );
		if( b ){
			if( c )
				System.out.println( "Found node on history" );
			else
				System.out.println( "Not Found node on history" );
		}
		return c ;
	}
	
	/**
	 * Deletes the most recent appearance of the Node passed as argument
	 * This function doesn't check if the node is contained in the history
	 * It assumes the user has already checked true for search.
	 * @param pNode		The node we want to delete
	 */
	public void Prune ( Node pNode ){
		int index = hist.lastIndexOf( pNode );
		Node deleted = hist.get( index );
		System.out.print( "You deleted this Node from history\n" );
		System.out.print( deleted );
		hist.remove( index );
	}
	
	/**
	 * Returns the size of the history, print the value on the console
	 * @param b 	1 for printing on screen, 0 for not printing.
	 * @return 		size of the history until now.
	 */
	public int Size ( boolean b ){
		int l  = hist.size( );
		if( b )
			System.out.println( "# items in history: " + l );
		return l ;
	}
	
}
