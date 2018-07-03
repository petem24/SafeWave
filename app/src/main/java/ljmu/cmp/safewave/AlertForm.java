package ljmu.cmp.safewave;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class AlertForm extends AppCompatActivity {

    Spinner type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Submit an Alert");
        setSupportActionBar(toolbar);

        //Alertbox for quick alert
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AlertForm.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_urgent, null);

        Button no = dialogView.findViewById(R.id.btnNo);
        Button yes = dialogView.findViewById(R.id.btnYes);

        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Emergency(Beach.name, ProfileFragment.currentLocation, getApplicationContext());
                Emergency.hasEmergency = true;
                Intent intent = new Intent(getApplicationContext(), EmergencyMap.class);
                startActivity(intent);
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.cancel();
            }
        });


        type = findViewById(R.id.spinAlert);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                changeFields();
            }


            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        Button btn = findViewById(R.id.btnSubmit);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Puts the animal sighting into the animal table and return to main map
                if (type.getSelectedItem().equals("Animal Sighting")) {
                    String d = getDetails();
                    String[] s = d.split(",");
                    String type = s[0];
                    String desc = s[1];
                    BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());
                    backgroundTask.execute("insertAnimal", type, desc);
                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(intent);
                    finish();
                } else {

                    EditText editAge = findViewById(R.id.editMissAge);
                    EditText editHeight = findViewById(R.id.editMissHeight);
                    EditText editName = findViewById(R.id.editMissName);

                    //sends notification to everyone on the beach about the missing person
                    if (type.getSelectedItem().equals("Missing Person")) {
                        BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());
                        backgroundTask.execute("sendMissingMessage", editName.getText().toString(), ", Age: " + editAge.getText().toString(), ", Height: " + editHeight.getText().toString() + "cm");
                    }

                    Intent intent = new Intent(getApplicationContext(), EmergencyMap.class);
                    startActivity(intent);
                    finish();

                    new Emergency(Beach.name, ProfileFragment.currentLocation, type.getSelectedItem().toString(), getDetails(), getApplicationContext());
                    Emergency.hasEmergency = true;

                }
            }
        });


    }


    //Changes the fields based on type of alert chosen
    public void changeFields() {
        final Spinner type = findViewById(R.id.spinAlert);

        TextView txtAge = findViewById(R.id.txtMissAge);
        TextView txtHeight = findViewById(R.id.txtMissHeight);
        TextView txtName = findViewById(R.id.txtMissName);
        EditText editAge = findViewById(R.id.editMissAge);
        EditText editHeight = findViewById(R.id.editMissHeight);
        EditText editName = findViewById(R.id.editMissName);
        TextView txtInjType = findViewById(R.id.txtInjType);
        Spinner spinInjType = findViewById(R.id.spinInjType);
        TextView txtAnimalType = findViewById(R.id.txtAnimalType);
        Spinner spinAnimalType = findViewById(R.id.spinAnimalType);

        if (type.getSelectedItem().toString().equals("Missing Person")) {

            txtAge.setVisibility(View.GONE);
            txtHeight.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);
            editAge.setVisibility(View.GONE);
            editHeight.setVisibility(View.GONE);
            editName.setVisibility(View.GONE);
            txtInjType.setVisibility(View.GONE);
            spinInjType.setVisibility(View.GONE);
            txtAnimalType.setVisibility(View.GONE);
            spinAnimalType.setVisibility(View.GONE);

            txtAge.setVisibility(View.VISIBLE);
            txtHeight.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.VISIBLE);

            editAge.setVisibility(View.VISIBLE);
            editHeight.setVisibility(View.VISIBLE);
            editName.setVisibility(View.VISIBLE);
        }

        if (type.getSelectedItem().toString().equals("Injury")) {

            txtAge.setVisibility(View.GONE);
            txtHeight.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);
            editAge.setVisibility(View.GONE);
            editHeight.setVisibility(View.GONE);
            editName.setVisibility(View.GONE);
            txtInjType.setVisibility(View.GONE);
            spinInjType.setVisibility(View.GONE);
            txtAnimalType.setVisibility(View.GONE);
            spinAnimalType.setVisibility(View.GONE);

            txtInjType.setVisibility(View.VISIBLE);
            spinInjType.setVisibility(View.VISIBLE);
        }

        if (type.getSelectedItem().toString().equals("Animal Sighting")) {

            txtAge.setVisibility(View.GONE);
            txtHeight.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);
            editAge.setVisibility(View.GONE);
            editHeight.setVisibility(View.GONE);
            editName.setVisibility(View.GONE);
            txtInjType.setVisibility(View.GONE);
            spinInjType.setVisibility(View.GONE);
            txtAnimalType.setVisibility(View.GONE);
            spinAnimalType.setVisibility(View.GONE);

            txtAnimalType.setVisibility(View.VISIBLE);
            spinAnimalType.setVisibility(View.VISIBLE);
        }

        if (type.getSelectedItem().toString().equals("Other")) {

            txtAge.setVisibility(View.GONE);
            txtHeight.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);
            editAge.setVisibility(View.GONE);
            editHeight.setVisibility(View.GONE);
            editName.setVisibility(View.GONE);
            txtInjType.setVisibility(View.GONE);
            spinInjType.setVisibility(View.GONE);
            txtAnimalType.setVisibility(View.GONE);
            spinAnimalType.setVisibility(View.GONE);

        }

    }

    //returns the details of all the fields
    public String getDetails() {

        if (type.getSelectedItem().toString().equals("Missing Person")) {
            EditText editAge = findViewById(R.id.editMissAge);
            EditText editHeight = findViewById(R.id.editMissHeight);
            EditText editName = findViewById(R.id.editMissName);
            EditText editDesc = findViewById(R.id.editDesc);
            return editName.getText().toString() + "," + editAge.getText().toString() + "," + editHeight.getText().toString() + "," + editDesc.getText().toString();
        }

        if (type.getSelectedItem().toString().equals("Injury")) {
            Spinner spinInjType = findViewById(R.id.spinInjType);
            EditText editDesc = findViewById(R.id.editDesc);
            return spinInjType.getSelectedItem().toString() + "," + editDesc.getText().toString();
        }

        if (type.getSelectedItem().toString().equals("Animal Sighting")) {
            Spinner spinAnimalType = findViewById(R.id.spinAnimalType);
            EditText editDesc = findViewById(R.id.editDesc);
            return spinAnimalType.getSelectedItem().toString() + "," + editDesc.getText().toString();
        }

        if (type.getSelectedItem().toString().equals("Lost")) {
            EditText editDesc = findViewById(R.id.editDesc);
            return editDesc.getText().toString();
        }

        if (type.getSelectedItem().toString().equals("Other")) {
            EditText editDesc = findViewById(R.id.editDesc);
            return editDesc.getText().toString();
        }

        return null;

    }
}
