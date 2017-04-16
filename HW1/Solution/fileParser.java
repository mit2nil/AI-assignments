/* Import in built java packages */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class fileParser
{
	/* Fields */
	private String inputFile;
	private String outputFile;

	/* constructor */

	public fileParser()
	{
		inputFile = "sampleInput.txt";
		outputFile = "output.txt";
	}

	public fileParser(String fileName)
	{
		inputFile = fileName;
		outputFile = "output.txt";
	}

	/* Methods */

	// Update output file with single line of output
	protected boolean updateOutputFile(String output)
	{
		try
		{
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile,true));
            bw.append(output);
            bw.close();
        }
        catch(FileNotFoundException ex)
		{
            //System.out.println("Unable to open output file '" +outputFile + "'");
            return false;
        }
        catch(IOException ex)
		{
            //System.out.println("IOException occured.\n");
            StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
            //System.out.println(sw.toString()+"\n");
            return false;
        }
        return true;
	}

	// Read single test case from input file
	protected boolean parseInputFile()
	{
		try
		{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String ln = br.readLine();

            // Number of test cases
            if (ln != "\n" || ln != null)
            {
            	waterFlow.testcaseCount = Integer.parseInt(ln);
			}

			// Read each test case parameters
			for (int i=0;i<waterFlow.testcaseCount;i++)
			{
				testCase tc = new testCase();
				// Task Name
				ln = br.readLine();
				tc.setTaskName(ln);

				// Source Node
				ln = br.readLine();
				tc.setSourceNode(new Node(ln));

				// Destination Nodes
				ln = br.readLine();
				String[] tokens = ln.split(" ");
				ArrayList<Node> d_nds = new ArrayList<Node>();
				for(String node: tokens)
					d_nds.add(new Node(node));
				tc.setDestinationNodes(d_nds);

				// Middle Nodes
				ln = br.readLine();
				tokens = ln.split(" ");
				ArrayList<Node> m_nds = new ArrayList<Node>();
				for(String node: tokens)
					m_nds.add(new Node(node));
				tc.setMiddleNodes(m_nds);

				// Pipe Count
				ln = br.readLine();
				tc.setPipeCount(Integer.parseInt(ln));

				// Pipes
				ArrayList<Pipe> pc = new ArrayList<Pipe>();
				for(int j=0;j<tc.getPipeCount();j++)
				{
					Pipe tempPipe = new Pipe();
					ln = br.readLine();
					tokens = ln.split(" ");
					tempPipe.startNode = tokens[0];
					tempPipe.endNode = tokens[1];
					tempPipe.pipeLenth = Integer.parseInt(tokens[2]);
					tempPipe.pipeOfftimeCount = Integer.parseInt(tokens[3]);
					for(int k=0;k<tempPipe.pipeOfftimeCount;k++)
					{
						String[] tkns = tokens[3+k+1].split("-");
						tempPipe.pipeOfftimeStart.add(Integer.parseInt(tkns[0]));
						tempPipe.pipeOfftimeStop.add(Integer.parseInt(tkns[1]));
					}
					pc.add(tempPipe);
				}
				// Sort the pipe so that it is convinient to construct the tree
				Collections.sort(pc);
				tc.setPipes(pc);

				// test case start time
				ln = br.readLine();
				tc.setStartTime(Integer.parseInt(ln));

				// End of loop - read one test case inputs
				waterFlow.testcases.add(tc);
				br.readLine();// test case seperator
			}
            br.close();
        }
        catch(FileNotFoundException ex)
		{
            //System.out.println("Unable to open input file '" +inputFile + "'");
            return false;
        }
        catch(IOException ex)
		{
            //System.out.println("IOException occured.\n");
            StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
            //System.out.println(sw.toString()+"\n");
            return false;
        }
        return true;
	}
}