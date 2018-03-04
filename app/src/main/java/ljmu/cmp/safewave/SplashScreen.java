package ljmu.cmp.safewave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


import java.io.IOException;
import java.io.InputStream;

public class SplashScreen extends AppCompatActivity {
    private static int splashTime = 4000;
    public static boolean startupDone = false;
    public static String nToken;
    public static PowerManager pm;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        runtimePermissions();

        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);

    }

    public void runtimePermissions() {
        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);


        } else {
            startup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startup();

                } else {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    builder.setTitle("We need access to your location for the app to run")
                            .setMessage("We only get your location when you check in!")
                            .setPositiveButton("Allow Access", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    runtimePermissions();
                                }
                            })
                            .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return;
            }

//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION) ) {
//
//                AlertDialog.Builder builder;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
//                } else {
//                    builder = new AlertDialog.Builder(this);
//                }
//                builder.setTitle("Oh No, without permission this app is unusable!")
//                        .setMessage("We only get your location when you check in! Since you have check 'Never Show Again' we can not request the permission again. If you want to use this app you must enable permission in settings")
//                        .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                runtimePermissions();
//                            }
//                        })
//                        .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//
//            }
        }
    }


    private void startup() {
        final Animation uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        final Animation downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);

        Emergency.id = "-1";
        ConstraintLayout top = findViewById(R.id.top);
        top.setAnimation(uptodown);

        ConstraintLayout bot = findViewById(R.id.bot);
        bot.setAnimation(downtoup);

        ImageView wave = findViewById(R.id.splashWave);
        wave.setScaleType(ImageView.ScaleType.FIT_XY);

        nToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("All");


        try {
            InputStream inputStream = getApplicationContext().getAssets().open("Images/wave.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            wave.setImageBitmap(bitmap);


            wave.setAnimation(downtoup);
        } catch (IOException e) {
            e.printStackTrace();
        }


        GPSBackground gpsBackground = new GPSBackground();
        gpsBackground.getLocationOnce(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (startupDone == true) {
                        Intent homeIntent = new Intent(SplashScreen.this, MainMenu.class);
                        startActivity(homeIntent);
                        finish();
                        break;
                    }
                }
            }
        }, splashTime);


    }

}
