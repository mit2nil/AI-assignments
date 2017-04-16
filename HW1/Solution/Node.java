/* Import in built java packages */
import java.util.ArrayList;

public class Node implements Comparable<Node>
{
	/* Fields */
	protected boolean isReferenced;// Used during find reference
	protected boolean isPrinted;// Used during tree printing
	protected int cost;
	private String name;
	protected ArrayList<Pipe> connections = new ArrayList<Pipe>();
	protected ArrayList<Node> children = new ArrayList<Node>();
	protected ArrayList<Node> parents = new ArrayList<Node>();
	protected Node theParent;

	/* default constructor */
	public Node(String nodeName)
	{
		name = nodeName;
		cost = 0;
		isReferenced = false;
		isPrinted = false;
	}

	/* Methods */

    public int compareTo(Node c)
    {
    	return this.getNodeName().compareTo(c.getNodeName());
	}

	protected String getNodeName()
	{
		return name;
	}

	public String toString()
	{
		String output = "";

		if (parents.size() != 0)
		{
			String parentList = "";
			for(int i=0;i<parents.size();i++)
				parentList += " "+parents.get(i).getNodeName();
			output = output+"Name: "+name+" Parent(s): ";
			output = output+parentList;
		}
		else
		{
			output = output+"Name: "+name+" Parent(s): None ";
		}
		output = output+", Cost: "+this.cost;
		if (theParent != null)
			output = output+", TheParent: "+this.theParent.getNodeName();
		else
			output = output+", TheParent: None";

		if (connections.size() == 0)
			output = output+" ,Connections: (0)\n";

		for(int i=0;i<connections.size();i++)
		{
			if (i==0)
				output = output+" ,Connections: ("+connections.size()+")\n";
			output = output+connections.get(i);
		}
		return output;
	}
}