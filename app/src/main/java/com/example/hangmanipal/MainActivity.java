package com.example.hangmanipal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    //variable declarations
    TextView txtWordToBeGuessed;
    ImageView imgHangmanMotion;
    String WordToBeGuessed;
    String WordDisplayedString;
    char[] wordDisplayedCharArr;
    Button btn;
    TextInputEditText tiet;
    ArrayList<String> listofwords;
    TextView txtLettersTried;
    String lettersTried;
    final String messageWithLettersTried = "Letters Tried: ";
    TextView txtTriesLeft;
    String TriesLeft;
    final String winningMessage = "YOU'VE WON";
    final String losingMessage = "YOU'VE LOST";

    void revealLetterInWord(char letter)
    {
        int indexOfLetter = WordToBeGuessed.indexOf(letter);

        //loop if indexes are positive or 0
        while(indexOfLetter>=0)
        {
            wordDisplayedCharArr[indexOfLetter] = WordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = WordToBeGuessed.indexOf(letter, indexOfLetter+1);
        }

        //update the string
        WordDisplayedString = String.valueOf(wordDisplayedCharArr);
    }

    void displayWordOnScreen()
    {
        String FormattedString = "";
        for(char character : wordDisplayedCharArr)
        {
            FormattedString += character + " ";
        }
        txtWordToBeGuessed.setText(FormattedString);
    }

    void initializeGame()
    {
        //1.word
        //shuffle the array list and get first element and then remove it
        Collections.shuffle(listofwords);
        WordToBeGuessed = listofwords.get(0);
        listofwords.remove(0);

        //intialize char array
        wordDisplayedCharArr = WordToBeGuessed.toCharArray();

        //add underscores
        for(int i=1; i< wordDisplayedCharArr.length-1;i++)
        {
            if(wordDisplayedCharArr[i]!= '-')
            {
                wordDisplayedCharArr[i] = '_';
            }
            if(wordDisplayedCharArr[i]=='-')
            {
                wordDisplayedCharArr[i]= ' ';
            }
        }

        //reveal all occurences of first character
        revealLetterInWord(wordDisplayedCharArr[0]);

        //reveal all occurences of last character
        revealLetterInWord(wordDisplayedCharArr[wordDisplayedCharArr.length-1]);

        //initialize a string from this char array (only for search purposes)
        WordDisplayedString = String.valueOf(wordDisplayedCharArr);

        //display word with underscore

        displayWordOnScreen();

        //2.input
        //clear input field

        tiet.setText("");

        //3.letters tried
        //initialize string for letters tried with a space
        lettersTried= " ";

        //display on screen
        txtLettersTried.setText(messageWithLettersTried);


        //4.tries left
        //initialize the string for tries left
        TriesLeft= "X X X X X ";
        imgHangmanMotion.setImageResource(R.drawable.hm0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization of variables
        listofwords = new ArrayList<String>();
        txtWordToBeGuessed = (TextView) findViewById(R.id.txtWordToBeGuessed);
        tiet = (TextInputEditText) findViewById(R.id.tiet);
        txtLettersTried = (TextView) findViewById(R.id.txtLettersTried);
        btn = (Button) findViewById(R.id.BT1);
        imgHangmanMotion = (ImageView) findViewById(R.id.imageView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "reset",Toast.LENGTH_SHORT).show();
                initializeGame();
            }
        });

        //traverse database

        InputStream myInputStream = null;
        Scanner in = null;
        String word = "";

        try {
            myInputStream = getAssets().open("DataBaseFile.txt");
            in = new Scanner(myInputStream);
            while(in.hasNext())
            {
                word= in.next();
                listofwords.add(word);
                //Toast.makeText(MainActivity.this, word, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,
                    e.getClass().getSimpleName() + ":" +e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        finally
        {
            //closing the scanner and input stream
            if(in != null) {
                in.close();
            }
                //close input stream
            try
            {
                if(myInputStream != null) {
                    myInputStream.close();
                }

            } catch (IOException e) {
                Toast.makeText(MainActivity.this,
                        e.getClass().getSimpleName() + ":" +e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
        }

        initializeGame();

        //setup the text changed listner

        tiet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if there is some letter on the input field
                if(charSequence.length()==1)
                {
                    CheckIfLetterIsInWord(charSequence.charAt(0));
                    tiet.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    void CheckIfLetterIsInWord(char letter)
    {
        //find if letter was found in word to be guessed
        if(WordToBeGuessed.indexOf(letter)>=0)
        {
            //if the letter was not displayed yet
            if(WordDisplayedString.indexOf(letter)<0)
            {
                //replace underscores with that letter
                revealLetterInWord(letter);

                //update changes on the screen
                displayWordOnScreen();

                //check if game is won
                if(!WordDisplayedString.contains("_"))
                {
                    imgHangmanMotion.setImageResource(R.drawable.hmw);
                    Toast.makeText(getApplicationContext(), "you've won!",Toast.LENGTH_SHORT).show();
                }
            }
        }
        //if letter was not found
        else
        {
            //decrease number of tries left
            DecreaseAndDisplayTriesMethod();

            //check if the game is lost
            if(TriesLeft.isEmpty())
            {
                imgHangmanMotion.setImageResource(R.drawable.hml);
                txtWordToBeGuessed.setText(WordToBeGuessed);
                Toast.makeText(getApplicationContext(), "you've lost",Toast.LENGTH_SHORT).show();
            }
        }

        //display the letter that was tried
        if(lettersTried.indexOf(letter)<0)
        {
            lettersTried += letter + ", ";
            String MessageToBeDisplayed = messageWithLettersTried + lettersTried;
            txtLettersTried.setText(MessageToBeDisplayed);
        }
    }

    void DecreaseAndDisplayTriesMethod()
    {
        //if there are some tries left
        if(!TriesLeft.isEmpty())
        {
            //take out the last two chars
            TriesLeft = TriesLeft.substring(0, TriesLeft.length()-2);
            if(TriesLeft.length()==10)
            {
                imgHangmanMotion.setImageResource(R.drawable.hm0);
            }
            if(TriesLeft.length()==8)
            {
                imgHangmanMotion.setImageResource(R.drawable.hm1);
            }
            if(TriesLeft.length()==6)
            {
                imgHangmanMotion.setImageResource(R.drawable.hm2);
            }
            if(TriesLeft.length()==4)
            {
                imgHangmanMotion.setImageResource(R.drawable.hm3);
            }
            if(TriesLeft.length()==2)
            {
                imgHangmanMotion.setImageResource(R.drawable.hm4);
            }
        }
    }
}