
class MyGame extends NGCKGame {
    int locx; //location of the car
    int locy;
    
    java.util.Random  randomizer;

    int car_r, car_g, car_b;
    int wall_r, wall_g, wall_b;

    int input_currentframe;

    int leftwall[];
    int rightwall[];
    int roadwidth;

    int shrinkframe; //current frame in the shrink cycle
    int shrinkevery; //when to shrink
    
    public MyGame (int assid, String login, String apiKey) {
	super(assid, login, apiKey);

	randomizer = new java.util.Random();
    }
    
    public void initialize(){
	
	
	locx = 15;
	locy = 28;

	car_r = 127;
	car_g = 127;
	car_b = 127;

	wall_r = 255;
	wall_g = 255;
	wall_b = 255;
	
	input_currentframe = 0;

	leftwall = new int[30];
	rightwall = new int[30];

	roadwidth = 19;
	
	for (int i=0; i<10; ++i) {
	    leftwall[i] = i;
	    rightwall[i] = leftwall[i]+roadwidth;
	}
	for (int i=10; i<20; ++i) {
	    leftwall[i] = 20-i;
	    rightwall[i] = leftwall[i]+roadwidth;
	}
	for (int i=20; i<30; ++i) {
	    leftwall[i] = i-20;
	    rightwall[i] = leftwall[i]+roadwidth;
	}

	shrinkevery = 30*10; //10 seconds
	shrinkframe = 0;
    }

    public void handleInput() {
	input_currentframe++;
	if (input_currentframe == 1) { //serves to take input only every x frame. here every one frame.
	    input_currentframe = 0;
	    
	    //update input
	    if (KeyLeft())
		locx--;
	    if (KeyRight())
		locx++;

	    if (locx<0 ) locx=0;
	    if (locx>29 ) locx=29;
	}
    }


    public void die() {
	car_r = 0;
	car_g = 0;
	car_b = 0;

    }
    
    public void checkCollision() {
	if (leftwall[28] == locx)
	    die();
	if (rightwall[28] == locx)
	    die();
    }

    public void paintScreen() {
	//paint black
	for (int i=0; i<30; ++i) {
	    for (int j=0; j<30; ++j) {
		SetColor(i, j, 0, 0, 0);
	    }
	}	
	
	//paint car
	SetColor(locx, locy, car_r, car_g, car_b);

	//render walls

	for (int i=0; i< 30; ++i) {
	    SetColor(leftwall[i], i, wall_r, wall_g, wall_b);
	    SetColor(rightwall[i], i, wall_r, wall_g, wall_b);
	}
    }

    private void moveRoad() {
	//move road by one
	for (int i=29; i>0; --i) {
	    leftwall[i] = leftwall[i-1];
	    rightwall[i] = rightwall[i-1];
	}
	

	//generate new road


	//is it a shrink frame?
	shrinkframe++;
	if (shrinkframe == shrinkevery) {
	    shrinkframe = 0;

	    int ra = randomizer.nextInt();
	    roadwidth --;
	    if (ra < 0) {
		//shrink left
		leftwall[0]++;
	    }
	    else {
		rightwall[0]--;
	    }
	    
	}
	else {
	    //not a shrink frame.
	    //generate next bit of road randomly

	    int ra = randomizer.nextInt();

	    if (ra < 0)
		leftwall[0]--;
	    else if (ra > 0)
		leftwall[0]++;
	    
	    if (leftwall[0] < 0)
		leftwall[0] = 0;
	    
	    
	    if (leftwall[0] > 29 - roadwidth)
		leftwall[0] = 29-roadwidth;
	    
	    rightwall[0] = leftwall[0]+roadwidth;
	}
    }	
	
    public void GameLoop(){
	handleInput();

	checkCollision();

	moveRoad();
	
	checkCollision();
	
	paintScreen();
    }

}
