import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.Scrollable;

public class EditField extends JComponent {
	private int height;
	private int width;
	private int blockX;
	private int blockY;
	private int sqrWidth;
	private double scale;

	public EditField(int width, int height) {
		blockX = width;
		blockY = height;
		scale = 1;
	}
   
	
	public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
	
	public void addScale(double s) {
		double scale = this.scale + s;
		if ((int)(this.getParent().getHeight()*scale) >= this.getParent().getHeight()) {
			this.scale += s;
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setFocusable(true);

	    this.height = this.getParent().getHeight();
		sqrWidth = (int)(height/blockY*scale);
		this.height = sqrWidth * blockY;
		this.width = sqrWidth*blockX;
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), height);
		
		g.setColor(Color.WHITE);
		EditPanel.setsqrWidth(sqrWidth);
	    int screenWidth = getWidth()/sqrWidth;
	    g.fillRect(0, 0, (int)(blockX*sqrWidth), (int)(blockY*sqrWidth));
	    
	    drawDashedLine(g);
	}
	
	public void drawDashedLine(Graphics g){

        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GRAY);

        //set the stroke of the copy, not the original 
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        for (int j=1; j<blockY; j++) {
        	g2d.drawLine(0, j*sqrWidth-1, (int)(width), j*sqrWidth-1);
    	}
        for (int i=1; i<blockX; i++) {
        	g2d.drawLine(i*sqrWidth-1, 0, i*sqrWidth-1, (int)(height));
	    }
	}


}
