/* Import in built java packages */
import java.io.*;
import java.util.ArrayList;

public class waterFlow
{
	/* Fields */
	static protected int testcaseCount;
	static protected ArrayList<testCase> testcases = new ArrayList<testCase>();

	/* Constructor */
	public waterFlow()
	{
		testcaseCount = 0;
	}

	/* Methods */

    public static void main(String[] args)
	{
		fileParser fp = new fileParser();

		// Parse Input file and read test cases
		for(int i=0;i<args.length;i++)
		{
			if (args[i].compareTo("-i") == 0)
			{
				fp = new fileParser(args[i+1]);
				break;
			}
		}
		fp.parseInputFile();

		// Making sure that outpu file is newly created everytime
		File fo = new File("output.txt");
		fo.delete();

		// Run each test case specified in the input file
		for(int i=0;i<testcaseCount;i++)
		{
			boolean rc = false;
			testCase tc = testcases.get(i);
			//System.out.println("Printing Test Case information");
			//System.out.println("++++++++++++++++++++++++++++++");
			//System.out.println(tc);

			// Construct Tree
			tc.constructTree();

			//System.out.println("Printing Tree Generated for testcase");
			//System.out.println("++++++++++++++++++++++++++++++++++++");
			//tc.printTree(tc.getSourceNode());

			// Apply search Algo as tree is constructed
			//System.out.println("Doing task that you assigned to me");
			//System.out.println("++++++++++++++++++++++++++++++++++++");
			rc = tc.applySearchAlgo();

			// update the output file based on the output
			if (rc)
				fp.updateOutputFile(tc.returnTestcaseOutput());
			else
				fp.updateOutputFile("None");

			if (i != testcaseCount -1)
				fp.updateOutputFile("\n");
		}
    }
}