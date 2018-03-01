package ljmu.cmp.safewave;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutionException;


public class ProfileFragment extends Fragment {


    public static boolean checkedIn = false;
    public static LatLng currentLocation;
    public static String checkInId;
    public static Bitmap bitmap;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflater = getActivity().getLayoutInflater();
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);



        TextView textViewName = view.findViewById(R.id.profileName);
        textViewName.setText(User.getFullName());

        if(ProfileFragment.checkedIn == true) {
            TextView textViewBeach = view.findViewById(R.id.txtBeach);
            textViewBeach.setText("Current Beach: "+Beach.name);
        }

        TextView textViewUserLevel = view.findViewById(R.id.txtUser);
        if(User.Level == 's') {
            textViewUserLevel.setText("User Level: Standard");
        }

        if(User.Level == 'v'){
            textViewUserLevel.setText("User Level: Verified");
        }

        if(User.Level == 'a'){
            textViewUserLevel.setText("User Level: Admin");
        }

        ImageView profileImage = view.findViewById(R.id.imgProfile);
        if(bitmap != null) {
            profileImage.setImageBitmap(bitmap);
        }


        TextView txtEmContact = view.findViewById(R.id.txtContact);
        TextView txtSettings = view.findViewById(R.id.txtSettings);
        TextView txtPersonal = view.findViewById(R.id.txtPersonal);


        txtEmContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EmergencyContactForm.class);
                startActivity(i);
            }
        });

        txtSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SettingsActivity.class);
                startActivity(i);
            }
        });

        txtPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PersonalDetailsActivity.class);
                startActivity(i);
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        ImageView imageView = getView().findViewById(R.id.imgProfile);
        imageView.setImageBitmap(ProfileFragment.bitmap);
    }
}
