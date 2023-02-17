package com.example.broadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class AgreementActivity extends AppCompatActivity {

    TextView details, details1;
    LinearLayout layout, layout1;
    CheckBox checkBox;
    Button button;
    boolean checked= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        checkBox = findViewById(R.id.checkbox);
        details= findViewById(R.id.details);
        details1=  findViewById(R.id.details1);
        layout= findViewById(R.id.layout);
        layout1= findViewById(R.id.layout1);
        button= findViewById(R.id.agreementbtn);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox.isChecked()){
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    button.setVisibility(View.GONE);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AgreementActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    public void expand(View view) {

        int v=(details.getVisibility()==View.GONE)? View.VISIBLE :View.GONE;


        TransitionManager.beginDelayedTransition(layout,new AutoTransition());
        details.setVisibility(v);
    }

    public void expand1(View view) {

        int v=(details1.getVisibility()==View.GONE)? View.VISIBLE :View.GONE;
        int v1=(checkBox.getVisibility()==View.GONE)? View.VISIBLE :View.GONE;


        TransitionManager.beginDelayedTransition(layout,new AutoTransition());
        details1.setVisibility(v);
        checkBox.setVisibility(v1);

    }

}