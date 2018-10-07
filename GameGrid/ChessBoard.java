
import java.security.SecureRandom;

import bridges.base.*;
import bridges.connect.Bridges;

public class ChessBoard {
	
	public static final SecureRandom random = new SecureRandom();
		
	public static void main(String[] args) throws Exception{
		Bridges bridges = new Bridges(0, "testy", "1144791091404");
		bridges.setServer("local");
		bridges.setTitle("Game Grid Chessboard");
		bridges.setDescription("Checkmate");
		
		int gridSize[] = {8,8};
		GameGrid chessboard = new GameGrid(gridSize[0], gridSize[1]);

		NamedColor color = NamedColor.black;
		int piece = 0;
		
		// draw chess board
		for(int i = 0; i < gridSize[0]; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				if(j % 2 == 1 && i % 2 == 0)  color = NamedColor.sienna;
				else if(j % 2 == 0 && i % 2 == 1)  color = NamedColor.sienna;
				else color = NamedColor.tan;
				chessboard.setBGColor(i, j, color);
			}
		}
		
		// draw black pieces
		color = NamedColor.black;
		piece = 0;
		for(int i = 0; i < 2; i++) {	// rows
			for(int j = 0; j < 8; j++) {	// cols
				if(i == 1) {
					piece = 74;
				} else {
					switch(j) {
						case 0:
						case 7:
							piece = 77;
							break;
						case 1:
						case 6:
							piece = 75;
							break;
						case 2: 
						case 5: 
							piece = 76;
							break;
						case 3: 
							piece = 78;
							break;
						case 4: 
							piece = 79;
							break;
					}
				}
				chessboard.drawObject(i, j, piece, color);
			}
		}
		
		// draw white pieces
		color = NamedColor.white;
		piece = 0;
		for(int i = 6; i < 8; i++) {	// rows
			for(int j = 0; j < gridSize[1]; j++) {	// cols
				if(i == 6) {
					piece = 74;
				} else {
					switch(j) {
						case 0:
						case 7:
							piece = 77;
							break;
						case 1:
						case 6:
							piece = 75;
							break;
						case 2: 
						case 5: 
							piece = 76;
							break;
						case 3: 
							piece = 78;
							break;
						case 4: 
							piece = 79;
							break;
					}
				}
				chessboard.drawObject(i, j, piece, color);
			}
		}
		
		System.out.println(chessboard.getDataStructureRepresentation());
		
		
		
		bridges.setDataStructure(chessboard);
		bridges.setVisualizeJSON(true);
		bridges.visualize();
	}
	
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        System.out.println(x);
        return clazz.getEnumConstants()[x];
    }


}
