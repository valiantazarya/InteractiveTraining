package com.thepoweroftether.interactivetraining;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    String id, username, fullname, password, usertype, usernameGet, passwordGet;
    private static int READ_BLOCK_SIZE = 100;

    private ProgressDialog pDialog;

    private static final String url_login = Server.URL + Server.login;

    public static final String SESSION_PREFERENCES = "SessionPreferences" ;
    public static final String pUsername = "pUsername";
    public static final String pPassword = "pPassword";
    SharedPreferences sharedpreferences;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_ACCOUNT_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_FULLNAME = "fullname";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_USERTYPE = "usertype";
    JSONParser jsonParser = new JSONParser();

    public static String alertMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ActionBar actionBar = getActionBar();
        if (actionBar!=null) actionBar.setDisplayShowTitleEnabled(false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final EditText usernameEdit = (EditText) findViewById(R.id.username_edit);
        final EditText passwordEdit = (EditText) findViewById(R.id.password_edit);


        //#################################### Auto Login ###################################

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/GobsFiles");
        directory.mkdirs();

        File file = new File(directory, "settingAutoLogin.txt");
        if (file.exists()){
            try {
                FileInputStream fIn = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fIn);

                char[] inputBuffer = new char[READ_BLOCK_SIZE];
                String s = "";
                int charRead;

                while ((charRead = isr.read(inputBuffer)) > 0){
                    String readString = String.copyValueOf(inputBuffer, 0, charRead);
                    s+= readString;
                    inputBuffer = new char[READ_BLOCK_SIZE];
                }
                isr.close();

                if (s.equals("Enabled")) {
                    File fileOwn = new File(directory, "ownerInfo.txt");
                    FileInputStream fInOwn = new FileInputStream(fileOwn);
                    InputStreamReader isrOwn = new InputStreamReader(fInOwn);

                    char[] inputBufferOwn = new char[READ_BLOCK_SIZE];
                    String sOwn = "";
                    int charReadOwn;

                    while ((charReadOwn = isrOwn.read(inputBufferOwn)) > 0){
                        String readString = String.copyValueOf(inputBufferOwn, 0, charReadOwn);
                        sOwn+= readString;
                        inputBufferOwn = new char[READ_BLOCK_SIZE];
                    }
                    isrOwn.close();

                    int separator = sOwn.indexOf("|");
                    usernameGet = sOwn.substring(0,separator);
                    passwordGet = sOwn.substring(separator+1);
                    finish();
                    new GetLoginDetails().execute();
                }

            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else {
            //sharedpreferences = getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE);

            Button loginButton = (Button) findViewById(R.id.login_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usernameGet = usernameEdit.getText().toString();
                    passwordGet = passwordEdit.getText().toString();

                    new GetLoginDetails().execute();
                }
            });
        }
        //###############################################################################

        TextView registerButton = (TextView) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    class GetLoginDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
            pDialog.setMessage("Logging in");
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
                        if (!isConnected(LoginActivity.this)){
                            Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(i);

                            while (!isConnected(LoginActivity.this)) {
                                //Wait to connect
                                Thread.sleep(1000);
                            }
                        }

                        if (usernameGet.equals("") || passwordGet.equals("")) {
                            alertMessage = "You cannot submit an empty field(s)";
                        }
                        else {
                            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
                            args.add(new Pair<>(TAG_USERNAME, usernameGet));
                            args.add(new Pair<>(TAG_PASSWORD, passwordGet));
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = jsonParser.makeHttpRequest(url_login, "POST", args);
                            } catch (IOException e) {
                                Log.d("Networking", e.getLocalizedMessage());
                            }

                            Log.d("Login details", jsonObject.toString());

                            success = jsonObject.getInt(TAG_SUCCESS);
                            if(success == 1) {
                                JSONArray accountObj = jsonObject.getJSONArray(TAG_ACCOUNT);
                                JSONObject account = accountObj.getJSONObject(0);

                                id = account.getString(TAG_ACCOUNT_ID);
                                username = account.getString(TAG_USERNAME);
                                fullname = account.getString(TAG_FULLNAME);
                                password = account.getString(TAG_PASSWORD);
                                usertype = account.getString(TAG_USERTYPE);

                                //Login Process
                                Intent intent;
                                if (usertype.equals("1")) {
                                    intent = new Intent(LoginActivity.this, AdminActivity.class);
                                }
                                else {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                }

                                //Shared Preferences
                            /*SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(pUsername, usernameGet);
                            editor.putString(pPassword, passwordGet);
                            editor.commit();*/

                                alertMessage = "Login successful";

                                intent.putExtra("ID", id);
                                intent.putExtra("USERNAME", username);
                                intent.putExtra("FULLNAME", fullname);
                                intent.putExtra("PASSWORD", password);
                                intent.putExtra("USERTYPE", usertype);
                                startActivity(intent);
                            }
                            else {
                                alertMessage = "Username and password doesn't seems to match";
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        alertMessage = "Login failed! Check your network connection";
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            Toast.makeText(
                    LoginActivity.this,
                    alertMessage,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
