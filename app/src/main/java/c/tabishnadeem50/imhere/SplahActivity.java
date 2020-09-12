package c.tabishnadeem50.imhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SplahActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splah);
        final SharedPreferences sharedPreferences = this.getSharedPreferences("c.tabishnadeem50.imhere",Context.MODE_PRIVATE);
        ImageView icon = (ImageView) findViewById(R.id.imageView);
        MobileAds.initialize(this, "ca-app-pub-9646733604958580~2749563524");
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        icon.startAnimation(myanim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int activityState = sharedPreferences.getInt("ActivityState", 0);
                Log.i("SplashActivityState", String.valueOf(activityState));
                if (activityState == 0) {
                     Intent intent = new Intent(SplahActivity.this, FetchLocationActivity.class);
                    startActivity(intent);
                } else {
                    final Intent intent = new Intent(SplahActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }
}