import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameField extends JPanel {
	private int height;
	private int width;
	private int blockX;
	private int blockY;
	private int sqrWidth;
	private double firstX = 0;
	
	private int speed = 1;
	private int FPS = 30;
	
	private Model model;
	
	private int nodeX;
    private int nodeY;
	
	private List<Obstacle> obstacles;
	private Timer repaintT;
	
	public GameField(int height, int width, List<Obstacle> obstacles, Model model, int scroll, int frame) {
		this.blockY = height;
		this.blockX = width;
		this.obstacles = obstacles;
		this.model = model;
		this.speed = scroll;
		this.FPS = frame;
		
		repaintT = new Timer(1000/FPS, new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (firstX*sqrWidth+getWidth() < width*sqrWidth) {
        			firstX += (double)speed/FPS;
        		}
    			repaint();
        	}
        });
		repaintT.start();
		this.addKeyListener(new KeyListener() {
			
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                model.moveNode(e.getKeyChar());
            }

            @Override
            public void keyPressed(KeyEvent e) {}
        });
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setFocusable(true);
		this.requestFocus();
	    this.height = getHeight();
		this.width = height*blockX/blockY;
	    sqrWidth = height/blockY;
	    int screenWidth = getWidth()/sqrWidth;
	    model.setScreenWidth(screenWidth);
	    g.setColor(Color.WHITE);
	    g.fillRect(0, 0, width, height);
		

	    g.setColor(Color.BLUE);
	    g.drawPolygon(new int[] {(int)((nodeX-firstX)*sqrWidth), (int)((nodeX-firstX)*sqrWidth), (int)((nodeX+1-firstX)*sqrWidth)}, 
	    		new int[] {nodeY*sqrWidth+sqrWidth, nodeY*sqrWidth, nodeY*sqrWidth+sqrWidth/2}, 3);
	    
	    if (firstX+screenWidth-1<nodeX)
	    	firstX = nodeX - screenWidth + 1;
	    
	    for (Obstacle o : obstacles) {
			if (o.x0+o.width>firstX && o.x0<firstX) {
				g.fillRect(0, o.y0*sqrWidth, (int)((o.width-firstX+o.x0)*sqrWidth), o.height*sqrWidth);
			} else if (o.x0>=firstX) {
				g.fillRect((int)((o.x0-firstX)*sqrWidth), o.y0*sqrWidth, o.width*sqrWidth, o.height*sqrWidth);
			}
	    }
	}
	
	public void pause() {
		repaintT.stop();
	}
	
	public void restart() {
		try {
			if (firstX*sqrWidth+getWidth() < width)
				repaintT.restart();
		} catch (Exception e) {
			
		}
	}
	
	public void update(int x, int y) {
		nodeX = x;
		nodeY = y;
	}
}
