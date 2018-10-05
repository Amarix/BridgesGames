package Minesweeper;

import org.json.JSONException;
import org.json.JSONObject;

import bridges.base.Color;
import bridges.base.ColorGrid;
import bridges.base.GameCell;
import bridges.base.Grid;
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
public class Minesweeper implements KeypressListener {

	// set up default color and sizes for the game grid
	static int gridSize[] = {10,10};
	static GameGrid board;
	static NamedColor oldColor;
	static Bridges bridges;
	static SocketConnection sock;
	static NamedColor color = NamedColor.grey;
	static int[] currPos = {0,0};
	static Grid<Integer> mines;
	static int numMines;
	static int bomb = 109;
	static int flag = 108;
	static boolean gameOver = false;
	static Grid<Integer> state;
	static int openCells = 0;
	
	
	// the main function will set up a bridges connection and a socket connection, then bind the game to the socket
	// TODO: there is probably a better way to set this all up eventually.
	public static void main(String[] args) throws Exception{
		
		bridges = new Bridges(1, "test1", "1277541020216");
		bridges.setServer("sockets");
		bridges.setTitle("Minesweeper");
		bridges.setDescription("Keys:\nClick: Space\nRefresh: 'r'\nFlag: 'f'");
		
		board = new GameGrid(gridSize[0], gridSize[1]);
		setupBoard(board);
		
		
		
		// set up socket connection to receive and send data
		sock = new SocketConnection();
		sock.setupConnection(bridges.getUserName(), bridges.getAssignment());	
		
		// game will now listen for keypresses from the socket
		Minesweeper game = new Minesweeper();
	    sock.addListener(game);
	    
	    // start running the game
	    game.start();
	}
	
	// generate the first board and visualize the grid
	public void start() {

		// associate the gamegrid with the Bridges object
//		bridges.setVisualizeJSON(true);
		System.out.println(board.getDataStructureRepresentation());
		bridges.setDataStructure(board);

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
		} else if(key.contains("Arrow")) {
			moveSelection(key);
		} else {
			defaultKeydown(key);
		}
		

		sendGameData();
	}
	
	// Do something with the key from a keydown event 
	public void defaultKeydown(String key) {
		System.out.println("You pressed the " + key + " key!");
		
		switch(key) {
		case "r":
		case "R":
			restart();
			break;
		case "f":
		case "F":
			flag();
			break;
		case "Enter":
		case " ":
			clickCell();
			break;
		}
	}
	
	public void clickCell() {
		int i = currPos[0]; // row
		int j = currPos[1]; // col
		
		// don't click flagged cells!
		if(state.get(i,  j) == 2) {
			return;
		}
		
		// if mine
		if(mines.get(i, j) == 1) {
			highlightMines();
			gameOver = true;
		} else {
			oldColor = NamedColor.lightgrey;
			visitCell(i, j);
		}
	}
	
	public void flag() {
		int i = currPos[0]; // row
		int j = currPos[1]; // col
		
		// add or remove flag
		if(state.get(i,  j) == 0) {
			board.drawObject(i, j, flag, NamedColor.green);
			state.set(i, j, 2);
		} else {
			board.drawObject(i, j, 0, NamedColor.white);
			state.set(i, j, 0);
		}
	}
	
	public void visitCell(int i, int j) {			
		if(state.get(i,  j) > 0) {
			return;
		}
		state.set(i, j, 1);
		openCells++;
		System.out.println("open cells: " + openCells);
		if(openCells >= gridSize[0]*gridSize[1] - numMines) {
			victory();
		}
		
		int adjacentMines = 0;
		
		// compute adjacent mines
		if(j > 0) {
			if(mines.get(i, j-1) == 1) adjacentMines++; // left
			if(i > 0 && mines.get(i-1, j-1) == 1) adjacentMines++; // top left
			if(i < gridSize[0]-1 && mines.get(i+1, j-1) == 1) adjacentMines++; // bottom left
		}
		if(j < gridSize[1]-1) {
			if(mines.get(i, j+1) == 1) adjacentMines++; // right
			if(i > 0 && mines.get(i-1, j+1) == 1) adjacentMines++; // top right
			if(i < gridSize[0]-1 && mines.get(i+1, j+1) == 1) adjacentMines++; // bottom right
		}
		if(i > 0) {
			if(mines.get(i-1, j) == 1) adjacentMines++; // top
		}
		if(i < gridSize[0]-1) {
			if(mines.get(i+1, j) == 1) adjacentMines++; // bottom
		}
		
		// draw the current cell
		if(adjacentMines > 0) { 
			board.drawObject(i, j, 53 + adjacentMines);
			board.setBGColor(i, j, NamedColor.lightgrey);
		}
		// recursively visit adjacent cells
		else {	
			board.setBGColor(i, j, NamedColor.lightgrey);
			if(j > 0) {
				visitCell(i, j-1); // left
				if(i > 0) visitCell(i-1, j-1); // top left
				if(i < gridSize[0]-1) visitCell(i+1, j-1);// bottom left
			}
			if(j < gridSize[1]-1) {
				visitCell(i, j+1); // right
				if(i > 0) visitCell(i-1, j+1); // top right
				if(i < gridSize[0]-1) visitCell(i+1, j+1);// bottom right
			}
			if(i > 0) {
				visitCell(i-1, j); // top
			}
			if(i < gridSize[0]-1) {
				visitCell(i+1, j); // bottom
			}
		}
	}
	
	public void highlightMines() {
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				if(mines.get(i, j) == 1) {
					board.setBGColor(i, j, NamedColor.red);
					board.drawObject(i, j, bomb, NamedColor.black);
				}
			}
		}
	}
	
	// Do something with the key from an arrow key event 
	public void moveSelection(String key) {

		if(gameOver) return;
		
		int[] oldPos = {currPos[0], currPos[1]};

		switch(key) {
			case "ArrowRight": 
				if(currPos[1] < gridSize[1]-1) currPos[1]++;
				break;
			case "ArrowLeft": 
				if(currPos[1] > 0) currPos[1]--;
				break;
			case "ArrowUp": 
				if(currPos[0] > 0) currPos[0]--;
				break;
			case "ArrowDown": 
				if(currPos[0] < gridSize[0]-1) currPos[0]++;
				break;
		}
		 
		// update the old color (previously selected cell)
		board.setBGColor(oldPos[0], oldPos[1], oldColor);
		if(board.get(currPos[0], currPos[1]).getBGColor() == 73) {
			oldColor = NamedColor.lightgrey;
		} else if (board.get(currPos[0], currPos[1]).getBGColor() == 54) {
			oldColor = NamedColor.grey;	
		}
		// highlight the current selected cell
		board.setBGColor(currPos[0], currPos[1], NamedColor.gold);
		
	}
	
	public static void restart() {
		gameOver = false;
		board = new GameGrid(gridSize[0], gridSize[1]);
		setupBoard(board);
		initializeState();
		sendGameData();
	}
	
	public static void victory() {
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				if(mines.get(i, j) == 1) 
					board.drawObject(i, j, 99, NamedColor.gold);
			}
		}
		sendGameData();
		gameOver = true;
	}
	
	// helper function to paint the whole color grid
	public static void setupBoard(GameGrid chessboard) {
		openCells = 0;
		setupMines();
		initializeState();
		
		// draw minesweeper board
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				board.setBGColor(i, j, NamedColor.grey);
			}
		}
		
		// draw current position
		oldColor = NamedColor.grey;
		board.setBGColor(currPos[0], currPos[1], NamedColor.gold);
	}
	
	public static void initializeState() {
		state = new Grid<Integer>(gridSize[0], gridSize[1]);
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				state.set(i,  j,  0);
			}
		}
	}
	
	// 0 = empty, 1 = mine
	public static void setupMines() {
		mines = new Grid<Integer>(gridSize[0], gridSize[1]);
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				mines.set(i,  j,  0);
			}
		}
		
		numMines = (gridSize[0] * gridSize[1]) / 7;
		System.out.println(numMines + " mines seeding...");
		
		int ri, rj;
		ri = (int) Math.floor(Math.random() * gridSize[0]);
		rj = (int) Math.floor(Math.random() * gridSize[1]);
		System.out.println("i (row) :" + ri + " , j (col): " + rj);
		
		for(int i = 0; i < numMines; i++) {
			// loop until finding a non-mine cell
			while(mines.get(ri, rj) == 1) {
				ri = (int) Math.floor(Math.random() * gridSize[0]);
				rj = (int) Math.floor(Math.random() * gridSize[1]);
				System.out.println("i (row) :" + ri + " , j (col): " + rj);
			}
			
			// add a mine!
			mines.set(ri, rj, 1);
		}
		
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				System.out.print(mines.get(i,j) + " ");
			}
			System.out.println();
		}
	}
	
	public static void sendGameData() {
		// get the JSON representation of the updated color grid
		String gamegridRepresentation = board.getDataStructureRepresentation();
		String gamegridJSON = '{' + gamegridRepresentation;
		System.out.println(gamegridJSON);
	  
		// send valid JSON for grid into the socket
		sock.sendData(gamegridJSON);
	}
}
