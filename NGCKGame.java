

import bridges.connect.Bridges;
import bridges.base.ColorGrid;
import bridges.base.Color;


abstract class NGCKGame {
    int rows = 30;
    int cols = 30;
    ColorGrid grid;
    String gridState = "";
    Bridges bridges;
    SocketConnection sock;

    public InputHelper ih;

    
    
    public NGCKGame(int assid, String login, String apiKey) {

	// bridges-sockets account (you need to make a new account: https://bridges-sockets.herokuapp.com/signup)
	bridges = new Bridges(assid, login, apiKey);


	// make sure the bridges connects to the socket version of the web app
	bridges.setServer("sockets");

	//is that even required?
	bridges.setTitle("Title");
	bridges.setDescription("Description");
		
	// create a new color grid with random color 
	grid = new ColorGrid(rows, cols);

	// set up socket connection to receive and send data
	sock = new SocketConnection();
	sock.setupConnection(bridges.getUserName(), bridges.getAssignment());	

	ih = new InputHelper(sock);
	
    }


    public void SetColor(int x, int y, int r, int g, int b) {
	grid.set(y, x, new Color(r,g,b));
    }

    public abstract void initialize();
    
    public abstract void GameLoop();

    private void render(){
	// get the JSON representation of the updated color grid
	String gridState = grid.getDataStructureRepresentation();
	String gridJSON = '{' + gridState;
	//System.out.println(gridJSON);
	
	// send valid JSON for grid into the socket
	sock.sendData(gridJSON);
    }
    
    private void controlFrameRate(){
	int fps = 30;
	double hz = 1./fps;

	try {
	    Thread.sleep((int)(hz*1000)); //this is super crude
	}
	catch (InterruptedException ie) {
	    //die?
	}
    }
    
    public void start() {

	try {
	    Thread.sleep(5*1000); //wait for browser to connect
	}
	catch (InterruptedException ie) {
	    //die?
	}

	
	// associate the grid with the Bridges object
	bridges.setDataStructure(grid);
	
	// visualize the grid
	try{
	    bridges.visualize();
	} catch (Exception err) {
	    System.out.println(err);
	}
	
	initialize();
	while (true) {
	    GameLoop();
	    
	    controlFrameRate();
	    render();
	    //System.out.println("rendered");
	}
    }
}
