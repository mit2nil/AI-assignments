/* Import in built java packages */
import java.io.*;
import java.util.*;

public class kSentence implements Comparable<kSentence>
{
	/* Fields */
	protected boolean implication;
	protected boolean facts;
	protected qSentence conclusion;
	protected ArrayList<qSentence> premise = new ArrayList<qSentence>();
	protected String sentence;

	/* Constructor */
	public kSentence(String sc)
	{
		sentence = sc;
		String[] s = sentence.split(" => ");

		if (s.length == 2)
		{
			this.implication = true;
			String[] q = s[0].split(" \\^ ");

			// Run regex on premise part of implication
			for(int i=0;i<q.length;i++)
			{
				premise.add(new qSentence(q[i]));
			}

			// Conclusion part of implication
			conclusion = new qSentence(s[1]);
		}
		else
		{
			this.implication = false;
			conclusion = new qSentence(s[0]);
		}

		//facts = checkImpliedFacts();
		//System.out.println("Allfacts: "+facts);
	}

	private boolean checkImpliedFacts()
	{
		boolean rc = true;

		// Check all parameters on conclusion side
		for(int i=0;i<conclusion.parameters.size();i++)
		{
			if ( ('A' <= conclusion.parameters.get(i).charAt(0)) &&
				(conclusion.parameters.get(i).charAt(0) <= 'Z') )
				continue;
			rc = false;
			break;
		}

		// Check all parameters on premise side
		for(int j=0;j<premise.size();j++)
		{
			for(int i=0;i<premise.get(j).parameters.size();i++)
			{
				if ( ('A' <= premise.get(j).parameters.get(i).charAt(0)) &&
					(premise.get(j).parameters.get(i).charAt(0) <= 'Z') )
					continue;
				rc = false;
				break;
			}
		}

		return rc;
	}

	public int compareTo(kSentence ks)
	{
		int rc = this.conclusion.fName.compareTo(ks.conclusion.fName);
		if ( rc == 0 && (this.conclusion.parameters.size()>ks.conclusion.parameters.size()))
			rc = 1;
		else if( rc == 0 && (this.conclusion.parameters.size()<ks.conclusion.parameters.size()))
			rc = -1;
		return rc;
	}

	public String toString()
	{
		String s = "";
		if (!implication)
			return "\t Conclusion: \n"+conclusion.toString();
		else
		{
			s = s + "++ premise: \n";
			for(int i=0;i<premise.size();i++)
				s = s + "\t Premise("+i+"): \n"+premise.get(i).toString()+"\n";
			s = s + "\t Conclusion: \n"+conclusion.toString();
		}
		return s;
	}
}