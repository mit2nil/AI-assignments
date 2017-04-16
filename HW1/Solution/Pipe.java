/* Import in built java packages */
import java.util.ArrayList;

public class Pipe implements Comparable<Pipe>
{
	/* Fields */
	protected boolean isAdded;
	protected String startNode;
	protected String endNode;
	protected int pipeLenth;
	protected int pipeOfftimeCount;
	protected ArrayList<Integer> pipeOfftimeStart = new ArrayList<Integer>();
	protected ArrayList<Integer> pipeOfftimeStop = new ArrayList<Integer>();

	/* constructor */
	public Pipe()
	{
		isAdded = false;
		startNode = null;
		endNode = null;
		pipeLenth = 0;
		pipeOfftimeCount = 0;
	}

	public Pipe(String start, String end, int length, int pipOfftimeCount, ArrayList<Integer> offtimeStart, ArrayList<Integer> offtimeStop)
	{
		isAdded = false;
		startNode = start;
		endNode = end;
		pipeLenth = length;
		pipeOfftimeCount = pipOfftimeCount;
		pipeOfftimeStart = offtimeStart;
		pipeOfftimeStop = offtimeStop;
	}

	/* Methods */

	public int compareTo(Pipe p)
    {
    	int returnValue = 0;
    	if ((returnValue = this.startNode.compareTo(p.startNode)) != 0)
    	{
    		return returnValue;
		}
		else
		{
			return this.endNode.compareTo(p.endNode);
		}
	}

	public String toString()
	{
		String output = "";
		output = output+"[Start-End] [Length] [OfflineCount] [OfflineDuration-1, ... ]\n";
		output = output+"["+startNode+"-"+endNode+"] ["+pipeLenth+"] ["+pipeOfftimeCount+"]";
		for(int i=0;i<pipeOfftimeCount;i++)
		{
			Integer val1 = pipeOfftimeStart.get(i);
			Integer val2 = pipeOfftimeStop.get(i);
			output = output+" ["+val1+"-"+val2+"]";
		}
		return output+"\n";
	}
}