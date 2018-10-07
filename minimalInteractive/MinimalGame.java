import babybridges.game.NGCKGame;

class MinimalGame extends NGCKGame {
    
    public MinimalGame (int assid, String login, String apiKey) {
	super(assid, login, apiKey);
    }
    
    public void initialize(){
    }
    
    public void GameLoop(){
    }

    public static void main (String args[]){
	MinimalGame mg = new MinimalGame (1, "esaule-interactive", "1239999531573");
		
	// start running the game
	mg.start();
    }
    
}
