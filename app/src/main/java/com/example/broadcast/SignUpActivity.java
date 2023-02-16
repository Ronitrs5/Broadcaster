package com.example.broadcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText phone, otp;
    TextView textView, textView1, textview2, guestdetails, guesttitle;
    Button phonebtn, otpbtn, guestbtn;
    ProgressBar progressBar;
    String userID;
    LottieAnimationView pb, image;
    LinearLayout layout;
    String otpId;
    ImageView imageView;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    public static final String SHARED_PREFS= "sharedPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        LottieAnimationView pb= findViewById(R.id.progressBar);
        firebaseFirestore= FirebaseFirestore.getInstance();
        LottieAnimationView image= findViewById(R.id.imageView);

        mAuth= FirebaseAuth.getInstance();
        layout= findViewById(R.id.layout2);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        guestbtn= findViewById(R.id.signupguestbtn);
        guestdetails= findViewById(R.id.guestdetails);
        textView= findViewById(R.id.signupTxt);
        phone= findViewById(R.id.signupPhone);
        ccp= (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phone);
        otp= findViewById(R.id.signupOtp);
        phonebtn= findViewById(R.id.signupphonebtn);
        otpbtn= findViewById(R.id.mainbroadcastbtn);

        checkBox();

        guestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, MainActivity2.class));
                finish();
            }
        });

        phonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(phone.getText().toString())){
                    phone.setError("Enter a valid phone number");
                }
                else{
                    ccp.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.INVISIBLE);
                    phonebtn.setVisibility(View.INVISIBLE);
                    otpbtn.setVisibility(View.VISIBLE);
                    otp.setVisibility(View.VISIBLE);
                    String numb= ccp.getFullNumberWithPlus().replace(" ", "");
                    initiateotp(numb);

                }
            }
        });

        otpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(otp.getText().toString())){
                    otp.setError("Cannot be empty");
                }

                else{
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(otpId, otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                    image.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    pb.setVisibility(View.VISIBLE);
                }


            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone.setVisibility(View.VISIBLE);
                ccp.setVisibility(View.VISIBLE);
                phonebtn.setVisibility(View.VISIBLE);
                otp.setVisibility(View.INVISIBLE);
                otpbtn.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        });


    }


    private void checkBox() {
        SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("name", "" );

        if(check.equals("true")){
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initiateotp(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks=
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    otpId=s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    signInWithPhoneAuthCredential(phoneAuthCredential);

                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor= sharedPreferences.edit();
                            editor.putString("name", "true");
                            editor.apply();
                            startActivity(new Intent(SignUpActivity.this, AgreementActivity.class));


                            finish();

                        }
                        else {
                        textView.setVisibility(View.VISIBLE);

                            Toast.makeText(SignUpActivity.this, "Something went wrong! Please try again", Toast.LENGTH_LONG).show();



                        }
                    }
                });

    }

    public void expand2(View view) {

        int v=(guestdetails.getVisibility()==View.GONE)? View.VISIBLE :View.GONE;
        int v1=(guestbtn.getVisibility()==View.GONE)? View.VISIBLE :View.GONE;


        TransitionManager.beginDelayedTransition(layout,new AutoTransition());
        guestdetails.setVisibility(v);
        guestbtn.setVisibility(v);

    }
}