package c.tabishnadeem50.imhere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class UserNameActivity extends AppCompatActivity {

boolean received = false;
ProgressBar myprogressBar;
private InterstitialAd mInterstitialAd;

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
        setContentView(R.layout.activity_user_name);
        setTitle("Enter Username");
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final SQLiteDatabase sql = this.openOrCreateDatabase("Users",MODE_PRIVATE,null);
        sql.execSQL("CREATE TABLE IF NOT EXISTS myUser (name VARCHAR, userId VARCHAR)");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9646733604958580/9238169348");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        myprogressBar = (ProgressBar)findViewById(R.id.progressBar2);
        final EditText usernameEditText = (EditText) findViewById(R.id.username);
        Button locationButton = (Button) findViewById(R.id.getLocationButton);
        final SharedPreferences sharedPreferences = this.getSharedPreferences("c.tabishnadeem50.imhere",Context.MODE_PRIVATE);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameEditText == null || usernameEditText.getText().toString().equals("")) {
                    Toast.makeText(UserNameActivity.this, "Field Should Not Be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    myprogressBar.setVisibility(View.VISIBLE);
                    final String fetchedUserName = sharedPreferences.getString("UserID", "121212");

                    firebaseFirestore.collection("Users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                            Log.i("Documents", doc.getId());
                                            if (usernameEditText.getText().toString().equals(doc.getId())) {
                                                String name = Objects.requireNonNull(doc.get("Name")).toString();
                                                Map<String, Object> friend = new HashMap<>();
                                                friend.put("Username", usernameEditText.getText().toString());
                                                friend.put("Name", name);
                                                try {
                                                    sql.execSQL("INSERT INTO myUser(name, userId) VALUES('" + name + "','" + usernameEditText.getText().toString() + "')");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    firebaseFirestore.collection("Users")
                                                            .document(fetchedUserName)
                                                            .collection("Friends")
                                                            .document(usernameEditText.getText().toString())
                                                            .set(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.i("Friend", "Added");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.i("Friend", "Not Added");
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                received = true;
                                            }} if (!received){
                                                Toast.makeText(UserNameActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
                                                myprogressBar.setVisibility(View.INVISIBLE);
                                            }


                                            if (usernameEditText == null || usernameEditText.getText().toString().equals("")) {
                                                Toast.makeText(UserNameActivity.this, "Field Should Not Be Empty", Toast.LENGTH_SHORT).show();
                                            }else if (usernameEditText.getText().toString().equals(fetchedUserName)){
                                                Toast.makeText(UserNameActivity.this, "You can't add your username!", Toast.LENGTH_SHORT).show();
                                                myprogressBar.setVisibility(View.INVISIBLE);
                                            }else
                                              {
                                                if (received) {
                                                    Intent intent = new Intent(UserNameActivity.this, ListActivity.class);
                                                    intent.putExtra("FriendUserName",usernameEditText.getText().toString());
                                                    myprogressBar.setVisibility(View.INVISIBLE);
                                                    startActivity(intent);
                                                }


                                            }

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserNameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.i("Value of T", String.valueOf(received));


                }
            }
        });





    }
}
