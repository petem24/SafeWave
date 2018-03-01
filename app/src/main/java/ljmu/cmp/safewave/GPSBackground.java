package ljmu.cmp.safewave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;


public class GPSBackground {


    static LocationRequest mLocationRequest;
    static FusedLocationProviderClient mFusedLocationClient;
    static LocationCallback mLocationCallback;

    @SuppressLint("MissingPermission")
    public void getLocation(final Context context) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(12000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    ProfileFragment.currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (ProfileFragment.checkedIn) {
                        BackgroundTask backgroundTask = new BackgroundTask(context);
                        backgroundTask.execute("updateLocation");

                        updateLocation(MapFragment.markerMap.get("User"));
                    }
                }


            }
        };


        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


    }

    @SuppressLint("MissingPermission")
    public void getLocationOnce(final Context context) {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    ProfileFragment.currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
                stopLocationUpdates();
                SplashScreen.startupDone = true;

            }
        };


        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    @SuppressLint("MissingPermission")
    public void getLocationEmergency(final Context context) {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    ProfileFragment.currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
                if (ProfileFragment.checkedIn) {
                    BackgroundTask backgroundTask = new BackgroundTask(context);
                    backgroundTask.execute("updateLocation");

                        updateLocation(EmergencyMap.marker);
                }

            }
        };


        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public static void updateLocation(Marker currentMarker) {

        if (currentMarker != null) {
            animateMarker(currentMarker.getPosition(), ProfileFragment.currentLocation, currentMarker);
        }
    }

    public static void animateMarker(final LatLng startPosition, final LatLng toPosition, final Marker mMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                mMarker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

}