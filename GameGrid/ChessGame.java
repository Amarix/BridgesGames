package GameGrid;

import org.json.JSONException;
import org.json.JSONObject;

import bridges.base.Color;
import bridges.base.ColorGrid;
import bridges.base.GameGrid;
import bridges.base.NamedColor;
import bridges.connect.Bridges;

/* 
 *		This class is a another rudimentary 'game' which:
 *			- sets up a socket connection to a socket server using bridges username and assignment number 
 *			- receives keypress events from a web browser 
 *			- sends JSON representation of a gamegrid back to the web browser
 * 		
 * 		Game implements KeypressListener, which is an interface providing a 'keypress' event to override 
 * 			keypress events can have type 'keydown' or 'keyup' and provide an associated 'key' name
 */
public class ChessGame implements KeypressListener {

	// set up default color and sizes for the game grid
	static int gridSize[] = {8,8};
	static GameGrid chessboard;
	static String gamegridState = "";
	static Bridges bridges;
	static SocketConnection sock;
	static NamedColor color = NamedColor.black;
	static int piece = 0;
	static int[] currPos = {0,0};
	
	// the main function will set up a bridges connection and a socket connection, then bind the game to the socket
	// TODO: there is probably a better way to set this all up eventually.
	public static void main(String[] args) throws Exception{
		
		// get dog
		
		bridges = new Bridges(0, "test1", "1277541020216");
		bridges.setServer("sockets");
		bridges.setTitle("Game Grid Chessboard (now with Sockets!)");
		bridges.setDescription("en passant");
		
		chessboard = new GameGrid(gridSize[0], gridSize[1]);
		setupBoard(chessboard);
		
		// set up socket connection to receive and send data
		sock = new SocketConnection();
		sock.setupConnection(bridges.getUserName(), bridges.getAssignment());	
		
		// game will now listen for keypresses from the socket
		ChessGame game = new ChessGame();
	    sock.addListener(game);
	    
	    // start running the game
	    game.start();
	}
	
	// generate the first board and visualize the grid
	public void start() {

		// associate the gamegrid with the Bridges object
//		bridges.setVisualizeJSON(true);
		System.out.println(chessboard.getDataStructureRepresentation());
		bridges.setDataStructure(chessboard);

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
		int[] oldPos = {currPos[0], currPos[1]};
		
		switch(key) {
			case "ArrowRight": 
				if(currPos[1] < 7) currPos[1]++;
				break;
			case "ArrowLeft": 
				if(currPos[1] > 0) currPos[1]--;
				break;
			case "ArrowUp": 
				if(currPos[0] > 0) currPos[0]--;
				break;
			case "ArrowDown": 
				if(currPos[0] < 7) currPos[0]++;
				break;
		}
		 
		chessboard.setBGColor(oldPos[0], oldPos[1], getCellColor(oldPos[0], oldPos[1]));
		chessboard.setBGColor(currPos[0], currPos[1], NamedColor.gold);
		 
		// get the JSON representation of the updated color grid
		String gamegridState = chessboard.getDataStructureRepresentation();
		String gamegridJSON = '{' + gamegridState;
		System.out.println(gamegridJSON);
  
		// send valid JSON for grid into the socket
		sock.sendData(gamegridJSON);
	}
	
	public static NamedColor getCellColor(int i, int j) {
		if(j % 2 == 1 && i % 2 == 0)  return  NamedColor.sienna;
		else if(j % 2 == 0 && i % 2 == 1)  return  NamedColor.sienna;
		else return NamedColor.tan;
	}
	
	// helper function to paint the whole color grid
	public static void setupBoard(GameGrid chessboard) {
		// draw chess board
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				chessboard.setBGColor(i, j, getCellColor(i, j));
			}
		}
		
		// draw black pieces
		color = NamedColor.black;
		piece = 0;
		for(int i = 0; i < 2; i++) {	// rows
			for(int j = 0; j < 8; j++) {	// cols
				if(i == 1) {
					piece = 74;
				} else {
					switch(j) {
						case 0:
						case 7:
							piece = 77;
							break;
						case 1:
						case 6:
							piece = 75;
							break;
						case 2: 
						case 5: 
							piece = 76;
							break;
						case 3: 
							piece = 78;
							break;
						case 4: 
							piece = 79;
							break;
					}
				}
				chessboard.drawObject(i, j, piece, color);
			}
		}
		
		// draw white pieces
		color = NamedColor.white;
		piece = 0;
		for(int i = 6; i < 8; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				if(i == 6) {
					piece = 74;
				} else {
					switch(j) {
						case 0:
						case 7:
							piece = 77;
							break;
						case 1:
						case 6:
							piece = 75;
							break;
						case 2: 
						case 5: 
							piece = 76;
							break;
						case 3: 
							piece = 78;
							break;
						case 4: 
							piece = 79;
							break;
					}
				}
				chessboard.drawObject(i, j, piece, color);
			}
		}
	}
	
	// helper function to get a random Color object
//	public static Color getRandomColor() {
//		Color color = new Color();
//		color.setRed(red = (int)(Math.random()*255));
//		color.setBlue(blue = (int)(Math.random()*255));
//		color.setGreen(green = (int)(Math.random()*255));
//		return color;
//	}
//	
//	// helper function to increment/decrement a color object
//	public void incrementColor(Color color, int increment) {
//		if(blue + increment > 0 && blue + increment < 255)
//			color.setBlue(blue+=increment);
//		if(green + increment > 0 && green + increment < 255)
//			color.setGreen(green+=increment);
//		if(red + increment > 0 && red + increment < 255)
//			color.setRed(red+=increment);
//	}	
}
