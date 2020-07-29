import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

//This program plays 6x6 chess game with alpha beta pruning
public class Chess
{
    private final char EMPTY = ' ';         //empty slot
    private final int MIN = 0;              //min level
    private final int MAX = 1;              //max level
    private final int LIMIT = 10;            //depth limit
    
    //Board class (inner class)
    private class Board
    {
        private char[][] array;             //board array
        
        //Constructor of Board class
        private Board(int size)
        {
            array = new char[size][size];   //create array
            
            //fill top row with computer pieces (uppercase)
            ArrayList<Character> c = createCompList();
            
            //fill bottom row with player pieces (lowercase)
            ArrayList<Character> p = createPlayerList();
            
            for(int i = 0; i < size; i++)
            {
                for(int j = 0; j < size; j++)
                {
                    array[i][j] = EMPTY;    //fill up array with empty space
                }
            }
            
            for(int k = 0; k < size; k++)
            {
                for(int j = 0; j < size; j++)
                {
                    array[0][j] = c.get(j);         //fill top row
                    array[size-1][j] = p.get(j);    //fill bottom row
                }
            }
        }
    }
    
    //Method creates a list of uppercase computer pieces
    public ArrayList<Character> createCompList()
    {
        //create list and add 5 pieces and blank
        ArrayList<Character> temp = new ArrayList<Character>();
        temp.add('R'); temp.add('K'); temp.add('B');
        temp.add('B'); temp.add('R'); temp.add(' ');
        
        Collections.shuffle(temp);  //shuffle to start with new order
        return temp;                //return list
    }
    
    //Method creates a list of lowercase player pieces
    public ArrayList<Character> createPlayerList()
    {
        //create list and add 5 pieces and blank
        ArrayList<Character> temp = new ArrayList<Character>();
        temp.add('r'); temp.add('k'); temp.add('b');
        temp.add('b'); temp.add('r'); temp.add(' ');
        
        Collections.shuffle(temp);  //shuffle to start with new order
        return temp;                //return list
    }
    
    private Board board;                            //game board
    private int size;                               //size of board (6x6)
    
    //Constructor of Chess class
    public Chess(int size)
    {
        this.board = new Board(size);               //create game board
        this.size = size;                           //assign board size
    }
    
    //Method plays game
    public void play()
    {
        displayBoard(board);                        //start board
        System.out.println();
        
        while(true)                                 //computer and player take turns
        {
            board = playerMove(board);              //player move
            
            if(!lookForKing(board, 'K'))            //look for computer King
            {
                System.out.println("Player wins");  //if not found then player wins
                break;
            }
            
            board = computerMove(board);            //computer move
            
            if(!lookForKing(board, 'k'))            //look for player king
            {
                System.out.println("Computer wins");//if not found then computer wins
                break;
            }
        }
    }
    
    //Method checks if king is alive
    private boolean lookForKing(Board board, char king)
    {
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                if(board.array[i][j] == king)
                    return true;
                    
        return false;
    }
    
    //Method performs player move
    private Board playerMove(Board board)
    {
        System.out.print("Player move: ");      //prompt player
        
        Scanner key = new Scanner(System.in);   //scanner object
        
        while(true)                //run until proper input from player
        {
            //read move
            int fromI = key.nextInt();
            int fromJ = key.nextInt();
            int toI = key.nextInt();
            int toJ = key.nextInt();
            
            //if player try to move computer piece
            if(Character.isUpperCase(board.array[fromI][fromJ]))
            {
                System.out.println("That is a computer piece. Try again.");
            }
            //if player try to move empty space
            else if(board.array[fromI][fromJ] == EMPTY)
            {
                System.out.println("That is an empty space. Try again.");
            }
            //if player try to kill own piece
            else if(!Character.isUpperCase(board.array[toI][toJ]) && board.array[toI][toJ] != EMPTY)
            {
                System.out.println("You cannot kill your own piece. Try again.");
            }
            //if player try to move multiple steps
            else if(Math.abs(toI - fromI) > 1 || Math.abs(toJ - fromJ) > 1)
            {
                System.out.println("You cannot move more than one space away. Try again.");
            }
            else    //player input correct move
            {
                //move piece to correct place
                board.array[toI][toJ] = board.array[fromI][fromJ];
                board.array[fromI][fromJ] = EMPTY;
                    
                //display board
                displayBoard(board);
                System.out.println();
                
                break;      //break after correct input
            }
        }
        
        return board;       //return updated board
    }
    
    //Method determines computer move
    private Board computerMove(Board board)
    {
        LinkedList<Board> children = generate(board);       //generate children of board
        
        int maxIndex = 0;
        int maxValue = minmax(children.get(0), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        for(int i = 1; i < children.size(); i++)    //find child with largest minmax value
        {
            int currentValue = minmax(children.get(i), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            if(currentValue > maxValue)
            {
                maxIndex = i;
                maxValue = currentValue;
            }
        }
        
        Board result = children.get(maxIndex);      //choose child as next move
        System.out.println("Computer move: ");      //print next move
        displayBoard(result);
        System.out.println();
        
        return result;                              //return updated board
    }
    
    //Method computes minmax value of board
    private int minmax(Board board, int level, int depth, int alpha, int beta)
    {
        if(depth >= LIMIT)
        {
            return evaluate(board);         //evaluate board at leaf
        }
        else if(!lookForKing(board, 'K'))   //Computer wins
        {
            return 999999;
        }
        else if(!lookForKing(board, 'k'))   //Player wins
        {
            return -999999;
        }
        else if(level == MAX)       //if board is at max level
        {
            int maxValue = Integer.MIN_VALUE;
            
            LinkedList<Board> children = generate(board);   //generate children of board
            
            for(int i = 0; i < children.size(); i++)
            {
                //find maximum of minmax values of children
                int currentValue = minmax(children.get(i), MIN, depth+1, alpha, beta);
                
                if(currentValue > maxValue)
                    maxValue = currentValue;
                    
                if(maxValue >= beta)    //if maximum exceeds beta stop
                    return maxValue;
                    
                if(maxValue > alpha)    //if maximum exceeds alpha update alpha
                    alpha = maxValue;
            }
            
            return maxValue;            //return maximum value
        }
        else                        //if board is at min level
        {
            int minValue = Integer.MAX_VALUE;
            
            LinkedList<Board> children = generate(board);   //generate children of board
            
            for(int i = 0; i < children.size(); i++)
            {
                //find minimum of minmax values of children
                int currentValue = minmax(children.get(i), MAX, depth+1, alpha, beta);
                
                if(currentValue < minValue)
                    minValue = currentValue;
                    
                if(minValue <= alpha)   //if minimum is less than alpha stop
                    return minValue;
                    
                if(minValue < beta)     //if minimum is less than beta update beta
                    beta = minValue;
            }
            
            return minValue;            //return minimum value
        }
    }
    
    //Method generates children of board
    private LinkedList<Board> generate(Board board)
    {
        LinkedList<Board> children = new LinkedList<Board>();
        
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                Board child = copy(board);
                
                //CREATE EVERY POSSIBLE BOARD AND ADD TO CHILDREN
                
                //ROOK PIECE
                if(child.array[i][j] == 'R')
                {
                    if(i == 0 && j == 0)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == 0 && j == size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j == 0)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j == size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == 0 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(j == 0 && i > 0 && i < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(j == size - 1 && i > 0 && i < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i > 0 && i < size - 1 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                }
                
                //BISHOP PIECE
                if(child.array[i][j] == 'B')
                {
                    if(i == 0 && j == 0)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == 0 && j == size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j == 0)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j == size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == 0 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(j == 0 && i > 0 && i < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(j == size - 1 && i > 0 && i < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i > 0 && i < size - 1 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                }
                
                //KING PIECE
                if(child.array[i][j] == 'K')
                {
                    if(i == 0 && j == 0)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == 0 && j == size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j == 0)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j == size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == 0 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(j == 0 && i > 0 && i < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(j == size - 1 && i > 0 && i < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i == size - 1 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                    if(i > 0 && i < size - 1 && j > 0 && j < size - 1)
                    {
                        if(!Character.isUpperCase(child.array[i+1][j+1]))
                        {
                            child.array[i+1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j-1]))
                        {
                            child.array[i-1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j+1]))
                        {
                            child.array[i-1][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j-1]))
                        {
                            child.array[i+1][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i+1][j]))
                        {
                            child.array[i+1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i-1][j]))
                        {
                            child.array[i-1][j] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j+1]))
                        {
                            child.array[i][j+1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                        else if(!Character.isUpperCase(child.array[i][j-1]))
                        {
                            child.array[i][j-1] = child.array[i][j];
                            child.array[i][j] = EMPTY;
                        }
                    }
                }
                
                //add child to list if it does not already exist in it
                if(!identicalBoard(child, board) && !children.contains(child))
                    children.addLast(child);
            }
        }
        
        return children;
    }
    
    //Method returns true if 2 boards are identical
    private boolean identicalBoard(Board child, Board board)
    {
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(child.array[i][j] != board.array[i][j])
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    //Method evaluates a board
    private int evaluate(Board board)
    {
        if(!lookForKing(board, 'k'))
            return 999999;               //score if computer high chance to win
        else if(!lookForKing(board, 'K'))
            return -999999;              //score if player high chance to win
        else
            return countC(board) + countP(board);         //calculate evaluation of board
    }
    
    //Method assigns scores to each computer piece and adds them up
    private int countC(Board board)
    {
        int score = 0;  //assign score to 0
        
        for(int i = 0; i < size; i++)       //iterate through board
        {
            for(int j = 0; j < size; j++)
            {
                //assign points and add up score
                if(board.array[i][j] == 'K')
                    score += scoreK(i, j);
                if(board.array[i][j] == 'R')
                    score += scoreR(i, j);
                if(board.array[i][j] == 'B')
                    score += scoreB(i, j);
            }
        }
        
        return score;   //return final count score
    }
    
    //Method assigns scores to each player piece and adds them up
    private int countP(Board board)
    {
        int score = 0;  //assign score to 0
        
        for(int i = 0; i < size; i++)       //iterate through board
        {
            for(int j = 0; j < size; j++)
            {
                //assign points and add up score
                if(board.array[i][j] == 'k')
                    score -= scoreK(i, j);
                if(board.array[i][j] == 'r')
                    score -= scoreR(i, j);
                if(board.array[i][j] == 'b')
                    score -= scoreB(i, j);
            }
        }
        
        return score;   //return final count score
    }
    
    //Method counts score for king
    private int scoreK(int i, int j)
    {
        if(i > 0 && i < size-1 && j > 0 && j < size-1)
            return 8;
        else
            return 5;
    }
    
    //Method counts score for rook
    private int scoreR(int i, int j)
    {
        if(i > 0 && i < size-1 && j > 0 && j < size-1)
            return 4;
        else
            return 3;
    }
    
    //Method counts score for bishop
    private int scoreB(int i, int j)
    {
        if(i > 0 && i < size-1 && j > 0 && j < size-1)
            return 3;
        else
            return 2;
    }
    
    //Method makes a copy of a board
    private Board copy(Board board)
    {
        Board result = new Board(size);
        
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                result.array[i][j] = board.array[i][j];
                
        return result;
    }
    
    //Method displays a board
    private void displayBoard(Board board)
    {
        System.out.println("-------------");
        for(int i = 0; i < size; i++)
        {
            System.out.print("|");
            for(int j = 0; j < size; j++)
            {
                System.out.print(board.array[i][j] + "|");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }
}