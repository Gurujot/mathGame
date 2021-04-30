package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_start, btn_answer0, btn_answer1, btn_answer2, btn_answer3, btn_playAgain;
    TextView tv_score, tv_questions, tv_timer, tv_answerResult;
    ProgressBar prog_timer;
    private boolean timerRunning = false;
    DatabaseHelper mDatabaseHelper;

    Game g = new Game();

    private int secondsRemaining = 25000;

    private void startTimer() {
        CountDownTimer timer = new CountDownTimer(secondsRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining -= 1000;
                tv_timer.setText(Integer.toString(secondsRemaining / 1000) + "s");
                prog_timer.setProgress(25 - (secondsRemaining/1000));
            }

            @Override
            public void onFinish() {
                secondsRemaining = 25000;
                btn_playAgain.setVisibility(View.VISIBLE);
                timerRunning = false;
                btn_answer0.setEnabled(false);
                btn_answer1.setEnabled(false);
                btn_answer2.setEnabled(false);
                btn_answer3.setEnabled(false);
                tv_answerResult.setText("Your Score: " + g.getNumberCorrect() + " / " + (g.getTotalQuestions() - 1));

                String newEntry = tv_score.getText().toString();
                addData(newEntry);
            }
        }.start();
        timerRunning = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        btn_answer0 = findViewById(R.id.btn_answer0);
        btn_answer1 = findViewById(R.id.btn_answer1);
        btn_answer2 = findViewById(R.id.btn_answer2);
        btn_answer3 = findViewById(R.id.btn_answer3);
        btn_playAgain =findViewById(R.id.btn_playAgain);

        tv_score = findViewById(R.id.tv_score);
        tv_questions = findViewById(R.id.tv_question);
        tv_timer = findViewById(R.id.tv_timer);
        tv_answerResult = findViewById(R.id.tv_answerResult);
        mDatabaseHelper = new DatabaseHelper(this);

        prog_timer = findViewById(R.id.prog_timer);

        tv_timer.setText("0 Seconds");
        tv_questions.setText("");
        tv_answerResult.setVisibility(View.INVISIBLE);
        tv_score.setText("0/0");

        View.OnClickListener startButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button start_button = (Button) v;
                start_button.setVisibility(View.INVISIBLE);
                btn_answer0.setVisibility(View.VISIBLE);
                btn_answer1.setVisibility(View.VISIBLE);
                btn_answer2.setVisibility(View.VISIBLE);
                btn_answer3.setVisibility(View.VISIBLE);

                btn_answer0.setEnabled(false);
                btn_answer1.setEnabled(false);
                btn_answer2.setEnabled(false);
                btn_answer3.setEnabled(false);

                btn_playAgain.setVisibility(View.VISIBLE);
                tv_score.setBackgroundColor(Color.parseColor("#fd9644"));
                tv_timer.setBackgroundColor(Color.parseColor("#f7b731"));
                /*nextTurn();
                startTimer();*/
            }
        };

        View.OnClickListener playAgainButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button playAgain_Button = (Button) v;
                btn_playAgain.setText("Play Again");
                playAgain_Button.setVisibility(View.INVISIBLE);
                tv_answerResult.setVisibility(View.INVISIBLE);

                btn_answer0.setEnabled(true);
                btn_answer1.setEnabled(true);
                btn_answer2.setEnabled(true);
                btn_answer3.setEnabled(true);

                g = new Game();
                nextTurn();
                startTimer();
            }
        };

        btn_playAgain.setOnClickListener(playAgainButtonClickListener);

        View.OnClickListener answerButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button buttonClicked = (Button) v;

                int answerSelected = Integer.parseInt(buttonClicked.getText().toString());

                g.checkAnswer(answerSelected);
                nextTurn();
                tv_answerResult.setVisibility(View.VISIBLE);
                if (g.updateResult == true) {
                    tv_answerResult.setText("Correct!");
                }
                else {
                    tv_answerResult.setText("Wrong!");
                }
            }
        };

        btn_start.setOnClickListener(startButtonClickListener);

        btn_answer0.setOnClickListener(answerButtonClickListener);
        btn_answer1.setOnClickListener(answerButtonClickListener);
        btn_answer2.setOnClickListener(answerButtonClickListener);
        btn_answer3.setOnClickListener(answerButtonClickListener);

    }

    public void addData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);
        if (insertData) {
            Toast.makeText(this, "Score Saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item1:
                if (!timerRunning) {
                    Toast.makeText(this, "Next game will be 10 secs", Toast.LENGTH_SHORT).show();
                    secondsRemaining = 10000;
                }
                return true;
            case R.id.item2:
                if (!timerRunning) {
                    Toast.makeText(this, "Next game will be 15 secs", Toast.LENGTH_SHORT).show();
                    secondsRemaining = 15000;
                }
                return true;
            case R.id.item3:
                if (!timerRunning) {
                    Toast.makeText(this, "Next game will be 20 secs", Toast.LENGTH_SHORT).show();
                    secondsRemaining = 20000;
                }
                return true;
            case R.id.item4:
                if (!timerRunning) {
                    Intent intent = new Intent(this, ListDataActivity.class);
                    startActivity(intent);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void nextTurn() {
        g.makeNewQuestions();
        int [] answer = g.getCurrentQuestion().getAnswerArray();
        btn_answer0.setText(Integer.toString(answer[0]));
        btn_answer1.setText(Integer.toString(answer[1]));
        btn_answer2.setText(Integer.toString(answer[2]));
        btn_answer3.setText(Integer.toString(answer[3]));

        btn_answer0.setEnabled(true);
        btn_answer1.setEnabled(true);
        btn_answer2.setEnabled(true);
        btn_answer3.setEnabled(true);

        tv_questions.setText(g.getCurrentQuestion().getQuestionPhrase());
        tv_score.setText(g.getNumberCorrect() + " / " + (g.getTotalQuestions() - 1));
    }
}