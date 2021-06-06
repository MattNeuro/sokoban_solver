package Map;

public class Deadlock {
	
	private Point location;
	

	public Deadlock(Point p) {
		location=p;
	}
	
	public Deadlock(int x, int y) {
		location=new Point(x,y);
	}
	
	public Point getLocation() {
		return location;
	}
	
	
	public void setLocation(Point x) {
		location=x;
	}


}


