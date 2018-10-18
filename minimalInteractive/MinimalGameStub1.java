package minimalInteractive;
import babybridges.game.NGCKGame;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;

class MinimalGameStub1 extends NGCKGame {
	
	// We will keep track of the maximum bounds of the grid
	int maxRows, maxCols;
	
	// Let's keep track of a few positions
	//  (we will store [row, column])
	int[] pos1 = {15,0};
	int[] pos2 = {15,29};
	
	public MinimalGameStub1 (int assid, String login, String apiKey) {
		super(assid, login, apiKey);
		
		// Populate the grid boundaries
		maxRows = grid.getDimensions()[0];
		maxCols = grid.getDimensions()[1];
	}
	
	// Set up the first state of the game grid
	public void initialize(){
		for(int i = 0; i < maxRows; i++) { // each row
			for(int j = 0; j < maxCols; j++) { // each column
				grid.setBGColor(i, j, NamedColor.black);
			}
		}
	}
	
	// Game loop will run many times per second.
	// 	Use KeyRight, KeyLeft, KeyUp, KeyDown methods to see if the arrow keys are pressed during the current loop
	//  Use Keyw Keya Keys and Keyd to see if w, a, s, or d keys are pressed
	// 	Use KeyButton1 and KeyButton2 to see if the 'q' and 'p' keys (respectively) are pressed
	// 
	public void GameLoop(){
		
		clearScreen();
		
		// we will modify the row of pos2 if the up or down arrow keys are pressed
		if (KeyUp()) pos2[0]--;
		if (KeyDown()) pos2[0]++;
		
		// draw a symbol at pos2 if it is still on the screen
		if(pos2[0] >= 0 && pos2[0] < maxRows) {
			grid.drawObject(pos2[0], pos2[1], NamedSymbol.triangle_left);
		}
		
		
		// we will modify the row of pos1 if the w or s keys are pressed
		if (Keyw()) pos1[0]--;
		if (Keys()) pos1[0]++;
		
		// draw a symbol at pos1 if it is still on the screen
		if(pos1[0] >= 0 && pos1[0] < maxRows) {
			grid.drawObject(pos1[0], pos1[1], NamedSymbol.triangle_right);
		}
	}
	
	public void clearScreen() {
		for(int i = 0; i < maxRows; i++) { // each row
			for(int j = 0; j < maxCols; j++) { // each column
				grid.setBGColor(i, j, NamedColor.black);
				grid.drawObject(i, j, NamedSymbol.none);
			}
		}
	}
	
	public static void main (String args[]){
		
		// Initialize our non-blocking game
		MinimalGameStub1 mg = new MinimalGameStub1 (1, "username", "apikey");
		mg.setTitle("A non-blocking game!");
		mg.setDescription("Let's make a fun game");
		
		// start running the game
		mg.start();
	}
}