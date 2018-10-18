package minimalBlocking;

import babybridges.game.BlockingGame;
import bridges.base.GameGrid;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
 

class minimalBlockingStub {
    public static void main (String args[]) {
    	int numRows = 10;
    	int numCols = 10;

    	// Initialize our blocking game
	 	BlockingGame bg = new BlockingGame(2, "username", "apikey", numRows, numCols);
	 	bg.setTitle("A blocking game!");
	 	bg.setDescription("Let's make something fun");
	 	
	 	// This is the grid we will modify 
	 	//  use grid.setBGColor to set the background color of a cell
	 	//  use grid.drawObject to draw a symbol (perhaps with a particular color) in a cell
	 	GameGrid grid = bg.getGameGrid();
	 	
	 	
	 	// Initial render
	 	bg.render();
		
	 	// Main game loop
	 	boolean cont = true;
		while (cont) {
			
			// Wait until a keypress comes through
		    String k = bg.getKeyPress();
	
		    // Perform some game logic
		    
		    // Render the next state of the grid
		    bg.render();	    
		}	
    }
}