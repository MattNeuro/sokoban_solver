package Map;

/**
 * A class for easier manipulation of the box locations
 * @author Radu
 *
 */

public class Box {
	
	private Point boxLocation;
	
	public Box (Point p) {
		boxLocation = p;
	}
	
	public Box (int x,int y) {
		boxLocation = new Point(x, y);
		
	}
	
	public Point getLocation() {
		return boxLocation;
	}
	
	public void setLocation(Point x) {
		boxLocation = x;
	}
	
	public void setLocation(int x, int y) {
		boxLocation = new Point(x, y);
	}
}
