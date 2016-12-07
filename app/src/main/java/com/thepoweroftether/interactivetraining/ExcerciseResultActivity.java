package com.thepoweroftether.interactivetraining;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ExcerciseResultActivity extends AppCompatActivity {

    String id, score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excercise_result);

        Intent i = getIntent();
        id = i.getStringExtra("id");
        score = i.getStringExtra("score");

        TextView resultText = (TextView) findViewById(R.id.result_text);
        RatingBar resultBar = (RatingBar) findViewById(R.id.result_bar);

        resultBar.setMax(5);
        resultBar.setRating(Integer.parseInt(score));

        if (Integer.parseInt(score) > 4)
            resultText.setText("Congratulations, you got " + score + " point. Keep that good point!");
        else if (Integer.parseInt(score) > 2)
            resultText.setText("Unfortunately, you got " + score + " point. Keep studying!");
        else if (Integer.parseInt(score) > 0)
            resultText.setText("Please study more, you only got " + score + " point!");
    }

}
