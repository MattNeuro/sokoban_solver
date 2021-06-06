package sokobanserver.service;

import java.io.IOException;
import java.net.ServerSocket;


/**
 *
 * @author Matthijs
 */
public class Service extends Thread {

    private ServerSocket    socket      =   null;
    private ThreadPool      pool        =   null;

    public  boolean         listening   =   true;


    /**
     *  Run the service listener.
     *
     *  This creates a thread pool, then adds clients whenever
     *  a new connection is made.
     */
    public void run () {
        pool = new ThreadPool();
        
        while (true) {
            try {
                listen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     *  Open a listener and listen on the port set in this.port.
     *
     *  For each connection a new ServerThread is created.
     *
     *  @throws IOException
     */
    private void listen () throws IOException {
        socket              =   new ServerSocket(5555);
        ServiceThread latest;

        while (listening) {
            latest = new ServiceThread(socket.accept());
            latest.setPool(pool);
            latest.start();
            pool.add(latest);
        }
        socket.close();
    }
}
