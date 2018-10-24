package Racecar;

import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
import babybridges.game.NGCKGame;

class Racecar extends NGCKGame {
	int loc_col; // location of the car
	int loc_row;

	java.util.Random randomizer;

	NamedColor wall_c, car_c, bg_c;
	NamedSymbol car_s;

	int input_currentframe;

	int leftwall[];
	int rightwall[];
	int roadwidth;

	int shrinkframe; // current frame in the shrink cycle
	int shrinkevery; // when to shrink

	public Racecar(int assid, String login, String apiKey) {
		super(assid, login, apiKey);

		randomizer = new java.util.Random();
	}

	public void initialize() {

		loc_col = 15;
		loc_row = 28;

		wall_c = NamedColor.white;
		car_c = NamedColor.gray;
		bg_c = NamedColor.black;
		
		car_s = NamedSymbol.triangle_up;

		input_currentframe = 0;

		leftwall = new int[30];
		rightwall = new int[30];

		roadwidth = 19;

		for (int i = 0; i < 10; ++i) {
			leftwall[i] = i;
			rightwall[i] = leftwall[i] + roadwidth;
		}
		for (int i = 10; i < 20; ++i) {
			leftwall[i] = 20 - i;
			rightwall[i] = leftwall[i] + roadwidth;
		}
		for (int i = 20; i < 30; ++i) {
			leftwall[i] = i - 20;
			rightwall[i] = leftwall[i] + roadwidth;
		}

		shrinkevery = 30 * 10; // 10 seconds
		shrinkframe = 0;
	}

	public void handleInput() {
		input_currentframe++;
		if (input_currentframe == 1) { // serves to take input only every x
										// frame. here every one frame.
			input_currentframe = 0;

			// update input
			if (KeyLeft())
				loc_col--;
			if (KeyRight())
				loc_col++;

			if (loc_col < 0)
				loc_col = 0;
			if (loc_col > 29)
				loc_col = 29;
		}
	}

	public void die() {
		car_c = NamedColor.black;
		System.exit(0);
	}

	public void checkCollision() {
		if (leftwall[28] == loc_col)
			die();
		if (rightwall[28] == loc_col)
			die();
	}

	public void paintScreen() {
		// paint black
		for (int row = 0; row < 30; ++row) {
			for (int col = 0; col < 30; ++col) {
				grid.setBGColor(row, col, bg_c);
				grid.drawObject(row, col, NamedSymbol.none);
			}
		}

		// paint car
		grid.drawObject(loc_row, loc_col, car_s, car_c);

		// render walls
		for (int i = 0; i < 30; ++i) {
			grid.setBGColor(i, leftwall[i], wall_c);
			grid.setBGColor(i, rightwall[i], wall_c);
		}
	}

	private void moveRoad() {
		// move road by one
		for (int i = 29; i > 0; --i) {
			leftwall[i] = leftwall[i - 1];
			rightwall[i] = rightwall[i - 1];
		}

		// generate new road

		// is it a shrink frame?
		shrinkframe++;
		if (shrinkframe == shrinkevery) {
			shrinkframe = 0;

			int ra = randomizer.nextInt();
			roadwidth--;
			if(roadwidth <= 0) die();
			
			if (ra < 0) {
				// shrink left
				leftwall[0]++;
			} else {
				rightwall[0]--;
			}
			

		} else {
			// not a shrink frame.
			// generate next bit of road randomly

			int ra = randomizer.nextInt();

			if (ra < 0)
				leftwall[0]--;
			else if (ra > 0)
				leftwall[0]++;

			if (leftwall[0] < 0)
				leftwall[0] = 0;

			if (leftwall[0] > 29 - roadwidth)
				leftwall[0] = 29 - roadwidth;

			rightwall[0] = leftwall[0] + roadwidth;
		}
	}

	public void GameLoop() {
		handleInput();

		checkCollision();

		moveRoad();

		checkCollision();

		paintScreen();
	}
	
	public static void main(String[] args) throws Exception {

		Racecar game = new Racecar(13, "username", "apikey");
		// start running the game
		game.start();
	}

}
