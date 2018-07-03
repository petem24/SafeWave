package ljmu.cmp.safewave;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BeachFullActivity extends AppCompatActivity {

    public static ArrayList<Integer> tempArray = new ArrayList();
    public static ArrayList<String> descArray = new ArrayList();
    public static ArrayList<String> iconArray = new ArrayList();
    public static ArrayList<Integer> windSpeedArray = new ArrayList();
    public static ArrayList<Integer> windDegArray = new ArrayList();
    public static ArrayList<Integer> timeArray = new ArrayList();

    public static ArrayList<TextView> timeRArray = new ArrayList();
    public static ArrayList<TextView> tempRArray = new ArrayList();
    public static ArrayList<ImageView> iconRArray = new ArrayList();
    public static ArrayList<TextView> windSpeedRArray = new ArrayList();
    public static ArrayList<ImageView> windDegRArray = new ArrayList();

    public static ArrayList<Integer> dtArray = new ArrayList();
    public static ArrayList<Integer> heightArray = new ArrayList();
    public static ArrayList<String> typeArray = new ArrayList();

    public static ArrayList<TextView> dtRArray = new ArrayList();
    public static ArrayList<TextView> heightRArray = new ArrayList();
    public static ArrayList<TextView> typeRArray = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String beachName = getIntent().getStringExtra("BeachName");
        setTitle(beachName);
        setContentView(R.layout.activity_beach_full);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView txtDesc = findViewById(R.id.txtDesc);
        txtDesc.setText(getIntent().getStringExtra("BeachDesc"));

        Drawable d = null;
        Drawable c = null;

        String[] a = beachName.split(" ");
        String shortName = a[0];

        //ADD IMAGES TO PAGE
        try {
            d = Drawable.createFromStream(getAssets().open("Images/"+shortName.toLowerCase()+".jpg"), null);
            c = Drawable.createFromStream(getAssets().open("Images/"+shortName.toLowerCase()+"-body.jpg"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //SETS IMAGES
        ImageView background = findViewById(R.id.main_backdrop);
        background.setImageDrawable(d);

        ImageView body = findViewById(R.id.imgBeach);
        body.setImageDrawable(c);

        //DEPENDING ON THE BEACH, GET THE WEATHER AND TIDAL
        switch (beachName){
            case"Formby Beach":
                get53Weather("53.407303", "-2.995082");
                getTidal("53.407303", "-2.995082");
                break;
            case"Crosby Beach":
                get53Weather("53.481000", "-3.045000");
                getTidal("53.481000", "-3.045000");
                break;
            case"Ainsdale Beach":
                get53Weather("53.608485", "-3.061847");
                getTidal("53.608485", "-3.061847");
                break;
        }



    }

    //GET THE 5 DAY 3 HOUR FORECAST
    private void get53Weather(String lat, String lng){

        String url = "http://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lng+"&appid=44c459a1d6f977072712ae7fe19451ad&units=metric&";

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray listArray = response.getJSONArray("list");
                            clearArrays();
                            for(int i = 0; i < listArray.length(); i++) {
                                JSONObject listObject = listArray.getJSONObject(i);

                                //GETS TIME
                                timeArray.add(listObject.getInt("dt"));

                                //GETS TEMPS
                                JSONObject mainObject = listObject.getJSONObject("main");
                                tempArray.add(Math.round((int)mainObject.getLong("temp")));


                                //GETS WEATHER DESC & ICON
                                JSONArray weatherArray = listObject.getJSONArray("weather");
                                JSONObject weatherObject = weatherArray.getJSONObject(0);
                                descArray.add(weatherObject.getString("description"));
                                iconArray.add(weatherObject.getString("icon"));

                                //GETS WIND
                                JSONObject windObject = listObject.getJSONObject("wind");
                                windSpeedArray.add(Math.round((int)windObject.getLong("speed")));
                                windDegArray.add(windObject.getInt("deg"));

                            }

                            Date inTime = new Date((long)timeArray.get(0)*1000);

                            Date currentTime = new Date();
                            if(inTime.after(currentTime)){
                                addViewsToArrays();
                                //UPDATES THE UI WITH THE WEATHER
                                setCurrentDay();
                                requestQueue.stop();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
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

    private void getTidal(String lat, String lng){

        //JSON File
        String url = "https://www.worldtides.info/api?extremes&lat="+lat+"&lon="+lng+"&key=c3c9948d-a8a4-4674-9b95-4936da92e4d1";

        //REQUEST QUEUE FOR MAKING JSON REQUESTS
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //CREATE NEW REQUEST
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    //ON RESPONSE
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //GETS ARRAY OF EXTREMES
                            JSONArray listArray = response.getJSONArray("extremes");
                            clearArrays(); //IGNORE

                            //FOR LOOP THE SIZE OF THE ARRAY
                            for(int i = 0; i < listArray.length(); i++) {

                                //CREATE NEW OBJECT OF i OF THE ARRAY
                                JSONObject listObject = listArray.getJSONObject(i);

                                //ADDS THE OBJECT VALUES TO DIFFERENT ARRAYS
                                dtArray.add(listObject.getInt("dt"));
                                heightArray.add(listObject.getInt("height"));
                                typeArray.add(listObject.getString("type"));

                            }

                            addViewsToArrays();
                            //UPDATED THE UI WITH THE CURRENT TIDE
                            setTide();
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

    private void setCurrentDay() throws IOException {

        for(int i = 0; i < 5; i++){
            Date inTime = new Date((long)timeArray.get(i)*1000);
            TextView txtTime = timeRArray.get(i);
            TextView txtTemp = tempRArray.get(i);
            TextView txtWind = windSpeedRArray.get(i);
            ImageView imgIcon = iconRArray.get(i);
            ImageView windIcon = windDegRArray.get(i);

            String iconURL = "http://openweathermap.org/img/w/"+iconArray.get(i)+".png";

            txtTime.setText(String.valueOf(inTime.getHours()+":00"));
            txtTemp.setText(tempArray.get(i)+"°C");
            txtWind.setText(windSpeedArray.get(i)+"m/s");
            Picasso.with(getApplicationContext()).load(iconURL).into(imgIcon);

            if(windDegArray.get(i) >= 0 && windDegArray.get(i) <= 44) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-up.png"), null));
            }

            if(windDegArray.get(i) >= 45 && windDegArray.get(i) <= 89) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-up-right.png"), null));
            }

            if(windDegArray.get(i) >= 90 && windDegArray.get(i) <= 134) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-right.png"), null));
            }

            if(windDegArray.get(i) >= 135 && windDegArray.get(i) <= 179) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-down-right.png"), null));
            }

            if(windDegArray.get(i) >= 180 && windDegArray.get(i) <= 224) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-down.png"), null));
            }

            if(windDegArray.get(i) >= 225 && windDegArray.get(i) <= 269) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-down-left.png"), null));
            }

            if(windDegArray.get(i) >= 270 && windDegArray.get(i) <= 314) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-left.png"), null));
            }

            if(windDegArray.get(i) >= 315 && windDegArray.get(i) <= 360) {
                windIcon.setImageDrawable(Drawable.createFromStream(getAssets().open("Images/arrow-up-left.png"), null));
            }

        }

    }

    private void setTide(){

        for(int i = 0; i < 2; i++) {
            TextView type =  typeRArray.get(i);
            TextView height = heightRArray.get(i);
            TextView time = dtRArray.get(i);

            type.setText(typeArray.get(i)+" Tide - ");
            height.setText("Height: "+String.valueOf(heightArray.get(i))+"m");
            time.setText("Time: "+String.valueOf(new Date((long)dtArray.get(i)*1000).getHours())+":"+new Date((long)dtArray.get(i)*1000).getMinutes());
        }
    }

    private void addViewsToArrays(){

        timeRArray.add((TextView)findViewById(R.id.txtTime1));
        timeRArray.add((TextView)findViewById(R.id.txtTime2));
        timeRArray.add((TextView)findViewById(R.id.txtTime3));
        timeRArray.add((TextView)findViewById(R.id.txtTime4));
        timeRArray.add((TextView)findViewById(R.id.txtTime5));

        tempRArray.add((TextView)findViewById(R.id.txtTemp1));
        tempRArray.add((TextView)findViewById(R.id.txtTemp2));
        tempRArray.add((TextView)findViewById(R.id.txtTemp3));
        tempRArray.add((TextView)findViewById(R.id.txtTemp4));
        tempRArray.add((TextView)findViewById(R.id.txtTemp5));

        windSpeedRArray.add((TextView)findViewById(R.id.txtWind1));
        windSpeedRArray.add((TextView)findViewById(R.id.txtWind2));
        windSpeedRArray.add((TextView)findViewById(R.id.txtWind3));
        windSpeedRArray.add((TextView)findViewById(R.id.txtWind4));
        windSpeedRArray.add((TextView)findViewById(R.id.txtWind5));

        iconRArray.add((ImageView)findViewById(R.id.imgIcon1));
        iconRArray.add((ImageView)findViewById(R.id.imgIcon2));
        iconRArray.add((ImageView)findViewById(R.id.imgIcon3));
        iconRArray.add((ImageView)findViewById(R.id.imgIcon4));
        iconRArray.add((ImageView)findViewById(R.id.imgIcon5));

        windDegRArray.add((ImageView)findViewById(R.id.imgWind1));
        windDegRArray.add((ImageView)findViewById(R.id.imgWind2));
        windDegRArray.add((ImageView)findViewById(R.id.imgWind3));
        windDegRArray.add((ImageView)findViewById(R.id.imgWind4));
        windDegRArray.add((ImageView)findViewById(R.id.imgWind5));

        typeRArray.add((TextView)findViewById(R.id.txtTitle1));
        typeRArray.add((TextView)findViewById(R.id.txtTitle2));

        heightRArray.add((TextView)findViewById(R.id.txtHeight1));
        heightRArray.add((TextView)findViewById(R.id.txtHeight2));

        dtRArray.add((TextView)findViewById(R.id.txtDT1));
        dtRArray.add((TextView)findViewById(R.id.txtDT2));

    }

    public static void clearArrays(){

        timeArray.clear();
        timeRArray.clear();

        tempArray.clear();
        tempRArray.clear();

        windSpeedArray.clear();
        windSpeedRArray.clear();

        iconArray.clear();
        iconRArray.clear();

        windDegArray.clear();
        windDegRArray.clear();

        typeArray.clear();
        typeRArray.clear();

        heightArray.clear();
        heightRArray.clear();

        dtRArray.clear();
        dtArray.clear();

    }

    }

