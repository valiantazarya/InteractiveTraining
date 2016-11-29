package com.thepoweroftether.interactivetraining;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        final EditText fullnameEdit = (EditText) findViewById(R.id.fullname_edit);
        final EditText usernameEdit = (EditText) findViewById(R.id.username_edit);
        final EditText passwordEdit = (EditText) findViewById(R.id.password_edit);
        final EditText confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password_edit);
        final EditText emailEdit = (EditText) findViewById(R.id.email_edit);
        final EditText addressEdit = (EditText) findViewById(R.id.address_edit);
        final EditText birthdateEdit = (EditText) findViewById(R.id.birthdate_edit);
        Button createButton = (Button) findViewById(R.id.create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountContract.AccountDbHelper dbHelper = new AccountContract.AccountDbHelper(RegisterActivity.this);
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
                            AccountContract.AccountEntry.COLUMN_NAME_BIRTHDATE,
                            birthdateEdit.getText().toString()
                    );

                    db.insert(AccountContract.AccountEntry.TABLE_NAME,
                            null,
                            values
                    );
                    Toast.makeText(
                            RegisterActivity.this,
                            "Account registration success.",
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
                }
            }
        });

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
