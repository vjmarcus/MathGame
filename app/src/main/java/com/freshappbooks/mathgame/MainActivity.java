package com.freshappbooks.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MyApp";
    private TextView mTextView0;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextViewTimer;
    private TextView mTextViewScore;
    private TextView mTextViewQuestion;
    private boolean gameOver = false;
    private long millisUntilFinished;

    private ArrayList<TextView> mArrayList = new ArrayList<>();

    private String mQuestion;
    private int rightAnswer;
    private int rightAnswerPosition;
    private boolean isPositive;
    private int min = 5;
    private int max = 30;
    private int countOfQuestions = 0;
    private int countOfRightQuestions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView0 = findViewById(R.id.textView0);
        mTextView1 = findViewById(R.id.textView1);
        mTextView2 = findViewById(R.id.textView2);
        mTextView3 = findViewById(R.id.textView3);
        mTextViewTimer = findViewById(R.id.textViewTimer);
        mTextViewScore = findViewById(R.id.textViewScore);
        mTextViewQuestion = findViewById(R.id.textViewQuestion);

        mArrayList.add(mTextView0);
        mArrayList.add(mTextView1);
        mArrayList.add(mTextView2);
        mArrayList.add(mTextView3);

        playNext();

        CountDownTimer timer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 5000) {
                    mTextViewTimer.setTextColor(Color.RED);
                }

                mTextViewTimer.setText(getTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (countOfRightQuestions >= max) {
                    preferences.edit().putInt("max", countOfRightQuestions).apply();
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightQuestions);
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void playNext() {
        generateQuestion();
        for (int i = 0; i < mArrayList.size(); i++) {
            if (i == rightAnswerPosition) {
                mArrayList.get(i).setText(Integer.toString(rightAnswer));
            } else {
                mArrayList.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
        }
        String score = String.format("%s / %s", countOfRightQuestions, countOfQuestions);
        mTextViewScore.setText(score);
    }

    private String getTime(long millis) {

        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void generateQuestion() {
        int a = (int) (Math.random() * (max - min + 1) + min);
        int b = (int) (Math.random() * (max - min + 1) + min);
        int mark = (int) (Math.random() * 2);
        isPositive = mark == 1;
        if (isPositive) {
            rightAnswer = a + b;
            mQuestion = String.format("%s + %s", a, b);
        } else {
            rightAnswer = a - b;
            mQuestion = String.format("%s - %s", a, b);
        }
        mTextViewQuestion.setText(mQuestion);
        rightAnswerPosition = (int) (Math.random() * 4);
    }

    private int generateWrongAnswer() {
        int result;
        do {
            result = (int) (Math.random() * max * 2 + 1) - (max - min);
        } while (result == rightAnswer);

        return result;
    }

    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int schoosenAnswer = Integer.parseInt(answer);
            if (schoosenAnswer == rightAnswer) {
                Toast.makeText(this, "Верно!", Toast.LENGTH_SHORT).show();
                countOfRightQuestions++;
                millisUntilFinished = millisUntilFinished + 3000;


            } else {
                Toast.makeText(this, "Нет... ", Toast.LENGTH_SHORT).show();
            }
            countOfQuestions++;
            playNext();
        }
    }
}
