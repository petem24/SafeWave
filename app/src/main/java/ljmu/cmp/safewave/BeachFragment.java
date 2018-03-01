package ljmu.cmp.safewave;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class BeachFragment extends Fragment {

    RecyclerView rv;
    BeachAdapter rvAdapter;
    List<BeachFragment> cardList;
    SQLite sqLite = new SQLite(getContext());

    String titles;
    String desc;
    String img;
    public static String cond;
    public static String temp;
    public static String wImg;


    public static String FormbyWeather = "http://api.openweathermap.org/data/2.5/weather?lat=53.557340&lon=-3.101870&appid=44c459a1d6f977072712ae7fe19451ad&units=metric";
    public static String CrosbyWeather = "http://api.openweathermap.org/data/2.5/weather?lat=53.481000&lon=-3.045000&appid=44c459a1d6f977072712ae7fe19451ad&units=metric";
    public static String AinsdaleWeather = "http://api.openweathermap.org/data/2.5/weather?lat=53.608485&lon=-3.061847&appid=44c459a1d6f977072712ae7fe19451ad&units=metric";
    public static String Icon;

    public BeachFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public BeachFragment(String titles, String desc, String img, String temp, String cond) {
        this.titles = titles;
        this.desc = desc;
        this.img = img;
        this.temp = temp;
        this.desc = desc;
    }

    public String getTitle() {
        return titles;
    }

    public String getDesc() {
        return desc;
    }

    public String getImg() {
        return img;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        final View view = lf.inflate(R.layout.fragment_beach, container, false);

        cardList = new ArrayList<>();
        rv = view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        rvAdapter = new BeachAdapter(getContext(), cardList);
        rv.setAdapter(rvAdapter);

        try {
            sqLite.writeTitles("Beach");
            sqLite.writeDesc("Beach");
            sqLite.writeImg("Beach");
            sqLite.writeFullDesc("Beach");
        } catch (SQLException e) {
            e.printStackTrace();
        }



        for (int i = 0; i < SQLite.titleArray.size(); i++) {
            titles = SQLite.titleArray.get(i);

            if (titles.contains("Formby")) {
                try {
                    getWeather(FormbyWeather, 0);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if (titles.contains("Crosby")) {
                try {
                    getWeather(CrosbyWeather, 1);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if (titles.contains("Ainsdale")) {
                try {
                    getWeather(AinsdaleWeather, 2);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }


            desc = SQLite.descArray.get(i);
            img = SQLite.imgArray.get(i);
            cardList.add(i, new BeachFragment(titles, desc, img, cond, temp));


        }


        return view;
    }

    public void getWeather(String url, final int id) throws ExecutionException{

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject mainObject = response.getJSONObject("main");
                            JSONArray array = response.getJSONArray("weather");
                            JSONObject object = array.getJSONObject(0);
                            temp = String.valueOf(mainObject.getDouble("temp"));
                            cond = object.getString("description").toUpperCase();
                            wImg = object.getString("icon");
                            Icon = "http://openweathermap.org/img/w/"+wImg+".png";
                            RecyclerView.ViewHolder holder = rv.findViewHolderForAdapterPosition(id);

                            TextView txtCond = holder.itemView.findViewById(R.id.currentWeatherCon);
                            txtCond.setText(cond);

                            TextView txtTemp = holder.itemView.findViewById(R.id.currentWeatherTemp);
                            txtTemp.setText(temp+"°C");

                            ImageView imgCurrent = holder.itemView.findViewById(R.id.currentWeatherImg1);
                            Picasso.with(getContext()).load(Icon).into(imgCurrent);



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


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsObjRequest);


    }


}



