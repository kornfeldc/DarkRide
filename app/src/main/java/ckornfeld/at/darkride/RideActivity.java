package ckornfeld.at.darkride;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import classes.DF;
import classes.Ride;

public class RideActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fabStart, fabStop, fabClose, fabReset;
    TextView textSpeed, textDistance, textDuration, textTime;

    Ride ride = null;
    Location lastLocation = null;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1000;  /* 1 secs */
    private long FASTEST_INTERVAL = 1000; /* 1 sec */
    private long UI_REFRESH_INTERVALL_SEC = 10;

    final Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        fabStart = findViewById(R.id.fabStart);
        fabStop = findViewById(R.id.fabStop);
        fabClose = findViewById(R.id.fabClose);
        fabReset = findViewById(R.id.fabReset);

        fabStart.setOnClickListener(this);
        fabStop.setOnClickListener(this);
        fabClose.setOnClickListener(this);
        fabReset.setOnClickListener(this);

        textSpeed = findViewById(R.id.textSpeed);
        textDistance = findViewById(R.id.textDistance);
        textTime = findViewById(R.id.textTime);
        textDuration = findViewById(R.id.textDuration);

        textTime.setText(DF.CalendarToString("HH:mm"));

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        int t = 0;

        runnable = new Runnable() {
            @Override
            public void run() {
                loadUi();
                handler.postDelayed(this, UI_REFRESH_INTERVALL_SEC*1000);
            }
        };

        handler.postDelayed(runnable, UI_REFRESH_INTERVALL_SEC*1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUi();
        startLocationUpdates();
    }

    private void loadUi() {
        ride = Ride.load(this);
        fabStart.setVisibility(!ride.isStarted() ? View.VISIBLE : View.GONE);
        fabStop.setVisibility(!ride.isStarted() ? View.GONE : View.VISIBLE);
        fabReset.setVisibility(!ride.isEmpty() && !ride.isStarted() ? View.VISIBLE : View.GONE);
        textDuration.setText(ride.getFormattedDuration());
        textDistance.setText(ride.getFormattedDistance());
        textTime.setText(DF.CalendarToString("HH:mm"));
        setWakeLock(ride.isStarted());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void close() {
        ride.save(this);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ride, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == fabStart) {
            ride.start(this);
            loadUi();
        }
        else if(view == fabStop) {
            ride.stop(this);
            lastLocation = null;
            loadUi();
        }
        else if(view == fabClose) {
            close();
        }
        else if(view == fabReset) {
            Ride.reset(this);
            ride = new Ride();
            lastLocation = null;
            loadUi();
        }
    }

    protected void setWakeLock(boolean on) {
        if(on)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setMaxWaitTime(0);
        mLocationRequest.setSmallestDisplacement(0);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        try {
            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        }
        catch(SecurityException se) {
            //todo handle me
            int x = 1;
        }
    }

    public void onLocationChanged(Location location) {
        if(ride.isStarted() && lastLocation != null)
            ride.addMeters(this, location.distanceTo(lastLocation));
        textSpeed.setText(String.valueOf(Math.round(location.getSpeed()*3.6 /*m/s to km/h*/)));
        lastLocation = location;
    }

}
