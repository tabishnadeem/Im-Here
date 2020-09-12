package c.tabishnadeem50.imhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NameActivity extends AppCompatActivity  {
    String name,username;
    int rnum;
    Random random = new Random();
    private ProgressBar progressBar;
    LocationListener locationListener;
    LocationManager locationManager;
    private Snackbar snackbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
         setTitle("Enter Name");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final SharedPreferences sharedPreferences = this.getSharedPreferences("c.tabishnadeem50.imhere", Context.MODE_PRIVATE);

        final EditText nameInput = (EditText)findViewById(R.id.nameText);
        Button button = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rnum = random.nextInt(99999) + 10000;
        final View mview = this.getCurrentFocus();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( nameInput.getText().toString().equals("")) {

                    Toast.makeText(NameActivity.this, "Field Should not be empty", Toast.LENGTH_SHORT).show();
                }else if (nameInput.getText().toString().length() < 3){
                    snackbar =  Snackbar.make(findViewById(R.id.constraintLayout),"Input Should be more than 3 characters",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {

                    if (mview != null){
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mview.getWindowToken(),0);
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    name = nameInput.getText().toString();
                    String s = name.substring(0, 3);
                    final String value = String.valueOf(rnum);
                    username = s + value;
                    sharedPreferences.edit().putString("UserID", username).apply();
                    sharedPreferences.edit().putString("UserName", name).apply();
                    sharedPreferences.edit().putInt("ActivityState", 1).apply();
                    int i = sharedPreferences.getInt("ActivityState", 0);
                    Log.i("ActivityState", String.valueOf(i));


                    Log.i("Username :", username);
                    Intent intent_new = new Intent(NameActivity.this, ListActivity.class);
                    intent_new.putExtra("CurrenUser", username);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Username", username);
                    user.put("Name", name);

                    db.collection("Users").document(username)
                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("Document", "ADDED!!!!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Document", "FAILED!!!!");
                        }
                    });
                    String fetchedUserName = sharedPreferences.getString("UserID", "121212");
                    Log.i("SharedPreferences", fetchedUserName);


                            Map<String, Object> locations = new HashMap<>();
                            String sk = getIntent().getStringExtra("Latitude");
                            String sl = getIntent().getStringExtra("Longitude");
                            int pointIndLat = sk.indexOf(".");
                            int pointIndLong = sl.indexOf(".");
                            String lat = sk.substring(0, pointIndLat + 5);
                            String lng = sl.substring(0, pointIndLong + 5);
                            locations.put("Latitude", lat);
                            locations.put("Longitude", lng);
                            locations.put("Username", username);
                            locations.put("Name", name);
                            db.collection("Users").document(username)
                                    .set(locations)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("Location ADDED", "YES");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("Location ADDED", "NO");
                                }
                            });
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(intent_new);


                }
            }
        });

    }
}