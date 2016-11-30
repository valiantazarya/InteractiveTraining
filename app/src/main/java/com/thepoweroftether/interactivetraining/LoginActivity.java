package com.thepoweroftether.interactivetraining;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEdit = (EditText) findViewById(R.id.username_edit);
        final EditText passwordEdit = (EditText) findViewById(R.id.password_edit);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                AccountContract.AccountDbHelper dbHelper = new AccountContract.AccountDbHelper(LoginActivity.this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = null;

                cursor = db.query(
                        AccountContract.AccountEntry.TABLE_NAME,
                        new String[] {
                                AccountContract.AccountEntry._ID
                        },
                        String.format(
                                "%s = ? AND %s = ?",
                                AccountContract.AccountEntry.COLUMN_NAME_USERNAME,
                                AccountContract.AccountEntry.COLUMN_NAME_PASSWORD
                        ),
                        new String[] {
                                username,
                                password
                        },
                        null,
                        null,
                        null
                );

                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    String id = cursor.getString(
                            cursor.getColumnIndex(AccountContract.AccountEntry._ID)
                    );
                    Intent intent;
                    if (username.equals("admin")) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    }
                    else {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }
                    intent.putExtra("ID", id);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(
                            LoginActivity.this,
                            "Username and password doesn't seems to match",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

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
}
