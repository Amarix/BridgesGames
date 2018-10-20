package Minesweeper;

import org.json.JSONException;
import org.json.JSONObject;

import babybridges.game.BlockingGame;
import bridges.base.GameCell;
import bridges.base.Grid;
import bridges.base.GameGrid;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
import bridges.connect.Bridges;

/* 
 * 
 */
public class Minesweeper {

	// set up default colors, symbols, positions, and sizes for the game grid
	static int numRows = 5;
	static int numCols = 5;
	static GameGrid gg;
	static NamedColor oldColor;
	static NamedColor color = NamedColor.grey;
	static NamedSymbol mine = NamedSymbol.bomb;
	static NamedSymbol flag = NamedSymbol.flag;
	static int[] currPos = { numRows/2, numCols/2 };
	
	// keep a grid of 0 or 1 for mine positions
	static Grid<Integer> mines;
	static int numMines;
	
	// keep sentinel variables for continuing, restarting, or quitting
	static boolean gameOver;
	static boolean quit;
	
	// keep a grid of integers for the state of each cell (flagged, numbered, opened, etc).
	static Grid<Integer> state;
	static int openCells = 0;

	
	public static void main (String args[]) {
	    	
    	// Initialize our blocking game
	 	BlockingGame bg = new BlockingGame(11, "username", "apikey", numRows, numCols);
	 	bg.setTitle("Minesweeper");
		bg.setDescription("Keys:\nClick: Space\nRefresh: 'r'\nFlag: 'f'");
	 		
	 	
	 	// This is the grid we will modify 
 	 	//  use gg.setBGColor to set the background color of a cell
 	 	//  use gg.drawObject to draw a symbol (perhaps with a particular color) in a cell
	 	gg = bg.getGameGrid();
	 	
	 	// Initialize variables and paint the first game grid
	 	setupgg(gg);
		
	 	// Render initial gg
		bg.render();
		
		// Main game loop
		while (!quit) {
			
			// Handle each keypress
			handleKeypress(bg.getKeyPress());
			
		    // Render the next state of the grid
			bg.render();
		}
		
		System.exit(0);
    }

	// handle keypress events from the player
	public static void handleKeypress(String k) {
		// System.out.println (k);

		switch(k) {
	    	case "q":
	    		quit = true;
	    		break;
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
			case "ArrowRight":
			case "ArrowLeft":
			case "ArrowUp":
			case "ArrowDown":
				moveSelection(k);
				break;
		}
	}

	// Handle 'clicking' on a particular cell
	public static void clickCell() {
		int i = currPos[0]; // row
		int j = currPos[1]; // col

		// can't click flagged cells!
		if (state.get(i, j) == 2) {
			return;
		}

		// if a mine was clicked, it's game over
		// otherwise, visit the cell (recursively)
		if (mines.get(i, j) == 1) {
			highlightMines();
			gameOver = true;
		} else {
			oldColor = NamedColor.lightgrey;
			visitCell(i, j);
		}
	}

	// add or remove a flag from the current cell selection
	public static void flag() {
		int i = currPos[0]; // row
		int j = currPos[1]; // col

		// add or remove flag
		if (state.get(i, j) == 0) {
			gg.drawObject(i, j, flag, NamedColor.green);
			state.set(i, j, 2);
			checkVictory();
		} else {
			gg.drawObject(i, j, 0, NamedColor.white);
			state.set(i, j, 0);
		}
	}

	// Recursively visit a cell. If it has no adjacent mines, visit its neighbors. 
	public static void visitCell(int i, int j) {
		if (state.get(i, j) > 0) {
			return;
		}
		state.set(i, j, 1);
		openCells++;
		if (openCells >= numRows * numCols - numMines) {
			victory();
		}

		int adjacentMines = 0;

		// compute adjacent mines
		if (j > 0) {
			if (mines.get(i, j - 1) == 1)
				adjacentMines++; // left
			if (i > 0 && mines.get(i - 1, j - 1) == 1)
				adjacentMines++; // top left
			if (i < numRows - 1 && mines.get(i + 1, j - 1) == 1)
				adjacentMines++; // bottom left
		}
		if (j < numCols - 1) {
			if (mines.get(i, j + 1) == 1)
				adjacentMines++; // right
			if (i > 0 && mines.get(i - 1, j + 1) == 1)
				adjacentMines++; // top right
			if (i < numRows - 1 && mines.get(i + 1, j + 1) == 1)
				adjacentMines++; // bottom right
		}
		if (i > 0) {
			if (mines.get(i - 1, j) == 1)
				adjacentMines++; // top
		}
		if (i < numRows - 1) {
			if (mines.get(i + 1, j) == 1)
				adjacentMines++; // bottom
		}

		// draw the current cell
		if (adjacentMines > 0) {
			gg.drawObject(i, j, 53 + adjacentMines);
			gg.setBGColor(i, j, NamedColor.lightgrey);
		}
		// recursively visit adjacent cells if no adjacent mines
		else {
			gg.setBGColor(i, j, NamedColor.lightgrey);
			if (j > 0) {
				visitCell(i, j - 1); // left
				if (i > 0)
					visitCell(i - 1, j - 1); // top left
				if (i < numRows - 1)
					visitCell(i + 1, j - 1);// bottom left
			}
			if (j < numCols - 1) {
				visitCell(i, j + 1); // right
				if (i > 0)
					visitCell(i - 1, j + 1); // top right
				if (i < numRows - 1)
					visitCell(i + 1, j + 1);// bottom right
			}
			if (i > 0) {
				visitCell(i - 1, j); // top
			}
			if (i < numRows - 1) {
				visitCell(i + 1, j); // bottom
			}
		}
	}

	// after losing, show all the mine positions
	public static void highlightMines() {
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				if (mines.get(i, j) == 1) {
					gg.setBGColor(i, j, NamedColor.red);
					gg.drawObject(i, j, mine, NamedColor.black);
				}
			}
		}
	}

	// Move the current cell selection 
	public static void moveSelection(String key) {
		if (gameOver)
			return;

		int[] oldPos = { currPos[0], currPos[1] };

		switch (key) {
		case "ArrowRight":
			if (currPos[1] < numCols - 1)
				currPos[1]++;
			break;
		case "ArrowLeft":
			if (currPos[1] > 0)
				currPos[1]--;
			break;
		case "ArrowUp":
			if (currPos[0] > 0)
				currPos[0]--;
			break;
		case "ArrowDown":
			if (currPos[0] < numRows - 1)
				currPos[0]++;
			break;
		}

		// update the old color (previously selected cell)
		gg.setBGColor(oldPos[0], oldPos[1], oldColor);
		if (gg.get(currPos[0], currPos[1]).getBGColor() == 73) {
			oldColor = NamedColor.lightgrey;
		} else if (gg.get(currPos[0], currPos[1]).getBGColor() == 54) {
			oldColor = NamedColor.grey;
		}
		// highlight the current selected cell
		gg.setBGColor(currPos[0], currPos[1], NamedColor.gold);

	}

	// reinitialize everything and start over
	public static void restart() {
		gameOver = false;
		setupgg(gg);
	}

	// see if all the mines have been flagged
	public static void checkVictory() {
		int flaggedMines = 0;
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				if (mines.get(i, j) == 1 && state.get(i, j) == 2) flaggedMines++;
			}
		}
		if(flaggedMines == numMines) victory();
	}
	
	// you won! Show all the mines you found. 
	public static void victory() {
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				if (mines.get(i, j) == 1) {
					gg.setBGColor(i, j, NamedColor.black);
					gg.drawObject(i, j, 99, NamedColor.gold);
				}
			}
		}
		gameOver = true;
	}

	// Initialize positions, counts, and board states
	public static void setupgg(GameGrid grid) {
		openCells = 0;
		setupMines();
		initializeState();
		currPos = new int[]{ numRows/2, numCols/2 };

		// draw minesweeper gg
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				grid.setBGColor(i, j, NamedColor.grey);
				grid.drawObject(i, j, NamedSymbol.none, NamedColor.white);
			}
		}

		// draw current position
		oldColor = NamedColor.grey;
		grid.setBGColor(currPos[0], currPos[1], NamedColor.gold);
	}

	// Initialize the state representation
	public static void initializeState() {
		state = new Grid<Integer>(numRows, numCols);
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				state.set(i, j, 0);
			}
		}
	}

	// Initialize a new set of mines
	// 0 = empty, 1 = mine
	public static void setupMines() {
		mines = new Grid<Integer>(numRows, numCols);
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				mines.set(i, j, 0);
			}
		}

		numMines = (numRows * numCols) / 7;
		System.out.println(numMines + " mines seeding...");

		int ri, rj;
		ri = (int) Math.floor(Math.random() * numRows);
		rj = (int) Math.floor(Math.random() * numCols);
		System.out.println("i (row) :" + ri + " , j (col): " + rj);

		for (int i = 0; i < numMines; i++) {
			// loop until finding a non-mine cell
			while (mines.get(ri, rj) == 1) {
				ri = (int) Math.floor(Math.random() * numRows);
				rj = (int) Math.floor(Math.random() * numCols);
				System.out.println("i (row) :" + ri + " , j (col): " + rj);
			}

			// add a mine!
			mines.set(ri, rj, 1);
		}

		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				System.out.print(mines.get(i, j) + " ");
			}
			System.out.println();
		}
	}
}
