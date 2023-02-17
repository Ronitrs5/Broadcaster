package com.example.broadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MessageDetailsActivity extends Activity {
    private TextView Title,Message ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        Title=(TextView)findViewById(R.id.detailsTitletxt);
        Message=(TextView)findViewById(R.id.detailsmessage);

        if(getIntent().getExtras()!=null)
        {
            for(String key : getIntent().getExtras().keySet())
            {
                if(key.equals("title")) {
                    Title.setText(getIntent().getExtras().getString(key));
                    String title = getIntent().getStringExtra("title");
                }
                else if(key.equals("message")) {
                    Message.setText(getIntent().getExtras().getString(key));
                    String body = getIntent().getStringExtra("body");
                }

            }

        }

    }
}