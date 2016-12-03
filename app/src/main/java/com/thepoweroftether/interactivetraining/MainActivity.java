package com.thepoweroftether.interactivetraining;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    String ID, USERNAME, FULLNAME, PASSWORD, USERTYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ID =  getIntent().getStringExtra("ID");
        USERNAME = getIntent().getStringExtra("USERNAME");
        FULLNAME = getIntent().getStringExtra("FULLNAME");
        PASSWORD = getIntent().getStringExtra("PASSWORD");
        USERTYPE = getIntent().getStringExtra("USERTYPE");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String profileInfo;
        int maxName = 20;
        if (FULLNAME.length() > maxName) {
            profileInfo = "Name  \t\t\t: " + FULLNAME.substring(0,FULLNAME.indexOf(FULLNAME.charAt(maxName))) + "...";
        }
        else {
            profileInfo = "Name  \t\t\t: " + FULLNAME;
        }
        profileInfo += "\nPassword : "+PASSWORD.substring(0,PASSWORD.length()/2);
        for(int i=PASSWORD.indexOf(PASSWORD.charAt(PASSWORD.length()/2)); i<PASSWORD.length() ; i++)
            profileInfo+="*";

        TextView txtProfile = (TextView) findViewById(R.id.txtProfile);
        txtProfile.setText(profileInfo);

        ImageButton btnProfile = (ImageButton) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        MainActivity.this, EditAccountActivity.class
                );
                intent.putExtra("id", ID);
                intent.putExtra("username",USERNAME);
                intent.putExtra("password", PASSWORD);
                intent.putExtra("usertype", USERTYPE);
                startActivity(intent);
            }
        });

        ImageButton btnModule = (ImageButton) findViewById(R.id.btnModule);
        btnModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(
                                MainActivity.this, LearningModule.class
                        )
                );
            }
        });

        ImageButton btnQuiz = (ImageButton) findViewById(R.id.btnQuiz);
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemberExcerciseActivity.class);
                intent.putExtra("id", ID);
                intent.putExtra("username",USERNAME);
                startActivity(intent);
            }
        });
    }
}
