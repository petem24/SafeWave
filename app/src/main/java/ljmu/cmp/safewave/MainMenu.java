package ljmu.cmp.safewave;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import java.io.IOException;


public class MainMenu extends AppCompatActivity {


    SQLite sqlite = new SQLite(this);


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            setTitle("Map");
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            //CHANGING FRAGMENT ON CLICK OF NAV BAR
            switch (item.getItemId()) {
                case R.id.navigation_emergency:

                    transaction.replace(R.id.frameLayout, new MapFragment()).commit();
                    setTitle("Map");
                    return true;

                case R.id.navigation_beaches:


                    transaction.replace(R.id.frameLayout, new BeachFragment()).commit();
                    setTitle("Beaches");
                    return true;

                case R.id.navigation_info:


                    transaction.replace(R.id.frameLayout, new InfoFragment()).commit();
                    setTitle("Information");
                    return true;
                case R.id.navigation_profile:
                    //IF USER IS SIGNED IN SHOW PROFILE PAGE
                    if (!User.signedIn) {
                        transaction.replace(R.id.frameLayout, new SignInFragment()).commit();
                        setTitle("Sign In");
                        return true;
                    } else {
                        transaction.replace(R.id.frameLayout, new ProfileFragment()).commit();
                        setTitle("Profile");
                        return true;
                    }

            }
            return false;


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);



        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().add(R.id.frameLayout, new MapFragment()).commit();



        try {
            sqlite.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onBackPressed() { }

}
