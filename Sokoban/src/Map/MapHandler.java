package Map;

import sokoban.Sokoban;

/**
 * Class for manipulating a sokoban map contents
 * 
 * @author Radu
 * 
 */

public class MapHandler {

	private Map map = null;

	/**
	 * Create a MapHandler object. MapHandler loads the map from the map string
	 * and allows us to retrieve the original map received from the server.
	 * 
	 * @throws Exception
	 */
	public MapHandler() throws Exception {
		
		map = new Map(Sokoban.getClient().getMap());
		
		System.out.println("Map after parse: ");
		map.printMap();
		
		System.out.println("Adding deadlocks.");
		map.addDeadlocks(); // determine initial deadlocks
		
		
		System.out.println("Map after deadlocks: ");
		map.printMap();
	
	}

	
	/**
	 * Retrieve the map object loaded by this maphandler. Note that this should
	 * always be the 'original' map, not some modified version! Hence, we do not
	 * have a setMap.
	 * 
	 * @return Map A clean Map object.
	 */
	public Map getMap() {
		return map;
	}
}
