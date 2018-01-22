import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

public class GamePanel extends JPanel {
	private MainView frame;
	private JPanel toolbar;
	private JButton restart;
	private JButton exit;
	private JButton pause;
	private GameField gameF;
	
	private boolean running;

	
	public GamePanel(MainView frame, Model model, int height, int width, List<Obstacle> obstacles, int scrollS, int frameS) {
		this.frame = frame;
		frame.setContentPane(this);
		this.setLayout(new BorderLayout());
		running = true;
		
		toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		restart = new JButton("Restart");
		exit = new JButton("Exit");
		pause = new JButton("Pause");

		restart.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			model.pauseGame();
    			pause.setEnabled(false);
    			frame.buildLevelChooser();
    		}
    	});
		pause.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			if (running) {
    				model.pauseGame();
    				pause.setText("Continue");
    				running = false;
    			} else {
    				model.restartGame();
    				pause.setText("Pause");
    				running = true;
    			}
    		}
    	});
		
		exit.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			model.exitGame(false);
    		}
    	});
		toolbar.add(restart);
		toolbar.add(pause);
		toolbar.add(exit);
		
		this.add(toolbar, BorderLayout.NORTH);

		gameF = new GameField(height, width, obstacles, model, scrollS, frameS);
		this.add(gameF, BorderLayout.CENTER);
	}
	
	public void update(int x, int y) {
		gameF.update(x, y);
	}
	
	public void pause() {
		gameF.pause();
	}
	
	public void restart() {
		gameF.restart();
	}
	
	public void buildEndFrame(int score) {
		gameF.pause();
    	JFrame gameOver = new JFrame();
    	gameOver.setSize(350, 280);
    	JPanel p = new JPanel();
    	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    	p.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
    	
    	JPanel buttonP = new JPanel();
    	buttonP.setLayout(new BoxLayout(buttonP, BoxLayout.X_AXIS));
    	JButton play = new JButton("Play Again");
    	play.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			gameOver.dispose();
    			frame.buildLevelChooser();
    		}
    	});
    	JButton cancel = new JButton("Exit to map");
    	cancel.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			gameOver.dispose();
    			frame.switchToMain();
    		}
    	});
    	buttonP.add(play);
    	buttonP.add(Box.createHorizontalGlue());
    	buttonP.add(cancel);
    	
    	if (score > 0) {
    		p.add(new JLabel("You Win"));
    		p.add(new JLabel("Score: "+score));
    	} else {
    		p.add(new JLabel("You lost :("));
    	}
    	p.add(Box.createHorizontalGlue());
    	p.add(buttonP);
    	gameOver.setContentPane(p);
    	gameOver.pack();
    	gameOver.setVisible(true);
    }
}
