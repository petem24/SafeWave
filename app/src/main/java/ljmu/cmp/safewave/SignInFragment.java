package ljmu.cmp.safewave;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;


public class SignInFragment extends Fragment {



    public SignInFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_signin, container, false);


        //On Sign in button click, collects the input data and checks it with data from the database
        Button btnSign = view.findViewById(R.id.signIn);
        btnSign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                BackgroundTask backgroundTask = new BackgroundTask(getActivity());

                TextView user = view.findViewById(R.id.InputUsername);
                String struser = user.getText().toString();

                TextView pass = view.findViewById(R.id.InputPassword);
                String strpass = pass.getText().toString();

                //CHECKS DATA WITH DATA FROM DATABASE
                if (!strpass.isEmpty() && !struser.isEmpty()) {

                    try {
                        backgroundTask.execute("login", struser, strpass).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if(backgroundTask.result.equals("Login Failed")){
                        Toast.makeText(getContext(), "Username or Password Incorrect",
                                Toast.LENGTH_LONG).show();
                    }

                    else {

                        ProgressBar progressBar = view.findViewById(R.id.signInProgress);
                        BackgroundTaskDialog backgroundTaskDialog = new BackgroundTaskDialog(getContext(), progressBar);

                        //gets the image and decodes it
                        try {
                            backgroundTaskDialog.execute("getImage").get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        String imgString = backgroundTaskDialog.result;

                        if(imgString.length() > 100) {
                            backgroundTaskDialog = new BackgroundTaskDialog(getContext(), progressBar);
                            try {
                                backgroundTaskDialog.execute("decodeImage", imgString).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }

                        backgroundTask = new BackgroundTask(getContext());
                        backgroundTask.execute("getEmergencyContact");


                        Toast.makeText(getContext(), "Log In Successful",
                                Toast.LENGTH_LONG).show();

                        Intent intent = getActivity().getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().overridePendingTransition(0, 0);
                        getActivity().finish();

                        getActivity().overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter details",
                            Toast.LENGTH_LONG).show();
                }


            }
        });

        Button btnReg = view.findViewById(R.id.register);
        btnReg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent i = new Intent(getActivity(), Register.class);
                startActivity(i);
                (getActivity()).overridePendingTransition(0, 0);

            }
        });

        return view;
    }


}
