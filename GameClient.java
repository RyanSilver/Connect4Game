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
import java.net.*;
import java.io.*;
import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.*;

public class GameClient extends Application {
	protected static boolean debug = false;
	
	//Declare network data streams
	protected static DataInputStream netIn;
	protected static DataOutputStream netOut;
	
	//Gamestate data
	protected static String playerName1 = "";
	protected static String playerName2 = "";
	protected static int me; //server = player 1, client = player 2
	protected static int turn = -1;
	protected static char board[][]=new char[6][7];
	protected static boolean gameOver = false;
	
	//Piped in/out streams to send data from thread up to GameClient
	final static PipedOutputStream pipeOut = new PipedOutputStream();
	final static PipedInputStream pipeIn = new PipedInputStream();
	final static PrintWriter pipeWriter = new PrintWriter(pipeOut);
	
	//Kind of inelegant, fix later if possible
	private final static void clearConsole(){
		for(int i=0; i<20; i++){
			System.out.println();
		}
	}
	
	@Override
	public void start(Stage s1){
		//Empty, to be overridden again in Window
	}
	
    public static void main(String args[]) {
		//Connect the piped input and output streams
		try {
			pipeOut.connect(pipeIn);
		} catch(IOException e){
			e.printStackTrace();
		}
		
		//If there is an argument, attempt to connect to host at that IP
		String IP = null;
		if(args.length > 0){
			IP = args[0];
		}
		//Port number to use
        int port = 49516;
		//Used to transmit data as well as display locally
		String msg = "";
		//Used for reading input
		Scanner s = new Scanner(System.in);
		Scanner p = new Scanner(pipeIn);
		
		try{
			if(IP == null){
				//SERVER/HOST
				me = 1;
				//If no IP, host the game and wait for player 2 to join
				ServerSocket serverSoc = new ServerSocket(port);
				System.out.println("Waiting for client on port " + serverSoc.getLocalPort() + "...");
				Socket server = serverSoc.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				
				//Setup network data streams
				DataInputStream netIn = new DataInputStream(server.getInputStream());
				DataOutputStream netOut = new DataOutputStream(server.getOutputStream());
				
				System.out.println();
				System.out.println();
				
				System.out.print("Enter your name: ");
				playerName1 = s.next();
				msg = "03:" + playerName1;
				netOut.writeUTF(msg);
				
				msg = "02:What is your name?";
				netOut.writeUTF(msg);
				playerName2 = netIn.readUTF();
				
				if(debug){
					System.out.println("Player 1's name is: " + playerName1 + " (me)");
					System.out.println("Player 2's name is: " + playerName2);
				}
				
				clearConsole();
				msg = "04:Clear console";
				netOut.writeUTF(msg);
				
				//Create a new window
				Window w = new Window();
				
				System.out.println("Starting a new game of Connect4");
				msg = "01:Starting a new game of Connect4";
				netOut.writeUTF(msg);
				
				//Determine which player gets first turn
				turn = (int)(Math.random() * 2 + 1);
				if(debug){turn = 1;} //turn always 1 for temporary debugging
				w.setTurn(turn);
				msg = "05:" + turn;
				netOut.writeUTF(msg);					
				
				//Set up the Connect4 game on both client and server
				Connect4 c4 = new Connect4(playerName1, playerName2, turn);
				msg = "20:Start a new game";
				netOut.writeUTF(msg);
				
				//Create the window on both client and server
				w.main(null);
				msg = "25:Create window";
				netOut.writeUTF(msg);
				
				//Display the initial board gamestate
				c4.displayStatus();
				msg = "21:Display game state";
				netOut.writeUTF(msg);
				
				//Game loop
				int move = -1;
				int moveHeight = -1;
				boolean validMove = false;
				while(!gameOver){
					if (c4.getTurn() == 1){
						msg = "01:Waiting on " + playerName1 + " to make a move...";
						netOut.writeUTF(msg);
						while(!validMove){
							//our turn
							System.out.println("Your turn - enter a move");
							move = p.nextInt();
							moveHeight = c4.place(move, '1');
							if((move >= 1)&&(move <=7)&&(moveHeight >= 0)){
								//If the move is valid, make the move and continue. Otherwise, ask again
								validMove = true;
								
								//Sync the move to the client
								msg = "22:" + move + "1";
								netOut.writeUTF(msg);
								
								//Pass the turn to the client
								turn = 2;
								c4.setTurn(turn);
								w.setTurn(turn);
								board=c4.getBoard();
								msg = "23:" + turn;
								netOut.writeUTF(msg);
								
								//Clear the console and display the updated board
								clearConsole();
								msg = "04:Clear console";
								netOut.writeUTF(msg);
								c4.displayStatus();
								msg = "21:Display game state";
								netOut.writeUTF(msg);
							} else {
								System.out.println("Move invalid - move=" + move + " moveHeight=" + moveHeight);
							}
						}
						validMove = false;
					} else if(c4.getTurn() == 2){
						//client's turn
						System.out.println("Waiting on " + playerName2 + " to make a move...");
						while(!validMove){
							//Ask the client for their move
							msg = "24:What is your move?";
							netOut.writeUTF(msg);
							move = Integer.parseInt(netIn.readUTF());
							moveHeight = c4.place(move, '2');
							if((move >= 1)&&(move <=7)&&(moveHeight>=0)){
							//If the move is valid, make the move and continue. Otherwise ask again
								validMove = true;
								
								//Sync the move to the client
								msg = "22:" + move + "2";
								netOut.writeUTF(msg);
								
								//Pass the turn to the client
								turn = 1;
								c4.setTurn(turn);
								w.setTurn(turn);
								board=c4.getBoard();
								msg = "23:" + turn;
								netOut.writeUTF(msg);
								
								//Clear the console and display the updated board
								clearConsole();
								msg = "04:Clear console";
								netOut.writeUTF(msg);
								c4.displayStatus();
								msg = "21:Display game state";
								netOut.writeUTF(msg);
							} else {
								msg = "01:Move invalid";
								netOut.writeUTF(msg);
							}
						}
						validMove = false;
					}
					
					//Check for game over
					//moveHeight = 0;
					if(debug){System.out.println("Checking if player at = (" + (moveHeight) + ", " + (move-1) + ") has won the game");}
					int winStatus = c4.check((moveHeight), (move-1));
					if(debug){System.out.println("Win status = " + winStatus);}
					if(winStatus == 1){
						if(turn == 2){
							System.out.println(playerName1 + " wins!");
							msg = "01:" + playerName1 + " wins!";
						} else {
							System.out.println(playerName2 + " wins!");
							msg = "01:" + playerName2 + " wins!";
						}
						netOut.writeUTF(msg);
						gameOver = true;
					} else if(winStatus == 0){
						//Game is not over
					} else if(winStatus == -1){
						//Set the turn to 0 for UI to display draw correctly
						turn = 0;
						c4.setTurn(turn);
						w.setTurn(turn);
						msg = "23:" + turn;
						netOut.writeUTF(msg);
						
						System.out.println("The game is a Draw.");
						msg = "01:The game is a Draw.";
						netOut.writeUTF(msg);
						gameOver = true;
					}
				}
				
				msg = "00:END TRANSMISSION";
				netOut.writeUTF(msg);
				
				server.close();
			} else {
				//CLIENT
				me = 2;
				//If IP is set, attempt to connect to the host at that IP
				
				try{
					System.out.println("Connecting to " + IP + " on port " + port + "...");
					Socket client = new Socket(IP, port);
					System.out.println("Just connected to " + client.getRemoteSocketAddress());
					
					OutputStream outToServer = client.getOutputStream();
					DataOutputStream netOut = new DataOutputStream(outToServer);
					InputStream inFromServer = client.getInputStream();
					DataInputStream netIn = new DataInputStream(inFromServer);
					
					System.out.println();
					System.out.println();
					
					//Create a new window
					Window w = new Window();
					
					//Create a new game using empty constructor
					Connect4 c4 = new Connect4();
					
					String input = "";
					int op;
					String data = "";
					while(!gameOver){
						input = netIn.readUTF();
						//INPUT FORMAT - "2 character opcode:data"
						//EXAMPLE - 01:Hello world
						//OPCODE LIST
						//00 - kill execution
						//01 - display message (01:'message')
						//02 - ask for name input from client
						//03 - give name from server (03:'player name')
						//04 - clear the console
						//05 - set which player gets first turn (05:'turn')
						//20 - start new game of connect 4
						//21 - display game data
						//22 - send new move to client (21:'1 digit column move''1 digit turn')
						//23 - set whose turn it is (22:'1 digit turn')
						//24 - request a move from the client
						//25 - create the game window
						op = Integer.parseInt(input.substring(0,2));
						data = input.substring(3,input.length());
						if(debug){System.out.println("OP=" + op + " Data=" + data);}
						switch(op){
							case 00:
							//00 - kill execution
								gameOver = true;
								break;
							case 01:
							//01 - display message (01:'message')
								System.out.println(data);
								break;
							case 02:
							//02 - ask for name input from client
								System.out.print("Enter your name: ");
								playerName2 = s.next();
								netOut.writeUTF(playerName2);
								break;
							case 03:
							//03 - give name from server (03:'player name')
								playerName1 = data;
								break;
							case 04:
							//04 - clear the console
								clearConsole();
								break;
							case 05:
							//05 - set which player gets first turn (05:'turn')
								turn = Integer.parseInt(data);
								break;
							case 20:
							//20 - start new game of connect 4
								c4.setupGame(playerName1, playerName2, turn);
								w.setTurn(turn);
								break;
							case 21:
							//21 - display game data
								c4.displayStatus();
								break;
							case 22:
							//22 - send new move to client (21:'1 digit column move''1 digit turn')
								c4.place(Integer.parseInt(data.substring(0,1)), data.charAt(1));
								board=c4.getBoard();
								break;
							case 23:
							//23 - set whose turn it is (22:'1 digit turn')
								c4.setTurn(Integer.parseInt(data.substring(0,1)));
								w.setTurn(Integer.parseInt(data.substring(0,1)));
								board=c4.getBoard();
								break;
							case 24:
							//24 - request a move from the client
								String move;
								System.out.println("Your turn - enter a move");
								move = p.next();
								netOut.writeUTF(move);
								break;
							case 25:
							//25 - create the game window
								w.main(null);
								break;
							default:
								System.out.println("Invalid operation code: " + op);
								break;
						}
					}
					client.close();
				} catch(UnknownHostException e){
					System.out.println("Unknown host.");
					System.exit(0);
				}
			}
			
			
		} catch (IOException e) {
            e.printStackTrace();
        }

    }
	
}
