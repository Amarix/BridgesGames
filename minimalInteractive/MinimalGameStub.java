package minimalInteractive;
import babybridges.game.NGCKGame;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;

class MinimalGameStub extends NGCKGame {

	// We will keep track of the maximum bounds of the grid
	int maxRows, maxCols;

	public MinimalGameStub (int assid, String login, String apiKey) {
		super(assid, login, apiKey);

		// Populate the grid boundaries
		maxRows = grid.getDimensions()[0];
		maxCols = grid.getDimensions()[1];
	}

	// Set up the first state of the game grid.
	public void initialize(){

	}

	// Game loop will run many times per second.
	public void GameLoop(){
		if (KeyUp()) System.out.println("The up key is currently pressed!")

	}

	public static void main (String args[]){

		// Initialize our non-blocking game
		MinimalGameStub mg = new MinimalGameStub (1, "username", "apikey");
		mg.setTitle("A non-blocking game!");
		mg.setDescription("Let's make a fun game");

		// start running the game
		mg.start();
	}
}
