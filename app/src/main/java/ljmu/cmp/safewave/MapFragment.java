package ljmu.cmp.safewave;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.appindexing.builders.StickerBuilder;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;


public class MapFragment extends Fragment {

    Beach beach = new Beach();
    public static CameraPosition cameraPosition;

    static ConcurrentHashMap<String, Marker> markerMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, LatLng> latLngMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, Marker> changemarkerMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, LatLng> changelatLngMap = new ConcurrentHashMap<>();

    FloatingActionButton alertButton;
    Switch swEmergency;
    Switch swUser;
    FloatingActionButton imgHosp;
    Button checkBar;

    String[] emergencyArray;
    String[] lifeArray;
    String Emlocation;

    Animation uptodown;
    Animation screentoup;

    static Handler handler = null;
    static Runnable r;

    static NotificationManager notificationManager;

    public static final BooVariable bv = new BooVariable();
    public static final BooVariable logoutListner = new BooVariable();

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

        imgHosp = rootView.findViewById(R.id.imgHosp);
        imgHosp.setVisibility(View.GONE);


        bv.setBoo(false);
        bv.setListener(new BooVariable.ChangeListener() {
            @Override
            public void onChange() {
                if (bv.isBoo()) {
                    checkOut();
                }
            }
        });

        //if user signs out while checked in
        logoutListner.setBoo(false);
        logoutListner.setListener(new BooVariable.ChangeListener() {
            @Override
            public void onChange() {
                if (logoutListner.isBoo()) {
                    checkOut();
                }
            }
        });

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
                    createBeaches(googleMap, markerMap, latLngMap);
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
                            // Sets the map bounds based on current visible region
                            googleMap.setLatLngBoundsForCameraTarget(googleMap.getProjection().getVisibleRegion().latLngBounds);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                }



                //SETS A ONCLICK FOR THE INFO BOXES OF THE MARKERS
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
                                        beach.setID("1");

                                        checkIn();

                                        marker.hideInfoWindow();
                                        break;
                                    }


                                case "Formby Beach":
                                    if (ProfileFragment.checkedIn == false) {

                                        beach.setName("Formby");
                                        beach.setLL(latLngMap.get("Formby Beach"));
                                        beach.setID("2");

                                        checkIn();

                                        marker.hideInfoWindow();
                                        break;
                                    }

                                case "Ainsdale Beach":
                                    if (ProfileFragment.checkedIn == false) {
                                        beach.setName("Ainsdale");
                                        beach.setLL(latLngMap.get("Ainsdale Beach"));
                                        beach.setID("3");


                                        checkIn();

                                        marker.hideInfoWindow();
                                        break;
                                    }


                                case "Test Beach":
                                    if (ProfileFragment.checkedIn == false) {
                                        beach.setName("Test");
                                        beach.setLL(latLngMap.get("Test Beach"));
                                        beach.setID("4");

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
                            String[] t = new String[0];

                            TextView txtName = dialogView.findViewById(R.id.txtEmName);
                            TextView txtNumber = dialogView.findViewById(R.id.txtEmNumber);
                            TextView txtGender = dialogView.findViewById(R.id.txtEmGender);
                            TextView txtDOB = dialogView.findViewById(R.id.txtEmDOB);
                            TextView txtTime = dialogView.findViewById(R.id.txtEmTime);
                            TextView txtType = dialogView.findViewById(R.id.txtEmType);
                            TextView txtDetails = dialogView.findViewById(R.id.txtEmDetails);


                            for (int i = 0; i < emergencyArray.length; i++) {
                                String[] inEmergency = emergencyArray[i].split(";");
                                String name = inEmergency[8];

                                t = title.split(",");

                                if (name.equals(t[1])) {
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

                            Button acceptBtn = dialogView.findViewById(R.id.GoToMapBtn);
                            final String d = t[0];
                            acceptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(), RescueMode.class);
                                    i.putExtra("Location", Emlocation);
                                    i.putExtra("id", d);
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

        //Gets the location once to make sure the user is checking in from the right place
        gpsBackground.getLocationOnce(getContext());
        gpsBackground = new GPSBackground();

        //Sets up location requests to keep track of the user
        gpsBackground.getLocation(getContext());
        final LatLng currentLocation = ProfileFragment.currentLocation;


        if (getDistance(currentLocation.latitude, currentLocation.longitude, Beach.latLng.latitude, Beach.latLng.longitude) < 1) {
            ProfileFragment.checkedIn = true;

            //This keeps the app active in the background
            wakeLock = SplashScreen.pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Some Tag");
            wakeLock.acquire();

            createCheckInMarkers();
            showCheckInMarkers();
            hideBeachMarkers();

            imgHosp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_hosp, null);
                    dialogBuilder.setView(dialogView);

                    TextView name = dialogView.findViewById(R.id.txtHospName);
                    TextView phone = dialogView.findViewById(R.id.txtPhoneNumberHosp);

                    //gets the hospitals from the database
                    BackgroundTask backgroundTask = new BackgroundTask(getContext());
                    try {
                        backgroundTask.execute("getHospitals").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    String[] s = backgroundTask.result.split(";");
                    name.setText(s[0]);
                    phone.setText(s[3]);

                    final String loc = s[2];

                    //opens google maps and directs them to the hospital location
                    Button directMe = dialogView.findViewById(R.id.btnDirect);
                    directMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + loc);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });
                    final AlertDialog b = dialogBuilder.create();
                    b.show();

                }
            });
            imgHosp.setVisibility(View.VISIBLE);

            FirebaseMessaging.getInstance().subscribeToTopic(beach.getBeachName());

            if (User.Username != null) {
                if (User.Level == 'v' || User.Level == 'a') {
                    FirebaseMessaging.getInstance().subscribeToTopic("Lifeguards");
                }
            }

            //Switches need to be set after the bool has been set
            setEmSwitch();
            setUserSwitch();

            final BackgroundTask backgroundTask = new BackgroundTask(getContext());


            //Adds the user into either the check in or check in verify table in the database
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

                        //if everything is successful
                        Toast.makeText(getContext(), "Checked into " + beach.getBeachName() + " Beach!",
                                Toast.LENGTH_SHORT).show();

                        LatLngBounds map = googleMap.getProjection()
                                .getVisibleRegion().latLngBounds;

                        //sets new lat lng bounds for the camera
                        googleMap.setLatLngBoundsForCameraTarget(map);
                        ProfileFragment.checkedIn = true;
                        cameraPosition = googleMap.getCameraPosition();
                        alertButton.setVisibility(View.VISIBLE);

                        //changes the bar at the top
                        checkBar.setText(Beach.name + " - Click to check out!");
                        checkBar.setBackgroundColor(Color.parseColor("#ff1616"));

                        String latlngstr[] = Beach.latLng.toString().split(",");
                        String lat = latlngstr[0].substring(10);
                        String lng = latlngstr[1].substring(0, latlngstr[1].length() - 1);

                        //gets the tidal information for the notification
                        getTidal(lat, lng);


                        //when the user clicks the bar check them out
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


        //Updates the markers every 5 seconds
        handler = new Handler();

        r = new Runnable() {
            public void run() {
                updateMarkers();
                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(r, 5000);
    }

    public void checkOut() {

        //RELEASE THE WAKE LOCK
        if (wakeLock.isHeld())
            wakeLock.release();

        //UNSUB FROM TOPIC
        FirebaseMessaging.getInstance().unsubscribeFromTopic(beach.getBeachName());
        imgHosp.setVisibility(View.GONE);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 600, new GoogleMap.CancelableCallback() {


            @Override
            public void onFinish() {

                //STOPS LOCATION UPDATES, CHANGES BAR, MARKERS AND SWITCHES
                gpsBackground.stopLocationUpdates();
                gpsBackground.equals(null);
                cancelNot();

                handler.removeCallbacks(r);
                Toast.makeText(getContext(), "Checked out",
                        Toast.LENGTH_SHORT).show();
                cameraPosition = googleMap.getCameraPosition();
                googleMap.setLatLngBoundsForCameraTarget(googleMap.getProjection().getVisibleRegion().latLngBounds);
                ProfileFragment.checkedIn = false;
                getActivity().stopService(new Intent(getActivity(), GPSBackground.class));
                alertButton.setVisibility(View.GONE);

                if (User.Username.equals("temp")) {
                    BackgroundTask backgroundTask = new BackgroundTask(getContext());
                    backgroundTask.execute("setCheckOut");
                } else {
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

                createBeaches(googleMap, markerMap, latLngMap);
                deleteCheckInMarkers();
                showBeachMarkers();


            }

            @Override
            public void onCancel() {

            }
        });

    }


    //RETURNS THE DISTANCE BETWEEN THE 2 POINTS IN MILES
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {

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

    //SHOWS THE BEACH MARKERS ON THE MAP
    public void showBeachMarkers() {
        for (String key : markerMap.keySet()) {
            if (key.contains("Beach"))
                markerMap.get(key).setVisible(true);
        }
    }

    //HIDES THE BEACH MARKERS ON THE MAP
    public void hideBeachMarkers() {
        for (String key : markerMap.keySet()) {
            if (key.contains("Beach"))
                markerMap.get(key).setVisible(false);
        }

    }

    //SHOWS THE EMERGENCY MARKERS ON THE MAP
    public void showEmMarkers() {
        for (String key : markerMap.keySet()) {
            if (!key.contains("Beach") && !key.contains("User") && !key.contains("Life"))
                markerMap.get(key).setVisible(true);
        }

    }

    //HIDES THE EMERGENCY MARKERS ON THE MAP
    public void hideEmMarkers() {
        for (String key : markerMap.keySet()) {
            if (!key.contains("Beach") && !key.contains("User") && !key.contains("Life")) {
                markerMap.get(key).setVisible(false);
            }
        }
    }

    //SHOWS THE USER MARKER ON THE MAP
    public void showUserMarker() {
        if (markerMap.get("User") != null) {
            markerMap.get("User").setVisible(true);
        }
    }

    //HIDES THE USER MARKER ON THE MAP
    public void hideUserMarker() {

        if (markerMap.get("User") != null) {
            markerMap.get("User").setVisible(false);
        }
    }

    //SHOWS THE LIFE MARKER ON THE MAP
    public void showLifeMarker() {
        for (String key : markerMap.keySet()) {
            if (key.contains("Life")) {
                markerMap.get(key).setVisible(true);
            }
        }
    }

    //SETS THE BEHAVIOUR OF THE EMERGENCY SWITCH
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

    //SETS THE BEHAVIOUR OF THE USER SWITCH
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

    //CREATES ALL THE MARKERS WHEN CHECKED IN
    public void createCheckInMarkers() {
        createEm(googleMap, markerMap, latLngMap);
        createUserMarker(googleMap, markerMap);
        createLife(googleMap, markerMap, latLngMap);
    }

    //SHOWS ALL THE MARKERS WHEN CHECKED IN
    public void showCheckInMarkers() {
        if (User.Level == 'v' || User.Level == 'a')
            showEmMarkers();

        showUserMarker();
        showLifeMarker();
    }

    //DELETES ALL THE MARKERS WHEN CHECKED IN
    public void deleteCheckInMarkers() {
        for (String key : markerMap.keySet()) {
            if (!key.contains("Beach")) {
                markerMap.get(key).setVisible(false);
                markerMap.remove(key);
            }
        }
    }

    //Removes and places markers if they have changed
    private void updateMarkers() {

        if (checkSame()) {

            deleteCheckInMarkers();
            createCheckInMarkers();
            showCheckInMarkers();
            showLifeMarker();
            showUserMarker();
        }
    }

    //CREATES THE BEACH MARKERS
    private void createBeaches(GoogleMap mv, ConcurrentHashMap mm, ConcurrentHashMap llm) {
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
            llm.put(name + " Beach", latLng);

            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .title(name + " Beach")
                    .snippet("Click to check in")
                    .icon(BitmapDescriptorFactory.fromAsset("Images/beach-marker.png"));

            Marker myMarker = mv.addMarker(markerOptions);
            myMarker.setVisible(false);
            mm.put(name + " Beach", myMarker);

            if (ProfileFragment.checkedIn)
                hideBeachMarkers();

        }
    }

    //CREATES ALL EMERGENCIES FROM DATABASE
    private void createEm(GoogleMap mv, ConcurrentHashMap mm, ConcurrentHashMap llm) {
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

                String id = inEmergency[0];
                String beachName = inEmergency[2];
                String location = inEmergency[3];
                String name = inEmergency[8];

                if (!beachName.equals(null)) {
                    if (beachName.contains(beach.getBeachName())) {

                        String[] latlngstr = location.split(",");

                        Double lat = Double.parseDouble(latlngstr[0]);
                        Double lng = Double.parseDouble(latlngstr[1]);

                        LatLng latLng = new LatLng(lat, lng);
                        llm.put(id, latLng);

                        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                .title(id+","+name)
                                .snippet("Click for more details")
                                .icon(BitmapDescriptorFactory.fromAsset("Images/warning-marker.png"));


                        Marker myMarker = mv.addMarker(markerOptions);
                        myMarker.setVisible(false);
                        mm.put(id, myMarker);

                    }
                }
            }

        }
    }

    //CREATES ALL LIFEGUARD MARKERS FROM DATABASE
    public void createLife(GoogleMap mv, ConcurrentHashMap mm, ConcurrentHashMap llm) {
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
                    llm.put("Life", latLng);

                    MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                            .title("Verified User")
                            .snippet(name)
                            .icon(BitmapDescriptorFactory.fromAsset("Images/lifeguard-marker.png"));

                    Marker myMarker = mv.addMarker(markerOptions);
                    myMarker.setVisible(false);
                    mm.put("Life", myMarker);

                }

            }
        }
    }

    //compares the new data against the old and returns true or false
    private boolean checkSame() {

        createBeaches(googleMap, changemarkerMap, changelatLngMap);
        createUserMarker(googleMap, changemarkerMap);
        createEm(googleMap, changemarkerMap, changelatLngMap);
        createLife(googleMap, changemarkerMap, changelatLngMap);
        String mmMap = "";
        String cmMap = "";

        if (markerMap.size() == changemarkerMap.size()) {

            for (String key : markerMap.keySet()) {
                if (key.contains("Life"))

                    mmMap += markerMap.get(key).getPosition().toString();
            }

            for (String key : changemarkerMap.keySet()) {
                if (key.contains("Life"))

                    cmMap += changemarkerMap.get(key).getPosition().toString();
            }

            if (!cmMap.equals(mmMap)) {
                return true;
            }

            return false;
        }


        return true;

    }

    //CREATES THE USER MARKER
    private void createUserMarker(GoogleMap mv, ConcurrentHashMap mm) {
        Marker userMarker = mv.addMarker(new MarkerOptions().position(ProfileFragment.currentLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromAsset("Images/self-marker.png")));
        userMarker.setVisible(false);
        mm.put("User", userMarker);

    }

    //THIS IS THE NOTIFICATION WHEN YOU CHECK IN THAT TELLS YOU THE CURRENT TIDES
    private void sendNotification() {
        Intent intent = new Intent(getActivity(), getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = "channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext(), channelId)
                        .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                        .setContentTitle("Checked Into " + Beach.name + " Beach")
                        .setContentText("High Tide is at - " + String.valueOf(new Date((long) BeachFullActivity.dtArray.get(0) * 1000).getHours()) + ":" + new Date((long) BeachFullActivity.dtArray.get(0) * 1000).getMinutes() + ", Low tide is at - " + String.valueOf(new Date((long) BeachFullActivity.dtArray.get(1) * 1000).getHours()) + ":" + new Date((long) BeachFullActivity.dtArray.get(1) * 1000).getMinutes())
                        .setOngoing(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(20, notificationBuilder.build());


    }

    //REMOVE NOTIFICATION
    private void cancelNot() {
        notificationManager.cancel(20);
    }

    //GETS THE TIDAL INFORMATION FROM THE WORLDTIDES API
    private void getTidal(String lat, String lng) {

        String url = "https://www.worldtides.info/api?extremes&lat=" + lat + "&lon=" + lng + "&key=c3c9948d-a8a4-4674-9b95-4936da92e4d1";

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray listArray = response.getJSONArray("extremes");
                            BeachFullActivity.clearArrays();
                            for (int i = 0; i < 2; i++) {
                                JSONObject listObject = listArray.getJSONObject(i);

                                BeachFullActivity.dtArray.add(listObject.getInt("dt"));
                                BeachFullActivity.typeArray.add(listObject.getString("type"));

                            }
                            sendNotification();
                            requestQueue.stop();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });


        requestQueue.add(jsObjRequest);

    }
}


