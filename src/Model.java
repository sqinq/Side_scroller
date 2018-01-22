
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class Model extends Observable {
    /** The observers that are watching this model for changes. */
    private List<Observer> observers;
    public List<Obstacle> obstacles;
    private Node node;
    
    private int limitX;
    private int limitY;
    private int screenWidth;
    private int firstX;
    
    private DTObstacle currentSelection;
    
    private Timer t;
    private UndoManager undoManager;
    private UndoableMove undoable;
    private Clipboard clipboard;

    /**
     * Create a new model.
     */
    public Model() {
        this.observers = new ArrayList<Observer>();
        undoManager = new UndoManager();
    }

    /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public void initalizeGame(File file, int scroll, int frame) {
        firstX = 0;
        readFile(file);
    	for (Observer o : observers) {
    		o.switchToGame(limitX, limitY, obstacles, scroll, frame);
    	}
    	
    	node = new Node(0, limitY/2, 1, limitX, limitY);
    	notifyObservers();
    	
    	t = new Timer(1000, new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		firstX += scroll;
        		for (int i=0; i<scroll; i++)
    				moveNode('d');
    		}
    	});
		moveNode('d');
    	t.start();
    }
    
    public void readFile(File file) {
    	LevelReader reader = new LevelReader(file);
    	reader.read();
    	this.limitX = reader.limitX;
    	this.limitY = reader.limitY;
    	obstacles = reader.getObstacles();
    }
    
    public void setSize(int x, int y) {
    	this.limitX = x;
    	this.limitY = y;
    }
    
    public int getX() {
    	return limitX;
    }
    
    public int getY() {
    	return limitY;
    }
    
    public void initalizeEditor() {
    	obstacles = new ArrayList<Obstacle>();
    	clipboard = new Clipboard("obstacle clipboard");
    
    }
    
    public void setScreenWidth(int w) {
    	node.setscreenWidth(w);;
    }
    
    public void moveNode(char c) {
    	switch (c) {
    		case 's':
    			node.down();
    			notifyObservers();
    			break;
    		case 'w':
    			node.up();
    			notifyObservers();
    			break;
    		case 'a':
    			node.back();
    			notifyObservers();
    			break;
    		case 'd':
    			node.forward();
    			notifyObservers();
    			break;
    	}
    	
    	int x = node.getx();
    	int y = node.gety();

		System.out.printf("%d  %d\n", x, y);
    	
		if (firstX>x) {
			System.out.println("you lose");
			exitGame(false);
		}
			
    	for (Obstacle o:obstacles) {
    		if (o.x0<=x && o.x0+o.width-1>=x) {
    			if (o.y0<=y&&o.y0+o.height-1>=y) {
    				exitGame(false);
    			}
    		}
    	}
    	
    	if (x==limitX) {
    		exitGame(true);
    	}
    }
    
    
    
    public void addObstacle(Obstacle obs, DTObstacle dto) {
    	// create undoable edit
    	UndoableEdit undoableEdit = new AbstractUndoableEdit() {
    		// capture variables for closure
    		final Obstacle obj = obs;
    		final DTObstacle DTO = dto;

    		// Method that is called when we must redo the undone action
    		public void redo() throws CannotRedoException {
    			super.redo();
    			obstacles.add(obj);
    			DTO.setObstacle(obj);
    			setCurrentSelection(DTO);
    			setChanged();
    			notifyObservers();
    		}

    		public void undo() throws CannotUndoException {
    			super.undo();
    			obstacles.remove(obj);
    			DTO.setObstacle(null);
    			setCurrentSelection(DTO);
    			setChanged();
    			notifyObservers();
    		}
    	};
    	// Add this undoable edit to the undo manager
    	undoManager.addEdit(undoableEdit);
    	
    	obstacles.add(obs);
    	obs.resetLocation(dto.getx(), dto.gety());
    	dto.setObstacle(obs);
		setChanged();
		notifyObservers();
    }
    
    public void removeObstacle(Obstacle obs, DTObstacle dto) {
    	// create undoable edit
    	UndoableEdit undoableEdit = new AbstractUndoableEdit() {
    		// capture variables for closure
    		final Obstacle obj = obs;
    		final DTObstacle DTO = dto;

    		// Method that is called when we must redo the undone action
    		public void undo() throws CannotRedoException {
    			super.undo();
    			obstacles.add(obj);
    			DTO.setObstacle(obj);
    			setCurrentSelection(DTO);
    			setChanged();
    			notifyObservers();
    		}

    		public void redo() throws CannotUndoException {
    			super.redo();
    			obstacles.remove(obj);
    			DTO.setObstacle(null);
    			setCurrentSelection(DTO);
    			setChanged();
    			notifyObservers();
    		}
    	};
    	// Add this undoable edit to the undo manager
    	undoManager.addEdit(undoableEdit);
    	
    	obstacles.remove(obs);
    	dto.setObstacle(null);
		setChanged();
		notifyObservers();
    }
    
    public void moveObstacleFrom(DTObstacle from) {
    	undoable.setFrom(from);
        from.setObstacle(null);
		setChanged();
		notifyObservers();
    }
    
    public void moveObstacleTo(Obstacle obs, DTObstacle to) {
    	undoable = new UndoableMove(obs, to);
    	undoManager.addEdit(undoable);
    	obstacles.remove(to.getObstacle());
        to.setObstacle(obs);
        
        obs.resetLocation(to.getx(), to.gety());
    }
    
    public void save(File f) {
    	try {
        	FileOutputStream fos = new FileOutputStream(f);
       	 
        	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        	
        	bw.write(Integer.toString(limitX)+", "+Integer.toString(limitY));
        	bw.newLine();
        	
	    	for (Obstacle o:obstacles) {
	    		bw.write(Integer.toString(o.x0)+", "+Integer.toString(o.y0)
	    					+", "+Integer.toString(o.x0+o.width-1)+", "+Integer.toString(o.y0+o.height-1));
	    		bw.newLine();
	    	}
	    	
	    	bw.close();
    	} catch (Exception e) {
    		
    	}
    }
    
    
    
    public void clearNode() {
    	node = null;
    }
    
    public void clearObstacles() {
    	if (obstacles != null)
    		obstacles.clear();
    }
    
    public void clearUndos() {
    	undoManager.discardAllEdits();
    }
    
	public void exitGame(boolean win) {
		t.stop();
    	for (Observer o : observers) {
    		o.exitGame(win);
    	}
    }
	
	public void pauseGame() {
		t.stop();
    	for (Observer o : observers) {
    		o.pauseGame();
    	}
    }
	
	public void restartGame() {
		t.restart();
    	for (Observer o : observers) {
    		o.restartGame();
    	}
    }
    
	public void undo() {
		if (canUndo())
			undoManager.undo();
	}

	public void redo() {
		if (canRedo())
			undoManager.redo();
	}

	public boolean canUndo() {
		return undoManager.canUndo();
	}

	public boolean canRedo() {
		return undoManager.canRedo();
	}

    
    public void resizeObs(int w, int h) throws Exception {
    	if (currentSelection.getObstacle() == null) {
    		throw new Exception("No obstacle selected");
    	} else {
    		Obstacle obs = currentSelection.getObstacle();
    		if (obs.x0+w>limitX || obs.y0+h>limitY) {
    			throw new Exception("Invalid size");
    		} else {
    			UndoableEdit undoableEdit = new AbstractUndoableEdit() {
    	    		// capture variables for closure
    				final Obstacle o = obs;
    	    		final int oldw = obs.width;
    	    		final int oldh = obs.height;
    	    		final int neww = w;
    	    		final int newh = h;

    	    		// Method that is called when we must redo the undone action
    	    		public void undo() throws CannotRedoException {
    	    			super.undo();
    	    			o.resize(oldw, oldh);
    	    			setChanged();
    	    			notifyObservers();
    	    		}

    	    		public void redo() throws CannotUndoException {
    	    			super.redo();
    	    			o.resize(neww, newh);
    	    			setChanged();
    	    			notifyObservers();
    	    		}
    	    	};
    	    	// Add this undoable edit to the undo manager
    	    	undoManager.addEdit(undoableEdit);

    			obs.resize(w, h);
    			notifyObservers();
    		}
    	}
    }
	
	public void doCopy() {
		if (currentSelection == null)
			return;
		// Create a transferable object encapsulating all the info for the copy
		Transferable transferObject = new ObstacleTransferable(currentSelection);

		// Now set the contents of the clipboard to our transferable object
		clipboard.setContents(transferObject, null);	
	}

	public void doCut() {
		if (currentSelection == null)
			return;
		this.doCopy(); // most of a cut is the same as a copy
		removeObstacle(currentSelection.getObstacle(), currentSelection);
	}

	public void doPaste() {
		if (currentSelection == null)
			return;
		DataFlavor obstacleFlavor = new DataFlavor(Obstacle.class, "obstacleFlavor");

		// Check if we can get the data as an image
		if (clipboard.isDataFlavorAvailable(obstacleFlavor)) {
			try {
				// Grab the data, set the selected picture to the provided image
				Obstacle obs = (Obstacle) clipboard
						.getData(obstacleFlavor);
				if (obs == null) return;
				if (currentSelection.getx()+obs.width>limitX || currentSelection.gety()+obs.height>limitY) {
		    		return;
		    	}
				Obstacle obsCopy = obs.copy();
				obsCopy.resetLocation(currentSelection.getx(), currentSelection.gety());
				addObstacle(obsCopy, currentSelection);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	
    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers() {
    	for (Obstacle o:obstacles) {
    		System.out.printf("%d  %d\n", o.x0, o.y0);
    	}
    	
        for (Observer observer: this.observers) {
        	if (node != null)
        		observer.update(node.getx(), node.gety());
        	else 
        		observer.update(currentSelection.getObstacle(), currentSelection);
        }
    }
    
    /**
     * Returns the currently selected Obstacle.
     * @return
     */
    public DTObstacle getCurrentSelection() {
        return currentSelection;
    }

    /**
     * Sets the currently selected Obstacle and notify the Views.
     * @param currentSelection
     */
    public void setCurrentSelection(DTObstacle currentSelection) {
        this.currentSelection = currentSelection;
        currentSelection.requestFocusInWindow();
        this.setChanged();
    }

    /**
     * Scale the currently selected DTObstacle about the West border, according to the specified screen points,
     * considering the current transformation of the selected DTObstacle.
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
  /*  public void scaleSelectedWest(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            if (this.currentSelection.getTransform() != null) {
                // We need to transform screen coordinates to model coordinates
                Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
                Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);
                double width = this.currentSelection.getBoundingBox().getWidth();
                double sx = (oldPointTransformed.getX() + width) / (newPointTransformed.getX() + width);
                this.currentSelection.getTransform().scale(sx, 1.0);
                this.currentSelection.getTransform().translate(newPointTransformed.getX() - oldPointTransformed.getX(), 0.0);
            } else {
                double width = this.currentSelection.getBoundingBox().getWidth();
                double sx = (oldPoint.getX() + width) / (newPoint.getX() + width);
                this.currentSelection.setTransform(AffineTransform.getScaleInstance(sx, 1.0));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Scale the currently selected drawable about the East border, according to the specified screen points,
     * considering the current transformation of the selected drawable.
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
    /* public void scaleSelectedEast(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            if (this.currentSelection.getTransform() != null) {
                // We need to transform screen coordinates to model coordinates
                Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
                Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);
                this.currentSelection.getTransform().scale(newPointTransformed.getX() / oldPointTransformed.getX(), 1.0);
            } else {
                this.currentSelection.setTransform(AffineTransform.getScaleInstance(newPoint.getX() / oldPoint.getX(), 1.0));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Scale the currently selected drawable about the North border, according to the specified screen points,
     * considering the current transformation of the selected drawable.
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
    /*  public void scaleSelectedNorth(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            if (this.currentSelection.getTransform() != null) {
                // We need to transform screen coordinates to model coordinates
                Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
                Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);
                double height = this.currentSelection.getBoundingBox().getHeight();
                double sy = (oldPointTransformed.getY() + height) / (newPointTransformed.getY() + height);
                this.currentSelection.getTransform().scale(1.0, sy);
                this.currentSelection.getTransform().translate(0.0, newPointTransformed.getY() - oldPointTransformed.getY());
            } else {
                double height = this.currentSelection.getBoundingBox().getHeight();
                double sy = (oldPoint.getY() + height) / (newPoint.getY() + height);
                this.currentSelection.setTransform(AffineTransform.getScaleInstance(1.0, sy));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Scale the currently selected drawable about the South border, according to the specified screen points,
     * considering the current transformation of the selected drawable.
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
    /*  public void scaleSelectedSouth(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            if (this.currentSelection.getTransform() != null) {
                // We need to transform screen coordinates to model coordinates
                Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
                Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);
                this.currentSelection.getTransform().scale(1.0, newPointTransformed.getY() / oldPointTransformed.getY());
            } else {
                this.currentSelection.setTransform(AffineTransform.getScaleInstance(1.0, newPoint.getY() / oldPoint.getY()));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }*/
    
    class UndoableMove extends AbstractUndoableEdit {
    	final Obstacle obs;
    	final Obstacle originalObs;
    	DTObstacle DTOfrom;
    	final DTObstacle DTOto;
    	
    	public UndoableMove(Obstacle obs, DTObstacle to) {
    		this.originalObs = to.getObstacle();
    		this.obs = obs;
    		this.DTOto = to;
    	}
    	
    	public void setFrom(DTObstacle from) {
    		this.DTOfrom = from;
    	}
    	
    	public void undo() throws CannotRedoException {
    		super.undo();
    		DTOfrom.setObstacle(obs);
    		DTOto.setObstacle(originalObs);
    		obs.resetLocation(DTOfrom.getx(), DTOfrom.gety());
    		if (originalObs != null) {
    			obstacles.add(originalObs);
    			originalObs.resetLocation(DTOto.getx(), DTOto.gety());
    		}
    		setCurrentSelection(DTOfrom);
            System.out.printf("%d  %d\n", DTOfrom.getx(), DTOfrom.gety());
            System.out.printf("%d  %d\n", DTOfrom.getObstacle().x0, DTOfrom.getObstacle().y0);
    		notifyObservers();
    	}
    	
    	public void redo() throws CannotUndoException {
    		super.redo();
    		DTOfrom.setObstacle(null);
    		DTOto.setObstacle(obs);
        	obstacles.remove(originalObs);
            obs.resetLocation(DTOto.getx(), DTOto.gety());
            setCurrentSelection(DTOto);
    		notifyObservers();
    	}
    }
}
