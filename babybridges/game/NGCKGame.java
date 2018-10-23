package babybridges.game;

import bridges.connect.Bridges;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
import bridges.base.GameGrid;

import babybridges.connect.SocketConnection;

public abstract class NGCKGame {

	// / the game map.
	private int rows = 30;
	private int cols = 30;
	protected GameGrid grid;

	// /Bridges interaction
	private Bridges bridges;
	private SocketConnection sock;

	// / this stores the JSON representation that will be sent to the BRIDGES
	// server.
	private String gridJSON;

	// /helper class to make Input Management a bit easier.
	private InputHelper ih;

	// /used for fps control
	private long timeoflastframe;

	// / @return true if "left" is pressed
	protected boolean KeyLeft() {
		return ih.left();
	}

	// / @return true if "right" is pressed
	protected boolean KeyRight() {
		return ih.right();
	}

	// / @return true if "up" is pressed
	protected boolean KeyUp() {
		return ih.up();
	}

	// / @return true if "down" is pressed
	protected boolean KeyDown() {
		return ih.down();
	}

	// / @return true if "button1" is pressed
	protected boolean KeyButton1() {
		return ih.button1();
	}

	// / @return true if "button2" is pressed
	protected boolean KeyButton2() {
		return ih.button2();
	}

	// / @return true if "w" is pressed
	protected boolean Keyw() {
		return ih.w();
	}

	// / @return true if "a" is pressed
	protected boolean Keya() {
		return ih.a();
	}

	// / @return true if "s" is pressed
	protected boolean Keys() {
		return ih.s();
	}

	// / @return true if "d" is pressed
	protected boolean Keyd() {
		return ih.d();
	}




	// /takes bridges credential and information as a parameter.
	public NGCKGame(int assid, String login, String apiKey) {
		timeoflastframe = System.currentTimeMillis();

		// bridges-sockets account (you need to make a new account:
		// https://bridges-sockets.herokuapp.com/signup)
		bridges = new Bridges(assid, login, apiKey);

		// make sure the bridges connects to the game version of the web app
		bridges.setServer("games");

		// create a new color grid with random color
		grid = new GameGrid(rows, cols);

		// set up socket connection to receive and send data
		sock = new SocketConnection();
		sock.setupConnection(bridges.getUserName(), bridges.getAssignment());

		ih = new InputHelper(sock);
	}

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

	// / function to define by the programmer. This function is called
	// / once at the beginning.
	public abstract void initialize();

	// / function to define by the programmer. This function is called
	// / once per frame.
	public abstract void GameLoop();

	// / This function prepare all that is needde to be able to render
	// / as fast as possible. Here it builds the correct representation
	// / to send to the server.
	private void prepareRender() {
		// get the JSON representation of the updated color grid
		String gridState = grid.getDataStructureRepresentation();
		gridJSON = '{' + gridState;
		// System.out.println(gridJSON);
	}

	// /send the representation to the serverf
	private void render() {

		// send valid JSON for grid into the socket
		sock.sendData(gridJSON);
	}

	// / should be called right before render() Aims at having a fixed
	// / fps of 30 frames per second. This work by waiting until
	// / 1/30th of a second after the last call to this function.
	private void controlFrameRate() {
		int fps = 20;
		double hz = 1. / fps;

		long currenttime = System.currentTimeMillis();
		long theoreticalnextframe = timeoflastframe + (int) (hz * 1000);
		long waittime = theoreticalnextframe - currenttime;

		if (waittime > 0) {
			try {
				Thread.sleep(waittime); // this is super crude
			} catch (InterruptedException ie) {
				// die?
			}
		}
		timeoflastframe = System.currentTimeMillis();
	}

	// / calling this function starts the game engine.
	public void start() {
		try {
			Thread.sleep(5 * 1000); // wait for browser to connect
		} catch (InterruptedException ie) {
			// die?
		}

		// associate the grid with the Bridges object
		bridges.setDataStructure(grid);

		// visualize the grid
		try {
			bridges.visualize();
		} catch (Exception err) {
			System.out.println(err);
		}

		initialize();
		while (true) {
			GameLoop();

			prepareRender();
			controlFrameRate();
			render();
			// System.out.println("rendered");
		}
	}
}
