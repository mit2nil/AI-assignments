/* Import in built java packages */
import java.io.*;
import java.util.*;

public class inferenceEngine
{
	/* Fields */
	private int queryCount;
	private int kbCount;

	// Queries
	private ArrayList<qSentence> queries = new ArrayList<qSentence>();

	// Hash structures for implication sentences of KB
	private ArrayList<kSentence> kbImp1 = new ArrayList<kSentence>();
	private HashMap<String,ArrayList<kSentence>> kbImp2 = new HashMap<String,ArrayList<kSentence>>();

	// Hash structures for fact sentences of KB
	private HashSet<qSentence> kbConst1 = new HashSet<qSentence>();
	private HashMap<String,ArrayList<qSentence>> kbConst2 = new HashMap<String, ArrayList<qSentence>>();
	private ArrayList<qSentence> kbConst3 = new ArrayList<qSentence>();

	// Used for loop detection
	private static HashSet<String> occuredKB = new HashSet<String>();

    // File related fields
    private String inputFile;
	private String outputFile;

	/* Constructor */
	public inferenceEngine()
	{
		inputFile = "input.txt";
		outputFile = "output.txt";
		queryCount = 0;
		kbCount = 0;
	}

	/* Methods */

	// Update output file with single line of output
	private boolean writeOutput(String s)
	{
		try
		{
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile,true));
			bw.append(s);
            bw.close();
        }
        catch(FileNotFoundException ex)
		{
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
            return false;
        }
        catch(IOException ex)
		{
            StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
            return false;
        }
        return true;
	}

	// Read single test case from input file
	protected void parseInputFile(String fileName)
	{
		inputFile = fileName;

        // Ensure that new output files are created
		File fo = new File(outputFile);
		fo.delete();

        try
		{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String tempS = br.readLine();

			// Read and process queries
            queryCount = Integer.parseInt(tempS);
			for(int i=0;i<queryCount;i++)
			{
				// Read, process and store query sentences
				tempS = br.readLine();
				//System.out.println("==Query Sentence is: " +tempS);
				queries.add(new qSentence(tempS));
				//System.out.println(queries.get(i));
			}

			// Read and process knowledgebase
			tempS = br.readLine();
			kbCount = Integer.parseInt(tempS);
			for(int i=0;i<kbCount;i++)
			{
				// Read, process and store kb sentences
				tempS = br.readLine();
				//System.out.println("==KB Sentence is: " +tempS);
				kSentence tempK = new kSentence(tempS);

				if (!tempK.implication)
				{
					kbConst1.add(tempK.conclusion);
					kbConst3.add(tempK.conclusion);
				}
				else
					kbImp1.add(tempK);
			}

			// Sort sentences in kb so that searching is faster using hash indexing
			Collections.sort(kbImp1);
			Collections.sort(kbConst3);

			// Divide kbImplications over function name on conclusion side
			for(int i=0;i<kbImp1.size();)
			{
				int j = i+1;
				ArrayList<kSentence> value = new ArrayList<kSentence>();
				value.add(kbImp1.get(i));

				String key = kbImp1.get(i).conclusion.fName;
				for(;j<kbImp1.size();j++)
				{
					kSentence ks = kbImp1.get(j);
					String tKey = ks.conclusion.fName;
					if (tKey.compareTo(key) == 0)
						value.add(ks);
					else
						break;
				}
				kbImp2.put(key,value);
				i = j;
			}

			// Divide kbConstant over function name on conclusion side
			for(int i=0;i<kbConst3.size();)
			{
				int j = i+1;
				ArrayList<qSentence> value = new ArrayList<qSentence>();
				value.add(kbConst3.get(i));

				String key = kbConst3.get(i).fName;
				for(;j<kbConst3.size();j++)
				{
					qSentence qs = kbConst3.get(j);
					String tKey = qs.fName;
					if (tKey.compareTo(key) == 0)
						value.add(qs);
					else
						break;
				}
				kbConst2.put(key,value);
				i = j;
			}

			/*
			System.out.println("+++++++Constants (Sorted)+++++++++++++++++++");
			Iterator<qSentence> itrC = kbConst1.iterator();
			while(itrC.hasNext())
			{
				qSentence tmp = itrC.next();
				System.out.println(tmp.toStringS()+" :hash: "+tmp.hashCode());
			}

			System.out.println("++++Implications (Sorted)++++++++++");
			for(int i=0;i<kbImp1.size();i++)
				System.out.println(kbImp1.get(i).sentence);

			System.out.println("+++++Hash indexed implications+++++++++");
			Set<String> itr = kbImp2.keySet();
			for(String s: itr)
			{
				System.out.println("-->"+s);
				ArrayList<kSentence> temp = kbImp2.get(s);
				if (temp != null)
					for(int i=0;i<temp.size();i++)
						System.out.println(temp.get(i).sentence);
			}

			System.out.println("+++++Hash indexed constants+++++++++");
			itr = kbConst2.keySet();
			for(String s: itr)
			{
				System.out.println("-->"+s);
				ArrayList<qSentence> temp = kbConst2.get(s);
				if (temp != null)
					for(int i=0;i<temp.size();i++)
						System.out.println(temp.get(i).toStringS());
			}*/

            br.close();
        }
        catch(FileNotFoundException ex)
		{
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
        }
        catch(IOException ex)
		{
            StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
        }

		// Start answering queries
		for(int i=0;i<queryCount;i++)
		{
			//System.out.println("+++++Resolving New Query+++++++++");
			occuredKB.clear();
			if (resolve(queries.get(i),null))
				writeOutput("TRUE");
			else
				writeOutput("FALSE");

			if (i != queryCount-1)
				writeOutput("\n");
		}
	}

	private boolean isVariable(String s)
	{
		if (s.length() == 1)
			if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z')
				return true;
		return false;
	}

	private void printSlist(ArrayList<HashMap<String,String>> p)
	{
		if (p != null)
		{
			System.out.println("Slist: ");
			for(int i=0;i<p.size();i++)
			{
                System.out.println("Slist["+i+"]");
				Set<String> itr = p.get(i).keySet();
				for(String s: itr)
					System.out.println("("+s+","+p.get(i).get(s)+")");
			}
		}
		else
		{
			System.out.println("Slist: Empty");
		}
	}

	private ArrayList<HashMap<String,String>> getPartialSlist(qSentence q)
	{
		// Check in KB with facts
		ArrayList<HashMap<String, String>> slists = new ArrayList<HashMap<String, String>>();
		if (kbConst2.containsKey(q.fName) && q.containsVariable())
		{
			ArrayList<qSentence> qs = kbConst2.get(q.fName);
			for(int i=0;i<qs.size();i++)
			{
				HashMap<String, String> slist = new HashMap<String,String>();
				qSentence ql = qs.get(i);
				if (ql.parameters.size() == q.parameters.size())
				{
					for(int j=0;j<q.parameters.size();j++)
					{
						String var = q.parameters.get(j);
						if (isVariable(var))
						{
							if (slist.containsKey(var))
							{
								if (slist.get(var).compareTo(ql.parameters.get(j)) != 0)
								{
									slist.clear();
									break;
								}
							}
							else
								slist.put(var,ql.parameters.get(j));
						}
						else
						{
							if (var.compareTo(ql.parameters.get(j)) != 0)
							{
								slist.clear();
								break;
							}
						}
					}
				}
				if (slist.size() != 0)
					slists.add(slist);
			}
		}
		if (slists.size() == 0)
			return null;
		else
			return slists;
	}

	private void printTheta(HashMap<String,String> a ,HashMap<String,String> b)
	{
		if (a != null)
		{
			System.out.println("ThetaIn");
			for(int i=0;i<a.size();i++)
			{
				Set<String> itr = a.keySet();
				for(String s: itr)
					System.out.println("("+s+","+a.get(s)+")");
			}
		}
		if (b != null)
		{
			System.out.println("ThetaOut");
			for(int i=0;i<b.size();i++)
			{
				Set<String> itr = b.keySet();
				for(String s: itr)
					System.out.println("("+s+","+b.get(s)+")");
			}
		}
	}

	private boolean hasConflicts(HashMap<String,String> a ,HashMap<String,String> b)
	{
		if (a == null || b == null)
			return false;

		if (a.size() > b.size())
		{
			Set<String> set = b.keySet();
			for(String s: set)
				if (a.containsKey(s))
					if (a.get(s).compareTo(b.get(s)) != 0)
						return true;
		}
		else
		{
			Set<String> set = a.keySet();
			for(String s: set)
				if (b.containsKey(s))
					if (b.get(s).compareTo(a.get(s)) != 0)
						return true;
		}
		return false;
	}

	private boolean resolve(qSentence q,  HashMap<String, String> thetaIn)
	{
		//System.out.println("Solving query: "+q.toStringS());

		// For case 3
		qSentence tempQ = q.clone();
		tempQ.negation = !tempQ.negation;

		if (occuredKB.contains(q.toStringS()))
		{
			//System.out.println("Loop deteccted");
			return false;
		}
		else if (kbConst1.contains(q))
		{
			//System.out.println("Found in KB");
			return true;
		}
		else if (kbConst1.contains(tempQ))
		{
			//System.out.println("Found negation in KB");
			return false;
		}
		else
		{
			// Non-trivial case
			// Check in KB with implications
			ArrayList<kSentence> ks = kbImp2.get(q.fName);
			if (ks != null)
			{
				// Multiple KB sentences with matching rhs
				for(int i=0;i<ks.size();i++)
				{
					boolean rcPremices = false;
					kSentence tmp = ks.get(i);
					boolean cond1 = (q.negation == tmp.conclusion.negation);
					boolean cond2 = (q.parameters.size() == tmp.conclusion.parameters.size());

					// Ensure that conclusion has same sign and same number of parameters
					if (!cond1 || !cond2)
					{
						continue;
					}

					// Create substitution list based on number of parameters on conclusion side
					HashMap<String, String> slist = new HashMap<String, String>();
					for(int j=0;j<tmp.conclusion.parameters.size();j++)
					{
						// Key as value of parameter (constant or variable)
						// Value as actual substitution
						slist.put(tmp.conclusion.parameters.get(j),q.parameters.get(j));
					}

					// Unify existing parameters for each term in lhs
					ArrayList<qSentence> qs = new ArrayList<qSentence>();
					for(int j=0;j<tmp.premise.size();j++)
					{
						qSentence ql = tmp.premise.get(j).clone();

						// Unify all variable of a term on lhs from substitution value
						for(int k=0;k<ql.parameters.size();k++)
						{
							// Update variable/constant values
							if (slist.containsKey(ql.parameters.get(k)))
								ql.parameters.set(k,slist.get(ql.parameters.get(k)));
						}
						qs.add(ql);
					}

					//System.out.println("LOOP1");

					// Unify experimental parameters within same set of premises
					occuredKB.add(q.toStringS());
					for(int j=0;j<qs.size();j++)
					{
						//System.out.println("LOOP2");

						qSentence ql = qs.get(j);
						//System.out.println("Deriving slist for: "+ql.toStringS());
						ArrayList<HashMap<String, String>> tmpSlist = getPartialSlist(ql);
						//printSlist(tmpSlist);

						if (tmpSlist != null)
						{
							HashMap<String,String> thetaOut = new HashMap<String,String>();
							//System.out.println("THETA 0");
							//printTheta(thetaIn,thetaOut);


							HashMap<String,String> backupThetaIn = new HashMap<String,String>();
							if (thetaIn != null)
								backupThetaIn.putAll(thetaIn);

							// Run over all possible partial assignment combinations
							for(int m=0;m<tmpSlist.size();m++)
							{
								//System.out.println("LOOP3");

								if (thetaIn != null)
								{
									thetaIn.clear();
									thetaIn.putAll(backupThetaIn);
								}

								thetaOut.clear();
								thetaOut.putAll(tmpSlist.get(m));

								// If conflict arise, don't pursue this slist
								// If not update the theta
								//System.out.println("THETA 1");
								//printTheta(thetaIn,thetaOut);
								if (hasConflicts(thetaIn,thetaOut))
									continue;
								else if (thetaIn != null)
									thetaOut.putAll(thetaIn);

								Set<String> sItr = tmpSlist.get(m).keySet();
								ArrayList<qSentence> tmpQs = new ArrayList<qSentence>();
								for(int k=0;k<qs.size();k++)
									tmpQs.add(qs.get(k).clone());

								//for(String s: sItr)
									//System.out.println("K :"+s+" V: "+tmpSlist.get(m).get(s));

								boolean rcSlists = false;
								// Run over all terms of one of chosen LHS
								for(int k=0;k<tmpQs.size();k++)
								{
									//System.out.println("LOOP4");
									// Run over all unknown variables of an LHS term
									for(int n=0;n<tmpQs.get(k).parameters.size();n++)
									{
										//System.out.println("LOOP5");
										// Update variable/constant values
										String tmpKey = tmpQs.get(k).parameters.get(n);

										// Check if "current slist (tmpSlist.get(m))" contains entry for
										// nth parameter in kth term of a premice (tmpQs.get(k).parameters.get(n))
										if (tmpSlist.get(m).containsKey(tmpKey))
											// If so, substitute value using slist
											tmpQs.get(k).parameters.set(n,tmpSlist.get(m).get(tmpKey));
									}

									rcSlists = resolve(tmpQs.get(k),thetaOut);

									// If failed, try another set of partial assignments
									// AND part
									if (!rcSlists)
										break;
									else
									{
										if (thetaIn != null)
										{
											thetaIn.clear();
											thetaIn.putAll(thetaOut);
										}
										else
										{
											thetaIn = new HashMap<String,String>();
											thetaIn.putAll(thetaOut);
										}
									}
									//System.out.println("THETA 2");
									//printTheta(thetaIn,thetaOut);
								}
								// If one of the partial assignment satisfied all terms on lhs, we found one solution, exit
								// OR part
								if (rcSlists)
									return true;
							}
						}
						else
						{
							//System.out.println("LOOP6");

							HashMap<String,String> thetaOut = new HashMap<String,String>();
							if (thetaIn != null)
								thetaOut.putAll(thetaIn);

							//System.out.println("THETA 3");
							//printTheta(thetaIn,thetaOut);

							rcPremices = resolve(ql,thetaOut);

							// If failed, try another KB sentence
							// AND part
							if (!rcPremices)
								break;
							else
							{
								if (thetaIn != null)
								{
									thetaIn.clear();
									thetaIn.putAll(thetaOut);
								}
								else
								{
									thetaIn = new HashMap<String,String>();
									thetaIn.putAll(thetaOut);
								}
								//System.out.println("THETA 4");
								//printTheta(thetaIn,thetaOut);
							}
						}
					}

					// If one of the matched KB satisfies, we found one solution, exit
					// OR part
					if (rcPremices)
						return true;
				}
			}
			else
			{
				//System.out.println("Fact not Found in KB");
				return false;
			}
		}
		return false;
	}
}