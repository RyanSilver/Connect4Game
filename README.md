# Connect4

Ryan Silver, John Stratton
28 November 2016
B438 Game Project
Networked Connect 4


The Game


The game is started by passing an optional command line argument into the console to GameClient. This argument is the IP of the host you want to connect to. If it is left blank, it is assumed you will be hosting the game for another player to connect to. After the two players connect successfully and share their names, the game of Connect 4 is initialized and started.


In the Window that is spawned, players trade turns back and forth by clicking the column they want to drop a piece into until either a player connects four pieces in a row (horizontal, vertical, or diagonal) or the board is filled, resulting in a draw.
Research
For our semester project, we decided to create a networked game of Connect 4, which connects users over the internet and provides a user interface to play the game with. After our initial research, the plan was to code the game in C++ using Windows Sockets to facilitate the connection, however this was later changed to Java as time went on so that we could use code from Homework 2’s Protocol 4 as a starting point. This too was changed later in favor of Java Sockets, as Protocol 4 was suited more for demonstration rather than actual use in our project.
So now with our language chosen, the next step was to research the individual components our game would need. After consulting professors and digging around online, we determined that threading would be necessary so that the planned user interface would be able to run alongside the code required for communication between the two clients. And finally, it was deemed appropriate that we include an actual game to run with this program, so we looked around for example code that others have made for the actual Connect 4 game that we could implement with our networking and user interface.


Program Design
We decided to organize our code into three classes: Connect4, Window, and GameClient. Connect4 holds everything related to the state of the game, including the pieces on the board, turn, player names, and methods to make new moves and check win conditions.
Window is an extending class of GameClient, so it has access to its members, including a copy of the game board. After the game is set up by GameClient, Window’s main is called, which spawns a new thread with the user interface, allowing it to execute independently of GameClient, which needs full use of its own main thread to keep the game logic progressing. In this thread, the user interface is generated and given event handlers for all the buttons, which sends out move attempt data if it is that player’s turn, to be caught and checked by GameClient. An animation timer is also added which continually checks for updates to the gamestate and reflects them by updating the display so that when their opponent makes a move, the player will know right away and respond with their own.
Finally, we have GameClient, which is essentially the main of our program, and where it all comes together. After taking an optional command line argument for IP, GameClient has two modes. The first mode if there is no given IP is for GameClient to run as the host for the game and predictably, the second mode is to connect to the host at the given IP as a client. While the client has access to all the same code as the host, its role here is almost entirely passive when compared to the host; all game actions are initiated and driven by the host.
Once GameClient has determined it is a client machine and successfully connected to the server, it enters a “listening” state, where it is constantly awaiting instructions from the server. When the server needs to send or request data to/from the client, it does so by sending messages in the form of “xx:yyyyyyyyyyyyyyyyy”, where xx is a 2-digit opcode and yyyy is the data. Based on the opcode, the client handles the data in yyyy in different ways. For example, if the server sends opcode 01, it is telling the client to simply print out the data in yyyy, while if it sends opcode 24, it is telling the client to make a move and that it expects the move data back in return. Everything the client does at this point is determined by these opcodes given from the server, so every logical action the client needs to make must have an associated opcode here.
Server-side, once the connection to the client is made, the server asks its own user and also polls the client (using the above opcode system) for their names. Next it sets up the Connect4 game on both itself and the client by randomizing the first turn player and spawning the Window process. At this point, the server enters the main game loop where, based on whose turn it is, the server either asks its player for a move or polls the client for theirs. After the move is validated, it is synced back to the other, the board state is checked for a winner/draw, and the display is updated.


Future Work
Originally, the plan for this project was to create a lobby on the server that more than one client could connect to. They would then be able to choose to create one of several games or join one already made by another player. The lobby system proved to be a bit too much of a job to realistically take on for this project, however we did leave groundwork code in place to where more games could easily be added in the future. We would probably have to rewrite the Window class or at least have separate Window classes for each new game added, but the networking code would remain practically untouched, only needing to add new opcodes based on what actions the new games would require.



Research Bibliography


Connect 4 - http://www.dreamincode.net/forums/topic/97190-connect-four-tutorial/


Java Networking - https://www.tutorialspoint.com/java/java_networking.htm


Threading - https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html


Java API - https://docs.oracle.com/javase/7/docs/api/


UI - https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html 





# MIT License
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
