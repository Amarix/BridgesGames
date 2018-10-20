package BUGSTOMP;

import babybridges.game.BlockingGame;
import babybridges.game.NGCKGame;
import bridges.connect.Bridges;
import bridges.base.GameGrid;
import bridges.base.NamedColor;
import bridges.base.NamedSymbol;

public class BUGSTOMP extends NGCKGame {
	
	int[] loc = {0,0}; // row, col
	int[] boardSize = grid.getDimensions();
	int[] bug;
	int bugttl = 100;
	int score = 0;
	NamedColor bugColor;
	
	static java.util.Random randomizer;

	public BUGSTOMP(int assid, String login, String apiKey) {
		super(assid, login, apiKey);
		
		randomizer = new java.util.Random();
		bugColor = randomEnum(NamedColor.class);
	}

	public void initialize() {
		for(int i = 0; i < grid.getDimensions()[0]; i++) { // each row
			for(int j = 0; j < grid.getDimensions()[1]; j++) { // each column
				grid.setBGColor(i, j, NamedColor.white);
			}
		}
		
		bug = new int[]{10,10};
	}
	
	public void handleInput() {
		if (KeyLeft())
			loc[1]--;
		if (KeyRight())
			loc[1]++;
		if (KeyUp())
			loc[0]--;
		if (KeyDown())
			loc[0]++;
		
		// check row top
		if(loc[0] < 1) loc[0] = 1;
		// check row bottom
		if(loc[0] > boardSize[0]-1) loc[0] = boardSize[0]-1;
		// check column left
		if(loc[1] < 0) loc[1] = 0;
		// check column right
		if(loc[1] > boardSize[1]-1) loc[1] = boardSize[1]-1;
	}
	
	public void handlebug() {
		if(bugttl < 1) {
			bug = new int[]{randomizer.nextInt(29)+1, randomizer.nextInt(29)+1};
			bugttl = randomizer.nextInt(100-50)+50;
			bugColor = randomEnum(NamedColor.class);
			score--;
			if(score < 0) score = 0;
		} else {
			bugttl--;
			if(overlap(bug, loc)) {
				bug = new int[]{randomizer.nextInt(29)+1, randomizer.nextInt(29)+1};
				bugColor = randomEnum(NamedColor.class);
				score++;
			}
		}
	}
	
	public Boolean overlap(int[] bug, int[] loc) {
		return Math.abs(bug[0] - loc[0]) < 2 && Math.abs(bug[1] - loc[1]) < 2;
	}
	
	public void paintScreen() {
		// paint black
		for (int i = 0; i < 30; ++i) {
			for (int j = 0; j < 30; ++j) {
				SetBGColor(i, j, NamedColor.black);
				SetSymbol(i, j, 0);
			}
		}
		
		if(score >= 10) {
			win();
			return;
		}
		
		paintScore(score);
		

		// paint bug
		grid.drawObject(bug[0], bug[1], NamedSymbol.bug3, bugColor);
		
		// paint current location
		grid.drawObject(loc[0], loc[1], NamedSymbol.man, NamedColor.white);
	}
	
	public void win() {
		// paint winner!
		grid.drawObject(0,0, NamedSymbol.man);
		grid.drawObject(0,1, NamedSymbol.W);
		grid.drawObject(0,2, NamedSymbol.i);
		grid.drawObject(0,3, NamedSymbol.n);
		grid.drawObject(0,4, NamedSymbol.n);
		grid.drawObject(0,5, NamedSymbol.e);
		grid.drawObject(0,6, NamedSymbol.r);
		grid.drawObject(0,7, NamedSymbol.man);
	}
	
	public void paintScore(int score) {
		// paint score
		grid.drawObject(0,0, NamedSymbol.S);
		grid.drawObject(0,1, NamedSymbol.c);
		grid.drawObject(0,2, NamedSymbol.o);
		grid.drawObject(0,3, NamedSymbol.r);
		grid.drawObject(0,4, NamedSymbol.e);
		grid.drawObject(0,6, NamedSymbol.values()[score + 53]);		
	}

	public void GameLoop() {
		if(score >= 10) System.exit(0);
		handlebug();
		handleInput();
		paintScreen();
	}
	
   public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = randomizer.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

	public static void main(String args[]) {
		
		// Initialize our nonblocking game
		BUGSTOMP mg = new BUGSTOMP(12, "username", "apikey");
		mg.setTitle("BUG STOMP");
		mg.setDescription("Use the arrow keys to move the person over the bugs - don't let them escape!");
		
		// start running the game
		mg.start();
	}

}
