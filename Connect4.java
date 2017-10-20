/*
MIT License

Copyright (c) 2016 Ryan Silver, Brad Stratton

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
public class Connect4 {
	private boolean debug = false;
	
	//private char board[][];//6 by 7 - why does this not work, frustrating. if you can figure it out, uncomment the initialization from the constructor and use this one
	private char board[][] = 
            {{' ', ' ', ' ', ' ', ' ', ' ', ' '}, 
            {' ', ' ', ' ', ' ', ' ', ' ', ' '}, 
            {' ', ' ', ' ', ' ', ' ', ' ', ' '}, 
            {' ', ' ', ' ', ' ', ' ', ' ', ' '}, 
            {' ', ' ', ' ', ' ', ' ', ' ', ' '}, 
            {' ', ' ', ' ', ' ', ' ', ' ', ' '}};//6 by 7
	private String player1;
	private String player2;
	private int turn;
	
	public Connect4(String playerName1, String playerName2, int t){
		//Set player names
		player1 = playerName1;
		player2 = playerName2;
		turn = t;
	}
	
	public Connect4(){
		//Empty constructor
	}
	
	public void setupGame(String playerName1, String playerName2, int t){
		player1 = playerName1;
		player2 = playerName2;
		turn = t;
	}
	
	public int getTurn(){
		return turn;
	}
	
	public void setTurn(int t){
		turn = t;
	}
	
	//Print all info relating the the current status of the game
	public void displayStatus(){
		System.out.println("Players: " + player1 + ", " + player2 );
		String PromptMsg="Turn = ";
		System.out.print("Turn = ");
		if(turn == 1){
			System.out.println(player1);
			PromptMsg+=player1;
		} else if(turn == 2){
			System.out.println(player2);
			PromptMsg+=player2;
		} else {
			System.out.println("???");
		}

		System.out.println(" 1  2  3  4  5  6  7");
		System.out.println("____________________");
		for (int a = 0; a <= 5; a++){
			for (int b = 0; b <= 6; b++){
				if(debug){System.out.print(" (" + a + ", " + b + ") ");}
				System.out.print(" " + board[a][b] + " ");
			}
			System.out.println();
		}
		System.out.println("____________________");
		
		
	}
	public char[][] getBoard(){
		return board;
	}

	//Attempt to make a move in the chosen column 'b' for 'player', return vertical position # if valid, -1 if invalid
	public int place(int b, char player){
		//Decrement b to account for columns going from 0-6 in the array instead of 1-7
		b--;
		//check if input is a valid column
		if (b >= 0 && b <= 6){
			//if empty space exists at top find out where lowest whitespace is
			if (board[0][b] == ' ') {
				int i;
				for (i = 0; board[i][b] == ' '; i++){
					if (i == 5) {
						board[i][b] = player;
						return i;
					}
				}
				i--;
				board[i][b] = player;
				//Return 
				return i;
			} else {
			//if column is full, fail
				return -1;
			}
		}
		//if column does not exist, fail
		else {
			return -1;
		}
	}
	
	//Check if the game is over, 1 = winner, 0 = not over, -1 = draw
	public int check(int a, int b){
		//Check if the player at (a,b) has won
		int vertical = 1;//(|)
		int horizontal = 1;//(-)
		int diagonal1 = 1;//(\)
		int diagonal2 = 1;//(/)

		char player = board[a][b]; //player we are checking for a win
		if(debug){System.out.println("Player at (" + a + ", " + b + ") = " + player);}
		int i;//vertical
		int ii;//horizontal
		
		//check for vertical(|)
		//Check down
		for (i = a + 1; i <= 5; i++){
			if(debug){System.out.println("Checking (" + i + ", " + b + ")");}
			if(board[i][b] == player){
				vertical++;
			} else {
				break;
			}
		}
		//Check up
		for (i = a - 1; i >= 0; i--){
			if(debug){System.out.println("Checking (" + i + ", " + b + ")");}
			if(board[i][b] == player){
				vertical++;
			} else {
				break;
			}
		}
		if(debug){System.out.println("Vertical = " + vertical);}
		if (vertical >= 4){
			return 1;
		}
		
		//check for horizontal(-)
		//Check right
		for (ii = b + 1; ii <= 6; ii++){
			if(debug){System.out.println("Checking (" + a + ", " + ii + ")");}
			if(board[a][ii] == player){
				horizontal++;
			} else {
				break;
			}
		}
		//Check left
		for (ii = b - 1; ii >= 0; ii--){
			if(debug){System.out.println("Checking (" + a + ", " + ii + ")");}
			if(board[a][ii] == player){
				horizontal++;
			} else {
				break;
			}
		}
		if(debug){System.out.println("Horizontal = " + horizontal);}
		if (horizontal >= 4){
			return 1;
		}
		
		
		//check for diagonal 1 (\)
		//up and left
		for (i = a - 1, ii = b - 1; i >= 0 && ii >= 0; i--, ii--){
			if(debug){System.out.println("Checking (" + i + ", " + ii + ")");}
			if(board[i][ii] == player){
				diagonal1++;
				if(debug){System.out.println("diagonal1 = " + diagonal1);}
			} else {
				break;
			}
		}
		//down and right
		for (i = a + 1, ii = b + 1; i <= 5 && ii <= 6; i++, ii++){
			if(debug){System.out.println("Checking (" + i + ", " + ii + ")");}
			if(board[i][ii] == player){
				diagonal1++;
				if(debug){System.out.println("diagonal1 = " + diagonal1);}
			} else {
				break;
			}
		}
		if(debug){System.out.println("Diagonal 1 = " + diagonal1);}
		if (diagonal1 >= 4){
			return 1;
		}
		
		//check for diagonal 2(/)
		//up and right
		for (i = a - 1, ii = b + 1; i >= 0 && ii <= 6; i--, ii++){
			if(debug){System.out.println("Checking (" + i + ", " + ii + ")");}
			if(board[i][ii] == player){
				diagonal2++;
				if(debug){System.out.println("diagonal2 = " + diagonal2);}
			} else {
				break;
			}
		}
		//up and left
		for (i = a + 1, ii = b - 1; i <= 5 && ii >= 0; i++, ii--){
			if(debug){System.out.println("Checking (" + i + ", " + ii + ")");}
			if(board[i][ii] == player){
				diagonal2++;
				if(debug){System.out.println("diagonal2 = " + diagonal2);}
			} else {
				break;
			}
		}
		if(debug){System.out.println("Diagonal 2 = " + diagonal2);}
		if (diagonal2 >= 4){
			return 1;
		}
		
		//if all columns are full, the game ends in a draw
		for (i = 0; i <= 6; i++) {
			if (board[0][i] != ' ') {
				if (i == 6) {
					return -1;
				}
			}
			else {
				break;
			}
		}
		if(debug){System.out.println("Full checked");}

		return 0;
	}
}
