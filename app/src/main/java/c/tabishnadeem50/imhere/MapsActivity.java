//Developed by Tabish Nadeem (2018-19)

package c.tabishnadeem50.imhere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
   private InterstitialAd mInterstitialAd;
   ImageButton imgbtn;
   int checkedItem = -1;

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SharedPreferences sharedPreferences = this.getSharedPreferences("c.tabishnadeem50.imhere", Context.MODE_PRIVATE);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9646733604958580/9229602505");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        imgbtn = (ImageButton)findViewById(R.id.imageButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        final SharedPreferences  shared = this.getSharedPreferences("c.tabishnadeem50.imhere",Context.MODE_PRIVATE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String usernamefriend = getIntent().getStringExtra("SelectedUserID");
        Log.i("Passed USERID",usernamefriend);
        final int current_checked_item = shared.getInt("CheckedItem",-1);


        if (current_checked_item == 0){
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }else if (current_checked_item == 1){
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }else if (current_checked_item == 2){
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }else if (current_checked_item == 3){
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }




        db.collection("Users")
                .document(usernamefriend)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String latitude = documentSnapshot.getString("Latitude");
                            String longitude = documentSnapshot.getString("Longitude");
                            shared.edit().putString("Latitude",latitude).apply();
                            shared.edit().putString("Longitude",longitude).apply();
                            Log.i("MapsActivity Latitude",latitude);
                            Log.i("FriendID",usernamefriend);
                            Log.i("MapsActivity Longitude",longitude);

                            final String UserName = getIntent().getStringExtra("SelectedUserName");
                            LatLng currentPosition = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            mMap.addMarker(new MarkerOptions().position(currentPosition).title(UserName+"'s Location"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18), 5000, new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    Toast.makeText(MapsActivity.this, "Showing "+UserName+"'s Current Location", Toast.LENGTH_LONG).show();
                                    imgbtn.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                        }else{
                            Toast.makeText(MapsActivity.this, "No Location Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        final String items[] = {"Satellite","Hybrid","Terrain","Normal"};

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Select View")
                        .setSingleChoiceItems(items, current_checked_item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               if (i == 0){
                                   googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                   checkedItem = 0;
                               }else if (i == 1){
                                   googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                   checkedItem = 1;
                               }else if (i == 2){
                                   googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                   checkedItem = 2;
                               }else if (i == 3){
                                   googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                   checkedItem = 3;
                               }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        shared.edit().putInt("CheckedItem",checkedItem).apply();
                    }
                }).show();
            }
        });

    }
}