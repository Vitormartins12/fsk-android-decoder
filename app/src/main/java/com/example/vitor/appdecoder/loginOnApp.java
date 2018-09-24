package com.example.vitor.appdecoder;
import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;


import java.util.concurrent.ExecutionException;

public class loginOnApp extends AppCompatActivity {


    //private decodeAudio mAPI;
    public EditText user;
    public EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        user = (EditText) findViewById(R.id.userText);
        password = (EditText) findViewById(R.id.passwordText);

        };

    //Action onClick for the button
    public void loginMethod(View v){
        Log.d("TAG","Attempting to login...");

        String user = ((EditText) findViewById(R.id.userText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordText)).getText().toString();

        //Intent intent = new Intent(decodeAudio.TESTE);
        try {
            startActivity(new Intent(this,decodeAudio.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}