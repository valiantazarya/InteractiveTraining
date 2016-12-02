package com.thepoweroftether.interactivetraining;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class EditAccountActivity extends AppCompatActivity {
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    EditText idEdit, fullnameEdit, usernameEdit, passwordEdit, confirmPasswordEdit, emailEdit, addressEdit, phoneEdit;
    String id, fullname, username, password, confirmPassword, email, address, phone;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    //Alert Message
    public static String alertMessage = "";

    private static final String url_edit_account = Server.URL + Server.editAccount;
    private static final String url_update_account = Server.URL + Server.updateAccount;
    private static final String url_delete_account = Server.URL + Server.deleteAccount;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_ACCOUNT_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_FULLNAME = "fullname";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button saveButton = (Button) findViewById(R.id.save_button);
        TextView backButton = (TextView) findViewById(R.id.back_button);
        Button deleteButton = (Button) findViewById(R.id.delete_button);

        Intent i = getIntent();
        id = i.getStringExtra(TAG_ACCOUNT_ID);
        new GetAccountDetails().execute();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                fullname = fullnameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                address = addressEdit.getText().toString();
                email = emailEdit.getText().toString();
                phone = phoneEdit.getText().toString();

                new SaveAccountDetails().execute();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogForm();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // untuk menampilkan dialog form
    private void DialogForm() {
        dialog = new AlertDialog.Builder(EditAccountActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.delete_layout, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle("Delete Account");

        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteAccount().execute();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class GetAccountDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditAccountActivity.this);
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
                        args.add(new Pair<>(TAG_ACCOUNT_ID, id));
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = jsonParser.makeHttpRequest(url_edit_account, "POST", args);
                        } catch (IOException e) {
                            Log.d("Networking", e.getLocalizedMessage());
                        }

                        Log.d("Single account details", jsonObject.toString());

                        success = jsonObject.getInt(TAG_SUCCESS);
                        if(success == 1) {
                            JSONArray accountObj = jsonObject.getJSONArray(TAG_ACCOUNT);
                            JSONObject account = accountObj.getJSONObject(0);

                            idEdit = (EditText) findViewById(R.id.username_edit);
                            usernameEdit = (EditText) findViewById(R.id.username_edit);
                            fullnameEdit = (EditText) findViewById(R.id.fullname_edit);
                            passwordEdit = (EditText) findViewById(R.id.password_edit);
                            addressEdit = (EditText) findViewById(R.id.address_edit);
                            emailEdit = (EditText) findViewById(R.id.email_edit);
                            phoneEdit = (EditText) findViewById(R.id.phone_edit);

                            idEdit.setText(account.getString(TAG_ACCOUNT_ID));
                            usernameEdit.setText(account.getString(TAG_USERNAME));
                            fullnameEdit.setText(account.getString(TAG_FULLNAME));
                            passwordEdit.setText(account.getString(TAG_PASSWORD));
                            addressEdit.setText(account.getString(TAG_ADDRESS));
                            emailEdit.setText(account.getString(TAG_EMAIL));
                            phoneEdit.setText(account.getString(TAG_PHONE));
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

    class SaveAccountDetails extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditAccountActivity.this);
            pDialog.setMessage("Updating account details");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            final List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<>(TAG_ACCOUNT_ID, id));
            args.add(new Pair<>(TAG_USERNAME, username));
            args.add(new Pair<>(TAG_FULLNAME, fullname));
            args.add(new Pair<>(TAG_PASSWORD, password));
            args.add(new Pair<>(TAG_ADDRESS, address));
            args.add(new Pair<>(TAG_EMAIL, email));
            args.add(new Pair<>(TAG_PHONE, phone));
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonParser.makeHttpRequest(url_update_account, "POST", args);
            } catch (IOException e) {
                Log.d("Networking", e.getLocalizedMessage());
            }

            try {
                int success = jsonObject.getInt(TAG_SUCCESS);

                if (success == 1) {
                    alertMessage = "Update account successful.";
                    Intent i = getIntent();
                    setResult(100, i);
                    finish();
                } else {
                    alertMessage = "Error! Update account failed.";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            Toast.makeText(
                    getApplicationContext(),
                    alertMessage,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    class DeleteAccount extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditAccountActivity.this);
            pDialog.setMessage("Deleting account");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            final List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<>(TAG_ACCOUNT_ID, id));
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonParser.makeHttpRequest(url_delete_account, "POST", args);
            } catch (IOException e) {
                Log.d("Networking", e.getLocalizedMessage());
            }

            try {
                int success = jsonObject.getInt(TAG_SUCCESS);

                if (success == 1) {
                    alertMessage = "Delete account successful.";
                    Intent i = getIntent();
                    setResult(100, i);
                    finish();
                } else {
                    alertMessage = "Error! Delete account failed.";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            Toast.makeText(
                    getApplicationContext(),
                    alertMessage,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
