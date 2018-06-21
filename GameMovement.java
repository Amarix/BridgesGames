
class GameMovement extends NGCKGame {
    int locx;
    int locy;

    int r, g, b;
    

    java.util.Random  randomizer;



    int currentframe;
    
    public GameMovement (int assid, String login, String apiKey) {
	super(assid, login, apiKey);

	r = 255;
	g = 255;
	b = 255;

	randomizer = new java.util.Random();
    }
    
    public void initialize(){
	locx = 10;
	locy = 10;
	currentframe=0;
    }
    
    private void changeColor() {
	r=randomizer.nextInt();
	g=randomizer.nextInt();
	b=randomizer.nextInt();

	if (r < 0) {r = r/2; r = -r;}
	if (g < 0) {g = g/2; g = -g;}
	if (b < 0) {b = b/2; b = -b;}
	r = r%256;
	g = g%256;
	b = b%256;
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
		SetColor(i, j, 0, 0, 0);
	    }
	}	
	
	//paint carret
	SetColor(locx, locy, r, g, b);
    }

}
