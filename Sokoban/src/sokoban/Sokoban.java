package sokoban;

import Map.*;
import communication.Client;

/**
 * Sokoban Solver class.
 * 
 * Handles loading a map, starting a sokoban solver finding a solution through
 * delegated subclasses.
 * 
 * Implemented as a singleton, so other classes can retrieve a single instance
 * of required resources shared with the rest of the application.
 * 
 * @author Matthijs
 */
final public class Sokoban {

	private static 	Sokoban 	instance 	= null;
	private  		Client 		client 		= null;
	private  		MapHandler 	mapHandler 	= null;
	private 		int 		mapId 		= 0;

	
	// Getters for our instantiated objects:
	public static Client getClient() {
		return Sokoban.instance.client;
	}

	public static MapHandler getMapHandler() {
		return Sokoban.instance.mapHandler;
	}

	public static int getMapId() {
		return Sokoban.instance.mapId;
	}
	

	/**
	 * Public main, this method gets called first and initializes our Sokoban
	 * object.
	 * 
	 * @param args
	 *            Command line arguments. First argument should be the map ID.
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 3)
				throw new Exception(
						"Add a command-line parameter for the server, port and map to load.");
			int port 	= Integer.parseInt(args[1]);
			int mapId 	= Integer.parseInt(args[2]);
			
			new Sokoban(args[0], port, mapId);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	
	/**
	 * Sokoban constructor. Create a new Sokoban object, though only if it is
	 * not already instantiated.
	 * 
	 * @param map
	 * @throws Exception
	 */
	private Sokoban (String server, int port, int mapId) throws Exception {
		if (Sokoban.instance != null)
			throw new Exception("Sokoban instance already created.");

		Sokoban.instance 	= this;
		this.mapId			= mapId;
		client 				= new Client(server, port);
		mapHandler			= new MapHandler();
		
		runSolvers();
	}
	
	
	/**
	 *	Run as many solvers as are possible, through the number of 
	 *	available CPU cores.
	 */
	private void runSolvers () {
		int processors 		= Runtime.getRuntime().availableProcessors();
		new SolverBestFS();
		if (processors > 1)
			new SolverIDS();
		for (int i = 2; i < processors; i++) 
			new SolverGA();
	}
}






