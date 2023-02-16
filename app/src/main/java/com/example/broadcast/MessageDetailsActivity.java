package com.example.broadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MessageDetailsActivity extends AppCompatActivity {

    TextView title, message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        title= findViewById(R.id.detailsTitletxt);
        message= findViewById(R.id.detailsmessage);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        Bundle bundle=intent.getExtras();

        String t1= intent.getStringExtra("title");
        String t2= intent.getStringExtra("message");

        Log.d("nitin","hello : "+bundle.toString());
        title.setText(t1);
        message.setText(t2);
    }
}