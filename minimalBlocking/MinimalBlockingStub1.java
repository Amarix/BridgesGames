package minimalBlocking;

import babybridges.game.BlockingGame;
import bridges.base.GameGrid;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
 

class MinimalBlockingStub1 {
	
	public NamedColor bgColor;

	public void init(){
		// Initialize cat position and color
		int cat_row = 5, cat_col = 5;
		bgColor = NamedColor.black;

		// Draw initial board 
		for (int i=0; i<numRows; ++i)
		    for (int j=0; j<numCols; ++j)
			setBGColor(i,j, bgColor);
		DrawObject(cat_row, cat_col, NamedSymbol.cat);

		// Render initial board
		render();

		// Main game loop
		boolean cont = true;
		while (cont) {

			// Wait until a keypress comes through
		    String k = GetKeyPress();
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
				    SetBGColor(i,j, bgColor);
				    DrawObject(i, j, NamedSymbol.none);
				}
		    }

		    // draw the cat if it's within the grid bounds
		    if (cat_col >=0 && cat_col<numCols && cat_row>=0 && cat_row<numRows) {
			DrawObject(cat_row, cat_col, NamedSymbol.cat);
		    }

		    // Render the next state of the grid
		    render();
		}
	}
	
    	public static void main (String args[]) {
		int assignmentNum = 0;
		String login = "studentLoginName";
		String apiKey = "studentAPIKey";
		int numRows = 10;
		int numCols = 10;

		// Initialize our blocking game
		MinimalBlockingStub1 bg = new MinimalBlockingStub1(assignmentNum, login, apiKey, numRows, numCols);
    	}
}
