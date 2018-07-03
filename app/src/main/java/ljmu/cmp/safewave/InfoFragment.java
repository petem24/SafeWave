package ljmu.cmp.safewave;


import android.annotation.SuppressLint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class InfoFragment extends Fragment {

    RecyclerView rv;
    InfoAdapter rvAdapter;
    List<InfoFragment> cardList;
    SQLite sqLite = new SQLite(getContext());

    String titles;
    String desc;
    String img;
    String fullDesc;

    public InfoFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public InfoFragment(String titles, String desc, String img, String fullDesc){
        this.titles = titles;
        this.desc = desc;
        this.img = img;
        this.fullDesc = fullDesc;
    }

    public String getTitle(){
        return titles;
    }

    public String getDesc(){
        return desc;
    }

    public String getFullDesc(){return fullDesc;}

    public String getImg(){ return img;}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("WrongViewCast")
    @Override
    //CREATES A LIST OF INFORMATION CARDS
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater lf = getActivity().getLayoutInflater();
        final View view = lf.inflate(R.layout.fragment_info, container, false);

        cardList = new ArrayList<>();
        rv = view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        rvAdapter = new InfoAdapter(getContext(), cardList);
        rv.setAdapter(rvAdapter);

        try {
            sqLite.writeTitles("Info");
            sqLite.writeDesc("Info");
            sqLite.writeImg("Info");
            sqLite.writeFullDesc("Info");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < SQLite.titleArray.size(); i++){

            String title =  SQLite.titleArray.get(i);
            String desc = SQLite.descArray.get(i);
            String fullDesc = SQLite.fullDescArray.get(i);
            String img = SQLite.imgArray.get(i);

            cardList.add(i, new InfoFragment(title, desc, img, fullDesc));
        }



        return view;
    }





}
