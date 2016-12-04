package com.thepoweroftether.interactivetraining;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AdminActivity extends AppCompatActivity {

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    EditText username_edit, fullname_edit, email_edit;
    String id, username, password, fullname, email;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_ACCOUNT_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_FULLNAME = "fullname";
    private static final String TAG_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent i = getIntent();
        id = i.getStringExtra("ID");
        username = i.getStringExtra("USERNAME");
        password = i.getStringExtra("PASSWORD");
        fullname = i.getStringExtra("FULLNAME");

        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_message);
        welcomeMessage.setText("Welcome " + fullname);

        ImageButton account_list_button = (ImageButton) findViewById(R.id.account_list_button);
        ImageButton create_account_button = (ImageButton) findViewById(R.id.create_account_button);
        ImageButton settings_button = (ImageButton) findViewById(R.id.settings_button);
        ImageButton upload_button = (ImageButton) findViewById(R.id.upload_button);
        ImageButton excercise_details_button = (ImageButton) findViewById(R.id.excercise_details_button);

        account_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),AllAccountActivity.class);
                startActivity(i);

            }
        });

        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SettingActivity.class);
                i.putExtra("ID", id);
                i.putExtra("USERNAME", username);
                i.putExtra("PASSWORD", password);
                startActivity(i);
            }
        });

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UploadModuleActivity.class);
                i.putExtra("USERNAME", username);
                startActivity(i);
            }
        });

        create_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                //DialogForm("","","","Delete");
            }
        });

        excercise_details_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MemberExcerciseActivity.class);
                startActivity(i);
            }
        });
    }

    // untuk menampilkan dialog form
    private void DialogForm(String pUsername, String pFullname, String pEmail, String button) {
        dialog = new AlertDialog.Builder(AdminActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.activity_register, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.icon);
        dialog.setTitle("REGISTER ACCOUNT");

        username_edit    = (EditText) dialogView.findViewById(R.id.username_edit);
        fullname_edit  = (EditText) dialogView.findViewById(R.id.fullname_edit);
        email_edit    = (EditText) dialogView.findViewById(R.id.email_edit);

        if (!pUsername.isEmpty()){
            username_edit.setText(pUsername);
            fullname_edit.setText(pFullname);
            email_edit.setText(pEmail);
        } else {
            emptyField();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                username    = username_edit.getText().toString();
                fullname    = fullname_edit.getText().toString();
                email       = email_edit.getText().toString();

                //simpan_update();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                emptyField();
            }
        });

        dialog.show();
    }

    private void emptyField(){
        username_edit.setText(null);
        fullname_edit.setText(null);
        email_edit.setText(null);
    }
}
