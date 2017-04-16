/* Import in built java packages */
import java.io.*;
import java.util.ArrayList;


public class inference
{
	/* Fields */

	/* Constructor */
	public inference()
	{
		// Nothing here
	}

    public static void main(String[] args)
	{
		// Game board
		inferenceEngine ie = new inferenceEngine();

		// Parse Input file and read test cases
		for(int i=0;i<args.length;i++)
		{
			if (args[i].compareTo("-i") == 0)
			{
				ie.parseInputFile(args[i+1]);
				break;
			}
		}
		// Hardcoding just for my debugging
		ie.parseInputFile("input.txt");
    }
}