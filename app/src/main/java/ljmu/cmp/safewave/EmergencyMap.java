package ljmu.cmp.safewave;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EmergencyMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        Marker currentLocation = MapFragment.markerMap.get("User");
        marker = mMap.addMarker(new MarkerOptions()
                .position(currentLocation.getPosition())
                .title("You"));

        final GPSBackground gpsBackground = new GPSBackground();
        gpsBackground.stopLocationUpdates();
        gpsBackground.getLocationEmergency(getApplicationContext());

        FloatingActionButton cancelButton = findViewById(R.id.fabCancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmergencyMap.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_cancel, null);
                dialogBuilder.setView(dialogView);

                final EditText edt = dialogView.findViewById(R.id.inputReason);
                Button no = dialogView.findViewById(R.id.btnNo);
                Button yes = dialogView.findViewById(R.id.btnYes);

                final AlertDialog b = dialogBuilder.create();
                b.show();

                no.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b.cancel();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());
                        Emergency.hasEmergency = false;
                        gpsBackground.stopLocationUpdates();
                        gpsBackground.getLocation(getApplicationContext());
                        backgroundTask.execute("completeEmergency", "Cancel", edt.getText().toString() );
                        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                        startActivity(intent);
                    }
                });



            }
        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
