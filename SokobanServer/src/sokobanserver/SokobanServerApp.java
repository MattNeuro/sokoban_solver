/*
 * SokobanServerApp.java
 */
package sokobanserver;

import org.jdesktop.application.SingleFrameApplication;
import sokobanserver.service.Service;
import sokobanserver.map.MapHandler;

/**
 * The main class of the application.
 */
public class SokobanServerApp extends SingleFrameApplication {


    public static MapHandler        map     =   null;
    public static SokobanServerView view    =   null;
    public static Service           service =   null;
    


    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        view    = new SokobanServerView(this);
        map     = new MapHandler();
        service = new Service();

        show(view);
        service.start();
        view.levelList.setSelectedIndex(0);
    }

    
    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(SokobanServerApp.class, args);
    }
}