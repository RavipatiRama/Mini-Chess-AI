import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

//This program plays 6x6 chess game with alpha beta pruning
public class ChessOne
{
    private final char EMPTY = ' ';         //empty slot
    private final int MIN = 0;              //min level
    private final int MAX = 1;              //max level
    private final int LIMIT = 6;            //depth limit
    
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
    
    
    public ArrayList<Character> createCompList()
    {
        ArrayList<Character> temp = new ArrayList<Character>();
        temp.add('R'); temp.add('K'); temp.add('B');
        temp.add('B'); temp.add('R'); temp.add(' ');
        Collections.shuffle(temp);
        return temp;
    }
    
    public ArrayList<Character> createPlayerList()
    {
        ArrayList<Character> temp = new ArrayList<Character>();
        temp.add('r'); temp.add('k'); temp.add('b');
        temp.add('b'); temp.add('r'); temp.add(' ');
        Collections.shuffle(temp);
        return temp;
    }
    
    private Board board;
    private int size;
    
    private ArrayList<Character> compKilled;
    private ArrayList<Character> playerKilled;
    
    public ChessOne(int size)
    {
        this.board = new Board(size);
        this.size = size;
        
        compKilled = new ArrayList<Character>();
        playerKilled = new ArrayList<Character>();
    }
    
    //Method plays game
    public void play()
    {
        displayBoard(board);                        //start board
        System.out.println();
        
        while(true)                                 //computer and player take turns
        {
            board = playerMove(board);              //player move
            
            //LinkedList<Board> list = generate(board);
            //printChildList(list);
            //System.out.println("Size: " + list.size());
            
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
                
                //if computer piece killed, add to list
                if(board.array[toI][toJ] != EMPTY && Character.isUpperCase(board.array[toI][toJ]))
                    compKilled.add(board.array[toI][toJ]);
                    
                //display board
                displayBoard(board);
                System.out.println();
                
                /*System.out.print("Computer piece killed: "); printKillList(compKilled);
                System.out.println();
                System.out.print("Player piece killed: "); printKillList(playerKilled);
                System.out.println("\n");*/
                
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
        
        /*System.out.print("Computer piece killed: "); printKillList(compKilled);
        System.out.println();
        System.out.print("Player piece killed: "); printKillList(playerKilled);
        System.out.println("\n");*/
        
        return result;                              //return updated board
    }
    
    //Method computes minmax value of board
    private int minmax(Board board, int level, int depth, int alpha, int beta)
    {
        if(!lookForKing(board, 'K') || !lookForKing(board, 'k') || depth >= LIMIT)
        {
            return evaluate(board); //if board leaf
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
        
        ArrayList<Integer> grabI = new ArrayList<Integer>();
        ArrayList<Integer> grabJ = new ArrayList<Integer>();
        
        ArrayList<Integer> lcI = new ArrayList<Integer>();
        ArrayList<Integer> lcJ = new ArrayList<Integer>();
        
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(Character.isUpperCase(board.array[i][j]))
                {
                    grabI.add(i);
                    grabJ.add(j);
                }
                if(!Character.isUpperCase(board.array[i][j]))
                {
                    lcI.add(i);
                    lcJ.add(j);
                }
            }
        }
        
        for(int k = 0; k < grabI.size(); k++)
        {
            Board child = copy(board);
            
            int I = 0;
            int J = 0;
            for(int m = 0; m < size; m++)
            {
                for(int n = 0; n < size; n++)
                {
                    if(child.array[m][n] == 'k')
                    {
                        I = m;
                        J = n;
                        break;
                    }
                }
            }
            
            int i = grabI.get(k);
            int j = grabJ.get(k);
            
            int x = lcI.get(k);
            int y = lcJ.get(k);
            
            if(child.array[i][j] == 'R')
            {
                int R = distance(i, j, I, J);
                
                if(R > distance(i+1, j, I, J) && !Character.isUpperCase(child.array[i+1][j]))
                {
                    child.array[i+1][j] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
                if(R > distance(i-1, j, I, J) && !Character.isUpperCase(child.array[i-1][j]))
                {
                    child.array[i-1][j] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
                if(R > distance(i, j+1, I, J) && !Character.isUpperCase(child.array[i][j+1]))
                {
                    child.array[i][j+1] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
                if(R > distance(i, j-1, I, J) && !Character.isUpperCase(child.array[i][j-1]))
                {
                    child.array[i][j-1] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
            }
            
            if(child.array[i][j] == 'B')
            {
                int B = distance(i, j, I, J);
                
                if(B > distance(i+1, j+1, I, J) && !Character.isUpperCase(child.array[i+1][j+1]))
                {
                    child.array[i+1][j+1] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
                if(B > distance(i-1, j-1, I, J) && !Character.isUpperCase(child.array[i-1][j-1]))
                {
                    child.array[i-1][j-1] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
                if(B > distance(i-1, j+1, I, J) && !Character.isUpperCase(child.array[i-1][j+1]))
                {
                    child.array[i-1][j+1] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
                if(B > distance(i+1, j-1, I, J) && !Character.isUpperCase(child.array[i+1][j-1]))
                {
                    child.array[i+1][j-1] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
            }
            
            if(child.array[i][j] == 'K')
            {
                int K = distance(x, y, i, j);
                System.out.println("K: " + K);
                if(K <= 1)
                {
                    child.array[x][y] = child.array[i][j];
                    child.array[i][j] = EMPTY;
                }
                /*else
                {
                    if(K > distance(i+1, j, i, j) && !Character.isUpperCase(child.array[i+1][j]))
                    {
                        child.array[i+1][j] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                    if(K > distance(i-1, j, i, j) && !Character.isUpperCase(child.array[i-1][j]))
                    {
                        child.array[i-1][j] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                    if(K > distance(i, j+1, i, j) && !Character.isUpperCase(child.array[i][j+1]))
                    {
                        child.array[i][j+1] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                    if(K > distance(i, j-1, i, j) && !Character.isUpperCase(child.array[i][j-1]))
                    {
                        child.array[i][j-1] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                    if(K > distance(i+1, j+1, I, J) && !Character.isUpperCase(child.array[i+1][j+1]))
                    {
                        child.array[i+1][j+1] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                    if(K > distance(i-1, j-1, I, J) && !Character.isUpperCase(child.array[i-1][j-1]))
                    {
                        child.array[i-1][j-1] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                    if(K > distance(i-1, j+1, I, J) && !Character.isUpperCase(child.array[i-1][j+1]))
                    {
                        child.array[i-1][j+1] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                    if(K > distance(i+1, j-1, I, J) && !Character.isUpperCase(child.array[i+1][j-1]))
                    {
                        child.array[i+1][j-1] = child.array[i][j];
                        child.array[i][j] = EMPTY;
                    }
                }*/
            }
            
            if(!identicalBoard(child, board) && !children.contains(child))
                children.addLast(child);
        }
        
        /*int score = scores(children);
        LinkedList<Board> result = new LinkedList<Board>();
        result.add(children.get(score));
        return result;*/
        return children;
    }
    
    private int scores(LinkedList<Board> children)
    {
        ArrayList<Integer> s = new ArrayList<Integer>();
        for(int i = 0; i < children.size(); i++)
        {
            s.add(countTok(children.get(i)));
        }
        return findMinIndex(s);
    }
    
    private int checkPiece(char c)
    {
        if(c == 'K')
            return 1;
        else if(c == 'R')
            return 2;
        else if(c == 'B')
            return 3;
        else
            return 0;
    }
    
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
    
    private void printChildList(LinkedList<Board> children)
    {
        for(int i = 0; i < children.size(); i++)
        {
            displayBoard(children.get(i));
        }
        System.out.println();
    }
    
    private int evaluate(Board board)
    {
        if(!lookForKing(board, 'k'))        //player lost
        {
            return 12;
        }
        else if(!lookForKing(board, 'K'))   //computer lost
        {
            return -3;
        }
        else
        {
            //return (countMin(board) * countC(board)) - (countMax(board) * countP(board));
            //return countToK(board) - countTok(board);
            return countToK(board);
        }
    }
    
    private int countToK(Board board)
    {
        ArrayList<Integer> distances = new ArrayList<Integer>();
        ArrayList<Character> closest = new ArrayList<Character>();
        
        //PLAYER KING COORDINATES
        int I = 0;
        int J = 0;
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(board.array[i][j] == 'K')
                {
                    I = i;
                    J = j;
                    break;
                }
            }
        }
        
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(board.array[i][j] != EMPTY && Character.isUpperCase(board.array[i][j]))
                {
                    closest.add(board.array[i][j]);
                    distances.add(distance(i, j, I, J));
                }
            }
        }
        
        return findMin(distances);
    }
    
    private int countTok(Board board)
    {
        ArrayList<Integer> distances = new ArrayList<Integer>();
        ArrayList<Character> closest = new ArrayList<Character>();
        
        //PLAYER KING COORDINATES
        int I = 0;
        int J = 0;
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(board.array[i][j] == 'k')
                {
                    I = i;
                    J = j;
                    break;
                }
            }
        }
        
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(board.array[i][j] != EMPTY && Character.isUpperCase(board.array[i][j]))
                {
                    closest.add(board.array[i][j]);
                    distances.add(distance(i, j, I, J));
                }
            }
        }
        
        return findMin(distances);
    }
    
    private int countC(Board board)
    {
        int count = 0;
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(board.array[i][j] == 'K')
                    count += 4;
                if(board.array[i][j] == 'R' || board.array[i][j] == 'B')
                    count += 2;
            }
        }
        return count;
    }
    
    private int countP(Board board)
    {
        int count = 0;
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(board.array[i][j] == 'k')
                    count -= 4;
                if(board.array[i][j] == 'r' || board.array[i][j] == 'b')
                    count -= 2;
            }
        }
        return count;
    }
    
    private void printList(ArrayList<Integer> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            System.out.print(list.get(i) + " ");
        }
        System.out.println();
    }
    
    private int distance(int i, int j, int I, int J)
    {
        int score = 0;
        while(true)
        {
            if(i < I)
            {
                i++;
                score++;
            }
            else if(i > I)
            {
                i--;
                score++;
            }
            
            if(j < J)
            {
                j++;
                score++;
            }
            else if(j > J)
            {
                j--;
                score++;
            }
            
            if(i == I && j == J)
            {
                break;
            }
        }
        return score;
    }
    
    private int distanceB(int i, int j, int I, int J)
    {
        int score = 0;
        if(!((i + j) % 2 == 0 && (I + J) % 2 == 0))
        {
            return Integer.MAX_VALUE;
        }
        while(true)
        {
            if(i+j <= I+J)
            {
                if(i < I && j < J)
                {
                    i++;
                    j++;
                    score++;
                }
                if(i > I && j > J)
                {
                    i--;
                    j--;
                    score++;
                }
                if(i < I && j > J)
                {
                    i++;
                    j--;
                    score++;
                }
                if(i > I && j < J)
                {
                    i--;
                    j++;
                    score++;
                }
                if(i < I && j == J)
                {
                    i++;
                    score++;
                }
                if(i > I && j == J)
                {
                    i--;
                    score++;
                }
                if(i == I && j < J)
                {
                    j++;
                    score++;
                }
                if(i == I && j > J)
                {
                    j--;
                    score++;
                }
                
                if(i == I && j == J)
                {
                    break;
                }
            }
        }
        return score;
    }
    
    private ArrayList<Character> getCurrentAlive(Board board)
    {
        ArrayList<Character> result = new ArrayList<Character>();
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(board.array[i][j] != EMPTY && Character.isUpperCase(board.array[i][j]))
                {
                    result.add(board.array[i][j]);
                }
            }
        }
        return result;
    }
    
    private int findMax(ArrayList<Integer> a)
    {
        int max = 0;
        int maxIndex = 0;
        for(int i = 0; i < a.size(); i++)
        {
            if(max < a.get(i))
            {
                max = a.get(i);
                maxIndex = i;
            }
        }
        return max;
    }
    
    private int findMin(ArrayList<Integer> a)
    {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < a.size(); i++)
        {
            if(min > a.get(i))
            {
                min = a.get(i);
            }
        }
        return min;
    }
    
    private int findMinIndex(ArrayList<Integer> a)
    {
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        for(int i = 0; i < a.size(); i++)
        {
            if(min > a.get(i))
            {
                min = a.get(i);
                minIndex = i;
            }
        }
        return minIndex;
    }
    
    private void printKillList(ArrayList<Character> kill)
    {
        for(int i = 0; i < kill.size(); i++)
        {
            System.out.print(kill.get(i) + " ");
        }
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