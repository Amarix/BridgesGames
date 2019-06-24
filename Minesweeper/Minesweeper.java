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
public class Minesweeper extends BlockingGame{

	// set up default colors, symbols, positions, and sizes for the game grid
	static int numRows = 5;
	static int numCols = 5;
	NamedColor oldColor;
	NamedColor color = NamedColor.grey;
	NamedSymbol mine = NamedSymbol.bomb;
	NamedSymbol flag = NamedSymbol.flag;
	int[] currPos = { numRows/2, numCols/2 };
	
	// keep a grid of 0 or 1 for mine positions
	Grid<Integer> mines;
	int numMines;
	
	// keep sentinel variables for continuing, restarting, or quitting
	boolean gameOver;
	boolean quit;
	
	// keep a grid of integers for the state of each cell (flagged, numbered, opened, etc).
	Grid<Integer> state;
	int openCells = 0;

        public Minesweeper(int assid, String login, String apiKey, int row, int col) {
            super(assid, login, apiKey, row, col);
        }
	
	public static void main (String args[]) {
	    	
    	// Initialize our blocking game
	 	Minesweeper bg = new Minesweeper(11, "username", "apikey", numRows, numCols);
                
		System.exit(0);
    }
        public void init(){
            
            // Title and description of game
            setTitle("Minesweeper");
            setDescription("Keys:\nClick: Space\nRefresh: 'r'\nFlag: 'f'");
                
            // Initialize variables and paint the first game grid
            setupgg();
                
            // Render initial gg
            render();
                
            // Main game loop
            while (!quit) {

                    // Handle each keypress
                    handleKeypress(GetKeyPress());

                // Render the next state of the grid
                    render();
            }
        }

	// handle keypress events from the player
	public void handleKeypress(String k) {
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
	public void clickCell() {
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
	public void flag() {
		int i = currPos[0]; // row
		int j = currPos[1]; // col

		// add or remove flag
		if (state.get(i, j) == 0) {
			DrawObject(i, j, flag, NamedColor.green);
			state.set(i, j, 2);
			checkVictory();
		} else {
			DrawObject(i, j, 0, NamedColor.white);
			state.set(i, j, 0);
		}
	}

	// Recursively visit a cell. If it has no adjacent mines, visit its neighbors. 
	public void visitCell(int i, int j) {
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
			DrawObject(i, j, 53 + adjacentMines);
			SetBGColor(i, j, NamedColor.lightgrey);
		}
		// recursively visit adjacent cells if no adjacent mines
		else {
			SetBGColor(i, j, NamedColor.lightgrey);
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
	public void highlightMines() {
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				if (mines.get(i, j) == 1) {
					SetBGColor(i, j, NamedColor.red);
					DrawObject(i, j, mine, NamedColor.black);
				}
			}
		}
	}

	// Move the current cell selection 
	public void moveSelection(String key) {
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
		SetBGColor(oldPos[0], oldPos[1], oldColor);
		if (GetBGColor(currPos[0], currPos[1]) == 73) {
			oldColor = NamedColor.lightgrey;
		} else if (GetBGColor(currPos[0], currPos[1])== 54) {
			oldColor = NamedColor.grey;
		}
		// highlight the current selected cell
		SetBGColor(currPos[0], currPos[1], NamedColor.gold);

	}

	// reinitialize everything and start over
	public void restart() {
		gameOver = false;
		setupgg();
	}

	// see if all the mines have been flagged
	public void checkVictory() {
		int flaggedMines = 0;
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				if (mines.get(i, j) == 1 && state.get(i, j) == 2) flaggedMines++;
			}
		}
		if(flaggedMines == numMines) victory();
	}
	
	// you won! Show all the mines you found. 
	public void victory() {
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				if (mines.get(i, j) == 1) {
					SetBGColor(i, j, NamedColor.black);
					DrawObject(i, j, 99, NamedColor.gold);
				}
			}
		}
		gameOver = true;
	}

	// Initialize positions, counts, and board states
	public void setupgg() {
		openCells = 0;
		setupMines();
		initializeState();
		currPos = new int[]{ numRows/2, numCols/2 };

		// draw minesweeper gg
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				SetBGColor(i, j, NamedColor.grey);
				DrawObject(i, j, NamedSymbol.none, NamedColor.white);
			}
		}

		// draw current position
		oldColor = NamedColor.grey;
		SetBGColor(currPos[0], currPos[1], NamedColor.gold);
	}

	// Initialize the state representation
	public void initializeState() {
		state = new Grid<Integer>(numRows, numCols);
		for (int i = 0; i < numRows; i++) { // rows
			for (int j = 0; j < numCols; j++) { // cols
				state.set(i, j, 0);
			}
		}
	}

	// Initialize a new set of mines
	// 0 = empty, 1 = mine
	public void setupMines() {
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
