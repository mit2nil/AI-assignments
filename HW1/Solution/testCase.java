/* Import in built java packages */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;


public class testCase
{
	/* Fields */

	// Input fields
	private String task;
	private Node sourceNode;
	private ArrayList<Node> destinationNodes = new ArrayList<Node>();
	private ArrayList<Node> middleNodes = new ArrayList<Node>();
	private int pipeCount;
	private ArrayList<Pipe> pipes = new ArrayList<Pipe>();
	private int startTime;

	// Output fields
	private String outputNode;
	private int outputTime;

	/* constructor */

	public testCase()
	{
		task = null;
		sourceNode = null;
		pipeCount = 0;
		startTime = 0;
		outputNode = " ";
	}

	public testCase(testCase tc)
	{
		// Initialize input/output params
		task = tc.task;
		sourceNode = tc.sourceNode;
		destinationNodes = tc.destinationNodes;
		middleNodes = tc.middleNodes;
		pipeCount = tc.pipeCount;
		pipes = tc.pipes;
		startTime = tc.startTime;

		outputNode = " ";
		outputTime = startTime;
	}

	/* Methods */
	protected void setTaskName(String name)
	{
		task = name;
	}

	protected void setSourceNode(Node n)
	{
		sourceNode = n;
	}

	protected Node getSourceNode()
	{
		return sourceNode;
	}

	protected void setDestinationNodes(ArrayList<Node> n)
	{
		destinationNodes = n;
	}

	protected void setMiddleNodes(ArrayList<Node> n)
	{
		middleNodes = n;
	}

	protected void setPipes(ArrayList<Pipe> p)
	{
		pipes = p;
	}

	protected void setPipeCount(int pc)
	{
		pipeCount = pc;
	}

	protected int getPipeCount()
	{
		return pipeCount;
	}

	protected void setStartTime(int t)
	{
		startTime = t;
	}

	protected String returnTestcaseOutput()
	{
		String output = "";
		outputTime = outputTime%24;
		output = output+outputNode+" "+outputTime;
		//System.out.println("Reached destination "+output);
		return output;
	}

	protected void clearReferenceFlags(Node n)
	{
		n.isReferenced = false;
		for(int i=0;i<n.children.size();i++)
			if (n.children.get(i).isReferenced)
				clearReferenceFlags(n.children.get(i));
	}

	protected boolean addTreeNode(Pipe p)
	{
		boolean rc = true;
		Node newNode = findTreeNodeReference(sourceNode,p.endNode);
		clearReferenceFlags(sourceNode);

		if (newNode == null)
			newNode = new Node(p.endNode);

		// Case where pipes are going out of source node
		if (sourceNode.getNodeName().compareTo(p.startNode) == 0)
		{
			sourceNode.connections.add(p);
			newNode.parents.add(sourceNode);
			sourceNode.children.add(newNode);
		}
		else	// All other cases
		{
			// Search in the existing tree
			Node tempNode = findTreeNodeReference(sourceNode,p.startNode);
			clearReferenceFlags(sourceNode);

			if (tempNode != null)
			{
				// Special case where pipes are coming in to source node
				if (sourceNode.getNodeName().compareTo(p.endNode) == 0)
				{
					tempNode.connections.add(p);
					sourceNode.parents.add(tempNode);
					tempNode.children.add(sourceNode);
				}
				else
				{
					tempNode.connections.add(p);
					newNode.parents.add(tempNode);
					tempNode.children.add(newNode);
				}
			}
			else
			{
				// This pipe is dangling. Its start point not available in the tree
				// Let's push it aside temporarily and come back to add after some time
				rc = false;
			}
		}
		return rc;
	}

	protected Node findTreeNodeReference(Node root, String name)
	{

		Node returnNode = null;

		// First search among first level
		for(int i=0;i<root.children.size();i++)
		{
			if (root.children.get(i).getNodeName().compareTo(name) == 0)
			{
				returnNode = root.children.get(i);
				break;
			}
		}

		// Search among second level chilren and so on (recursion)
		if (returnNode == null)
		{
			for(int i=0;i<root.children.size();i++)
			{
				if (!root.children.get(i).isReferenced)
				{
					root.children.get(i).isReferenced = true;
					returnNode = findTreeNodeReference(root.children.get(i),name);
					if ( returnNode != null)
					{
						break;
					}
				}
			}
		}
		return returnNode;
	}

	protected void constructTree()
	{
		boolean rc;

		while(!allPipesAdded())
		{
			boolean noNewAdded = true;
			for(int i=0;i<pipeCount;i++)
			{
				// Add only if it is not already added
				if (!pipes.get(i).isAdded)
				{
					rc = addTreeNode(pipes.get(i));
					// Set added flag as true so that it can be tracked
					if (rc)
					{
						noNewAdded = false;
						pipes.get(i).isAdded = true;
					}
				}
			}

			// Time to go out. Remaining pipes can be abondoned
			if (noNewAdded)
				break;
		}
	}

	private boolean allPipesAdded()
	{
		for(int i=0;i<pipeCount;i++)
		{
			if (!pipes.get(i).isAdded)
				return false;
		}
		return true;
	}

	protected void printTree(Node n)
	{
		if (!n.isPrinted)
		{
			n.isPrinted = true;
			// Print Node and its connections aka pipes (only for root)
			if (n.compareTo(sourceNode) == 0)
				//System.out.println(n);

			// Print all children aka pipe end nodes
			for(int i=0;i<n.children.size();i++)
			{
				//System.out.println("Child["+i+"] of "+n.getNodeName()+" : "+n.children.get(i));
			}

			// Recursively call it for all child if exist
			////System.out.println("Now printing children of "+n.getNodeName()+" (Total: "+n.children.size()+"\n");
			for(int i=0;i<n.children.size();i++)
			{
				printTree(n.children.get(i));
			}
		}
	}

	protected boolean applySearchAlgo()
	{
		boolean rc = true;
		if (task.compareTo("BFS") == 0)
		{
			rc = applyBFS();
		}
		else if (task.compareTo("DFS") == 0)
		{
			rc = applyDFS();
		}
		else if (task.compareTo("UCS") == 0)
		{
			rc = applyUCS();
		}
		else
		{
			// Invalid task
			rc = false;
		}
		return rc;
	}

	private boolean applyBFS()
	{
		// Setup of the algorithm
		LinkedBlockingQueue<Node> exploredSet = new LinkedBlockingQueue<Node>(destinationNodes.size()+middleNodes.size()+1);
		LinkedBlockingQueue<Node> frontierSet = new LinkedBlockingQueue<Node>(destinationNodes.size()+middleNodes.size()+1);
		sourceNode.cost = startTime;
		exploredSet.add(sourceNode);

		// Special case for sourceNode
		for(int i=0;i<sourceNode.children.size();i++)
		{
			Node currentNode = sourceNode.children.get(i);
			currentNode.theParent = sourceNode;
			currentNode.cost = sourceNode.cost + 1;

			if (applyGoalTest(currentNode.getNodeName()))
			{
				// Found one of the destination(s), :)
				outputTime = currentNode.cost;
				outputNode = currentNode.getNodeName();
				return true;
			}
			frontierSet.add(currentNode);
		}

		// Proceed with the rest of the cases in similar manner
		while(true)
		{
			if (frontierSet.isEmpty())
				break;

			Node n = frontierSet.poll();
			exploredSet.add(n);
			for(int i=0;i<n.children.size();i++)
			{
				Node currentNode = n.children.get(i);
				if (! (frontierSet.contains(currentNode) || exploredSet.contains(currentNode)) )
				{
					currentNode.theParent = n;
					currentNode.cost = n.cost + 1;

					if (applyGoalTest(currentNode.getNodeName()))
					{
						// Found one of the destination(s), :)
						outputTime = currentNode.cost;
						outputNode = currentNode.getNodeName();
						return true;
					}
					frontierSet.add(currentNode);
				}
				else
				{
					// Redundant for BFS
				}
			}
		}
		//System.out.println("Could not reach any destination");
		return false;
	}

	private boolean applyDFS()
	{
		// Setup of the algorithm
		Stack<Node> exploredSet = new Stack<Node>();
		Stack<Node> frontierSet = new Stack<Node>();
		sourceNode.cost = startTime;
		exploredSet.push(sourceNode);

		// Special case for sourceNode
		for(int i=sourceNode.children.size()-1;i>=0;i--)
		{
			Node currentNode = sourceNode.children.get(i);
			currentNode.theParent = sourceNode;
			currentNode.cost = sourceNode.cost + 1;
			frontierSet.push(currentNode);
		}

		// Proceed with the rest of the cases in similar manner
		while(true)
		{
			if (frontierSet.isEmpty())
				break;

			Node n = frontierSet.pop();
			if (exploredSet.contains(n))
				continue;

			if (applyGoalTest(n.getNodeName()))
			{
				// Found one of the destination(s), :)
				outputTime = n.cost;
				outputNode = n.getNodeName();
				return true;
			}

			exploredSet.push(n);
			for(int i=n.children.size()-1;i>=0;i--)
			{
				Node currentNode = n.children.get(i);
				//if (! (frontierSet.contains(currentNode) || (exploredSet.contains(currentNode))))
				if (!(exploredSet.contains(currentNode)))
				{
					currentNode.theParent = n;
					currentNode.cost = n.cost + 1;
					frontierSet.push(currentNode);
				}
			}
		}
		//System.out.println("Could not reach any destination");
		return false;
	}

	private boolean applyUCS()
	{
		// Setup of the algorithm
		boolean reachedDestination = false;
		Comparator<Node> cmp = new Comparator<Node>() {
			public int compare(Node a,Node b)
			{
				if (a.cost > b.cost)
					return 1;
				else if (a.cost == b.cost)
					return a.getNodeName().compareTo(b.getNodeName());
				else
					return -1;
			}
		};
		ArrayList<Node> exploredSet = new ArrayList<Node>(destinationNodes.size()+middleNodes.size()+1);
		ArrayList<Node> frontierSet = new ArrayList<Node>(destinationNodes.size()+middleNodes.size()+1);
		sourceNode.cost = startTime;
		frontierSet.add(sourceNode);

		for(int i=0;;i++)
		{
			if (frontierSet.isEmpty())
				break;

			// Choose first node from priority Queue (frontier)
			Node n = frontierSet.remove(0);
			//System.out.println("Removed node from frontier "+n.getNodeName());

			// These steps do not apply to sourceNode
			if (n.compareTo(sourceNode) != 0)
			{
				if (applyGoalTest(n.getNodeName()))
				{
					// Found one of the destination(s), :)
					outputTime = n.cost;
					outputNode = n.getNodeName();
					//System.out.println("Found destination "+outputNode+" at "+outputTime);
					reachedDestination = true;
					break;// UCS is complete
				}
				else
				{
					//System.out.println("Goal test is not satisfied");
				}
			}

			// Generate child nodes and add them to frontier as per increasing cost.
			// Collections.sort(n.children, cmp);
			//System.out.println("Adding children to frontiers");
			for(int k=0;k<n.children.size();k++)
			{
				// Get ith Child
				Node child = n.children.get(k);
				//System.out.println("child under consideration is "+child.getNodeName());

				// Check if child already present in frontier or explored
				if (! (frontierSet.contains(child) || exploredSet.contains(child) ))
				{
					// In case pipe is inactive, roll back
					int backupLevel = child.cost;
					Node backupParent = child.theParent;

					child.theParent = n;
					// Calculate current node's cost and update its cost value
					for(int j=0;j<child.parents.size();j++)
					{
						if (child.parents.get(j).getNodeName().compareTo(n.getNodeName()) == 0)
						{
							//System.out.println("Path calculated: parent cost "+child.parents.get(j).cost+" Start Time: "+startTime);
							child.cost = child.parents.get(j).cost;
							break;
						}
						else if (j == child.parents.size()-1)
						{
							//System.out.println("Something weird going on");
						}
					}

					// Check if pipe is active
					Pipe currentPipe = isPipeActive(child.theParent.getNodeName(),child.getNodeName(),child.cost);
					if (currentPipe == null)// Pipe not active
					{
						//System.out.println("Pipe not active or does not exist");
						child.theParent = backupParent;
						child.cost = backupLevel;
						continue;
					}

					child.cost = child.cost + currentPipe.pipeLenth;
					//System.out.println("Adding new node to frontier "+child);
					frontierSet.add(child);
				}
				else if (frontierSet.contains(child))
				{
					//System.out.println("Frontier contains "+child.getNodeName());
					int index = frontierSet.indexOf(child);
					int nodeLevel = child.cost;

					// Backup in case update is not required
					Node backupParent = child.theParent;
					int backupLevel = child.cost;

					//System.out.println("Level1 - Level2 "+child.cost+" "+nodeLevel );

					// Update child
					child.theParent = n;
					child.cost = child.theParent.cost;

					// Check if pipe is active
					Pipe currentPipe = isPipeActive(child.theParent.getNodeName(),child.getNodeName(),child.cost);
					if (currentPipe == null)// Pipe not active
					{
						//System.out.println("Pipe not active or does not exist");
						child.theParent = backupParent;
						child.cost = backupLevel;
						continue;
					}
					child.cost = child.cost + currentPipe.pipeLenth;

					//System.out.println("Level1 - Level2 "+child.cost+" "+nodeLevel );
					if (child.cost < nodeLevel)
					{
						//System.out.println("Updating node to frontier "+child);
						frontierSet.set(index,child);
					}
					else
					{
						// All customization are wasted. Fall back
						//System.out.println("Did not update node in frontier ");
						child.theParent = backupParent;
						child.cost = backupLevel;
						//System.out.println("Node "+child.getNodeName()+" has cost "+child.cost);
					}
				}
				else if (exploredSet.contains(child))
				{
					//System.out.println("Explored contains "+child.getNodeName());
					int index = exploredSet.indexOf(child);
					int nodeLevel = child.cost;

					// Backup in case update is not required
					Node backupParent = child.theParent;
					int backupLevel = child.cost;

					//System.out.println("Level1 - Level2 "+child.cost+" "+nodeLevel );

					// Update child
					child.theParent = n;
					child.cost = child.theParent.cost;

					// Check if pipe is active
					Pipe currentPipe = isPipeActive(child.theParent.getNodeName(),child.getNodeName(),child.cost);
					if (currentPipe == null)// Pipe not active
					{
						//System.out.println("Pipe not active or does not exist");
						child.theParent = backupParent;
						child.cost = backupLevel;
						continue;
					}
					child.cost = child.cost + currentPipe.pipeLenth;

					//System.out.println("Level1 - Level2 "+child.cost+" "+nodeLevel );
					if (child.cost < nodeLevel)
					{
						//System.out.println("Removing node to from explored and putting into frontier "+child);
						exploredSet.remove(index);
						frontierSet.add(child);
					}
					else
					{
						// All customization are wasted. Fall back
						//System.out.println("Did not replace node in frontier from explored ");
						child.theParent = backupParent;
						child.cost = backupLevel;
						//System.out.println("Node "+child.getNodeName()+" has cost "+child.cost);
					}
				}
			}

			// Add node(with current state held in node class to explored set
			exploredSet.add(n);
			Collections.sort(frontierSet, cmp);
		}
		return reachedDestination;
	}

	private boolean applyGoalTest(String goal)
	{
		for(int i=0;i<destinationNodes.size();i++)
			if (destinationNodes.get(i).getNodeName().compareTo(goal) == 0)
				return true;
		return false;
	}

	private Pipe isPipeActive(String p_s, String p_e, int time)
	{
		Pipe p = new Pipe();

		//System.out.println("Checking pipe availibility");
		// Set time limit
		if (time > 23)
			time = time%24;

		//System.out.println("Current time is "+time+"");

		// Find pipe
		for(int i=0;i<pipes.size();i++)
		{
			if (pipes.get(i).startNode.compareTo(p_s) == 0)
				if (pipes.get(i).endNode.compareTo(p_e) == 0)
				{
					p = pipes.get(i);
					break;
				}

			if (i == pipes.size()-1)
				return null;
		}
		//System.out.println("Found pipe: "+p);

		// Check off times for the selected pipe
		for(int i=0;i<p.pipeOfftimeCount;i++)
			if (p.pipeOfftimeStart.get(i).intValue() <= time && time <= p.pipeOfftimeStop.get(i).intValue())
				return null;

		return p;
	}

	public String toString()
	{
		String output = "";
		output = output+"Task: "+task+" \n";
		output = output+"Source Node: "+sourceNode+" \n";

		for(int i=0;i<destinationNodes.size();i++)
		{
			Node val = destinationNodes.get(i);
			output = output+"Destination Node["+i+"]: "+val;
		}
		output = output+" \n";

		for(int i=0;i<middleNodes.size();i++)
		{
			Node val = middleNodes.get(i);
			output = output+"Middle Node["+i+"]: "+val;
		}
		output = output+" \n";

		output = output+"Pipe Count: "+pipeCount+" \n";
		for(int i=0;i<pipes.size();i++)
		{
			Pipe val = pipes.get(i);
			output = output+"Pipe["+i+"]: "+val;
		}
		output = output+"Start Time: "+startTime+" \n";
		return output;
	}
}