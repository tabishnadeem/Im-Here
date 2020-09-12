package c.tabishnadeem50.imhere;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ListActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    ListView listView;
    ArrayList<String> users;
    ConstraintLayout constraintLayout;
    ArrayAdapter arrayAdapter;
    TextView textView , textView2;
    LinearLayout linearLayout;
    LocationListener locationListener;
    LocationManager locationManager;

    private long backPressedTime;
    private Toast backToast;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }else{
            Toast.makeText(this, "Enable your Location Permission", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            finishAffinity();
            super.onBackPressed();
        }else {
            backToast = Toast.makeText(getBaseContext(),"Press Back Again to Exit",Toast.LENGTH_SHORT);
            backToast.show();
            backPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mybutton) {
            // do something here
                new AlertDialog.Builder(this)
                        .setTitle("Info :")
                        .setMessage("If your friend has not opened the app in a while the location shown will be his/her last known location!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_list);
        setTitle("Your Friends");
        constraintLayout = (ConstraintLayout)findViewById(R.id.constraintLayout);
        locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final SharedPreferences sharedPreferences = this.getSharedPreferences("c.tabishnadeem50.imhere",Context.MODE_PRIVATE);
        final SQLiteDatabase sql = this.openOrCreateDatabase("Users",MODE_PRIVATE,null);
        sql.execSQL("CREATE TABLE IF NOT EXISTS myUser (name VARCHAR, userId VARCHAR)");
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        linearLayout = (LinearLayout) findViewById(R.id.linLayout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab) ;
        listView = (ListView)findViewById(R.id.listView);
        users = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(ListActivity.this,R.layout.mylistview,R.id.textView3, users);
        listView.setAdapter(arrayAdapter);
        final String fetchedUserName =  sharedPreferences.getString("UserID","121212");
        setTitle("UserID :"+fetchedUserName);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                HashMap<String,Object> mylocation = new HashMap<>();
                //String sk = getIntent().getStringExtra("Latitude");
                //String sl = getIntent().getStringExtra("Longitude");
                String sk = Double.toString(location.getLatitude());
                String sl = Double.toString(location.getLongitude());
                int pointIndLat = sk.indexOf(".");
                int pointIndLong = sl.indexOf(".");
                String lat = sk.substring(0, pointIndLat + 5);
                String lng = sl.substring(0, pointIndLong + 5);
                mylocation.put("Latitude", lat);
                mylocation.put("Longitude", lng);

                db.collection("Users").document(fetchedUserName)
                        .update(mylocation)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("Location ADDED", "YES");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Location ADDED", "NO");
                            }
                        });

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

        if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        final int listState =  sharedPreferences.getInt("ListState",0);

        if (listState == 0) {
            textView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
        }else {
            textView.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        try{
         db.collection("Users").document(fetchedUserName).collection("Friends")
               .get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()){
                         for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                             String userData = (Objects.requireNonNull(document.get("Name"))).toString();

                             users.add(userData);
                             arrayAdapter.notifyDataSetChanged();
                         }
                             }
                         }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Log.i("Error",e.getMessage());
             }
         });}catch (Exception ex){
            ex.printStackTrace();
        }



         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    String name =(String) listView.getItemAtPosition(i);
                 Log.i(name+" ID ",String.valueOf(listView.getItemIdAtPosition(i)));
                 final String friendUsername = getIntent().getStringExtra("FriendUserName");
                                      final Intent intent_this = new Intent(ListActivity.this,MapsActivity.class);
                                      intent_this.putExtra("SelectedUserName",name);
                                      try {
                                          Cursor cursor = sql.rawQuery("SELECT userId FROM myUser WHERE name = '" + name + "'", null);
                                          int userIdIndex = cursor.getColumnIndex("userId");
                                          cursor.moveToFirst();
                                          String selectedUserID = cursor.getString(userIdIndex);


                                          intent_this.putExtra("SelectedUserID", selectedUserID);
                                          intent_this.putExtra("FriendUsername", friendUsername);
                                          startActivity(intent_this);
                                      }catch (Exception e){
                                          e.printStackTrace();
                                      }
                                     }

                                 });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putInt("ListState",1).apply();
                Intent intent =new Intent(ListActivity.this , UserNameActivity.class);
                startActivity(intent);
            }
        });
    }
}