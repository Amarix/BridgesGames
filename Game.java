import org.json.JSONException;
import org.json.JSONObject;

import bridges.base.Color;
import bridges.base.ColorGrid;
import bridges.connect.Bridges;

/* 
 *		This class is a first version of a rudimentary 'game' which:
 *			- sets up a socket connection to a socket server using bridges username and assignment number 
 *			- receives keypress events from a web browser 
 *			- sends JSON representation of a grid back to the web browser
 * 		
 * 		Game implements KeypressListener, which is an interface providing a 'keypress' event to override 
 * 			keypress events can have type 'keydown' or 'keyup' and provide an associated 'key' name
 */
public class Game implements KeypressListener {

	// set up default color and sizes for the game grid
	static int rows = 30;
	static int cols = 30;
	static ColorGrid grid;
	static String gridState = "";
	static Bridges bridges;
	static SocketConnection sock;
	static int blue, red, green;
	Color color = new Color("black");
	
	// the main function will set up a bridges connection and a socket connection, then bind the game to the socket
	// TODO: there is probably a better way to set this all up eventually.
	public static void main(String[] args) throws Exception{
		
		// bridges-sockets account (you need to make a new account: https://bridges-sockets.herokuapp.com/signup)
		bridges = new Bridges(, "", "");

		//local account (you will probably not use this unless you run a local version of the socket server)
		// bridges = new Bridges(, "", "");

		// make sure the bridges connects to the socket version of the web app
		bridges.setServer("sockets");
		
		bridges.setTitle("Simple Color Grid Socket Game");
		bridges.setDescription("Use the arrow keys to change the color of the grid");
		
		// create a new color grid with random color 
		grid = new ColorGrid(rows, cols, getRandomColor());
		
		// set up socket connection to receive and send data
		sock = new SocketConnection();
		sock.setupConnection(bridges.getUserName(), bridges.getAssignment());	
		
		// game will now listen for keypresses from the socket
		Game game = new Game();
	    sock.addListener(game);
	    
	    // start running the game
	    game.start();
	}
	
	// generate the first board and visualize the grid
	public void start() {

		// associate the grid with the Bridges object
		bridges.setDataStructure(grid);

		// visualize the grid
		try{
			bridges.visualize();
		} catch (Exception err) {
			System.out.println(err);
		}
	}

	//handle keypress events from socket (currently fired by keydown and keyup events) 
	@Override
	public void keypress(JSONObject keypress) {
		String type = "";
		String key = "";
		
		// get keypress details
		try {
			type = (String) keypress.get("type");
			key = (String) keypress.get("key");
			
			System.out.println(type + ": " + key);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// currently just ignore keyup events
		if(type.compareTo("keyup") == 0) {
			//doSomethingWithKeyUp(key);
			return;
		} else {
			doSomethingWithKeyDown(key);
		}
	}
	
	// Do something with the key from a keydown event 
	public void doSomethingWithKeyDown(String key) {
		System.out.println("You pressed the " + key + " key!");
		
		// do something interesting
		try {
			doNextColor(key);
		} catch (JSONException err) {
			System.out.println(err);
		}
		 
		// get the JSON representation of the updated color grid
		String gridState = grid.getDataStructureRepresentation();
		String gridJSON = '{' + gridState;
		System.out.println(gridJSON);
  
		// send valid JSON for grid into the socket
		sock.sendData(gridJSON);
	}
	
	
	// helper function to do something interesting with color grid arrow keys
	public void doNextColor(String key) throws JSONException {
		switch(key) {
			case "ArrowRight": 
				incrementColor(color, 5);
				break;
			case "ArrowLeft": 
				incrementColor(color, -5);
				break;
			case "ArrowUp": 
				color = getRandomColor();
				break;
			case "ArrowDown": 
				color = new Color("white");
				red = blue = green = 255;
				break;
		}
		System.out.println(red + " " + green + " " + blue);
		paintGrid(color);
	}
	
	// helper function to paint the whole color grid
	public void paintGrid(Color color) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				grid.set(i, j, color);
			}
		}
	}
	
	// helper function to get a random Color object
	public static Color getRandomColor() {
		Color color = new Color();
		color.setRed(red = (int)(Math.random()*255));
		color.setBlue(blue = (int)(Math.random()*255));
		color.setGreen(green = (int)(Math.random()*255));
		return color;
	}
	
	// helper function to increment/decrement a color object
	public void incrementColor(Color color, int increment) {
		if(blue + increment > 0 && blue + increment < 255)
			color.setBlue(blue+=increment);
		if(green + increment > 0 && green + increment < 255)
			color.setGreen(green+=increment);
		if(red + increment > 0 && red + increment < 255)
			color.setRed(red+=increment);
	}	
}
