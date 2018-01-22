import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ObstacleTransferable implements Transferable {
	private Obstacle obstacle;
	private DataFlavor obstacleFlavor;
	
	public ObstacleTransferable(DTObstacle o) {
		obstacle = o.getObstacle();
		obstacleFlavor = new DataFlavor(Obstacle.class, "obstacleFlavor");
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { obstacleFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(obstacleFlavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        } 
        return obstacle;
	}

}
