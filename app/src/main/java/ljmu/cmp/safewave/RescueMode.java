package ljmu.cmp.safewave;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RescueMode extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_mode);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String inLoc = getIntent().getStringExtra("Location");
        String[] s = inLoc.split(",");
        Double lat = Double.valueOf(s[0]);
        Double lng = Double.valueOf(s[1]);

        LatLng location = new LatLng(lat, lng);

        MarkerOptions youMarkerOptions = new MarkerOptions().position(ProfileFragment.currentLocation)
                .title("You")
                .icon(BitmapDescriptorFactory.fromAsset("Images/self-marker.png"));

        Marker you = googleMap.addMarker(youMarkerOptions);

        MarkerOptions emMarkerOptions = new MarkerOptions().position(location)
                .title("Emergency")
                .icon(BitmapDescriptorFactory.fromAsset("Images/warning-marker.png"));

        Marker em = googleMap.addMarker(emMarkerOptions);

        mMap.addMarker(new MarkerOptions().position(location).title("Emergency").icon(BitmapDescriptorFactory.fromAsset("Images/warning-marker.png")));
        mMap.addMarker(new MarkerOptions().position(ProfileFragment.currentLocation).title("You").icon(BitmapDescriptorFactory.fromAsset("Images/self-marker.png")));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(you.getPosition());
        builder.include(em.getPosition());
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }
}
