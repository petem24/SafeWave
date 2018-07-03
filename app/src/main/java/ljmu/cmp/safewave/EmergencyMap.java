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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.ConcurrentHashMap;

public class EmergencyMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static Marker marker;
    static ConcurrentHashMap<String, Marker> eMarkerMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, Marker> eLLMap = new ConcurrentHashMap<>();

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

        //Adds user marker
        Marker currentLocation = MapFragment.markerMap.get("User");
        marker = mMap.addMarker(new MarkerOptions()
                .position(currentLocation.getPosition())
                .icon(BitmapDescriptorFactory.fromAsset("Images/self-marker.png"))
                .title("You"));

        //creates lifeguard marker
        new MapFragment().createLife(mMap, eMarkerMap, eLLMap);
        showLifeMarker();


        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(MapFragment.cameraPosition), 600, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                mMap.setLatLngBoundsForCameraTarget(mMap.getProjection().getVisibleRegion().latLngBounds);
            }

            @Override
            public void onCancel() {

            }
        });


        //stops the location updates
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

                //completes the emergency
                yes.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());
                        Emergency.hasEmergency = false;
                        gpsBackground.stopLocationUpdates();
                        gpsBackground.getLocation(getApplicationContext());
                        backgroundTask.execute("completeEmergency", "Cancel", edt.getText().toString(), Emergency.id);
                        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                        startActivity(intent);
                    }
                });



            }
        });
    }


    public void showLifeMarker() {
        for (String key : eMarkerMap.keySet()) {
            if (key.contains("Life")) {
                eMarkerMap.get(key).setVisible(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


