package com.project.josias.bluetoothtictactoe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class MainActivity extends AppCompatActivity {

    Button newGame;
    Button matrix[] = new Button[9];
    Chronometer timeElapsed;

    AlertDialog popupEnd;

    Boolean myTurn, xPlayer,  timeRunning, winner;
    int i, k;
    int numOfMoves;

    ConnectionThread connect;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTurn = false;
        xPlayer = false;

        connect = MainBluetoothActivity.getInstance();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                byte[] data = bundle.getByteArray("data");
                String dataString= new String(data);
                if(dataString.equals("New Game")){
                    xPlayer = false;
                    startGame();
                }
                else{
                    setPositionInverse(Integer.parseInt(dataString));
                    myTurn = true;
                }

            }
        };

        innitiateButtons();
        clearButtons();

        timeElapsed = (Chronometer) findViewById(R.id.gameTime);
        timeElapsed.setText("00:00");

        timeRunning = false;
        handleNewGame();

        popupEnd = new AlertDialog.Builder(this).create();

    }

    protected void innitiateButtons() {
        newGame = (Button) findViewById(R.id.buttonNewGame);
        matrix[0] = (Button) findViewById(R.id.button1x1);
        matrix[1] = (Button) findViewById(R.id.button1x2);
        matrix[2] = (Button) findViewById(R.id.button1x3);
        matrix[3] = (Button) findViewById(R.id.button2x1);
        matrix[4] = (Button) findViewById(R.id.button2x2);
        matrix[5] = (Button) findViewById(R.id.button2x3);
        matrix[6] = (Button) findViewById(R.id.button3x1);
        matrix[7] = (Button) findViewById(R.id.button3x2);
        matrix[8] = (Button) findViewById(R.id.button3x3);

    }

    protected void clearButtons() {

        for (i = 0; i < matrix.length; i++) {
            final int j = i;
            matrix[j].setText("");
        }

    }

    protected void handleNewGame() {
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTurn = true;
                xPlayer = true;
                connect.write("New Game".getBytes());
                startGame();
            }
        });

    }
    protected void startGame(){
        numOfMoves = 0;
        winner = false;
        if (timeRunning) {
            stopCount();
            timeRunning = false;
        } else {
            clearButtons();
            startCount();
            timeRunning = true;
        }
        handleMatrix();

    }

    protected void startCount() {
        timeElapsed.setBase(SystemClock.elapsedRealtime());
        timeElapsed.start();
    }

    protected void stopCount() {
        timeElapsed.stop();
    }

    protected void handleMatrix() {
        for (i = 0; i < matrix.length; i++) {
            final int j = i;
            matrix[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(myTurn)
                        setPosition(j);
                }
            });
        }
    }
    protected void setPosition(int j){
        if ((matrix[j].getText().toString() != "O") && (matrix[j].getText().toString() != "X") && timeRunning) {
            if (xPlayer) {
                matrix[j].setText("X");
                connect.write(String.valueOf(j).getBytes());
                myTurn = false;
                numOfMoves++;
                if (numOfMoves >= 5)
                    checkEndGame("X");
            } else {
                matrix[j].setText("O");
                connect.write(String.valueOf(j).getBytes());
                myTurn = false;
                numOfMoves++;
                if (numOfMoves >= 5)
                    checkEndGame("O");
            }
            if (numOfMoves == 9 && !winner)
                noWinner();

        }

    }
    protected void setPositionInverse(int j){
        if (xPlayer) {
            matrix[j].setText("O");
            numOfMoves++;
            if (numOfMoves >= 5)
                checkEndGame("O");
        } else {
            matrix[j].setText("X");
            numOfMoves++;
            if (numOfMoves >= 5)
                checkEndGame("X");
        }
        if (numOfMoves == 9 && !winner)
            noWinner();



    }

    protected void checkEndGame(String player) {
        for (k = 0; k < 3; k++) {
            final int j = k;
            if ((matrix[j*3].getText().toString() == matrix[(j*3)+1].getText().toString()) && (matrix[(j*3)+1].getText().toString() == matrix[(j*3)+2].getText().toString()) && (matrix[j*3].getText().toString() == player)){
                popupEnd.setMessage("Player "+player+" is the Winner!!!");
                popupEnd.setCancelable(true);
                popupEnd.show();
                winner = true;
                endGame();
            }
            else if((matrix[j].getText().toString() == matrix[3+j].getText().toString()) && (matrix[j].getText().toString() == matrix[6+j].getText().toString()) && (matrix[j].getText().toString() == player)){
                popupEnd.setMessage("Player "+player+" is the Winner!!!");
                popupEnd.setCancelable(true);
                popupEnd.show();
                winner = true;
                endGame();
            }
            else if((matrix[0].getText().toString() == matrix[4].getText().toString()) && (matrix[0].getText().toString() == matrix[8].getText().toString()) && (matrix[0].getText().toString() == player)){
                popupEnd.setMessage("Player "+player+" is the Winner!!!");
                popupEnd.setCancelable(true);
                popupEnd.show();
                winner = true;
                endGame();

            }
            else if((matrix[2].getText().toString() == matrix[4].getText().toString()) && (matrix[2].getText().toString() == matrix[6].getText().toString()) &&(matrix[2].getText().toString() ==player)) {
                popupEnd.setMessage("Player "+player+" is the Winner!!!");
                popupEnd.setCancelable(true);
                popupEnd.show();
                winner = true;
                endGame();
            }
        }
    }

    protected void noWinner() {
        popupEnd.setMessage("No Winner!!!");
        popupEnd.setCancelable(true);
        popupEnd.show();
        endGame();
    }
    protected void endGame() {
        stopCount();
        timeRunning = false;

    }
}