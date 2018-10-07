package babybridges.game;


import bridges.connect.Bridges;
import bridges.base.GameGrid;
import babybridges.connect.KeypressListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Queue;
import java.util.ArrayDeque;

import babybridges.connect.SocketConnection;


public class BlockingGame implements KeypressListener {

    boolean firsttime;
    Bridges bridges;
    GameGrid gg;
    SocketConnection sock;

    Queue<String> keyqueue;

    public void keypress(JSONObject keypress) {
	String type = "";
	String key = "";
	
	// get keypress details
	try {
	    type = (String) keypress.get("type");
	    key = (String) keypress.get("key");
	    
	    //System.out.println(type + ": " + key);
	    
	} catch (JSONException e) {
	    e.printStackTrace();
	}

	if (type.equals("keydown") ) {
	    synchronized(keyqueue) {
		//System.err.println(type+" "+key);
		keyqueue.add(key);
		keyqueue.notify();
	    }
	}

	
    }
    
    public String getKeyPress() {

	String ret = "";
	
	synchronized(keyqueue) {

	    try {
		keyqueue.wait();
	    } catch (InterruptedException ie) {
		
	    }
	    
	    ret = keyqueue.poll();
	}
	
	return ret;
    }

    public GameGrid getGameGrid() {
	return gg;
    }

    public BlockingGame (int assignmentID, String username, String apikey,
			 int gridsizeX, int gridsizeY) {
	firsttime = true;
	
	// bridges-sockets account (you need to make a new account: https://bridges-sockets.herokuapp.com/signup)
	bridges = new Bridges(assignmentID, username, apikey);

	// make sure the bridges connects to the socket version of the web app
	bridges.setServer("sockets");

	//is that even required?
	bridges.setTitle("Title");
	bridges.setDescription("Description");
		
	// create a new color grid with random color 
	gg = new GameGrid(gridsizeX, gridsizeY);

	keyqueue = new ArrayDeque<String>();
	
	// set up socket connection to receive and send data
	sock = new SocketConnection();
	sock.setupConnection(bridges.getUserName(),
			     bridges.getAssignment());	

	sock.addListener(this);


    }

    public void render() {
	if (firsttime) {
	    firsttime = false;
	    // associate the grid with the Bridges object
	    bridges.setDataStructure(gg);
	    
	    // visualize the grid
	    try{
		bridges.visualize();
	    } catch (Exception err) {
		System.out.println(err);
	    }
	}


	String gridState = gg.getDataStructureRepresentation();
	String gridJSON = '{' + gridState;
	//System.out.println(gridJSON);

	// send valid JSON for grid into the socket
	sock.sendData(gridJSON);
    }

    public void quit() {
	sock.close();
    }
}
