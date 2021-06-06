package sokobanserver.map;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;

/**
 *  The 'canvas' we draw our map on. This is basically a way to paint
 *  images and other structures onto a region of the screen. 
 * 
 *  We use this to show the different tiles in the current map, in their
 *  correction position.
 *
 *  @author Matthijs
 */
public class MapCanvas extends Canvas {


    private char[][]                    data        = null;
    private HashMap<Character, Image>   images      = null;


    /**
     *  Load the source data for this map. This is a square two-dimensional char
     *  array containing the image code for each tile.
     *
     *  @param data
     */
    public void setData (char[][] data) {
        this.data = data;
        loadImages();
    }


    /**
     *  Load the source images we need to paint our canvas with. These
     *  include all the distinct tiles used in this data. Thus, we need
     *  to have  data set correctly before we can call this.
     */
    private void loadImages () {
        images = new HashMap();
        Toolkit kit = Toolkit.getDefaultToolkit();

        for (char[] i : data) {
            for (char j : i) {
                if (images.containsKey(j))
                    continue;
                images.put(j, kit.createImage(j + ".png"));
            }
        }
    }


    /**
     *  Pain the canvas.
     *
     *  Called whenever the canvas object's repaint method is called in
     *  a threaded manner (so drawing the canvas should in fact have minimal
     *  influence on the performance of the algorithm).
     *
     * @param graphics
     */
    @Override public void paint (Graphics graphics) {
        if (data == null)
            return;

        int     squareSize  = graphics.getClipBounds().width / data.length;
        Image   current     = null;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                current = images.get(data[j][i]);
                graphics.drawImage(current, (i * squareSize), (j * squareSize), squareSize, squareSize, this);
            }
        }
    }


    // Override update method to prevent flicker.
    @Override public void update(Graphics graphics) {
        paint(graphics);
    }
}