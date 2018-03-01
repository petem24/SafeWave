package ljmu.cmp.safewave;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pete on 03/02/2018.
 */

public class Beach {
    static String name;
    static LatLng latLng;

    public Beach(){

    }

    public Beach(String name, LatLng latLng){
        this.name = name;
        this.latLng = latLng;
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



}
