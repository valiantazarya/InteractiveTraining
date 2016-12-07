package com.thepoweroftether.interactivetraining;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Boolean exit = false;

    String ID, USERNAME, FULLNAME, PASSWORD, USERTYPE;
    private static String scoreInfo;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    //Alert Message
    public static String alertMessage = "";

    private static final String url_score = Server.URL + Server.getScore;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ACCOUNT = "score";
    private static final String TAG_ACCOUNT_ID = "id";
    private static final String TAG_SCORE = "score";
    private static final String TAG_METHOD = "method";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ID =  getIntent().getStringExtra("ID");
        USERNAME = getIntent().getStringExtra("USERNAME");
        FULLNAME = getIntent().getStringExtra("FULLNAME");
        PASSWORD = getIntent().getStringExtra("PASSWORD");
        USERTYPE = getIntent().getStringExtra("USERTYPE");

        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_message);
        welcomeMessage.setText("Welcome " + FULLNAME);

        String profileInfo;
        int maxName = 20;
        if (FULLNAME.length() > maxName) {
            profileInfo = "Name  \t\t\t\t : " + FULLNAME.substring(0,FULLNAME.indexOf(FULLNAME.charAt(maxName))) + "...";
        }
        else {
            profileInfo = "Name  \t\t\t\t : " + FULLNAME;
        }
        profileInfo += "\nUsername\t : "+USERNAME;
        profileInfo += "\nPassword\t : "+PASSWORD.substring(0,PASSWORD.length()/2);
        for(int i=PASSWORD.indexOf(PASSWORD.charAt(PASSWORD.length()/2)); i<PASSWORD.length() ; i++)
            profileInfo+="*";

        TextView txtProfile = (TextView) findViewById(R.id.txtProfile);
        txtProfile.setText(profileInfo);

        //Set Excercise Score
        new GetScoreDetails().execute();

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

    @Override
    public void onBackPressed() {
        if (exit) {
            System.exit(1);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            },2 * 1000);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            intent.putExtra("ID", ID);
            intent.putExtra("USERNAME",USERNAME);
            intent.putExtra("PASSWORD", PASSWORD);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsolutePath() + "/GobsFiles");

                File file = new File(directory, "settingAutoLogin.txt");
                file.delete();

                File fileOwn = new File(directory, "ownerInfo.txt");
                fileOwn.delete();

            } catch (Exception e){
                e.printStackTrace();
            }
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    class GetScoreDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading account details");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Looper.prepare();

                    int success;
                    try {
                        List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
                        args.add(new Pair<>(TAG_ACCOUNT_ID, ID));
                        args.add(new Pair<>(TAG_METHOD, "1"));
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = jsonParser.makeHttpRequest(url_score, "POST", args);
                        } catch (IOException e) {
                            Log.d("Networking", e.getLocalizedMessage());
                        }

                        Log.d("Single score details", jsonObject.toString());

                        success = jsonObject.getInt(TAG_SUCCESS);
                        int message = jsonObject.getInt("message");
                        if(success == 1) {
                            JSONArray accountObj = jsonObject.getJSONArray(TAG_ACCOUNT);
                            JSONObject account = accountObj.getJSONObject(0);

                            scoreInfo = account.getString(TAG_SCORE);
                        }
                        else {
                            //Not found
                            if (message == 0){
                                scoreInfo = "- [No scores yet]";
                            }
                        }

                        TextView txtExercise = (TextView) findViewById(R.id.txtExercise);
                        RatingBar rsNilai = (RatingBar) findViewById(R.id.rsNilai);
                        rsNilai.setRating(Float.parseFloat(scoreInfo));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }
}
