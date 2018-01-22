import java.util.List;

import javax.swing.JPanel;

/**
 * An interface that allows an object to receive updates from the object
 * they listen to.
 */
interface Observer {
	
    public void update(int x, int y);
    public void update(Obstacle obs, DTObstacle o);
    public void switchToGame(int x, int y, List<Obstacle> obstacles, int scroll, int frame);
    public void exitGame(boolean win);
    public void pauseGame();
    public void restartGame();
}

