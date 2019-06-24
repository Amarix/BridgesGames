package babybridges.game;

import bridges.connect.Bridges;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
import bridges.base.GameGrid;
import babybridges.connect.KeypressListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Queue;
import java.util.ArrayDeque;

import babybridges.connect.SocketConnection;

public abstract class BlockingGame implements KeypressListener {

	private boolean firsttime;
	private Bridges bridges;
	protected GameGrid grid;
	private SocketConnection sock;

	protected Queue<String> keyqueue;

	public void keypress(JSONObject keypress) {
		String type = "";
		String key = "";

		// get keypress details
		try {
			type = (String) keypress.get("type");
			key = (String) keypress.get("key");

			// System.out.println(type + ": " + key);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (type.equals("keydown")) {
			synchronized (keyqueue) {
				// System.err.println(type+" "+key);
				keyqueue.add(key);
				keyqueue.notify();
			}
		}

	}

	public String getKeyPress() {

		String ret = "";

		synchronized (keyqueue) {

			try {
				keyqueue.wait();
			} catch (InterruptedException ie) {

			}

			ret = keyqueue.poll();
		}

		return ret;
	}

	protected GameGrid getGameGrid() {
		return grid;
	}

	public BlockingGame(int assignmentID, String username, String apikey,
			int rows, int cols) {
		firsttime = true;

		// bridges-sockets account (you need to make a new account:
		// https://bridges-sockets.herokuapp.com/signup)
		bridges = new Bridges(assignmentID, username, apikey);

		// make sure the bridges connects to the games version of the web app
		bridges.setServer("games");

		// create a new color grid with random color
		grid = new GameGrid(rows, cols);

		keyqueue = new ArrayDeque<String>();

		// set up socket connection to receive and send data
		sock = new SocketConnection();
		sock.setupConnection(bridges.getUserName(), bridges.getAssignment());

		sock.addListener(this);

		init();
	}

	protected abstract init();
	
	protected void setTitle(String title) {
		bridges.setTitle(title);
	}
	
	protected void setDescription(String desc) {
		bridges.setDescription(desc);
	}
        
	// /set background color of cell x, y to c
	// /
	protected void SetBGColor(int x, int y, NamedColor c) {
		grid.setBGColor(y, x, c);
	}

	// /set foreground color of cell x, y to c
	// /
	protected void SetFGColor(int x, int y, NamedColor c) {
		grid.setFGColor(y, x, c);
	}

	// /set symbol of cell x, y to s
	// /
	protected void SetSymbol(int x, int y, int s) {
		grid.drawObject(y, x, s);
	}

	// /set symbol of cell x, y to s
	// /
	protected void DrawObject(int x, int y, NamedSymbol s) {
		grid.drawObject(y, x, s);
	}

	// /set symbol and foreground color of cell x, y to s and c
	// /
	protected void DrawObject(int x, int y, NamedSymbol s, NamedColor c) {
		grid.drawObject(y, x, s, c);
	}
        
        protected int GetBGColor(int col, int row){
            int color = grid.get(col, row).getBGColor();
            return color;
        }
        
        protected String GetKeyPress(){
            String kp = bridges.getKeyPress();
            return kp;
        }

	protected void render() {
		if (firsttime) {
			firsttime = false;
			// associate the grid with the Bridges object
			bridges.setDataStructure(grid);

			// visualize the grid
			try {
				bridges.visualize();
			} catch (Exception err) {
				System.out.println(err);
			}
		}

		String gridState = grid.getDataStructureRepresentation();
		String gridJSON = '{' + gridState;
		// System.out.println(gridJSON);

		// send valid JSON for grid into the socket
		sock.sendData(gridJSON);
	}

	protected void quit() {
		sock.close();
	}
}
