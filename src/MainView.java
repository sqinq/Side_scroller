
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MainView extends JFrame implements Observer {

    private Model model;
    //private EditorModel editorM;
   // private JTextField levelField;
    private GamePanel gamep;
    private EditPanel editp;

    /**
     * Create a new View.
     */
    public MainView(Model model) {
        // Set up the window.
        this.setTitle("My Game");
        this.setMinimumSize(new Dimension(128, 128));
        this.setSize(800, 600);
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new MainPanel(this));

        // Hook up this observer so that it will be notified when the model
        // changes.
        this.model = model;
        model.addObserver(this);
        

        setVisible(true);
    }
    
    /**
     * Update with data from the model.
     */
    public void update(int x, int y) {
    	if (gamep != null)
    		gamep.update(x, y);
    }
    
    public void update(Obstacle obs, DTObstacle o) {
    	if (editp != null)
    		editp.update(obs, o);
    }
    
    public void switchToGame(int x, int y, List<Obstacle> obstacles, int scroll, int frame) {
        this.getContentPane().removeAll();
        gamep = new GamePanel(this, model, y, x, obstacles, scroll, frame);
    	this.setContentPane(gamep);
    	this.revalidate();
    	this.repaint();
    }
    
    public void switchToMain() {
        this.getContentPane().removeAll();
        gamep = null;
        editp = null;
        model.clearNode();
        model.clearObstacles();
        model.clearUndos();
    	this.setContentPane(new MainPanel(this));
    	this.revalidate();
    	this.repaint();
    }
    
    public void switchToEditor() {
    	this.getContentPane().removeAll();
        editp = new EditPanel(this, model);
        model.initalizeEditor();
    	this.setContentPane(editp);
    	this.revalidate();
    	this.repaint();
    }
    
    
    public void buildLevelChooser() {
    	JFrame chooser = new JFrame("Choose your level and speed");
    	chooser.setSize(350, 280);
    	chooser.setResizable(false);
    	JPanel p = new JPanel();
    	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    	p.setAlignmentX(LEFT_ALIGNMENT);
    	p.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
    	
    	JPanel chooserP = new JPanel(new FlowLayout());
    	chooserP.add(new JLabel("Choose level:"));
    	JTextField levelField = new JTextField("default.txt", 12);
    	chooserP.add(levelField);
    	JButton chooserB = new JButton("...");
    	chooserB.setMaximumSize(new Dimension(5, 5));
    	chooserB.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			selectFile(chooserP, levelField);
    		}
    	});
    	chooserP.add(chooserB);
    	
    	JPanel scrollP = new JPanel(new FlowLayout());
    	scrollP.add(new JLabel("Scroll speed:"));
    	JTextField scrollSpeed = new JTextField("1", 12);
    	scrollP.add(scrollSpeed);
    	
    	JPanel frameP = new JPanel(new FlowLayout());
    	frameP.add(new JLabel("Frame speed:"));
    	JTextField frameSpeed = new JTextField("30", 12);
    	frameP.add(frameSpeed);
    	
    	JPanel buttonP = new JPanel();
    	buttonP.setLayout(new BoxLayout(buttonP, BoxLayout.X_AXIS));
    	
    	JButton play = new JButton("play");
    	play.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			try {
	    			int scroll = Integer.parseInt(scrollSpeed.getText());
	    			if (scroll<=0) {
	    				throw new Exception("invalid scroll speed");
	    			}
	    			int frame = Integer.parseInt(frameSpeed.getText());
	    			if (frame<=0) {
	    				throw new Exception("invalid scroll speed");
	    			}
	    			File f = new File("levels/"+levelField.getText());
	    			if (!f.canRead())
	    				throw new Exception("invalid file");
	    			model.initalizeGame(f, scroll, frame);
    			} catch(Exception ex) {
    				ex.printStackTrace();
    				JOptionPane.showMessageDialog(buttonP, "Invalid input");
    			}

    			chooser.dispose();
    		}
    	});
    	JButton cancel = new JButton("cancel");
    	cancel.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			switchToMain();
    			chooser.dispose();
    		}
    	});
    	
    	buttonP.add(play);
    	buttonP.add(Box.createHorizontalGlue());
    	buttonP.add(cancel);
    	p.add(chooserP);
    	p.add(Box.createVerticalGlue());
    	p.add(scrollP);
    	p.add(frameP);
    	p.add(buttonP);
    	chooser.setContentPane(p);
    	chooser.setVisible(true);
    }
    
    private void selectFile(final JPanel panel, final JTextField levelField) {
    	JFileChooser filechooser = new JFileChooser("levels");
    	int result = filechooser.showOpenDialog(panel);
    	if (result == JFileChooser.APPROVE_OPTION) {
            File f = filechooser.getSelectedFile();
            levelField.setText(f.getName());
            // read  and/or display the file somehow. ....
        } 
    }
    

    
    public void exitGame(boolean win) {
    	if (win) {
        	gamep.buildEndFrame(100);
    	} else {
        	gamep.buildEndFrame(0);
    	}
    }
    
    public void exitGame() {
    	gamep.buildEndFrame(0);
    	switchToMain();
    }
    
    public void pauseGame() {
    	gamep.pause();
    }
    
    public void restartGame() {
    	gamep.restart();
    }
}
