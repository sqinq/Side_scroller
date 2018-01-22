
public class Obstacle {
	public int x0;
	public int width;
	public int y0;
	public int height;
	
	public Obstacle(int x0, int x1, int y0, int y1) {
		this.x0 = x0;
		this.width = x1-x0+1;
		this.y0 = y0;
		this.height = y1-y0+1;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void resetLocation(int x, int y) {
		this.x0 = x;
		this.y0 = y;
	}
	
	public void resize(int w, int h) {
		this.width = w;
		this.height = h;
	}
	
	public Obstacle copy() {
		return new Obstacle(x0, x0+width-1, y0, y0+height-1);
	}
}
