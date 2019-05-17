package com.example.mapsactivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    Location mLastLocation;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private static final String TAG = "tag";
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView mLocationTextView;
    LocationManager locationManager ;
    boolean GpsStatus ;
    private boolean firsttime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        if (!GPSStatus()) showLocationError();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initViews() {
        mLocationTextView = findViewById(R.id.textview_location);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                                mLocationTextView.setText(
                                        getString(R.string.location_text,
                                                mLastLocation.getLatitude(),
                                                mLastLocation.getLongitude(),
                                                mLastLocation.getTime()));
                            } else {
                                mLocationTextView.setText(R.string.no_location);
                            }
                        }
                    });
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                            "permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public boolean GPSStatus(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void enableGPS(View view) {
        showLocationError();
        firsttime = false;
        if (GPSStatus()){
            getLocation();
        }else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Location is off")
                    .setMessage("Enable location to get nearby services")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent1);
                        }
                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_menu_mylocation)
                    .show();

        }
    }

    private void showLocationError() {
        mLocationTextView.setText("Location not enabled");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firsttime) {
            if (GPSStatus()){
                getLocation();
            }else {
                Toast.makeText(this, "Sorry app wont work without location permission", Toast.LENGTH_SHORT).show();

            }
        }

    }
}
