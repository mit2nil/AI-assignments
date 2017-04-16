/* Import in built java packages */
import java.io.*;
import java.util.ArrayList;

/* Rules
 * 1. Place stones in counter clock wise
 * 2. Bonus turn if last stone ends up inside Mancala
 * 3. If last stone on my side empty pit, then put that stone+opposition stones inside my Macala
 * 4. If all of my pits become empty, all stones go to opponent

 * End game condition: You should end the game whenever one of the side of the board is empty.
 * Stone ending up in empty pit on your side: You should put that stone in your mancala even if the opposite pit is empty.
*/

public class mancala
{
	/* Fields */

	/* Constructor */
	public mancala()
	{
		// Nothing here
	}

    public static void main(String[] args)
	{
		// Game board
		gameInstance game = new gameInstance();

		// Parse Input file and read test cases
		for(int i=0;i<args.length;i++)
		{
			if (args[i].compareTo("-i") == 0)
			{
				game.parseInputFile(args[i+1]);
				break;
			}
		}
		// Hardcoding just for my debugging
		game.parseInputFile("input_1.txt");
    }
}