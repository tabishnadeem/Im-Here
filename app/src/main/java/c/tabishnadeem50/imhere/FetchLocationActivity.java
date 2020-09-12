package c.tabishnadeem50.imhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class FetchLocationActivity extends AppCompatActivity {
    LocationListener locationListener;
    LocationManager locationManager;
    ProgressBar progressBar;
    TextView textView;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(FetchLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }else{
            Toast.makeText(this, "Enable your Location Permission", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_location);
        Objects.requireNonNull(getSupportActionBar()).hide();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        textView = (TextView)findViewById(R.id.textView7);
        progressBar = (ProgressBar) findViewById(R.id.progressBar4) ;

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                progressBar.setVisibility(View.VISIBLE);
                Intent mintent = new Intent(FetchLocationActivity.this,NameActivity.class);
                String latitude = Double.toString(location.getLatitude());
                String longtitude = Double.toString(location.getLongitude());
                mintent.putExtra("Latitude",latitude);
                mintent.putExtra("Longitude",longtitude);
                Log.i( "onLocationChanged: ",latitude);
                progressBar.setVisibility(View.INVISIBLE);
                textView.setText("Redirecting");
                locationManager.removeUpdates(this);
                startActivity(mintent);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ContextCompat.checkSelfPermission(FetchLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FetchLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Intent myIntent = new Intent(FetchLocationActivity.this,NameActivity.class);
            startActivity(myIntent);
            finish();
        }

    }
}
//10-01-2109
//In this activity everything is good expect the fact that the location is not printed in logcat!