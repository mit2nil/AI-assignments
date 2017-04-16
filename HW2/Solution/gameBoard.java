/* Import in built java packages */
import java.io.*;
import java.util.ArrayList;

public class gameBoard
{
	/* Fields */
    protected ArrayList<Integer> currentPos = new ArrayList<Integer>();
    protected int currentPlayer;
    protected int boardSize; // N in 2XN

	/* Constructor */
	public gameBoard()
	{
		// Nothing here
        currentPlayer = 0;
        boardSize = 0;
	}

    protected int countIllegalMoves()
    {
		int totalIllegal = 0;

		for(int i=0;i<boardSize;i++)
            if (currentPlayer == 1)
                if (currentPos.get(i+1) == 0)
                    totalIllegal++;
            else
                if (currentPos.get((2*boardSize+2)-(i+1)) == 0)
                    totalIllegal++;
		return totalIllegal;
	}

    protected int countIllegalMoves(int player)
    {
		int totalIllegal = 0;

		for(int i=0;i<boardSize;i++)
            if (player == 1)
                if (currentPos.get(i+1) == 0)
                    totalIllegal++;
            else if (player == 2)
                if (currentPos.get((2*boardSize+2)-(i+1)) == 0)
                    totalIllegal++;
			else
				totalIllegal = -1;
		return totalIllegal;
	}

	//B3 means player=1, pit=2
	//A6 means player=2, pit=5
	protected boolean isLegal(int pit)
    {
		if (currentPlayer == 1)
		{
			if (1 <= pit && pit <= boardSize)

				if (currentPos.get(pit) == 0)
					return false;
				else
					return true;
		}
		else if (currentPlayer == 2)
		{
			if (1 <= pit && pit <= boardSize)
				if (currentPos.get((2*boardSize+2)-pit) == 0)
					return false;
				else
					return true;
		}
		else
			return false;
		return false;
	}

	//B3 means player=1, pit=2
	//A6 means player=2, pit=5
	protected boolean isLegal(int pit, int player)
    {
		if (player == 1)
		{
			if (1 <= pit && pit <= boardSize)

				if (currentPos.get(pit) == 0)
					return false;
				else
					return true;
		}
		else if (player == 2)
		{
			if (1 <= pit && pit <= boardSize)
				if (currentPos.get((2*boardSize+2)-pit) == 0)
					return false;
				else
					return true;
		}
		else
			return false;
		return false;
	}

    protected void boardClone(gameBoard cloned)
	{
		cloned.currentPlayer = this.currentPlayer;
        cloned.boardSize = this.boardSize;
		// Create different copy of the board
        cloned.currentPos.clear();
		for(int i=0;i<this.currentPos.size();i++)
			cloned.currentPos.add(this.currentPos.get(i));
	}

	protected void boardClone(gameBoard cloned, int player)
	{
		if (player != 1 || player != 2)
		{
			cloned.currentPlayer = player;
	        cloned.boardSize = this.boardSize;
			// Create different copy of the board
	        cloned.currentPos.clear();
			for(int i=0;i<this.currentPos.size();i++)
				cloned.currentPos.add(this.currentPos.get(i));
		}
	}

	// Read current board state in predefined format and return as a String
    protected String getBoardInArray()
	{
		String st = "";
		for(int i=0;i<currentPos.size();i++)
			st = st+currentPos.get(i);
		st = st+"\n";
		return st;
	}

    // Read current board state in predefined format and return as a String
    protected String getBoard()
	{
		String st = "";
		for(int i=0;i<boardSize;i++)
		{
			st = st+currentPos.get((2*boardSize+2)-(i+1))+" ";
		}
		st = st+"\n";
		// Print player 1 stones
		for(int i=0;i<boardSize;i++)
		{
			st = st+currentPos.get(i+1)+" ";
		}
		st = st+"\n";
		st = st+currentPos.get(0)+"\n";
		st = st+currentPos.get(boardSize+1);
		return st;
	}

    protected void printBoardInArray()
	{
		System.out.println(getBoardInArray());
	}

	protected void printBoard()
    {
		System.out.println(getBoard());
	}

    protected int evalBoard()
    {
        int value = 0;
        if (currentPlayer == 1)
            value = currentPos.get(boardSize+1) - currentPos.get(0);
        else
            value = currentPos.get(0) - currentPos.get(boardSize+1);
        return value;
    }

    protected int evalBoard(int player)
    {
        int value = 0;
        if (player == 1)
            value = currentPos.get(boardSize+1) - currentPos.get(0);
        else if (player == 2)
            value = currentPos.get(0) - currentPos.get(boardSize+1);
		//else
			//Not sure what to return
        return value;
    }

    protected int getPitNumber(int player, int index)
    {
    	if (player == 1)
    		return index;
		else if (player == 2)
			return 2*boardSize + 2 - index;
		else
			return -1;//error
	}

	protected int getPitNumber(int index)
    {
    	if (currentPlayer == 1)
    		return index;
		else
			return 2*boardSize + 2 - index;
	}
}