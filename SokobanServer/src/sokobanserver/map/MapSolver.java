package sokobanserver.map;

import sokobanserver.SokobanServerApp;

/**
 *
 * @author Matthijs
 */
public class MapSolver extends Thread {

    private Map     source      =   null;
    private int     x           =   0;
    private int     y           =   0;
    private char[]  actions     =   null;


    public MapSolver (Map source, String solution) {
        System.out.println("Attempting solution: " + solution);
        solution        = solution.replace(" ", "").trim();
        this.source     = source;
        actions         = solution.toCharArray();
        findPlayer();
        System.out.println("Found player at " + x + ", " + y);
    }


    public void run () {
        try {
            for (char c : actions)
                move(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        source.parseMapData();
    }



    private void move (char direction) throws Exception {
        System.out.println("Moving in direction " + direction);
        int targetX = x, targetY = y;
        switch (direction) {
            case 'u': case 'U':
                targetX = x - 1;
                break;
            case 'd': case 'D':
                targetX = x + 1;
                break;
            case 'l': case 'L':
                targetY = y - 1;
                break;
            case 'r': case 'R':
                targetY = y + 1;
                break;
        }
        int boxX           = targetX + (targetX - x);
        int boxY           = targetY + (targetY - y);

        char target        = source.data[targetX][targetY];
        char boxTarget     = source.data[boxX][boxY];
        
        if (target == '$' || target == 'a') {
            if (boxTarget == '.')
                source.data[boxX][boxY] = 'a';
            if (boxTarget == '_')
                source.data[boxX][boxY] = '$';

            if (target == '$')
                source.data[targetX][targetY] = '@';
            if (target == 'a')
                source.data[targetX][targetY] = '+';
        }

        if (target == '.')
            source.data[targetX][targetY] = '+';
        if (target == '_')
            source.data[targetX][targetY] = '@';

        if (source.data[x][y] == '+')
            source.data[x][y] = '.';
        if (source.data[x][y] == '@')
            source.data[x][y] = '_';

        x = targetX;
        y = targetY;
        source.show();
        if (!SokobanServerApp.view.menuItemQuickSolve.isSelected())
            MapSolver.sleep(500);
    }


    private void findPlayer () {
        int length = source.data.length;
        for (x = 0; x < length; x++)
            for (y = 0; y < length; y++)
                if (source.data[x][y] == '@' || source.data[x][y] == '+')
                    return;
    }
}