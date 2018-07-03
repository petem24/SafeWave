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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        TextView textViewName = view.findViewById(R.id.profileName);
        textViewName.setText(User.getFullName());

        //TEXTVIEWS FILLED WITH USER DATA
        if (ProfileFragment.checkedIn == true) {
            TextView textViewBeach = view.findViewById(R.id.txtBeach);
            textViewBeach.setText("Current Beach: " + Beach.name);
        }

        TextView textViewUserLevel = view.findViewById(R.id.txtUser);
        if (User.Level == 's') {
            textViewUserLevel.setText("User Level: Standard");
        }

        if (User.Level == 'v') {
            textViewUserLevel.setText("User Level: Verified");
        }

        if (User.Level == 'a') {
            textViewUserLevel.setText("User Level: Admin");
        }

        ImageView profileImage = view.findViewById(R.id.imgProfile);
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap);
        }

        TextView txtVerify = view.findViewById(R.id.txtUser);
        TextView txtEmContact = view.findViewById(R.id.txtContact);
        TextView txtLogout = view.findViewById(R.id.txtLogout);
        TextView txtPersonal = view.findViewById(R.id.txtPersonal);

        //IF USER CLICKS THE VERIFY BUTTON, SEND REQUEST TO DATABASE
        txtVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackgroundTask backgroundTask = new BackgroundTask(getContext());
                try {
                    backgroundTask.execute("setVerify").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if (backgroundTask.result.equals("Requested Before")) {
                    Toast.makeText(getContext(), "Already Requested",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Request Successful",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtEmContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EmergencyContactForm.class);
                startActivity(i);
            }
        });

        //ON LOGOUT CLICK, SETS ALL VARIABLES TO NULL
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beach.name = null;
                Beach.id = null;
                Beach.latLng = null;

                User.signedIn = false;
                User.Username = null;
                User.FirstName = null;
                User.LastName = null;
                User.Gender = null;
                User.DOB = null;
                User.Phone = null;
                User.VerifyStatus = '\u0000';
                User.Level = '\u0000';
                User.Height = 0;
                User.Build = null;
                User.Allergies = null;
                User.AddInfo = null;
                ProfileFragment.bitmap = null;

                //CHECKS THE USER OUT
                if (checkedIn) {
                    MapFragment.logoutListner.setBoo(true);
                    ProfileFragment.checkedIn = false;
                    ProfileFragment.checkInId = null;
                }

                Intent i = new Intent(getContext(), MainMenu.class);
                startActivity(i);

                //UNSUB TO TOPICS
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Lifeguards");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Crosby");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Ainsdale");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Test");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Formby");

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
