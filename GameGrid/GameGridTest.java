
import java.security.SecureRandom;

import org.json.JSONException;
import org.json.JSONObject;

import bridges.base.*;
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
public class GameGridTest implements KeypressListener {
	
	public static final SecureRandom random = new SecureRandom();
	// set up default color and sizes for the game grid
	static int gridSize[] = {3,3};
	static GameGrid ggrid;
	static Bridges bridges;
	static SocketConnection sock;
	static int currSymbol = 0;
	
	public static void main(String[] args) throws Exception{
	    bridges = new Bridges(0, "esaule-interactive", "1239999531573");
		//bridges.setServer("local");
		bridges.setServer("sockets");
		bridges.setTitle("Game Grid Symbol Test");
		bridges.setDescription("hi");
		
		ggrid = new GameGrid(gridSize[0], gridSize[1]);

		//	set up socket connection to receive and send data
		sock = new SocketConnection();
		sock.setupConnection(bridges.getUserName(), bridges.getAssignment());			
				
		// game will now listen for keypresses from the socket
		GameGridTest game = new GameGridTest();
	    sock.addListener(game);
			    
	    // start running the game
	    game.start();
	}
			
	// generate the first board and visualize the grid
	public void start() {
		
		drawCurrentSymbol();
		
		// associate the gamegrid with the Bridges object
		// bridges.setVisualizeJSON(true);
		System.out.println(ggrid.getDataStructureRepresentation());
		bridges.setDataStructure(ggrid);

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

		switch(key) {
			case "ArrowRight":
				currSymbol = currSymbol < 255 ? currSymbol+1 : currSymbol;
				break;
			case "ArrowLeft":
				currSymbol = currSymbol > 0 ? currSymbol-1 : currSymbol;
				break;
		}
		
		drawCurrentSymbol();
		 
		// get the JSON representation of the updated color grid
		String gamegridState = ggrid.getDataStructureRepresentation();
		String gamegridJSON = '{' + gamegridState;
		System.out.println(gamegridJSON);
  
		// send valid JSON for grid into the socket
		sock.sendData(gamegridJSON);
	}
	
	public void drawCurrentSymbol() {
		ggrid.setBGColor(1, 1, NamedColor.white);
		ggrid.drawObject(1, 1, currSymbol, NamedColor.black);
	}
	
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        System.out.println(x);
        return clazz.getEnumConstants()[x];
    }


}
