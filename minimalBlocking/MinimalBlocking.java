import babybridges.game.BlockingGame;

import bridges.base.GameGrid;
import bridges.base.NamedColor;

class MinimalBlocking {

    public static void main (String args[]) {

	BlockingGame bg = new BlockingGame(0, "esaule-interactive", "1239999531573",
					   10, 10);

	// try {
	//     Thread.sleep(30*1000);
	// } catch (InterruptedException ie) {
	// }

	GameGrid gg = bg.getGameGrid();

	int car_x = 5, car_y = 5;
	
	for (int i=0; i<10; ++i)
	    for (int j=0; j<10; ++j)
		gg.setBGColor(i,j, NamedColor.black);

	gg.setBGColor(car_y, car_x, NamedColor.white);
	
	
	bg.render();

	boolean cont = true;
	while (cont) {
	    String k = bg.getKeyPress();
	    System.out.println (k);

	    if (k.equals("q"))
		cont = false;

	    if (k.equals("ArrowUp"))
		car_y--;

	    if (k.equals("ArrowDown"))
		car_y++;

	    if (k.equals("ArrowLeft"))
		car_x--;

	    if (k.equals("ArrowRight"))
		car_x++;
	    
	    
	    for (int i=0; i<10; ++i)
		for (int j=0; j<10; ++j)
		    gg.setBGColor(i,j, NamedColor.black);

	    if (car_x >=0 && car_x<10 && car_y>=0 && car_y<10)
		gg.setBGColor(car_y, car_x, NamedColor.white);
	
	    
	    bg.render();
	}

	System.out.println("quitting");
	
    }
}
