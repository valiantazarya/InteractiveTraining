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

public class AdminActivity extends AppCompatActivity {

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    EditText username_edit, fullname_edit, email_edit;
    String username, fullname, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button account_list_button = (Button) findViewById(R.id.account_list_button);
        Button create_account_button = (Button) findViewById(R.id.create_account_button);

        account_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),AllAccountActivity.class);
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
