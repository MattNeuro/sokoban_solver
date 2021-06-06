package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


import sokoban.Sokoban;

/**
 * 	Client to the Sokoban server. This class establishes a 
 * 	connection to the server, sends which map we want to solve,
 * 	receives the map data, and, when it is solved, sends the
 * 	solution back to the server to be checked.
 * 
 * 	@author Matthijs
 */
public class Client {

	private	BufferedReader	in			=	null;
	private	PrintWriter		out			=	null;
	private	String			map			=	null;
	private	Socket			socket		=	null;

	
	/**
	 * 	Client constructor. Attempts to establish a connection to
	 * 	the server.
	 */
	public Client (String host, int port) throws IOException {
		System.out.println("Starting client listener.");
		connect(host, port);
	}
	
	
	/**
	 * 	Retrieve the string representation of the map that
	 * 	was send to this client by the server. Will throw an
	 * 	exception if no map was loaded.
	 * 
	 * 	Note, due to the crappy way the client was designed, this
	 * 	might contain excess bytes.	
	 * 
	 * 	@return		String containing the map representation.
	 * 	@throws 	Exception
	 */
	public String getMap () throws Exception {
		if (map == null) 
			throw new Exception("Attempt to read board, but board not set.");
		return map;
	}
	
	
	/**
	 * 	Send the solution (for the loaded map?) back to the server. Then, we 
	 * 	wait for the server to evaluate it: this is a string representing 
	 * 	either success or failure.
	 * 
	 * 	@param solution		The (String) representation of the moves that solve
	 * 						the loaded Sokoban puzzle. Should consist of 'UDLR'. 
	 * 	@return
	 * 	@throws Exception
	 */
	public void sendSolution (String solution) throws Exception {
		System.out.println("Sending solution to the server: ");
		System.out.println("\t" + solution);
        out.println(solution);
        out.flush();
        String result = in.readLine();
        System.out.println("Result: " + result + ".");
        socket.close();
	}
	
	
	/**
	 * 	Attempt to connect to a server and read the map.
	 * 
	 * 	The protocol for this is as follows:
	 * 
	 * 	1. Client -> Server: ID of the map to load.
	 *  2. Client <- Server: Number of lines in the map.
	 *  3. Client <- Server: For each line, send the line of the map.
	 *  4. Client -> Server: Solution for that map.
	 *  5. Client <- Server: Result of that solution. 
	 * 
	 * 	@throws Exception
	 */
	private void connect (String target, int port) throws IOException {
        socket 		= new Socket(target, port);
        in 			= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out 		= new PrintWriter(socket.getOutputStream(), true);
        map 		= new String("");

        System.out.println("Succesfully connected to " + target);
        out.println(Sokoban.getMapId());
        int rowCount  		= Integer.parseInt(in.readLine());
        
        for (int i = 1; i < rowCount; i++)
        	map += in.readLine() + "\r\n";
        map += in.readLine();
        
        System.out.println("Received board:\r\n" + map);
	}
}