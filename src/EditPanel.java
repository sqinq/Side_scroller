import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EditPanel extends JPanel {
	private Model model;
	private MainView main;
	private static DTObstacle [][] obstacles;
	private EditField editf;
	private static int width = 100;
	private static int height = 10;
    static int sqrWidth;
    JScrollPane scrollEdit = new JScrollPane();

	JTextField widthField = new JTextField("100", 3);
	JTextField heightField = new JTextField("10", 3);

	JButton copy = new JButton("Copy");
	JButton cut = new JButton("Cut");
	JButton paste = new JButton("paste");
	JButton redo = new JButton("Redo");
	JButton undo = new JButton("Undo");
	JButton read = new JButton("choose saved file");
	JButton save = new JButton("Save");
	JButton cancel = new JButton("Cancel");
	JButton zoomIn = new JButton("Zoom In");
	JButton zoomOut = new JButton("Zoom Out");
	JButton size = new JButton("Reset");
	JButton resize = new JButton("Change Size");
	
    public static void setsqrWidth(int w) {
    	if (sqrWidth != w) {
    		sqrWidth = w;
    		for (int i=0; i<height; i++) {
    			for (int j=0; j<width; j++) {
    				obstacles[i][j].resetsqrWidth(w);
    			}
    		}
    	}
    }
	
	public EditPanel(MainView main, Model model) {
		this.main = main;
		this.model = model;
		
		this.setLayout(new BorderLayout());
		Box buttonBox = new Box(BoxLayout.PAGE_AXIS);
		buttonBox.add(Box.createVerticalGlue());
		buttonBox.add(resize);
		buttonBox.add(copy);
		buttonBox.add(cut);
		buttonBox.add(paste);
		buttonBox.add(redo);
		buttonBox.add(undo);
		buttonBox.add(read);
		buttonBox.add(save);
		buttonBox.add(cancel);
		
		this.add(buttonBox, BorderLayout.EAST);
		
		JPanel topP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topP.add(new JLabel("Double click to put an obstacle."));
    	topP.add(new JLabel("Set field size: "));
    	topP.add(heightField);
    	topP.add(widthField);
    	topP.add(size);
		topP.add(zoomIn);
		topP.add(zoomOut);
		
		this.add(topP, BorderLayout.NORTH);
		this.add(scrollEdit, BorderLayout.CENTER);
		
		registerControllers();

		model.setSize(width, height);
		setField(width, height);
		scrollEdit.updateUI();
	}
	
	public void setField(int width, int height) {
		this.width = width;
		this.height = height;
		editf = new EditField(width, height);
		obstacles = new DTObstacle[height][width];
		scrollEdit.setViewportView(editf);

		ObstacleTransferHandler obsTransHandler = new ObstacleTransferHandler(model, obstacles);
		ObsFocusListener obsFL = new ObsFocusListener();
		editf.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.FIRST_LINE_START;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = width-1;
		c.weighty = height-1;
	
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				obstacles[i][j] = new DTObstacle(model, j, i, sqrWidth, c);
				obstacles[i][j].setTransferHandler(obsTransHandler);
				obstacles[i][j].addFocusListener(obsFL);
				c.gridx = j;
				c.gridy = i;
				editf.add(obstacles[i][j], c);
			}
		}
		editf.repaint();
	}
	
	
	private void registerControllers() {
		resize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
    			model.doCopy();
    		}
		});
		

		cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
    			model.doCut();
    		}
		});
		

		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
    			model.doPaste();
    		}
		});

		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
    			model.redo();
    		}
		});
		
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
    			model.undo();
    		}
		});
		
		read.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser filechooser = new JFileChooser("levels");
		    	int result = filechooser.showOpenDialog(null);
		    	if (result == JFileChooser.APPROVE_OPTION) {
		            model.readFile(filechooser.getSelectedFile());
	    			setField(model.getX(), model.getY());
		            for (Obstacle o:model.obstacles) {
		            	int x = o.x0;
		            	int y = o.y0;
		            	obstacles[y][x].setObstacle(o);
		            	obstacles[y][x].setPreferredSize(new Dimension(sqrWidth*o.width, sqrWidth*o.height));
		            	GridBagConstraints c = ((GridBagLayout) editf.getLayout()).getConstraints(obstacles[y][x]);
						c.gridwidth = o.width;
						c.gridheight = o.height;
		            	editf.add(obstacles[y][x], c);
		            	for (int i=0; i<o.height; i++) {
		            		for (int j=0; j<o.width; j++) {
		            			if (i!=0 || j!=0) {
		            				obstacles[y+i][x+j].setVisible(false);
		            			}
		            		}
		            	}
		            }
		        } 

    			model.clearUndos();
    			scrollEdit.updateUI();
    		}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser filechooser = new JFileChooser("levels");
				filechooser.addChoosableFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
		    	int result = filechooser.showSaveDialog(null);
		    	if (result == JFileChooser.APPROVE_OPTION) {
		            model.save(filechooser.getSelectedFile());
		        } 
    		}
		});
		
		cancel.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			main.switchToMain();
    		}
    	});
		

    	size.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			try {
	    			int width = Integer.parseInt(widthField.getText());
	    			if (width<=9 || width>500) {
	    				throw new Exception("invalid width");
	    			}
	    			int height = Integer.parseInt(heightField.getText());
	    			if (height<=2 ||height>25) {
	    				throw new Exception("invalid height");
	    			}
	    			model.clearObstacles();
	    			model.clearUndos();
	    			model.setSize(width, height);
	    			setField(width, height);
	    			scrollEdit.updateUI();
	    			
    			} catch(Exception ex) {
    				ex.printStackTrace();
    				JOptionPane.showMessageDialog(null, "Invalid input");
    			}
    		}
    	});
    	
    	zoomIn.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			editf.addScale(0.2);
    			
    			for (int i=0; i<height; i++) {
    				for (int j=0; j<width; j++) {
    					obstacles[i][j].repaint();
    				}
    			}
    			editf.repaint();
    		}
    	});
    	
    	zoomOut.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			editf.addScale(-0.2);
    			editf.repaint();
    			
    			for (int i=0; i<height; i++) {
    				for (int j=0; j<width; j++) {
    					obstacles[i][j].repaint();
    				}
    			}
    		}
    	});
    	
    	resize.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFrame chooser = new JFrame("Set size of the obstacle");
    	    	chooser.setSize(230, 200);
    	    	chooser.setResizable(false);
    	    	JPanel p = new JPanel();
    	    	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    	    	p.setAlignmentX(LEFT_ALIGNMENT);
    	    	p.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
    	    	
    	    	
    	    	p.add(new JLabel("width:"));
    	    	JTextField widthField = new JTextField("1", 12);
    	    	p.add(widthField);
    	    	
    	    	p.add(new JLabel("height:"));
    	    	JTextField heightField = new JTextField("1", 12);
    	    	p.add(heightField);
    	    	
    	    	
    	    	JButton set = new JButton("OK");
    	    	set.addActionListener(new ActionListener() {
    	    		public void actionPerformed(ActionEvent e) {
    	    			try {
    		    			int w = Integer.parseInt(widthField.getText());
    		    			if (w<=0) {
    		    				throw new Exception("invalid width");
    		    			}
    		    			
    		    			int h = Integer.parseInt(heightField.getText());
    		    			if (h<=0) {
    		    				throw new Exception("invalid height");
    		    			}
    		    			
    		    			model.resizeObs(w, h);
    		    			
    	    			} catch(Exception ex) {
    	    				ex.printStackTrace();
    	    				JOptionPane.showMessageDialog(null, ex.getMessage());
    	    			}

    	    			chooser.dispose();
    	    		}
    	    	});
    	    	
    	    	p.add(set);
    	    	chooser.setContentPane(p);
    	    	chooser.setVisible(true);
    		}
    	});
	}
	
	public void update(Obstacle obs, DTObstacle o) {
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				if (obstacles[i][j].getObstacle() == null){ 
					GridBagConstraints c = ((GridBagLayout) editf.getLayout()).getConstraints(obstacles[i][j]);
					c.gridwidth = 1;
					c.gridheight = 1;
		        	editf.add(obstacles[i][j], c);
		        	if (!obstacles[i][j].isVisible())
		        		obstacles[i][j].setVisible(true);
				}
			}
		}
		
        if (obs != null) {
        	GridBagConstraints c = ((GridBagLayout) editf.getLayout()).getConstraints(o);
			c.gridwidth = obs.width;
			c.gridheight = obs.height;
        	editf.add(o, c);
        	for (int i=0; i<obs.height; i++) {
        		for (int j=0; j<obs.width; j++) {
        			if (i!=0 || j!=0) {
        				obstacles[obs.y0+i][obs.x0+j].setVisible(false);
        			}
        		}
        	}
        } else {

        }
        editf.repaint();
	}
	

	
	class ObsFocusListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			Component c = e.getComponent();
			for (int i=0; i<height; i++) {
				for (int j=0; j<width; j++) {
					if (obstacles[i][j].equals(c)) {
						model.setCurrentSelection(obstacles[i][j]);
						System.out.printf("selected %d, %d\n", j, i);
					}
				}
			}
			assert false;
		}

		public void focusLost(FocusEvent e) {
		}
	}
	
}
