package com.thepoweroftether.interactivetraining;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class MemberExcerciseActivity extends AppCompatActivity {

    //Alert Message
    public static String alertMessage = "";

    private static final String url_get_excercise_details = Server.URL + Server.getExcerciseDetails;
    String account_id, excercise_id, username, question, opt_a, opt_b, opt_c, opt_d, answer;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EXCERCISE = "excercise";
    private static final String TAG_EXCERCISE_ID = "id";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_OPTA = "opt_a";
    private static final String TAG_OPTB = "opt_b";
    private static final String TAG_OPTC = "opt_c";
    private static final String TAG_OPTD = "opt_d";
    private static final String TAG_ANSWER = "answer";

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    List<ExcerciseClass.Question> quesList;
    private static int score = 0;
    private static int qid = 1;
    ExcerciseClass.Question currentQ;
    TextView questionText;
    RadioButton rda, rdb, rdc, rdd;
    RadioGroup answerGroup;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_excercise);

        Intent i = getIntent();
        account_id = i.getStringExtra("id");
        username = i.getStringExtra("username");

        new GetExcerciseDetails().execute();

        //RADIO BUTTON FILTER
        answerGroup = (RadioGroup) findViewById(R.id.answer_group);
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selectedRadio = (RadioButton) findViewById(
                        answerGroup.getCheckedRadioButtonId()
                );

                if (selectedRadio.getText().toString().equals(answer)){
                    score++;
                }
                if (qid < 5){
                    new GetExcerciseDetails().execute();
                    qid++;
                }
                else {
                    finish();
                    Intent i = new Intent(getApplicationContext(), ExcerciseResultActivity.class);
                    i.putExtra("id", account_id);
                    i.putExtra("score", Integer.toString(score));
                    startActivity(i);
                }
            }
        });
    }

    class GetExcerciseDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MemberExcerciseActivity.this);
            pDialog.setMessage("Loading question details");
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
                        args.add(new Pair<>(TAG_EXCERCISE_ID, Integer.toString(qid)));
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = jsonParser.makeHttpRequest(url_get_excercise_details, "POST", args);
                        } catch (IOException e) {
                            Log.d("Networking", e.getLocalizedMessage());
                        }

                        Log.d("Single account details", jsonObject.toString());

                        success = jsonObject.getInt(TAG_SUCCESS);
                        if(success == 1) {
                            JSONArray accountObj = jsonObject.getJSONArray(TAG_EXCERCISE);
                            JSONObject account = accountObj.getJSONObject(0);

                            questionText = (TextView) findViewById(R.id.questionText);
                            rda = (RadioButton) findViewById(R.id.answer1);
                            rdb = (RadioButton) findViewById(R.id.answer2);
                            rdc = (RadioButton) findViewById(R.id.answer3);
                            rdd = (RadioButton) findViewById(R.id.answer4);
                            nextButton = (Button) findViewById(R.id.nextButton);

                            rda.setChecked(false);
                            rdb.setChecked(false);
                            rdc.setChecked(false);
                            rdd.setChecked(false);
                            excercise_id = account.getString(TAG_EXCERCISE_ID);
                            questionText.setText(account.getString(TAG_QUESTION));
                            rda.setText(account.getString(TAG_OPTA));
                            rdb.setText(account.getString(TAG_OPTB));
                            //check null variable
                            if (TAG_OPTC.equals("null"))
                                rdc.setVisibility(View.GONE);
                            else {
                                rdc.setVisibility(View.VISIBLE);
                                rdc.setText(account.getString(TAG_OPTC));
                            }

                            //check null variable
                            if (TAG_OPTD.equals("null"))
                                rdd.setVisibility(View.GONE);
                            else {
                                rdd.setVisibility(View.VISIBLE);
                                rdd.setText(account.getString(TAG_OPTD));
                            }
                            answer = account.getString(TAG_ANSWER);
                        }
                        else {
                            //Not found
                        }
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
