package com.thepoweroftether.interactivetraining;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import dalvik.system.BaseDexClassLoader;

public class SettingActivity extends AppCompatActivity {

    Switch autoLoginSwitch;
    String statusAutoLogin;
    String id, username, password;
    private static int READ_BLOCK_SIZE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        autoLoginSwitch = (Switch) findViewById(R.id.auto_login_switch);

        Intent i = getIntent();
        id = i.getStringExtra("ID");
        username = i.getStringExtra("USERNAME");
        password = i.getStringExtra("PASSWORD");

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/GuteFiles");
            directory.mkdirs();

            File file = new File(directory, "settingAutoLogin.txt");
            if (file.exists()){
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
                    autoLoginSwitch.setChecked(true);
                }
            }
            else {
                autoLoginSwitch.setChecked(false);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        /*
        File autoLoginFile = new File("settingAutoLogin.txt");

        //Read
        try {
            autoLoginFile.createNewFile();
            FileInputStream fIn = openFileInput("settingAutoLogin.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;
            while((charRead = inputStreamReader.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;
                inputBuffer = new char[READ_BLOCK_SIZE];
            }
            inputStreamReader.close();

            if (s.equals("Enabled")) {
                autoLoginSwitch.setChecked(true);
            }
            else {
                autoLoginSwitch.setChecked(false);
            }
        } catch (IOException e){
            e.printStackTrace();
        }*/



        autoLoginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Create
                if (autoLoginSwitch.isChecked()) {
                    statusAutoLogin = autoLoginSwitch.getTextOn().toString();

                    try {
                        File sdCard = Environment.getExternalStorageDirectory();
                        File directory = new File(sdCard.getAbsolutePath() + "/GuteFiles");
                        directory.mkdirs();

                        File file = new File(directory, "settingAutoLogin.txt");
                        boolean deleted = file.delete();
                        FileOutputStream fOut = new FileOutputStream(file);
                        OutputStreamWriter osw = new OutputStreamWriter(fOut);

                        osw.write(statusAutoLogin);
                        osw.flush();
                        osw.close();

                        try {
                            File fileOwn = new File(directory, "ownerInfo.txt");
                            boolean deletedOwn = fileOwn.delete();
                            FileOutputStream fOutOwn = new FileOutputStream(fileOwn);
                            OutputStreamWriter oswOwn = new OutputStreamWriter(fOutOwn);

                            oswOwn.write(username+"|"+password);
                            oswOwn.flush();
                            oswOwn.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(),
                                "User auto login " + statusAutoLogin.toString(),
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else {
                    statusAutoLogin = autoLoginSwitch.getTextOff().toString();

                    try {
                        File sdCard = Environment.getExternalStorageDirectory();
                        File directory = new File(sdCard.getAbsolutePath() + "/GuteFiles");

                        File file = new File(directory, "settingAutoLogin.txt");
                        boolean deleted = file.delete();

                        File fileOwn = new File(directory, "ownerInfo.txt");
                        boolean deletedOwn = fileOwn.delete();

                        Toast.makeText(getApplicationContext(),
                                "User auto login " + statusAutoLogin.toString(),
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
