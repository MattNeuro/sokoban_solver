package Map;

/**
 * A class for easier use of coordinates
 * @author Radu
 *
 */
public class Point {
	public int x;
	public int y;
	

	public Point(int x,int y) {
		this.x=x;
		this.y=y;
	}
	
	
	public Point(Point p) {
		this(p.x,p.y);
	}
	
	
	public int compareTo(Point p) {
        if (x != p.x)
            return x - p.x;
        return y - p.y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;
        Point p = (Point)o;
        return x == p.x && y == p.y;
    }
}
