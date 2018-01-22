import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class ObstacleTransferHandler extends TransferHandler {
	 	DTObstacle sourceObs;
	    boolean shouldRemove;
	    Model model;
		private DTObstacle [][] obstacles;
		private DataFlavor obstacleFlavor = new DataFlavor(Obstacle.class, "obstacleFlavor");
		
		public ObstacleTransferHandler(Model model, DTObstacle [][] obstacles) {
			this.model = model;
			this.obstacles = obstacles;
		}

	    /* 
	     * Can we import one of the flavors provided? 
	     */
	    public boolean canImport(JComponent c, DataFlavor[] flavors) {
	        for (int i = 0; i < flavors.length; i++) {
	            if (flavors[i].equals(obstacleFlavor)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    /* 
	     * Import the data from the transferable to the component.
	     */
	    public boolean importData(JComponent c, Transferable t) {
	        Obstacle obs;
	        if (canImport(c, t.getTransferDataFlavors())) {
	        	DTObstacle o = (DTObstacle)c;
	            //Don't drop on myself.
	            if (sourceObs == o) {
	                shouldRemove = false;
	                return true;
	            }
	            
	            if (o.getObstacle() != null) 
	            	return false;
	            
	            try {
	            	if (t.isDataFlavorSupported(obstacleFlavor)) {
	            		obs = (Obstacle)t.getTransferData(obstacleFlavor);
	            	} else {
	            		obs = null;	// assure compiler everything was initialized
	            		assert false;
	            	}
	                //Set the component to the new picture.
	            	if (o.getx()+obs.width>obstacles[0].length || o.gety()+obs.height>obstacles.length) {
	            		return false;
	            	}
	            	model.moveObstacleTo(obs, o);
	            	model.setCurrentSelection(o);
	                return true;
	            } catch (UnsupportedFlavorException ufe) {
	                System.out.println("importData: unsupported data flavor");
	            } catch (IOException ioe) {
	                System.out.println("importData: I/O exception");
	            }
	        }
	        return false;
	    }

	    /* 
	     * What kinds of drag actions can we support?
	     */
	    public int getSourceActions(JComponent c) {
	        return COPY_OR_MOVE;
	    }

	    /*
	     * Create a transferable to drag somewhere else.
	     */
	    protected Transferable createTransferable(JComponent c) {
	        sourceObs = (DTObstacle)c;
	        shouldRemove = true;
	        return new ObstacleTransferable(sourceObs);
	    }

	    /*
	     * Finish the export.
	     */
	    protected void exportDone(JComponent c, Transferable data, int action) {
	        if (shouldRemove && (action == MOVE)) {
	    		model.moveObstacleFrom((DTObstacle)c);
	        }
	        sourceObs = null;
	    }
}
