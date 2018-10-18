package minimalBlocking;

import babybridges.game.BlockingGame;
import bridges.base.GameGrid;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
 

class MinimalBlockingStub1 {
    public static void main (String args[]) {
    	int numRows = 10;
    	int numCols = 10;
    	NamedColor bgColor;
    	
    	// Initialize our blocking game
	 	BlockingGame bg = new BlockingGame(2, "username", "apikey", numRows, numCols);
	
	 	// This is the grid we will modify 
 	 	//  use grid.setBGColor to set the background color of a cell
 	 	//  use grid.drawObject to draw a symbol (perhaps with a particular color) in a cell
	 	GameGrid gg = bg.getGameGrid();
	 	
	 	
	 	// Initialize cat position and color
	 	int cat_row = 5, cat_col = 5;
	 	bgColor = NamedColor.black;
		
	 	// Draw initial board 
		for (int i=0; i<numRows; ++i)
		    for (int j=0; j<numCols; ++j)
		    	gg.setBGColor(i,j, bgColor);
	 	gg.drawObject(cat_row, cat_col, NamedSymbol.cat);
		
	 	// Render initial board
		bg.render();
		
		// Main game loop
	 	boolean cont = true;
		while (cont) {
			
			// Wait until a keypress comes through
		    String k = bg.getKeyPress();
		    System.out.println (k);
	 	    if (k.equals("q"))
	 	    	cont = false;
	 	    
	 	    // update the cat position
	 	    if (k.equals("ArrowUp")) cat_row--;
	 	    if (k.equals("ArrowDown")) cat_row++;
	 	    if (k.equals("ArrowLeft")) cat_col--;
	 	    if (k.equals("ArrowRight")) cat_col++;
		    
		    // clear the game grid
		    for (int i=0; i<numRows; ++i) {
				for (int j=0; j<numCols; ++j) {
				    gg.setBGColor(i,j, bgColor);
				    gg.drawObject(i, j, NamedSymbol.none);
				}
		    }
		    
		    // draw the cat if it's within the grid bounds
		    if (cat_col >=0 && cat_col<numCols && cat_row>=0 && cat_row<numRows) {
		    	gg.drawObject(cat_row, cat_col, NamedSymbol.cat);
		    }
		    
		    // Render the next state of the grid
		    bg.render();
		}
    }
}