package com.thepoweroftether.interactivetraining;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    //Alert Message
    public static String alertMessage = "Account registration successful.";

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_create_account = Server.URL + Server.createAccount;
    private static final String TAG_SUCCESS = "success";

    EditText fullnameEdit, usernameEdit, passwordEdit, confirmPasswordEdit, emailEdit, addressEdit, phoneEdit;
    String fullname, username, password, confirmPassword, email, address, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);*/

        fullnameEdit = (EditText) findViewById(R.id.fullname_edit);
        usernameEdit = (EditText) findViewById(R.id.username_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password_edit);
        emailEdit = (EditText) findViewById(R.id.email_edit);
        addressEdit = (EditText) findViewById(R.id.address_edit);
        phoneEdit = (EditText) findViewById(R.id.phone_edit);
        Button createButton = (Button) findViewById(R.id.create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullname = fullnameEdit.getText().toString();
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                confirmPassword = confirmPasswordEdit.getText().toString();
                email = emailEdit.getText().toString();
                address = addressEdit.getText().toString();
                phone = phoneEdit.getText().toString();

                if (fullname.equals("") || username.equals("") || password.equals("") || email.equals("") ||
                        address.equals("") || phone.equals("")){
                    Toast.makeText(
                            RegisterActivity.this,
                            "Error! Your cannot submit an empty field(s).",
                            Toast.LENGTH_SHORT)
                            .show();
                } else{
                    if (password.equals(confirmPassword)){
                        new CreateNewAccount().execute();

                        Toast.makeText(
                                getApplicationContext(),
                                alertMessage,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Password and confirm password doesn't seems to match.",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                /*AccountContract.AccountDbHelper dbHelper = new AccountContract.AccountDbHelper(RegisterActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                if (passwordEdit.getText().toString().equals(confirmPasswordEdit.getText().toString())){
                    ContentValues values = new ContentValues();
                    values.put(
                            AccountContract.AccountEntry.COLUMN_NAME_FULLNAME,
                            fullnameEdit.getText().toString()
                    );
                    values.put(
                            AccountContract.AccountEntry.COLUMN_NAME_USERNAME,
                            usernameEdit.getText().toString()
                    );
                    values.put(
                            AccountContract.AccountEntry.COLUMN_NAME_PASSWORD,
                            passwordEdit.getText().toString()
                    );
                    values.put(
                            AccountContract.AccountEntry.COLUMN_NAME_EMAIL,
                            emailEdit.getText().toString()
                    );
                    values.put(
                            AccountContract.AccountEntry.COLUMN_NAME_ADDRESS,
                            addressEdit.getText().toString()
                    );
                    values.put(
                            AccountContract.AccountEntry.COLUMN_NAME_PHONE,
                            phoneEdit.getText().toString()
                    );

                    db.insert(AccountContract.AccountEntry.TABLE_NAME,
                            null,
                            values
                    );
                    Toast.makeText(
                            RegisterActivity.this,
                            "The data has been saved.",
                            Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
                else {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Password and confirm password doesn't seems to match.",
                            Toast.LENGTH_SHORT)
                            .show();
                }*/
            }
        });

        TextView backButton = (TextView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class CreateNewAccount extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating Account");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Pair<String, String>> args =
                    new ArrayList<Pair<String, String>>();
            args.add(new Pair<>("username", username));
            args.add(new Pair<>("fullname", fullname));
            args.add(new Pair<>("password", password));
            args.add(new Pair<>("address", address));
            args.add(new Pair<>("email", email));
            args.add(new Pair<>("phone", phone));
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonParser.makeHttpRequest(url_create_account, "POST", args);
            }catch (IOException e){
                Log.d("Networking", e.getLocalizedMessage());
            }

            Log.d("Create response", jsonObject.toString());

            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    alertMessage = "Account registration success.";
                    finish();
                }
                else{
                    alertMessage = "Error! Account registration failed.";
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }
}
