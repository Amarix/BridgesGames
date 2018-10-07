import org.json.JSONException;
import org.json.JSONObject;

import bridges.base.Color;
import bridges.base.ColorGrid;
import bridges.connect.Bridges;

import babybridges.connect.KeypressListener;
import babybridges.connect.SocketConnection;

public class Game  {
	public static void main(String[] args) throws Exception{
		
	    MyGame mg = new MyGame (1, "esaule-interactive", "1239999531573");
	    
		
	    // start running the game
	    mg.start();
	}
	
}
