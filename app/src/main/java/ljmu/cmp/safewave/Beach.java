package ljmu.cmp.safewave;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pete on 03/02/2018.
 */

public class Beach {
    static String name;
    static LatLng latLng;
    static String id;

    public Beach(){

    }

    public Beach(String name, LatLng latLng, String id){
        this.name = name;
        this.latLng = latLng;
        this.id = id;
    }

    public String getBeachName(){
        return name;
    }

    public LatLng getBeachLatLng(){
        return latLng;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setLL(LatLng latLng) {
        Beach.latLng = latLng;
    }

    public void setID(String id) {
        Beach.id = id;
    }



}
