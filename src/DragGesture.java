import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;


// MouseInputAdapter implements and provides default methods for 
	// both MouseListener and MouseMotionListener interfaces.
class DragGesture extends MouseInputAdapter {
	private MouseEvent firstMouseEvent = null;
	private boolean isMoving;
	private BoundingBoxBorder isResizing;
	
	private DTObstacle DTO;
	private Model model;
	
	public DragGesture(DTObstacle DTO, Model model) {
		this.DTO = DTO;
		this.model = model;
        this.isMoving = false;
        this.isResizing = BoundingBoxBorder.NONE;
	}

	public void mouseClicked(MouseEvent e) {
		// Since the user clicked on us, let's get focus!
		if (e.getClickCount() == 2) {
			model.setCurrentSelection(DTO);
			if (DTO.getObstacle() == null) {
				Obstacle obstacle = new Obstacle(DTO.getx(), DTO.getx(), DTO.gety(), DTO.gety());
				model.addObstacle(obstacle, DTO);
			} else {
				model.removeObstacle(DTO.getObstacle(), DTO);
			}
		} else {
			model.setCurrentSelection(DTO);
		}
	}	
			
	public void mousePressed(MouseEvent e) {
		//Don't bother to drag if there is no image.
		if (DTO.getObstacle() == null) return;

		firstMouseEvent = e;
		// prevent any other listeners from acting on this event
		e.consume();
	}
		    
	public void mouseReleased(MouseEvent e) {
		firstMouseEvent = null;
	}

	public void mouseDragged(MouseEvent e) {
	    //Don't bother to drag if the component displays no image.
	    if (DTO.getObstacle() == null) return;

	    if (firstMouseEvent != null) {
	        // prevent other listeners from acting on this event
	        e.consume();

	        int dx = Math.abs(e.getX() - firstMouseEvent.getX());
	        int dy = Math.abs(e.getY() - firstMouseEvent.getY());
	        //Arbitrarily define a 5-pixel shift as the
	        //official beginning of a drag.
	        if (dx > 5 || dy > 5) {
		        //This is a drag, not a click.
	
		        //If they are holding down the control key, COPY rather than MOVE
		        int ctrlMask = InputEvent.CTRL_DOWN_MASK;
		        int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ?
	                          TransferHandler.COPY : TransferHandler.MOVE;

	            JComponent c = (JComponent)e.getSource();
	            TransferHandler handler = c.getTransferHandler();
	                    
	            //Tell the transfer handler to initiate the drag.
	            handler.exportAsDrag(c, firstMouseEvent, action);
	            firstMouseEvent = null;
	         }
	    }
	}	
		    
	enum BoundingBoxBorder {
		NORTH, SOUTH, WEST, EAST, NONE;
	}
}