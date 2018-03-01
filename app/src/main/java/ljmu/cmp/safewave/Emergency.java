package ljmu.cmp.safewave;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pete on 02/02/2018.
 */

public class Emergency {

    String Beach;
    LatLng Location;
    String Type;
    String Details;
    public static String id;
    private Context context;
    static boolean hasEmergency;


    public Emergency(String Beach, LatLng Location, Context context){
        this.Location = Location;
        this.context = context;

        BackgroundTask backgroundTask = new BackgroundTask(context);

        if(User.signedIn == true) {

            backgroundTask.execute("emergencyEmptySigned", Beach, Location.toString(), User.getFullName(), User.Phone, User.Gender, User.DOB);
        }

        else {

            backgroundTask.execute("emergencyEmpty", Beach, Location.toString());
        }
    }

    public Emergency(String Beach, LatLng Location, String Type, String Details, Context context){

        this.Beach = Beach;
        this.Location = Location;
        this.Type = Type;
        this.Details = Details.toString();
        this.context = context;

        BackgroundTask backgroundTask = new BackgroundTask(context);

        if(User.signedIn == true) {

            backgroundTask.execute("emergencyFullSigned", Beach, Location.toString(), Type, Details);
        }

        else {

            backgroundTask.execute("emergencyFull", Beach, Location.toString(), Type, Details);

        }

    }



}
