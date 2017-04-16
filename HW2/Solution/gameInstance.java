/* Import in built java packages */
import java.io.*;
import java.util.ArrayList;

public class gameInstance
{
	/* Fields */

	// Game Setup fields
    private gameBoard board;
	private boolean gameEnded;
	private int strategyType;
	private int cutoffDepth;
    private ArrayList<Integer> boardMoves = new ArrayList<Integer>();

    // File related fields
    private String inputFile;
	private String outputFile;
	private String outputFileLog;

	/* Constructor */
	public gameInstance()
	{
        board = new gameBoard();

		gameEnded = false;
		strategyType = 0;
		cutoffDepth = 0;

		inputFile = "input.txt";
		outputFile = "next_state.txt";
		outputFileLog = "traverse_log.txt";
	}

	class alphaBeta {
	  protected int alpha;
	  protected int beta;
	  protected boolean abUsed;
	}

	/* Methods */

	// Update output file (next_state_X.txt) with single line of output
	protected boolean updateNextStateOutput(String output)
	{
		try
		{
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile,true));
            bw.append(output);
            bw.close();
        }
        catch(FileNotFoundException ex)
		{
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

    // Update output file (traverse_log_X.txt) with single line of output
	protected boolean updateTraverselogOutput(String output)
	{
		try
		{
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileLog,true));
            bw.append(output);
            bw.close();
        }
        catch(FileNotFoundException ex)
		{
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
	protected boolean parseInputFile(String fileName)
	{
		inputFile = fileName;

        // Ensure that new output files are created
		File fo = new File(outputFile);
		fo.delete();
		fo = new File(outputFileLog);
		fo.delete();

        try
		{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));

            strategyType = Integer.parseInt(br.readLine());
			board.currentPlayer = Integer.parseInt(br.readLine());
			cutoffDepth = Integer.parseInt(br.readLine());

			// Read board size and initialize stone values
			String ln = br.readLine();
			String[] tkns = ln.split(" ");
			board.boardSize = tkns.length;
			for(int i=tkns.length;i>0;i--)
			{
				// adding in reverse order to optimize stone distribution
				board.currentPos.add(Integer.parseInt(tkns[i-1]));
			}
			ln = br.readLine();
			tkns = ln.split(" ");
			for(int i=0;i<tkns.length;i++)
			{
				board.currentPos.add(i,Integer.parseInt(tkns[i]));
			}

			// Mancala values
			board.currentPos.add(0,Integer.parseInt(br.readLine()));// Player 2 Mancala
			board.currentPos.add(board.boardSize+1,Integer.parseInt(br.readLine()));// Player 1 Mancala

            br.close();
        }
        catch(FileNotFoundException ex)
		{
            return false;
        }
        catch(IOException ex)
		{
            StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
            return false;
        }

		// Start a game from root
        gameBoard tempBoard = new gameBoard();
        board.boardClone(tempBoard);

        switch(strategyType)
        {
            case 1:// Greedy
            {
                //root at depth=0, MinMax = max(true)
                playGreedyMove(tempBoard,0);
                break;
            }
            case 2:// Minimax
            {
            	alphaBeta ab = new alphaBeta();
				ab.alpha = 0;
				ab.beta = 0;
				ab.abUsed = false;
                updateTraverselogOutput("Node,Depth,Value\n");
                playMinmaxMove(tempBoard,ab);
                break;
            }
            case 3:// Minimax with Alpha Beta pruning
            {
            	alphaBeta ab = new alphaBeta();
				ab.alpha = -10000;
				ab.beta = 10000;
				ab.abUsed = true;
                updateTraverselogOutput("Node,Depth,Value,Alpha,Beta\n");
                playMinmaxMove(tempBoard,ab);
                break;
            }
            case 4:// Custom - for now Greedy
            {
                //root at depth=0, MinMax = max(true)
                playGreedyMove(tempBoard,0);
                break;
            }
            default:
            {
                break;
            }
        }
        return true;
	}

	protected void printTraverseLogEntry(int player,int pit, int depth, int value, alphaBeta ab)
	{
		String st = "";

		// Player and pit (Move)
		if (player == 1)
			st=st+"B"+(pit+1)+",";
		else if (player == 2)
			st=st+"A"+(pit+1)+",";

		// Depth
		st=st+depth+",";

		// Value
		st=st+value;

		if (ab.abUsed)
		{
			if (ab.alpha == -10000 && ab.beta == 10000)
				updateTraverselogOutput(st+",-Infinity,Infinity\n");
			else if (ab.alpha == -10000)
				updateTraverselogOutput(st+",-Infinity,"+ab.beta+"\n");
			else if (ab.beta == 10000)
				updateTraverselogOutput(st+","+ab.alpha+",Infinity\n");
			else
				updateTraverselogOutput(st+","+ab.alpha+","+ab.beta+"\n");
		}
		else
			updateTraverselogOutput(st+"\n");
	}

	protected void printTraverseLogEntry(int player,int pit, int depth, String value, alphaBeta ab)
	{
		String st = "";

		// Player and pit (Move)
		if (player == 1)
			st=st+"B"+(pit+1)+",";
		else if (player == 2)
			st=st+"A"+(pit+1)+",";

		// Depth
		st=st+depth+",";

		// Value
		st=st+value;

		if (ab.abUsed)
		{
			if (ab.alpha == -10000 && ab.beta == 10000)
				updateTraverselogOutput(st+",-Infinity,Infinity\n");
			else if (ab.alpha == -10000)
				updateTraverselogOutput(st+",-Infinity,"+ab.beta+"\n");
			else if (ab.beta == 10000)
				updateTraverselogOutput(st+","+ab.alpha+",Infinity\n");
			else
				updateTraverselogOutput(st+","+ab.alpha+","+ab.beta+"\n");
		}
		else
			updateTraverselogOutput(st+"\n");
	}

	//B3 means player=1, pit=2
	//A6 means player=2, pit=5
	protected void makeMove(gameBoard boardInstance, int pit)
    {
        int player = boardInstance.currentPlayer;
        int boardSize = boardInstance.boardSize;

		//System.out.format("Before: player:%d, pit:%d\n",boardInstance.currentPlayer,pit);
        //boardInstance.printBoard();

        if (!gameEnded && boardInstance.isLegal(pit))
    	{
    		int startIndex = 0;
			int startStones = 0;

			startIndex = boardInstance.getPitNumber(pit);
			startStones = boardInstance.currentPos.get(startIndex);
			boardInstance.currentPos.set(startIndex,0);

    		// Logic of updating board
			for(int i=startIndex+1;startStones > 0;i++)
			{
				// Reset i value to go round the board
				if (i == (2*boardSize+2))
					i = 0;

				// Player 2 Mancala - Do not touch
				if (player == 1 && i == 0)
					continue;

				// Player 1 Mancala - Do not touch
				if (player == 2 && i == boardSize+1)
					continue;

				startStones--;
				boardInstance.currentPos.set(i, boardInstance.currentPos.get(i)+1);

				// Last iteration
				if (startStones == 0)
				{
					// Give turn back to the same player
					if (player == 1 && i == boardSize+1)
                        player = 1;
					else if (player == 2 && i == 0)
						player = 2;
					// Last stone in empty pit condition
					else if (boardInstance.currentPos.get(i) == 1)
					{
						if (player == 1)
						{
							if (1<=i && i <= boardSize)
							{
								int newCount = boardInstance.currentPos.get(boardSize+1);// Get original stones
								newCount += boardInstance.currentPos.get((2*boardSize+2)-i);// Add opposite count
								boardInstance.currentPos.set(i,0);// Empty player pit
								boardInstance.currentPos.set((2*boardSize+2)-i,0);//Empty opposite pit
								boardInstance.currentPos.set(boardSize+1,newCount+1);// Update player 1 Mancala
							}
							// change player
                            player = 2;
						}
						else
						{
							if (boardSize+2<=i && i<=2*boardSize+1)
							{
								int newCount = boardInstance.currentPos.get(0);// Get original stones
								newCount += boardInstance.currentPos.get((2*boardSize+2)-i);// Add opposite count
								boardInstance.currentPos.set(i,0);// Empty player pit
								boardInstance.currentPos.set((2*boardSize+2)-i,0);//Empty opposite pit
								boardInstance.currentPos.set(0,newCount+1);//Update player 2 Mancala
							}
							// change player
                            player = 1;
						}

					}
					else
					{
						if (player == 1)
							player = 2;
						else
							player = 1;
					}
				}
			}

			// Check end condition
			if (boardInstance.countIllegalMoves(1) == boardSize)
			{
				gameEnded = true;
				int stones = boardInstance.currentPos.get(0);
				for(int i=0;i<boardSize;i++)
				{
                    // Count stones
                    stones = stones + boardInstance.currentPos.get(i+boardSize+2);
					boardInstance.currentPos.set(i+boardSize+2,0);
				}
				boardInstance.currentPos.set(0,stones);
			}
            else if (boardInstance.countIllegalMoves(2) == boardSize)
			{
				gameEnded = true;
				int stones = boardInstance.currentPos.get(boardSize+1);
				for(int i=0;i<boardSize;i++)
				{
                    // Count stones
                    stones = stones + boardInstance.currentPos.get(i+1);
					boardInstance.currentPos.set(i+1,0);
				}
				boardInstance.currentPos.set(boardSize+1,stones);
			}

            // Update changed player
            boardInstance.currentPlayer = player;
		}
		else
		{
			//System.out.println("Either move was not legal or game has already ended");
			boardInstance.currentPlayer = 0;
		}

        //boardInstance.printBoard();
        //System.out.format("After: Next player:%d\n",boardInstance.currentPlayer);
	}

	// Greedy Move
	protected int playGreedyMove(gameBoard boardInstance, int depth)
	{
		int value = -100000;
		int chosenValue = -10000;
		int chosenIndex = -1;
		int boardSize = board.boardSize;
		int currentPlayer = boardInstance.currentPlayer;
		gameBoard prev = new gameBoard();

		if (depth == 0)
			board.boardClone(boardInstance,currentPlayer);

		//  Backup
		boardInstance.boardClone(prev);

		// Loop over all pits on the player side
		for(int i=1;i<=boardSize;i++)
		{
			int prevPlayer = currentPlayer;
			if (!gameEnded)
			{
				// Make move
				makeMove(boardInstance,i);
				currentPlayer = boardInstance.currentPlayer;
				if (currentPlayer != 0)
					value = boardInstance.evalBoard(board.currentPlayer);

				// If same player, play with current position
				if (currentPlayer == board.currentPlayer && !gameEnded)
				{
					value = playGreedyMove(boardInstance,1);

					// Max decision
					if (chosenValue <  value)
					{
						chosenValue = value;
						chosenIndex = board.getPitNumber(prevPlayer,i);
					}
					else
						boardMoves.clear();
				}
				else// Reset
				{
					currentPlayer = prevPlayer;
					// Max decision
					if (chosenValue <  value)
					{
						chosenValue = value;
						chosenIndex = board.getPitNumber(prevPlayer,i);
					}
				}
				prev.boardClone(boardInstance,currentPlayer);
			}
			else
			{
				break;
			}
		}

		// Add selected index to final movess
		if (chosenIndex != -1)
			boardMoves.add(chosenIndex);

		// If it is last iteration (or first), play stored moves to get next state
		if (depth == 0)	// End condition
		{
			// Game might have endded as this is a traceback
			gameEnded = false;

			// Play all move in reverse - they should be all legal
			int startPlayer = board.currentPlayer;
			int tempPlayer = board.currentPlayer;
			int tempIndex = 1;
			for(int i=boardMoves.size()-1;i>=0;i--)
			{
				// breal condition
				if (tempPlayer != startPlayer)
					break;

				// get next index, convert to pit and make next move
				tempIndex = boardMoves.get(i);
				tempIndex = board.getPitNumber(tempIndex);
				makeMove(board,tempIndex);
				tempPlayer = board.currentPlayer;

				//board.printBoard();
			}

			// Update output file
			updateNextStateOutput(board.getBoard());
		}
		return chosenValue;
	}

	protected void playMinmaxMove(gameBoard boardInstance, alphaBeta ab)
	{
		board.boardClone(boardInstance);

		if (ab.abUsed)
			updateTraverselogOutput("root,0,-Infinity,-Infinity,Infinity\n");
		else
			updateTraverselogOutput("root,0,-Infinity\n");

		// depth/pit = 0 at root, alpha = beta = 0 for Minimax
		maxValue(boardInstance,0,0,ab);

		// Game might have endded as this is a traceback
		gameEnded = false;

		// Play all move in reverse - they should be all legal
		int startPlayer = board.currentPlayer;
		int tempPlayer = board.currentPlayer;
		int tempIndex = 1;
		for(int i=boardMoves.size()-1;i>=0;i--)
		{
			//break condition
			if (tempPlayer != startPlayer)
				break;

			// get next index, convert to pit and make next move
			tempIndex = boardMoves.get(i);
			tempIndex = board.getPitNumber(tempIndex);

			makeMove(board,tempIndex);
			tempPlayer = board.currentPlayer;

			//board.printBoard();
		}

		// Update output file
		updateNextStateOutput(board.getBoard());
	}

	//board, depth, alpha, beta (alpha=beta=0 for minimax)
	protected int maxValue(gameBoard boardInstance, int depth, int pit, alphaBeta ab)
	{
		int chosenValue = -10000;
		int value = -10000;
		int boardSize = board.boardSize;
		int chosenIndex = -1;

		if (depth == 0)
			pit = 1;

		for(int i=1;i<=board.boardSize;i++)
		{
			int beforeMoves = boardMoves.size();
			int afterMoves = boardMoves.size();

			// Locals
			gameBoard tempBoard = new gameBoard();
			alphaBeta localAB = new alphaBeta();

			// Initialization
			localAB.alpha = ab.alpha;
			localAB.beta = ab.beta;
			localAB.abUsed = ab.abUsed;
			boardInstance.boardClone(tempBoard);

			if (gameEnded)
				break;

			// Result step
			makeMove(tempBoard,pit);

			if (tempBoard.currentPlayer == 0 && depth != 0)
				continue;

			// Terminal condition
			if (depth == cutoffDepth && tempBoard.currentPlayer != boardInstance.currentPlayer)
			{
				//updateTraverselogOutput("MAX:1\n");
				chosenValue = tempBoard.evalBoard(board.currentPlayer);
				printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,chosenValue,ab);
				break;
			}
			else if (depth == 0)
			{
				//updateTraverselogOutput("MAX:2\n");
				boardInstance.boardClone(tempBoard);
				gameBoard tempBoard2 = new gameBoard();
				tempBoard.boardClone(tempBoard2);
				makeMove(tempBoard2,i);

				if (tempBoard2.currentPlayer == board.currentPlayer && boardSize > tempBoard2.countIllegalMoves())
				{
					//updateTraverselogOutput("MAX:2.1\n");
					value = maxValue(tempBoard,depth+1,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
				else
				{
					//updateTraverselogOutput("MAX:2.2\n");
					value = minValue(tempBoard,depth+1,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
			}
			else if (tempBoard.currentPlayer == boardInstance.currentPlayer)
			{
				//updateTraverselogOutput("MAX:3\n");
				if (tempBoard.countIllegalMoves() != boardSize && i == 1)
					printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,"-Infinity",ab);

				gameBoard tempBoard2 = new gameBoard();
				tempBoard.boardClone(tempBoard2);
				makeMove(tempBoard2,i);

				if (tempBoard2.currentPlayer == board.currentPlayer && boardSize > tempBoard2.countIllegalMoves())
				{
					//updateTraverselogOutput("MAX:3.1\n");
					value = maxValue(tempBoard,depth,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
				else
				{
					//updateTraverselogOutput("MAX:3.2\n");
					value = minValue(tempBoard,depth,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
			}
			else// depth between 0-cutoff and next player is different than current player
			{
				//updateTraverselogOutput("MAX:4\n");
				if (tempBoard.countIllegalMoves() != boardSize && i == 1)
					printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,"-Infinity",ab);

				gameBoard tempBoard2 = new gameBoard();
				tempBoard.boardClone(tempBoard2);
				makeMove(tempBoard2,i);

				if (tempBoard2.currentPlayer == board.currentPlayer && boardSize > tempBoard2.countIllegalMoves())
				{
					//updateTraverselogOutput("MAX:4.1\n");
					value = maxValue(tempBoard,depth+1,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
				else
				{
					//updateTraverselogOutput("MAX:4.2\n");
					value = minValue(tempBoard,depth+1,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
			}

			afterMoves = boardMoves.size();
			if (value > chosenValue)
			{
				if (depth == 1)
					chosenIndex = board.getPitNumber(boardInstance.currentPlayer,pit);
				chosenValue = value;
			}
			else
			{
				for(int j=1;j<=afterMoves-beforeMoves;j++)
					boardMoves.remove(boardMoves.size()-j);
			}

			if (ab.abUsed)
				if (chosenValue >= ab.beta)
				{
					if (depth == 0)
					{
						String st = "root,0,"+chosenValue;
						if (ab.alpha == 10000 && ab.beta == -10000)
							updateTraverselogOutput(st+",-Infinity,Infinity\n");
						else if (ab.alpha == -10000)
							updateTraverselogOutput(st+",-Infinity,"+ab.beta+"\n");
						else if (ab.beta == 10000)
							updateTraverselogOutput(st+","+ab.alpha+",Infinity\n");
						else
							updateTraverselogOutput(st+","+ab.alpha+","+ab.beta+"\n");
					}
					else
						printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,chosenValue,ab);

					if (chosenIndex != -1)
						boardMoves.add(chosenIndex);
					return chosenValue;
				}

			if (ab.abUsed)
				if (chosenValue > ab.alpha)
					ab.alpha = chosenValue;

			if (depth == 0)
				if (ab.abUsed)
				{
					String st = "root,0,"+chosenValue;
					if (ab.alpha == 10000 && ab.beta == -10000)
						updateTraverselogOutput(st+",-Infinity,Infinity\n");
					else if (ab.alpha == -10000)
						updateTraverselogOutput(st+",-Infinity,"+ab.beta+"\n");
					else if (ab.beta == 10000)
						updateTraverselogOutput(st+","+ab.alpha+",Infinity\n");
					else
						updateTraverselogOutput(st+","+ab.alpha+","+ab.beta+"\n");
				}
				else
					updateTraverselogOutput("root,0,"+chosenValue+"\n");
			else
				printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,chosenValue,ab);
		}

		if (chosenIndex != -1)
			boardMoves.add(chosenIndex);
		return chosenValue;
	}

	//board, depth, alpha, beta (alpha=beta=0 for minimax)
	protected int minValue(gameBoard boardInstance, int depth, int pit, alphaBeta ab)
	{
		int chosenValue = 10000;
		int value = 10000;
		int boardSize = board.boardSize;
		int chosenIndex = -1;

		for(int i=1;i<=board.boardSize;i++)
		{
			int beforeMoves = boardMoves.size();
			int afterMoves = boardMoves.size();

			// Locals
			gameBoard tempBoard = new gameBoard();
			alphaBeta localAB = new alphaBeta();

			// Initialization
			localAB.alpha = ab.alpha;
			localAB.beta = ab.beta;
			localAB.abUsed = ab.abUsed;
			boardInstance.boardClone(tempBoard);

			if (gameEnded)
				break;

			// Result step
			makeMove(tempBoard,pit);

			if (tempBoard.currentPlayer == 0 && depth != 0)
				continue;

			// Terminal condition
			if (depth == cutoffDepth && tempBoard.currentPlayer != boardInstance.currentPlayer)
			{
				//updateTraverselogOutput("MIN:1\n");
				chosenValue = tempBoard.evalBoard(board.currentPlayer);
				printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,chosenValue,ab);
				break;
			}
			else if (tempBoard.currentPlayer == boardInstance.currentPlayer)
			{
				//updateTraverselogOutput("MIN:2\n");
				if (tempBoard.countIllegalMoves() != boardSize && i == 1)
					printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,"Infinity",ab);

				gameBoard tempBoard2 = new gameBoard();
				tempBoard.boardClone(tempBoard2);
				makeMove(tempBoard2,i);

				if (tempBoard2.currentPlayer == board.currentPlayer && boardSize > tempBoard2.countIllegalMoves())
				{
					//updateTraverselogOutput("MIN:2.1\n");
					value = maxValue(tempBoard,depth,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
				else
				{
					//updateTraverselogOutput("MIN:2.2\n");
					value = minValue(tempBoard,depth,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
			}
			else// depth between 0-cutoff and next player is different than current player
			{
				//updateTraverselogOutput("MIN:3\n");
				if (tempBoard.countIllegalMoves() != boardSize && i == 1)
					printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,"Infinity",ab);

				gameBoard tempBoard2 = new gameBoard();
				tempBoard.boardClone(tempBoard2);
				makeMove(tempBoard2,i);

				if (tempBoard2.currentPlayer == board.currentPlayer && boardSize > tempBoard2.countIllegalMoves())
				{
					//updateTraverselogOutput("MIN:3.1\n");
					value = maxValue(tempBoard,depth+1,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
				else
				{
					//updateTraverselogOutput("MIN:3.2\n");
					value = minValue(tempBoard,depth+1,i,localAB);
					if (tempBoard2.currentPlayer == 0)
						continue;
				}
			}

			afterMoves = boardMoves.size();
			if (value < chosenValue)
			{
				if (depth == 1)
					chosenIndex = board.getPitNumber(boardInstance.currentPlayer,pit);
				chosenValue = value;
			}
			else
			{
				for(int j=1;j<=afterMoves-beforeMoves;j++)
					boardMoves.remove(boardMoves.size()-j);
			}

			if (ab.abUsed)
				if (chosenValue <= ab.alpha)
				{
					if (depth == 0)
					{
						String st = "root,0,"+chosenValue;
						if (ab.alpha == 10000 && ab.beta == -10000)
							updateTraverselogOutput(st+",-Infinity,Infinity\n");
						else if (ab.alpha == -10000)
							updateTraverselogOutput(st+",-Infinity,"+ab.beta+"\n");
						else if (ab.beta == 10000)
							updateTraverselogOutput(st+","+ab.alpha+",Infinity\n");
						else
							updateTraverselogOutput(st+","+ab.alpha+","+ab.beta+"\n");
					}
					else
						printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,chosenValue,ab);

					if (chosenIndex != -1)
						boardMoves.add(chosenIndex);
					return chosenValue;
				}

			if (ab.abUsed)
				if (chosenValue < ab.beta)
					ab.beta = chosenValue;

			if (depth == 0)
				if (ab.abUsed)
				{
					String st = "root,0,"+chosenValue;
					if (ab.alpha == 10000 && ab.beta == -10000)
						updateTraverselogOutput(st+",-Infinity,Infinity\n");
					else if (ab.alpha == -10000)
						updateTraverselogOutput(st+",-Infinity,"+ab.beta+"\n");
					else if (ab.beta == 10000)
						updateTraverselogOutput(st+","+ab.alpha+",Infinity\n");
					else
						updateTraverselogOutput(st+","+ab.alpha+","+ab.beta+"\n");
				}
				else
					updateTraverselogOutput("root,0,"+chosenValue+"\n");
			else
				printTraverseLogEntry(boardInstance.currentPlayer,pit,depth,chosenValue,ab);
		}

		if (chosenIndex != -1)
			boardMoves.add(chosenIndex);

		return chosenValue;
	}
}