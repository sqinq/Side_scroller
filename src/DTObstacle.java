import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;


public class DTObstacle extends JComponent {
	private Obstacle obstacle;
	private GridBagConstraints c;
	private int x;
	private int y;
	private int sqrWidth;
	
	
	public DTObstacle(Model model, int x, int y, int sqrWidth, GridBagConstraints c) {
		this.x = x;
		this.y = y;
		this.sqrWidth = sqrWidth;
		this.c = c;
		
		this.setOpaque(false);
		this.setFocusable(true);
		DragGesture dg = new DragGesture(this, model);
		this.addMouseListener(dg);
        this.addMouseMotionListener(dg);
		this.addFocusListener(new HighlightWhenFocusedListener());
	}
	
	public void setObstacle(Obstacle obs) {
		obstacle = obs;
	}
	
	public Obstacle getObstacle() {
		return obstacle;
	}
	
	public void resetsqrWidth(int w) {
		sqrWidth = w;
	}
	
	public int getx() {
		return x;
	}
	
	public int gety() {
		return y;
	}
	
	protected void paintComponent(Graphics g) {
		if (obstacle != null) {
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, obstacle == null ? getHeight() : obstacle.getWidth()*getHeight(),
					obstacle == null ? getHeight() : obstacle.getHeight()*getHeight());

		} 
		
		if (this.isFocusOwner()) {
			this.setVisible(true);
			g.setColor(Color.red);
			g.drawRect(0, 0, obstacle == null ? getHeight() : obstacle.getWidth()*getHeight(),
					obstacle == null ? getHeight() : obstacle.getHeight()*getHeight());
		}

	}
	
	public Dimension getPreferredSize() {
		if (obstacle != null)
			return new Dimension(sqrWidth*obstacle.width, sqrWidth*obstacle.height);
		else 
			return new Dimension(sqrWidth, sqrWidth);
    }
	
	class HighlightWhenFocusedListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			// Draw the component with a red border
			// indicating that it has focus.
			DTObstacle.this.repaint();
		}

		public void focusLost(FocusEvent e) {
			// Draw the component with a black border
			// indicating that it doesn't have focus.
			DTObstacle.this.repaint();
		}

	}
	
	
		

}
