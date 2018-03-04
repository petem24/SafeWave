package ljmu.cmp.safewave;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;


public class MapFragment extends Fragment {

    Beach beach = new Beach();
    public static CameraPosition cameraPosition;

    static ConcurrentHashMap<String, Marker> markerMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, LatLng> latLngMap = new ConcurrentHashMap<>();

    FloatingActionButton alertButton;
    Switch swEmergency;
    Switch swUser;
    Button checkBar;

    String[] emergencyArray;
    String[] lifeArray;
    String Emlocation;

    Animation uptodown;
    Animation screentoup;

    static PowerManager.WakeLock wakeLock;

    GPSBackground gpsBackground = new GPSBackground();


    public MapFragment() {
        // Required empty public constructor
    }


    MapView mMapView;
    static GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        uptodown = AnimationUtils.loadAnimation(getContext(), R.anim.uptodown);
        screentoup = AnimationUtils.loadAnimation(getContext(), R.anim.screentoup);

        checkBar = rootView.findViewById(R.id.btnCheckOut);

        checkBar.setAnimation(uptodown);


        // Creates the alert button and creates a listener
        alertButton = rootView.findViewById(R.id.addAlertFAB);
        alertButton.setVisibility(View.GONE);

        if (ProfileFragment.checkedIn == true)
            alertButton.setVisibility(View.VISIBLE);

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AlertForm.class);
                startActivity(i);
                (getActivity()).overridePendingTransition(0, 0);
            }
        });

        swEmergency = rootView.findViewById(R.id.switchEm);
        swUser = rootView.findViewById(R.id.switchUserLocation);

        swEmergency.setVisibility(View.GONE);
        swUser.setVisibility(View.GONE);


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {

                googleMap = mMap;

                // Hides the not needed ui elements
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                if (ProfileFragment.checkedIn) {
                    checkBar.setText(Beach.name + " - Click to check out!");
                    checkBar.setBackgroundColor(Color.parseColor("#ff1616"));
                    checkBar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkOut();
                        }
                    });
                    createCheckInMarkers();
                    showCheckInMarkers();
                    setEmSwitch();
                    setUserSwitch();
                } else {
                    createBeaches();
                    showBeachMarkers();
                }

                // Changes camera to position to previous state
                if (cameraPosition == null) {
                    cameraPosition = new CameraPosition.Builder().target(new LatLng(53.557340, -3.101870)).zoom(10).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 600, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            googleMap.setLatLngBoundsForCameraTarget(googleMap.getProjection().getVisibleRegion().latLngBounds);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                } else {
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 600, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            googleMap.setLatLngBoundsForCameraTarget(googleMap.getProjection().getVisibleRegion().latLngBounds);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                }


                // Sets the map bounds based on current visible region


                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        String title = marker.getTitle().toString();

                        if (title.contains("Beach") || title.contains("Verified")) {
                            switch (title) {

                                case "Crosby Beach":

                                    if (ProfileFragment.checkedIn == false) {
                                        beach.setName("Crosby");
                                        beach.setLL(latLngMap.get("Crosby Beach"));

                                        checkIn();

                                        marker.hideInfoWindow();
                                        break;
                                    }


                                case "Formby Beach":
                                    if (ProfileFragment.checkedIn == false) {

                                        beach.setName("Formby");
                                        beach.setLL(latLngMap.get("Formby Beach"));

                                        checkIn();

                                        marker.hideInfoWindow();
                                        break;
                                    }

                                case "Ainsdale Beach":
                                    if (ProfileFragment.checkedIn == false) {
                                        beach.setName("Ainsdale");
                                        beach.setLL(latLngMap.get("Ainsdale Beach"));


                                        checkIn();

                                        marker.hideInfoWindow();
                                        break;
                                    }


                                case "Test Beach":
                                    if (ProfileFragment.checkedIn == false) {
                                        beach.setName("Test");
                                        beach.setLL(latLngMap.get("Test Beach"));

                                        checkIn();

                                        marker.hideInfoWindow();
                                        break;
                                    }

                                case "Verified User":
                                    break;


                                default:
                                    checkOut();
                                    marker.hideInfoWindow();

                            }

                        } else {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.dialog_emergency, null);
                            dialogBuilder.setView(dialogView);

                            final AlertDialog b = dialogBuilder.create();
                            b.show();

                            TextView txtName = dialogView.findViewById(R.id.txtEmName);
                            TextView txtNumber = dialogView.findViewById(R.id.txtEmNumber);
                            TextView txtGender = dialogView.findViewById(R.id.txtEmGender);
                            TextView txtDOB = dialogView.findViewById(R.id.txtEmDOB);
                            TextView txtTime = dialogView.findViewById(R.id.txtEmTime);
                            TextView txtType = dialogView.findViewById(R.id.txtEmType);
                            TextView txtDetails = dialogView.findViewById(R.id.txtEmGender);


                            for (int i = 0; i < emergencyArray.length; i++) {
                                String[] inEmergency = emergencyArray[i].split(";");
                                String name = inEmergency[8];


                                if (name.equals(title)) {
                                    String userName = inEmergency[1];
                                    String beachName = inEmergency[2];
                                    Emlocation = inEmergency[3];
                                    String cTime = inEmergency[4];
                                    String type = inEmergency[5];
                                    String details = inEmergency[6];
                                    String phone = inEmergency[7];
                                    String gender = inEmergency[9];
                                    String DOB = inEmergency[10];

                                    txtName.setText(name);
                                    txtNumber.setText(phone);
                                    txtNumber.setText(phone);
                                    txtGender.setText(gender);
                                    txtDOB.setText(DOB);
                                    txtTime.setText(cTime);
                                    txtType.setText(type);
                                    txtDetails.setText(details);
                                }
                            }

                            Button acceptBtn = dialogView.findViewById(R.id.AcceptEm);

                            acceptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(), RescueMode.class);
                                    i.putExtra("Location", Emlocation);
                                    startActivity(i);
                                    (getActivity()).overridePendingTransition(0, 0);
                                }
                            });

                        }


                    }


                });

            }
        });

        return rootView;


    }


    @SuppressLint("MissingPermission")
    public void checkIn() {


        wakeLock = SplashScreen.pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Some Tag");
        wakeLock.acquire();

        createCheckInMarkers();
        showCheckInMarkers();
        hideBeachMarkers();

        FirebaseMessaging.getInstance().subscribeToTopic(beach.getBeachName());

        gpsBackground.getLocationOnce(getContext());

        gpsBackground = new GPSBackground();
        gpsBackground.getLocation(getContext());
        final LatLng currentLocation = ProfileFragment.currentLocation;


        if (getDistance(currentLocation.latitude, currentLocation.longitude, Beach.latLng.latitude, Beach.latLng.longitude) < 100) {
            ProfileFragment.checkedIn = true;

            //Switches need to be set after the bool has been set
            setEmSwitch();
            setUserSwitch();

            final BackgroundTask backgroundTask = new BackgroundTask(getContext());

            if (User.Level == 's' || User.Level == '\u0000') {
                try {
                    backgroundTask.execute("setCheckIn", beach.getBeachName(), currentLocation.toString()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    backgroundTask.execute("setCheckInVerify", beach.getBeachName(), currentLocation.toString()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            if (backgroundTask.result.contains("Successful")) {

                if (User.Level == 's' || User.Level == '\u0000') {
                    String[] a = backgroundTask.result.split(";");
                    ProfileFragment.checkInId = a[1];
                }

                googleMap.setLatLngBoundsForCameraTarget(googleMap.getProjection().getVisibleRegion().latLngBounds);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 600, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                        Toast.makeText(getContext(), "Checked into " + beach.getBeachName() + " Beach!",
                                Toast.LENGTH_SHORT).show();

                        LatLngBounds map = googleMap.getProjection()
                                .getVisibleRegion().latLngBounds;

                        googleMap.setLatLngBoundsForCameraTarget(map);
                        ProfileFragment.checkedIn = true;
                        cameraPosition = googleMap.getCameraPosition();
                        alertButton.setVisibility(View.VISIBLE);

                        checkBar.setText(Beach.name + " - Click to check out!");
                        checkBar.setBackgroundColor(Color.parseColor("#ff1616"));

                        checkBar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = getLayoutInflater();
                                final View dialogView = inflater.inflate(R.layout.dialog_checkout, null);
                                dialogBuilder.setView(dialogView);

                                Button no = dialogView.findViewById(R.id.btnNo);
                                Button yes = dialogView.findViewById(R.id.btnYes);

                                final AlertDialog b = dialogBuilder.create();
                                b.show();

                                no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        b.cancel();
                                    }
                                });

                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        b.cancel();
                                        checkOut();
                                    }
                                });


                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            } else {
                Toast.makeText(getContext(), "Check in unsuccessful",
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "You must be at the beach to check in!",
                    Toast.LENGTH_SHORT).show();
        }


    }


    public void checkOut() {

        wakeLock.release();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(beach.getBeachName());
        deleteCheckInMarkers();
        showBeachMarkers();
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 600, new GoogleMap.CancelableCallback() {


            @Override
            public void onFinish() {

                gpsBackground.stopLocationUpdates();

                Toast.makeText(getContext(), "Checked out",
                        Toast.LENGTH_SHORT).show();
                cameraPosition = googleMap.getCameraPosition();
                googleMap.setLatLngBoundsForCameraTarget(googleMap.getProjection().getVisibleRegion().latLngBounds);
                ProfileFragment.checkedIn = false;
                getActivity().stopService(new Intent(getActivity(), GPSBackground.class));
                alertButton.setVisibility(View.GONE);

                if(User.Username.equals("temp")) {
                    BackgroundTask backgroundTask = new BackgroundTask(getContext());
                    backgroundTask.execute("setCheckOut");
                }

                else {
                    BackgroundTask backgroundTask = new BackgroundTask(getContext());
                    backgroundTask.execute("setCheckOutVerify");
                }

                swEmergency.setVisibility(View.GONE);
                swUser.setVisibility(View.GONE);

                checkBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                checkBar.setText("Click a beach to check in!");
                checkBar.setBackgroundColor(Color.parseColor("#2396F1"));


            }

            @Override
            public void onCancel() {

            }
        });

    }

    private double getDistance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }


    public void showBeachMarkers() {
        for (String key : markerMap.keySet()) {
            if (key.contains("Beach"))
                markerMap.get(key).setVisible(true);
        }
    }

    public void hideBeachMarkers() {
        for (String key : markerMap.keySet()) {
            if (key.contains("Beach"))
                markerMap.get(key).setVisible(false);
        }

    }

    public void showEmMarkers() {
        for (String key : markerMap.keySet()) {
            if (!key.contains("Beach") && !key.contains("User") && !key.contains("Life"))
                markerMap.get(key).setVisible(true);
        }

    }

    public void hideEmMarkers() {
        for (String key : markerMap.keySet()) {
            if (!key.contains("Beach") && !key.contains("User") && !key.contains("Life")) {
                markerMap.get(key).setVisible(false);
            }
        }
    }


    public void showUserMarker() {
        if (markerMap.get("User") != null) {
            markerMap.get("User").setVisible(true);
        }
    }


    public void hideUserMarker() {

        if (markerMap.get("User") != null) {
            markerMap.get("User").setVisible(false);
        }
    }

    public void showLifeMarker() {
        for (String key : markerMap.keySet()) {
            if (key.contains("Life")) {
                markerMap.get(key).setVisible(true);
            }
        }
    }

    public void hideLifeMarker() {

        for (String key : markerMap.keySet()) {
            if (markerMap.get("Life") != null) {
                markerMap.get(key).setVisible(false);
            }
        }

    }


    private void setEmSwitch() {
        swEmergency = getView().findViewById(R.id.switchEm);
        if ((User.Level == 'a' || User.Level == 'v') && ProfileFragment.checkedIn == true) {
            swEmergency.setChecked(false);
            swEmergency.setVisibility(View.VISIBLE);

            swEmergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        showEmMarkers();
                    } else {
                        hideEmMarkers();
                    }
                }
            });
        } else {
            swEmergency.setVisibility(View.GONE);
        }
    }


    private void setUserSwitch() {
        if (ProfileFragment.checkedIn == true) {
            swUser.setChecked(true);
            swUser.setVisibility(View.VISIBLE);
            swUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        showUserMarker();
                    } else {
                        hideUserMarker();
                    }
                }
            });
        }
    }


    public void createCheckInMarkers() {
        createEm();
        createUserMarker();
        createLife();
    }

    public void showCheckInMarkers() {
        if(User.Level == 'v' || User.Level == 'a')
        showEmMarkers();

        showUserMarker();
        showLifeMarker();
    }

    public void deleteCheckInMarkers() {
        for (String key : markerMap.keySet()) {
            if (!key.contains("Beach")) {
                markerMap.get(key).setVisible(false);
                markerMap.remove(key);
            }
        }
    }

    private void createBeaches() {
        //Create Beaches
        BackgroundTask backgroundTask = new BackgroundTask(getContext());

        try {
            backgroundTask.execute("getBeachMarkers").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String[] inBeach = backgroundTask.result.split("<br>");
        backgroundTask.cancel(true);


        for (int i = 0; i < inBeach.length; i++) {
            String[] beach = inBeach[i].split(";");
            String name = beach[0];
            String latlngstr[] = beach[1].split(",");
            Double lat = Double.parseDouble(latlngstr[0]);
            Double lng = Double.parseDouble(latlngstr[1]);

            LatLng latLng = new LatLng(lat, lng);
            latLngMap.put(name + " Beach", latLng);

            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .title(name + " Beach")
                    .snippet("Click to check in")
                    .icon(BitmapDescriptorFactory.fromAsset("Images/beach-marker.png"));

            Marker myMarker = googleMap.addMarker(markerOptions);
            myMarker.setVisible(true);
            markerMap.put(name + " Beach", myMarker);

            if (ProfileFragment.checkedIn)
                hideBeachMarkers();

        }
    }

    private void createEm() {
        BackgroundTask backgroundTask = new BackgroundTask(getContext());


        try {
            backgroundTask.execute("getEmergency").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!backgroundTask.result.equals("")) {

            emergencyArray = backgroundTask.result.split("<br>");


            for (int i = 0; i < emergencyArray.length; i++) {

                String[] inEmergency = emergencyArray[i].split(";");
                String userName = inEmergency[1];
                String beachName = inEmergency[2];
                String location = inEmergency[3];
                String cTime = inEmergency[4];
                String type = inEmergency[5];
                String details = inEmergency[6];
                String phone = inEmergency[7];
                String name = inEmergency[8];
                String gender = inEmergency[9];
                String DOB = inEmergency[10];

                if (!beachName.equals(null)) {
                    if (beachName.contains(beach.getBeachName())) {

                        String[] latlngstr = location.split(",");

                        Double lat = Double.parseDouble(latlngstr[0]);
                        Double lng = Double.parseDouble(latlngstr[1]);

                        LatLng latLng = new LatLng(lat, lng);
                        latLngMap.put(name, latLng);

                        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                .title(name)
                                .snippet("Click for more details")
                                .icon(BitmapDescriptorFactory.fromAsset("Images/warning-marker.png"));

                        Marker myMarker = googleMap.addMarker(markerOptions);
                        myMarker.setVisible(false);
                        markerMap.put(name, myMarker);
                    }
                }
            }
        }
    }

    private void createLife() {
        BackgroundTask backgroundTask = new BackgroundTask(getContext());

        try {
            backgroundTask.execute("getLife").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!backgroundTask.result.equals("")) {

            lifeArray = backgroundTask.result.split("<br>");


            for (int i = 0; i < lifeArray.length; i++) {

                String[] inEmergency = lifeArray[i].split(";");
                String userName = inEmergency[0];
                String beachName = inEmergency[1];
                String location = inEmergency[2];
                String name = inEmergency[3];


                if (!userName.equals(User.Username)) {

                    String[] latlngstr = location.split(",");

                    Double lat = Double.parseDouble(latlngstr[0]);
                    Double lng = Double.parseDouble(latlngstr[1]);

                    LatLng latLng = new LatLng(lat, lng);
                    latLngMap.put("Life", latLng);

                    MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                            .title("Verified User")
                            .snippet(name)
                            .icon(BitmapDescriptorFactory.fromAsset("Images/lifeguard-marker.png"));

                    Marker myMarker = googleMap.addMarker(markerOptions);
                    myMarker.setVisible(false);
                    markerMap.put("Life", myMarker);

                }
            }
        }
    }

    public void createUserMarker() {
        Marker userMarker = googleMap.addMarker(new MarkerOptions().position(ProfileFragment.currentLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromAsset("Images/self-marker.png")));
        userMarker.setVisible(false);
        markerMap.put("User", userMarker);

    }

}


