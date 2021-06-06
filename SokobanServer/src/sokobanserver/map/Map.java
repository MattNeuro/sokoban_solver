package sokobanserver.map;

import sokobanserver.SokobanServerApp;

/**
 *
 * @author Matthijs
 */
public class Map {

    private   String      sourceData    = null;
    private   int         level         = 0;
    protected char[][]    data          = null;


    /**
     *  Construct a map from source string and level.
     *
     *  This creates a new Map object from the data-string
     *  provided. The level index is provided mostly for esthetic
     *  reasons, so we can show which level is currently being loaded.
     *
     *  @param data     The source (layout) of the map.
     *  @param level    Level indicator of this map.
     */
    public Map (String data, int level) {
        sourceData  = data.replace(' ', '_').trim().replace('_', ' ');
        this.level  = level;
        System.out.println("Loaded map: " + data);
        parseMapData();
    }


    /**
     *  Show the map.
     *
     *  This loads the map data onto the map canvas, then repaints the
     *  canvas to actually show it.
     */
    public void show () {
        System.out.println("Showing map " + level);
        MapCanvas canvas = (MapCanvas) SokobanServerApp.view.mapCanvas;
        canvas.setData(data);
        canvas.repaint();
    }
    

    /**
     *  Retrieve map data.
     *
     *  @return String the original map data, as string.
     */
    public String getMapData () {
        return sourceData;
    }


    /**
     *  Attempt a solution: move the player following the
     *  instructions provided.
     * 
     *  @return result.
     */
    public String trySolution (String data) {
        MapSolver solver = new MapSolver(this, data);
        solver.start();
        return "fail";

    }


    /**
     *  Parse map data: create a square array of characters, where each
     *  character corresponds to a specific position on the map.
     *
     *  Excess length on either side of the map is automatically padded
     *  with empty fields to ensure the char array is always square.
     */
    protected void parseMapData () {
        String mapData  = sourceData.replace(' ', '_').trim();
        mapData         = mapData.replace('*', 'a');
        String[] lines  = mapData.split("\r\n");
        int length      = 0;
        for (String line : lines)
            length = length > line.length() ? length : line.length();
        if (lines.length > length)
            length = lines.length;

        data = new char[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                char c;
                if (lines.length > i) {
                    char[] line = lines[i].toCharArray();
                    if (line.length > j)
                        c = line[j];
                    else
                        c = '_';
                }
                else
                    c = '_';
                data[i][j] = c;
            }
        }
    }


    /**
     *  Name of this map: this should be the level indicator.
     *
     *  Note that this does NOT return the original map source
     *  string: use getMapData() for that!
     *
     *  @return String the name of this map.
     */
    @Override public String toString () {
        return "   Level " + level;
    }
}