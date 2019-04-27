
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cristine Artanty
 */
public class TicTacToe implements Cloneable {
    //constants used to a move made by player or computer
    private final int NOBODY_TURN = 0;
    private final int PLAYER_TURN = 1;
    private final int COMPUTER_TURN = -1;
    
    //the mark denoting moves made by player or computer
    private final char NOBODY_MARK = ' ';
    private final char PLAYER_MARK = 'X';
    private final char COMP_MARK = 'O';
    
    //the level of the games
    private final int GAME_LEVEL = 5;
    
    //to describe whose turn it is to make a move
    private int whoseTurn = NOBODY_TURN;
    
    //describe the who will begin the game
    private int firstTurn = NOBODY_TURN;
    
    //array to represent the current game board
    private char[] board = new char[9];
    
    //an array reprsenting the current set of moves made in this game
    //moves[i] will contain a 0 if grid i is unavaible
    //or a 1 if it is avail
    private int[] moves = new int[9];
    
    //to count how many move have been made during the game
    private int countMoves = 0;
    
    public TicTacToe(){//generate new game
        
        //fill out the board so it is empty
        for(int i=0; i<9; i++){
            board[i] = NOBODY_MARK;
        }
        
        generateMoves();
    }
    
    public void setFirstTurn(int firstTurn){
        this.firstTurn = firstTurn;
    }
    
    public int getFirstTurn(){
        return this.firstTurn;
    }
    
    public int getPlayer_Turn(){
        return PLAYER_TURN;
    }
    
    public int getComputer_Turn(){
        return COMPUTER_TURN;
    }
    
    public int [] getMoves(){
        return moves;
    }
    
    public int getWhoseTurn(){
        return this.whoseTurn;
    }
    
    public void setWhoseTurn(int whoseTurn){
        this.whoseTurn = whoseTurn;
    }
    
    public TicTacToe clone() throws CloneNotSupportedException{
        //clone the arrays since they're threated as obj in java
        TicTacToe clone = (TicTacToe)super.clone();
        clone.board = this.board.clone();
        clone.moves = this.moves.clone();
        
        return clone;
    }
    
    public void chooseFirstPlayer(){
        //randomly choose who will go first
        Random randGenerator = new Random();
        
        if(randGenerator.nextInt(2)==0){
            setFirstTurn(PLAYER_TURN);
        }
        else{
            setFirstTurn(COMPUTER_TURN);
        }
    }
    
    //return a string describe the state of the board
    //this string can be used by the client to draw the board
    public String drawBoard(){
        String output = "";
        
        for(char space : board){
            if(space == PLAYER_MARK){
                output += "1";
            }
            else if(space == COMP_MARK){
                output += "2";
            }
            else{
                output += "-";
            }
        }
        return output+"\n";
    }
    
    public int [] generateMoves(){
        //generate the list of possible move
        for(int i=0; i<moves.length; i++){
            moves[i] = (board[i]==NOBODY_MARK) ? 1 : 0;
            //if it is not taken, mark the board as avail
            //otherwise it is unavail
        }
        return moves;
    }
    
    public int [] generateLegalMoves(){
        //create the list of the legal moves from the moves set
        
        int [] legalMoves = new int[0];
        
        for(int i=0; i<moves.length; i++){
            if(moves[i]==1){
                //if the moves is available, add it to the legal array
                int [] tempLM = legalMoves;
                legalMoves = new int[tempLM.length + 1];//the legal moves array increase size by 1
                
                for(int j=0; j<tempLM.length; j++){
                    legalMoves[j] = tempLM[j];
                }
                
                legalMoves[legalMoves.length-1]=i;
            }
        }
        return legalMoves;
    }
    
    public boolean legalMove(int move){
        //return false if move out from the bound
        if(move<0 || move>8){
            return false;
        }
        
        //true if move is avail , false if not
        return moves[move]==1;
    }
    
    public void computerMove() throws CloneNotSupportedException{
        //method to generate computer move
        int computerMove = bestMove();
        placePiece(COMPUTER_TURN,computerMove);
    }
    
    public int bestMove() throws CloneNotSupportedException{
        //determine the best move for the computer
        
        //Keeps track of the guess value of the best move and the guess value
        int bestValue;
        int currentValue;
        
        //store the best move fount so far and the next move to try
        int best;
        int tryNext;
        
        //temporary obj to store copy of the current game
        TicTacToe temp;
        
        //store the set of legal move from the temp board
        int [] legalMove;
        
        //to copy current board and generate the possible move
        temp = this.clone();
        legalMove = temp.generateLegalMoves();
        
        //start determine with place in the legalMove in index 0
        tryNext = legalMove[0];
        
        temp.placePiece(COMPUTER_TURN,tryNext);
        bestValue = temp.bestGuess(GAME_LEVEL);
        
        //store the value from the first possible in best variable
        best = tryNext;
        
        //try every remain possible
        int currIndex = 1;
        while(currIndex<legalMove.length){
            //try every remaining possible, make a new board
            temp = this.clone();
            tryNext = legalMove[currIndex];
            
            temp.placePiece(COMPUTER_TURN,tryNext);
            
            //determine chance to win with the move
            currentValue = temp.bestGuess(GAME_LEVEL);
            
            if((temp.getWhoseTurn() == COMPUTER_TURN && currentValue > bestValue) ||
                    temp.getWhoseTurn() != COMPUTER_TURN && currentValue < bestValue){
                bestValue = currentValue;
                best = tryNext;
            }
            
            currIndex++;
        }
        return best;
    }
    
    public int bestGuess(int level) throws CloneNotSupportedException{
        int bestValue;
        int currValue;
        
        int tryNext;
        
        TicTacToe temp;
        
        int [] legalMove = this.generateLegalMoves();
        
        if(level==0 || this.isOver()){
            //if we in the base level or the game is over
            //just return how well the computer has done
            
            return judge();
        }
        
        temp = this.clone();//make copy of the curr game
        
        if(level%2==0){
            temp.setWhoseTurn(COMPUTER_TURN);
        }
        else{
            temp.setWhoseTurn(PLAYER_TURN);
        }
        
        tryNext = legalMove[0];
        temp.placePiece(temp.getWhoseTurn(),tryNext);
        bestValue = temp.bestGuess(level-1);
        
        int currInd = 1;
        while(currInd<legalMove.length){
            temp = this.clone();
            tryNext = legalMove[currInd];
            temp.placePiece(temp.getWhoseTurn(),tryNext);
            
            currValue = temp.bestGuess(level-1);
            
            if(temp.getWhoseTurn()==PLAYER_TURN){
                bestValue = Math.max(bestValue, currValue);
            }
            else{
                bestValue = Math.min(bestValue, currValue);
            }
            
            currInd++;
        }
        return bestValue;
    }
    
    public void placePiece(int p, int move){
        board[move]=(p==PLAYER_TURN) ? PLAYER_MARK : COMP_MARK;
        countMoves++;
        
        //generate the new set of moves taken/not taken
        generateMoves();
    }
    
     /**
     * Returns an integer value based on examining the state of the game:
     * 0 - game is on-going
     * 1 - player has won
     * 2 - computer has won
     * 3 - game is a tie
     */
    public int result(){
        //first check the columns to see if there are any winners
        for(int i=0; i<3; i++){
            //has player won?
            if(board[i]==PLAYER_MARK && board[i+3]==PLAYER_MARK && board[i+6]==PLAYER_MARK){
                return 1;
            }
            else if(board[i]==COMP_MARK && board[i+3]==COMP_MARK && board[i+6]==COMP_MARK){
                return 2;
            }
        }
        
        //then check row
        for(int i=0; i<=6; i+=3){
            //has the player won?
            if(board[i]==PLAYER_MARK && board[i+1]==PLAYER_MARK && board[i+2]==PLAYER_MARK){
                return 1;
            }
            else if(board[i]==COMP_MARK && board[i+1]==COMP_MARK && board[i+2]==COMP_MARK){
                return 2;
            }
        }
        
        //check diagonal
        if(board[0]==PLAYER_MARK && board[4]==PLAYER_MARK && board[8]==PLAYER_MARK){
            return 1;
        }
        else if(board[2]==PLAYER_MARK && board[4]==PLAYER_MARK && board[6]==PLAYER_MARK){
            return 1;
        }
        
        
        if(board[0]==COMP_MARK && board[4]==COMP_MARK && board[8]==COMP_MARK){
            return 2;
        }
        else if(board[2]==COMP_MARK && board[4]==COMP_MARK && board[6]==COMP_MARK){
            return 2;
        }
        
        //if there are 9 moves, the game is a draw
        if(countMoves==9){
            return 3;
        }
        else{
            //otherwise the game continues
            return 0;
        }
    }
    
    /**
     * Returns an integer value describing how good the current situation is for the computer.
     * 100: computer has won
     * 50:  computer/player are tied
     * 0:   the player has won
     */
    public int judge(){
        switch(result()){
            case 0:
                return 50;
            case 1:
                return 0;
            case 2:
                return 100;
        }
        return -1;
    }
    
    /**
     * @return true if the game is over or false if it is not.
     */
    public boolean isOver()
    {
      return(result() != 0);
    }
}
