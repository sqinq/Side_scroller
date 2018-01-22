import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Node {
	private int nodex;
	private int nodey;
	private int s;
	
	private int screenWidth;
	private int limitX;
	private int limitY;
	
	public Node(int x, int y, int s, int limitX, int limitY) {
		this.nodex = x;
		this.nodey = y;
		this.s = s;
		this.limitX = limitX;
		this.limitY = limitY;

	}
	
	public int getx() {
		return nodex;
	}
	
	public int gety() {
		return nodey;
	}
	
	public void setscreenWidth(int w) {
		screenWidth = w;
	}
	
	
	public void up() {
		if (nodey-s >= 0)
			nodey -= s;
	}
	
	public void down() {
		if (nodey+s < limitY)
		nodey += s;
	}
	
	public void back() {
		nodex -= s;
	}
	
	public void forward() {
		nodex += s;
	}
}
