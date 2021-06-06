package sokobanserver.map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import sokobanserver.SokobanServerApp;

/**
 *  MapHandler
 *
 *  Loads maps in the "maps.txt" file and allows other classes to retrieve
 *  individual map data, or show a map on screen.
 *
 * @author Matthijs
 */
public class MapHandler {


    private HashMap<Integer, Map>   mapList =   null;


    public MapHandler () {
        System.out.println("Maphandler created.");
        mapList = new HashMap();
        try {
            loadMaps();
            showMap(11);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getMap (int index) throws Exception {
        System.out.println("Retrieving map " + index);
        if (!mapList.containsKey(index))
            throw new Exception("Map not found");

        return mapList.get(index).getMapData();
    }


    /**
     *  Show a specific map on the screen; if that map is
     *  loaded of course.
     * 
     *  @param index
     *  @throws Exception
     */
    public void showMap (int index) throws Exception {
        if (!mapList.containsKey(index))
            throw new Exception("Map not found");

        mapList.get(index).show();
    }


    /**
     *  Load all maps in the maps.txt file that should be provided
     *  in the root of the directory.
     *
     *  @throws IOException
     */
    private void loadMaps () throws IOException {
        SokobanServerApp.view.levelList.removeAll();
        BufferedReader inputStream  = new BufferedReader(new FileReader("maps.txt"));
        String line                 = null;
        String map                  = "";
        
        while ((line = inputStream.readLine()) != null) {
            if (line.contains(";LEVEL")) {
                if (map.length() > 0)
                    createMap(map);
                map = line + "\r\n";
            }
            else
                map += line + "\r\n";
        }
        createMap(map);

        Stack<Map> allMaps = new Stack();
        for (Map current : mapList.values())
            allMaps.add(current);
        SokobanServerApp.view.levelList.setListData(allMaps);
    }


    /**
     *  Create a new Map object from the map string provided.
     *
     *  @param mapString
     */
    private void createMap (String mapString) {
        int lineBreak = mapString.indexOf("\r\n");
        int mapIndex  = Integer.parseInt(mapString.substring(7, lineBreak));
        Map map       = new Map(mapString.substring(lineBreak), mapIndex);
        mapList.put(mapIndex, map);
    }
}