/* Import in built java packages */
import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;


// Class to resolve terms of a single query sentence
public class qSentence implements Comparable<qSentence>
{
	/* Fields */
	protected boolean negation;
	protected String fName;
	protected ArrayList<String> parameters = new ArrayList<String>();

	/* Constructor */
	public qSentence()
	{
		// Dummy one
	}

	public qSentence(String sentence)
	{
		Pattern pr = Pattern.compile("(~?)([A-Z][A-Za-z]*)\\(\\s*([\\w,\\s]+)\\s*\\)");
		Matcher mr = pr.matcher(sentence);

		if (mr.groupCount( ) == 3 && mr.find())
		{
			// Negetion check
			String s = mr.group(1);
			if (s.compareTo("") == 0)
				this.negation = false;
			else
				this.negation = true;

			// Store function name
			this.fName = mr.group(2);

			// tokenize parameters and store them
			s = mr.group(3);
			String[] tkns = s.split(",");
			for(int i=0;i<tkns.length;i++)
				this.parameters.add(tkns[i].trim());
		}
		else
		{
			System.out.println("no match for sentence: "+sentence);
		}
	}

	protected boolean containsVariable()
	{
		for(int i=0;i<parameters.size();i++)
		{
			String s = parameters.get(i);
			if (s.length() == 1)
				if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z')
					return true;
		}
		return false;
	}

	public int compareTo(qSentence qs)
	{
		return this.fName.compareTo(qs.fName);
	}

	public boolean equals(Object o)
	{
		qSentence q = (qSentence) o;
		boolean rc = false;
		if (this.negation == q.negation)
		{
			if (this.fName.compareTo(q.fName) == 0)
			{
				if (this.parameters.size() == q.parameters.size())
				{
					int i =0;
					for(i=0;i<this.parameters.size();i++)
						if (this.parameters.get(i).compareTo(q.parameters.get(i)) != 0)
							break;
					if ( i == this.parameters.size())
					{
						//System.out.println("Hash1");
						rc = true;
					}
					//else
					{
						//System.out.println("Hash2");
					}
				}
				//else
					//System.out.println("Hash3");
			}
			//else
				//System.out.println("Hash4");
		}
		//else
			//System.out.println("Hash5");
		return rc;
	}

	public int hashCode()
	{
		String sr = "";
		if (negation)
			sr = "-";
		else
			sr = "+";
		sr = sr+fName.toString();
		for(int i=0;i<parameters.size();i++)
			sr = sr+parameters.get(i);
		return sr.hashCode();
	}

	public String toString()
	{
		String s = "";
		if (negation)
			s = "++ Negation: ~\n";
		else
			s = "++ Negetion: None\n";

		s = s + "++ Name: "+fName+"\n"+"++ Parameters: \n";
		for(int i=0;i<parameters.size();i++)
			s = s + "\t Param["+i+"]: "+parameters.get(i)+"\n";

		return s;
	}

	protected qSentence clone()
	{
		qSentence q = new qSentence();
		q.negation = this.negation;
		q.fName = this.fName;
		q.parameters = new ArrayList<String>();
		for(int i=0;i<this.parameters.size();i++)
			q.parameters.add(this.parameters.get(i));
		//return new qSentence(this.toStringS());
		return q;
	}

	protected String toStringS()
	{
		String s = "";
		if (negation)
			s = "~";
		s = s+fName+"(";
		int i=0;
		for(i=0;i<parameters.size()-1;i++)
		{
			s = s + parameters.get(i)+",";
		}
		s = s + parameters.get(i)+")";
		return s;
	}
}