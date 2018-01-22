import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainPanel extends JPanel {
	
    private Dimension button_di = new Dimension(150, 60);
    private MainView frame;
	private JButton newGame;
	private JButton help;
	private JButton newLevel;
	private JButton exit;
	
	public MainPanel(MainView frame) {
		super();
		this.frame = frame;
		this.setLayout(new GridBagLayout());
		Box p = new Box(BoxLayout.PAGE_AXIS);
    	newGame = new JButton("New Game");
    	newGame.setMaximumSize(button_di);
    	newGame.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			frame.buildLevelChooser();
    		}
    	});
    	
    	help = new JButton("Help");
    	help.setMaximumSize(button_di);
    	help.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JOptionPane.showMessageDialog(null, "Everything works", "Help", JOptionPane.PLAIN_MESSAGE);
    		}
    	});
    	
    	newLevel = new JButton("Create My Level");
    	newLevel.setMaximumSize(button_di);
    	newLevel.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			frame.switchToEditor();
    		}
    	});
    	
    	exit = new JButton("Exit");
    	exit.setMaximumSize(button_di);
    	exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	frame.dispose();
            	System.exit(0);
            }
        });
    	
    	p.add(newGame);
    	p.add(help);
    	p.add(newLevel);
    	p.add(exit);
    	this.add(p);
	}
	


}
