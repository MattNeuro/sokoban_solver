/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sokobanserver.service;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import sokobanserver.SokobanServerApp;
import sokobanserver.map.Map;

/**
 *
 * @author Matthijs
 */
public class ServiceThread extends Thread {
    
    private PrintWriter         out     = null;
    private InputStreamReader   in      = null;
    private Socket 		socket 	= null;
    private Map                 map     = null;
    private ThreadPool          pool    = null;


    public ServiceThread (Socket socket) throws SocketException {
        this.socket = socket;
        System.out.println("Socket thread created.");
    }


    /**
     * 	Overwriting the Threads run method, this performs the required
     * 	action in a separate thread, ie, starts and keeps listening.
     */
    @Override public void run () {
        try {
                listen();
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }


    @Override public String toString () {
        return "   Client " + this.getId();
    }

    /**
     *	Connect the socket given to this thread with a buffered in- and output
     *	reader, then keep listening on those readers for input. If any is
     *	given, we tell our parent!
    **/
    private void listen () throws Exception {
        out     =  new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in	=  new InputStreamReader(socket.getInputStream());
        readBoardNumber();
        sendSelectedBoard();
        readSolution();
        finalize();
    }
    
    
    /**
     *  Set the ThreadPool this object is listed in. We need to remove it from
     *  that pool after the client disconnects.
     * 
     *  @param pool 
     */
    public void setPool (ThreadPool pool) {
        this.pool = pool;
    }
    

    /**
     *  Read in a solution from the client. This is a series of moves, send
     *  as a string, which determine what way the agent moves.
     *
     *  @throws Exception
     */
    private void readSolution () throws Exception {
        String  data    =  "";
        while (!in.ready())
            ServiceThread.sleep(50);
        while (in.ready() && socket.isConnected())
            data += (char) in.read();

        String result = map.trySolution(data);
        sendResult(result);
    }


    /**
     *  Provide feedback to the connected client about his solution: whether
     *  this lead to a successful solve, or a failed attempt.
     */
    private void sendResult (String result) throws IOException {
        out.println(result);
        out.flush();
    }



    /**
     *  After connecting, a client will send us the board number it wants
     *  to solve. Read in this board number, load and select it, then
     *  send the actual board data.
     *
     * @throws Exception
     */
    private void readBoardNumber () throws Exception {
        String  data    =  "";
        while (!in.ready())
            ServiceThread.sleep(50);
        while (in.ready() && socket.isConnected())
            data += (char) in.read();

        int board = Integer.parseInt(data.trim()) - 1;
        if (!SokobanServerApp.view.menuItemOverwriteMap.isSelected())
            SokobanServerApp.view.levelList.setSelectedIndex(board);
    }


    /**
     *  Once a board has been selected, we send the string data of that
     *  board back to the connected client.
     *
     *  @throws IOException
     */
    private void sendSelectedBoard () throws IOException {
        map = (Map) SokobanServerApp.view.levelList.getSelectedValue();
        String      data    = map.getMapData();
        String[]    lines   = data.split("\n");
        out.println(lines.length);

        for (String line : lines)
            out.print(line);
        out.println();
        out.flush();
    }


    /**
     *	Destructor: close off any open connections and file streams
     *	to make sure we won't get memory leaks here.
     */
    @Override protected void finalize () {
        try {
            in.close();
            out.close();
            socket.close();
            pool.remove(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Socket thread closed.");
    }
}