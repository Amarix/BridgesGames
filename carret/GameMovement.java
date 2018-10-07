import babybridges.game.NGCKGame;
import bridges.base.NamedColor;

class GameMovement extends NGCKGame {
    int locx;
    int locy;

    int c;
    

    java.util.Random  randomizer;



    int currentframe;
    
    public GameMovement (int assid, String login, String apiKey) {
	super(assid, login, apiKey);

	c=1;

	randomizer = new java.util.Random();
    }
    
    public void initialize(){
	locx = 10;
	locy = 10;
	currentframe=0;
    }
    
    private void changeColor() {
	
	c=randomizer.nextInt();
	
	if (c < 0) {c = -c;}
	c = c%127;
    }

    public void GameLoop(){
	currentframe++;
	if (currentframe == 2) {
	    currentframe = 0;
	    //ih.status();
	    
	    //update input
	    if (KeyUp())
		locy--;
	    if (KeyDown())
		locy++;	
	    if (KeyLeft())
		locx--;
	    if (KeyRight())
		locx++;

	    if (KeyButton1())
		changeColor();
	    
	    if (locx<0 ) locx=0;
	    if (locx>29 ) locx=29;
	    
	    if (locy<0 ) locy=0;
	    if (locy>29 ) locy=29;
	}

	System.out.println("carret: "+locx +" "+ locy);
	
	//paint black
	for (int i=0; i<30; ++i) {
	    for (int j=0; j<30; ++j) {
		SetBGColor(i, j, NamedColor.black);
	    }
	}	
	
	//paint carret
	SetBGColor(locx, locy, NamedColor.values()[c]);
    }

    public static void main (String args[]) {
	GameMovement mg = new GameMovement (1, "esaule-interactive", "1239999531573");
	
	// start running the game
	mg.start();

    }
}
