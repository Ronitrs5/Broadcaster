package com.example.broadcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String lat;
    String provider;
    protected String latitude,longitude;
    public static final String SHARED_PREFS= "sharedPrefs";
    protected boolean gps_enabled,network_enabled;
    TextView txtLat, txtLon;
    FusedLocationProviderClient mFusedLocationClient;
    int permissionID=44;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    Button button, mapButton;
    ImageView logout;
    EditText title, message;
    AlertDialog.Builder builder;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout= findViewById(R.id.main);
        navigationView= findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        txtLat= findViewById(R.id.lattxt);
        txtLon= findViewById(R.id.lontxt);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        LottieAnimationView b2= findViewById(R.id.imageView2);
        LottieAnimationView b3= findViewById(R.id.imageView3);
        mapButton= findViewById(R.id.gotomapbtn);
        firebaseAuth= FirebaseAuth.getInstance();
        logout= findViewById(R.id.mainlogout);
        button= findViewById(R.id.mainbroadcastbtn);
        title= findViewById(R.id.maintitle);
        message= findViewById(R.id.mainmessage);

        builder= new AlertDialog.Builder(this);


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.main, mapFragment)
                        .commit();

                button.setVisibility(View.GONE);
                mapButton.setVisibility(View.GONE);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("name", "");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(title.getText().toString())) {
                    title.setError("Cannot be empty");
                }
                else if (TextUtils.isEmpty(message.getText().toString())) {
                    message.setError("Cannot be empty");
                } else
                {
                    showDialogBox();
                }
            }
        });


    }


    private void showDialogBox() {

        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.dialoguebox_bk, (ConstraintLayout)findViewById(R.id.dialogbk));
        builder.setView(view);
        final AlertDialog alertDialog= builder.create();
        view.findViewById(R.id.dialogyesbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FcmNotificationsSender notificationsSender= new FcmNotificationsSender("/topics/all"
                        ,title.getText().toString(),
                        message.getText().toString(), getApplicationContext(), MainActivity.this);

                notificationsSender.SendNotifications();
                alertDialog.dismiss();
                Toast.makeText(MainActivity.this, "Broadcast sent successfully", Toast.LENGTH_SHORT).show();
            }
        });




        view.findViewById(R.id.dialognobtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow()!=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if(checkPermissions()){
            if (isLocationEnabled()){
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location= task.getResult();

                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        txtLat.setText("Latitude: "+location.getLatitude() + "");
                        txtLon.setText("Longitude: "+location.getLongitude() + "");
                    }
                });
            }
            else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, permissionID);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Location mLastLocation = locationResult.getLastLocation();
            txtLat.setText("Latitude: " + mLastLocation.getLatitude() + "");
            txtLon.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkPermissions() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}