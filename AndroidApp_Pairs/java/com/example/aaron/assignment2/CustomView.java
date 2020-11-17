package com.example.aaron.assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Dimension;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CustomView extends View {
    //Colour
    private Paint cardBack, transparent, white;

    //Bitmap
    private Bitmap spade, diamond, heart, club, jack, queen, king, joker;

    private int screenWidth;
    private int screenHeight;

    //the card
    private Rect card;
    private int cardSize;

    //Arrays to hold information about the board
    private int [][] stateArray;
    private int [][] pairArray;
    private Bitmap [][] symbolArray;

    private int playerOneScore;
    private int playerTwoScore;
    private int c_player;
    private int winning;

    //How many cards have been flipped this turn
    private int cardsTurned;
    private int cardOne;
    private int cardTwo;

    //======================================================================
    public CustomView(Context c){
        super(c);
        init();
    }
    public CustomView(Context c, AttributeSet as){
        super(c, as);
        init();
    }
    public CustomView(Context c, AttributeSet as, int default_style){
        super(c, as, default_style);
        init();
    }
    //Initialize variables==================================================
    private void init(){

        playerOneScore = 0;
        playerTwoScore = 0;
        c_player = 1;
        winning = 0;

        cardsTurned = 0;


        //Initialize our board arrays
        stateArray = new int[4][4];
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                stateArray [i][j] = 1;
            }
        }

        pairArray = new int [4][4];
        symbolArray = new Bitmap [4][4];

        initializePaintAndSymbols();

        populateArrays();

        //shuffle array
        shuffleArray();
    }

    /**
     * Method to populate both the pairArray to keep track of the
     * pair positions and the symbolArray to store the symbols used
     * on the card faces at the init stage of the application
     */
    public void populateArrays(){
        //populate the pairArray and SymbolArray
        pairArray[0][0] = 1;  pairArray[0][1] = 1; pairArray[0][2] = 2;  pairArray[0][3] = 2;
        pairArray[1][0] = 3;  pairArray[1][1] = 3; pairArray[1][2] = 4;  pairArray[1][3] = 4;
        pairArray[2][0] = 5;  pairArray[2][1] = 5; pairArray[2][2] = 6;  pairArray[2][3] = 6;
        pairArray[3][0] = 7;  pairArray[3][1] = 7; pairArray[3][2] = 8;  pairArray[3][3] = 8;

        symbolArray[0][0] = spade;  symbolArray[0][1] = spade; symbolArray[0][2] = heart;  symbolArray[0][3] = heart;
        symbolArray[1][0] = club;  symbolArray[1][1] = club; symbolArray[1][2] = diamond;  symbolArray[1][3] = diamond;
        symbolArray[2][0] = jack;  symbolArray[2][1] = jack; symbolArray[2][2] = queen;  symbolArray[2][3] = queen;
        symbolArray[3][0] = king;  symbolArray[3][1] = king; symbolArray[3][2] = joker;  symbolArray[3][3] = joker;
    }

    /**
     * Initialize any Paint objects that will be used along with the
     * bitmaps that will be used for the faces of the cards
     */
    public void initializePaintAndSymbols(){
        //Paint variables for the cards
        cardBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        transparent = new Paint(Paint.ANTI_ALIAS_FLAG);
        white = new Paint(Paint.ANTI_ALIAS_FLAG);

        cardBack.setColor(0xFF888888);
        transparent.setColor(0x00000000);
        white.setColor(0xFFFFFFFF);

        //Read in the .png images and save them as bitmaps
        spade = BitmapFactory.decodeResource(getResources(), R.mipmap.spades);
        heart = BitmapFactory.decodeResource(getResources(), R.mipmap.heart);
        diamond = BitmapFactory.decodeResource(getResources(), R.mipmap.diamond);
        club = BitmapFactory.decodeResource(getResources(), R.mipmap.club);
        jack = BitmapFactory.decodeResource(getResources(), R.mipmap.jack);
        queen = BitmapFactory.decodeResource(getResources(), R.mipmap.queen);
        king = BitmapFactory.decodeResource(getResources(), R.mipmap.king);
        joker = BitmapFactory.decodeResource(getResources(), R.mipmap.joker);

    }
    //===================================================================================
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        screenWidth = getMeasuredWidth();
        cardSize = (screenWidth / 4);
        screenHeight = getMeasuredHeight();

        //use to loop through arrays while drawing cards
        int x = 0;
        int y = 0;
        for(int i = 0; i < screenWidth; i = i + cardSize){
            for(int j = 0; j < screenWidth; j = j + cardSize){
                card = new Rect(i+10, j+10, (i + cardSize)-10, (j + cardSize)-10);

                Bitmap b2= Bitmap.createScaledBitmap(symbolArray[x][y], cardSize-20, cardSize-20, false);
                if(stateArray[x][y] == 2){
                    canvas.drawBitmap(b2, i+10, j+10, white);
                }
                else if (stateArray[x][y] == 0){
                    canvas.drawRect(card, transparent);
                }
                else {
                    canvas.drawRect(card, cardBack);
                }

                canvas.drawLine((x*cardSize)+ cardSize, y*cardSize, (x*cardSize)+ cardSize, screenHeight, white);
                x++;
                if(x == 4)
                    x = 0;
            }
            canvas.drawLine(x*cardSize, (y*cardSize)+ cardSize, screenWidth, (y*cardSize)+ cardSize , white);

            y++;
            if(y == 4)
                y = 0;
        }

        //check if it the end of current players turn
        endOfTurn();

        //Display the score and whos turn it is
        updateMainActivityDisplay();
    }

    /**
     * Check if the current player has turned two cards, if so, check if
     * cards match
     */
    public void endOfTurn(){
        //if it is the end of current players turn
        if (cardsTurned == 2){
            //Do all appropriate checks to compare flipped cards,
            //update score if match, swap player, update who
            //is winning and then delay before re drawing view
            checkMatch();
            cardsTurned = 0;
            isWinning();
            delay();
        }
    }

    /**
     * Wait for one second before redrawing the board.  This method is used
     * once the current player has turned two cards
     */
    public void delay(){
        //Create a delay of 1 second before view is re drawn
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                invalidate();
            }
        },1000);
    }

    /**
     * Get the position of the users click and divide the X and Y position
     * by the card size so that we can align the position of the drawn
     * cards with their position in the pairArray and symbolArray
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event){
        //handle touches from user
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
            if(event.getY()  < screenHeight && event.getY() > screenHeight - (cardSize*4)) {
                int x = (((int) event.getX()) / cardSize);
                int y = (((int) event.getY()) / cardSize);

                //Check if selected card is able to be turned
                if(stateArray[y][x] != 0 && stateArray[y][x]!=2) {
                    if(cardsTurned == 0){
                        cardOne = pairArray[y][x];
                    }
                    else if(cardsTurned == 1){
                        cardTwo = pairArray[y][x];
                    }
                    stateArray[y][x] = 2;
                    cardsTurned++;
                    invalidate();
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * Shuffle the pairArray and the symbolArray at the same time using the
     * same random shuffle position so that the faces in the symbolArray stay
     * lined up with their corresponding int in the pairArray
     */
    private void shuffleArray(){
        //Randomly shuffle pairArray
        for (int x = 0; x < 5; x++) {
            for (int i = 0; i < pairArray.length; i++) {
                for (int j = 0; j < pairArray[i].length; j++) {
                    int iRandom = (int) (Math.random() * pairArray.length);
                    int jRandom = (int) (Math.random() * pairArray[i].length);

                    //use the same random positions to shuffle both arrays so that
                    //the numbers in the pairArray line up with the
                    //connected bitmap in the symbol array

                    int tempPair = pairArray[i][j];
                    Bitmap tempSymbol = symbolArray[i][j];
                    pairArray[i][j] = pairArray[iRandom][jRandom];
                    pairArray[iRandom][jRandom] = tempPair;
                    symbolArray[i][j] = symbolArray[iRandom][jRandom];
                    symbolArray[iRandom][jRandom] = tempSymbol;
                }
            }
        }
    }

    //========================================================================
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        //OnMeasure method
        Log.i("CustomView", "onMeasure - entered");
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }

    /**
     * Send the information to the MainActivity that needs to be displayed
     * to the players (current player, scores)
     */
    private void updateMainActivityDisplay(){
        //update The mainActivity with current player and score
        Log.i("CustomView", "updateMainActivityDisplay - entered");
        MainActivity md = (MainActivity)this.getContext();
        String player = "Player "+ c_player +"s turn";
        String score = "";

        //If all cards have been turned print out the winner
        if (!checkEndGame()){
            score = "Score - [Player 1: " + playerOneScore + "]  [Player 2: " + playerTwoScore + "]";
        }
        else{
            if(winning == 0){
                score = "It is a draw";
            }
            else {
                score = "Player" + winning + " has won the game";
            }
        }

        md.updateMainActivity(score, player);
    }

    /**
     * Check who has the greater score and update who is winning
     */
    private void isWinning(){
        //check who is winning
        Log.i("CustomView", "isWinning - entered");
        //player 1 is winning
        if(playerOneScore > playerTwoScore){
            winning = 1;
        }
        //player 2 is winning
        else if(playerTwoScore > playerOneScore){
            winning = 2;
        }
        //is a draw
        else{
            winning = 0;
        }
    }

    /**
     * If current player is one swap to player 2 and vice versa
     */
    private void swapPlayer(){
        //swap the current player
        Log.i("CustomView", "Swapping current player");
        if(c_player == 1){
            c_player = 2;
        }
        else
            c_player = 1;
    }

    /**
     * check if flipped cards match, if they match remove them from the display.
     * If they don't match unflip them and swap player
     */
    private void checkMatch(){
        Log.i("CustomView", "Checking if there is a match");
        if(cardOne ==  cardTwo){
            removeCards();
        }
        else{
            swapPlayer();
            unflip();
        }
    }

    /**
     * if the flipped cards do not match then unflip them
     */
    private void unflip(){
        //unflip cards if not match
        Log.i("CustomView", "Unflipping cards");
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(stateArray[i][j] == 2){
                    stateArray[i][j] = 1;
                }
            }
        }
    }

    /**
     * remove the flipped cards by setting their state in the stateArray
     * to 0 to indicate that they have been removed. if the match and update
     * the current players score.
     */
    private void removeCards(){
        //update stateArray to remove the flipped cards that match
        Log.i("CustomView", "Removing matched cards from diplay");
        //update score
        if(c_player == 1){
            playerOneScore++;
        }
        else{
            playerTwoScore++;
        }

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(stateArray[i][j] == 2){
                    stateArray[i][j] = 0;
                }
            }
        }
    }

    /**
     * Check if all cards have been matched and return whether the game is over
     * or not
     * @return
     */
    private boolean checkEndGame(){
        //Check if all cards have been matched
        Log.i("CustomView", "Checking if the game has ended");
        boolean endGame = true;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(stateArray[i][j] != 0){
                    endGame = false;
                }
            }
        }
        return endGame;
    }
}
