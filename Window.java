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
import java.io.*;
import javafx.stage.*;
import javafx.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import java.lang.Thread;
import java.time.Duration;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;

public class Window extends GameClient implements Runnable {

    private static int turn = -1;

    @Override
    public void run() {
        if(debug){System.out.println("Hello from a thread!");}
        launch();
    }

    private static Circle[][] circleBoard = new Circle[6][7];
    private static Text prompt;

    //Empty constructor
    public Window() {

    }

    protected void setTurn(int t) {
        turn = t;
    }

    @Override
    public void start(Stage primaryStage) {

        if(debug){System.out.println("Creating window");}

        //The base for the ui
        Pane root = new Pane();

        //Set up the circleBoard
        for (int i = 0; i < circleBoard.length; i++) {
            for (int j = 0; j < circleBoard[i].length; j++) {
                circleBoard[i][j] = new Circle((j + 1) * 100, (i + 1) * 100, 39, Paint.valueOf("black"));
                root.getChildren().add(circleBoard[i][j]);
            }
        }

        //Set the title
        String title = "Connect 4 - ";
        if (me == 1) {
            title += playerName1;
        } else {
            title += playerName2;
        }
        primaryStage.setTitle(title);

        //Create the message
        String message;
        if (turn == me) {
            message = "It's your turn.";
        } else {
            message = "Waiting on ";
            if (me == 1) {
                message += playerName2 + " to make a move...";
            } else {
                message += playerName1 + " to make a move...";
            }
        }
        prompt = new Text(375, 30, message);
        root.getChildren().add(prompt);

        //Create the buttons
        Button[] b = new Button[7];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Button((i + 1) + "");
            b[i].setLayoutX(((i + 1) * 100) - 10);
            b[i].setLayoutY(700);
            root.getChildren().add(b[i]);
        }
        Button CloseWindow = new Button("Ok");
        CloseWindow.setLayoutX(475);
        CloseWindow.setLayoutY(15);

        //show the board
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();

        //Attach actions
        b[0].setOnAction(e -> {
            handleOnAction(1);
        });
        b[1].setOnAction(e -> {
            handleOnAction(2);
        });
        b[2].setOnAction(e -> {
            handleOnAction(3);
        });
        b[3].setOnAction(e -> {
            handleOnAction(4);
        });
        b[4].setOnAction(e -> {
            handleOnAction(5);
        });
        b[5].setOnAction(e -> {
            handleOnAction(6);
        });
        b[6].setOnAction(e -> {
            handleOnAction(7);
        });
        CloseWindow.setOnAction(e -> {
			if(debug){System.out.println("Closing window");}
            primaryStage.close();
		});

        //animation timer to update board with
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                String msg;
                if (!gameOver) {//display a turn prompt
                    if (turn == me) {
                        msg = "It's your turn.";
                    } else {
                        msg = "Waiting on ";
                        if (me == 1) {
                            msg += playerName2 + " to make a move...";
                        } else {
                            msg += playerName1 + " to make a move...";
                        }
                    }
                    updateDisplay(msg, '1', '2');
                } else {//display a gameover prompt. 
                    if (turn == 0) {
						msg = "Draw.";
                    } else if(turn == me){
                        msg = "Sorry, you lost.";
                    } else {
                        msg = "You won!";
					}

                    updateDisplay(msg, '1', '2');
                    try {
                        root.getChildren().add(CloseWindow);
                    } catch (Exception e) {//to prevent window crashes after buttons are already added. 
                    }
                }
            }

        };
        timer.start();

    }

    public void handleOnAction(int move) {
        //If it is my turn
        if (turn == me) {
            System.out.println("Attempting move at column " + move);
            pipeWriter.println(move);
            pipeWriter.flush();
        } else {
            System.out.println("Wait your turn!");
        }
    }

    public static void main(String[] args) {
        Thread t = new Thread(new Window());
        t.start();

    }

    public void updateDisplay(String PromptMsg, char player1, char player2) {
        for (int i = 0; i < circleBoard.length; i++) {
            for (int j = 0; j < circleBoard[i].length; j++) {
                if (board[i][j] == player1) {
                    circleBoard[i][j].setFill(Paint.valueOf("red"));
                } else {
                    if (board[i][j] == player2) {
                        circleBoard[i][j].setFill(Paint.valueOf("blue"));
                    }else{
                        circleBoard[i][j].setFill(Paint.valueOf("black"));
                    }
                }
            }
        }
        if (PromptMsg != null) {
            prompt.setText(PromptMsg);
        }
    }
}
