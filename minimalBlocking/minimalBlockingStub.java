package minimalBlocking;

import babybridges.game.BlockingGame;
import bridges.base.GameGrid;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
 

class minimalBlockingStub {

	public minimalBlockingStub(int assid, String login, String apiKey, int col, int row){
		super(assid, login, apiKey, col, row);
	}

	public void init(){
		// Initial render
	 	render();
		
	 	// Main game loop
	 	boolean cont = true;
		while (cont) {
			
			// Wait until a keypress comes through
			String k = GetKeyPress();
			
		    // Perform some game logic
		    
		    // Render the next state of the grid
		    render();	    
		}
	}
	
    public static void main (String args[]) {
    	int numRows = 10;
    	int numCols = 10;

    	// Initialize our blocking game
	 	minimalBlockingGame bg = new minimalBlockingGame(2, "username", "apikey", numRows, numCols);
	 	bg.setTitle("A blocking game!");
		bg.setDescription("Let's make something fun");	
	}
}