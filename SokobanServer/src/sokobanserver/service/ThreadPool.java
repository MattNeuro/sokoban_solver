package sokobanserver.service;

import java.util.LinkedList;
import sokobanserver.SokobanServerApp;

public class ThreadPool extends LinkedList<ServiceThread> {


    private static final long serialVersionUID = 8973833150088966650L;

    /**
     *	Make sure only ServerThread objects are added
     *	to the thread pool for type-safety.
     *
     * 	@param latest	The ServerThread object to add to the pool.
     */
    @Override public boolean add (ServiceThread latest) {
        boolean result = super.add(latest);
        Object[] clients = this.toArray();
        SokobanServerApp.view.clientList.setListData(clients);
        return result;
    }
    
    
    @Override public boolean remove (Object object) {
        boolean result = super.remove(object);
        Object[] clients = this.toArray();
        SokobanServerApp.view.clientList.setListData(clients);
        return result;        
    }
}