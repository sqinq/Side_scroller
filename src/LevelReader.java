import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LevelReader {
	private File f;
	public int limitX;
	public int limitY;
	private List<Obstacle> obstacles;
	
	public LevelReader (File f) {  
		this.f = f;
		obstacles = new ArrayList<Obstacle>();
	}
	
	public void read() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
		    String line;
		    boolean firstRead = true;
		    while ((line = br.readLine()) != null) {
		       if (line.startsWith("#"))
		    	   continue;
		       if (firstRead) {
		    	   String [] limits = line.split(", ");
		    	   if (limits.length != 2)
		    		   throw new Exception();
		    	   limitX = Integer.parseInt(limits[0]);
		    	   limitY = Integer.parseInt(limits[1]);
		    	   firstRead = false;
		    	   continue;
		       }
	    	   String [] dimensions = line.split(", ");
	    	   if (dimensions.length != 4) 
	    		   throw new Exception();
	    	   int x0 = Integer.parseInt(dimensions[0]);
	    	   int x1 = Integer.parseInt(dimensions[2]);
	    	   int y0 = Integer.parseInt(dimensions[1]);
	    	   int y1 = Integer.parseInt(dimensions[3]);
	    	   if (x0<0 || x0>=limitX || x1<0 || x1>=limitX)
	    		   throw new Exception("invalid obstacles");
	    	   if (y0<0 || y0>=limitY || y1<0 || y1>=limitY)
	    		   throw new Exception("invalid obstacles");
	    	   obstacles.add(new Obstacle(x0, x1, y0, y1));
	    	   
		       
		    }
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public List<Obstacle> getObstacles() {
		return obstacles;
	}
}
