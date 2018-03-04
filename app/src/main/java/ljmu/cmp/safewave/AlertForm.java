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


            new Emergency(Beach.name, ProfileFragment.currentLocation, type.getSelectedItem().toString(), getDetails(), getApplicationContext());
            Emergency.hasEmergency = true;

            if(type.getSelectedItem().equals("Missing Person")){
                BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());
                backgroundTask.execute("sendMissingMessage");
            }

                Intent intent = new Intent(getApplicationContext(), EmergencyMap.class);
                startActivity(intent);



            }
        });




    }


    public void changeFields() {
        final Spinner type = findViewById(R.id.spinAlert);

        if (!type.getSelectedItem().toString().equals("Missing Person")) {

            TextView txtAge = findViewById(R.id.txtMissAge);
            TextView txtHeight = findViewById(R.id.txtMissHeight);
            TextView txtName = findViewById(R.id.txtMissName);

            txtAge.setVisibility(View.GONE);
            txtHeight.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);

            EditText editAge = findViewById(R.id.editMissAge);
            EditText editHeight = findViewById(R.id.editMissHeight);
            EditText editName = findViewById(R.id.editMissName);

            editAge.setVisibility(View.GONE);
            editHeight.setVisibility(View.GONE);
            editName.setVisibility(View.GONE);
        }

        if (type.getSelectedItem().toString().equals("Missing Person")) {

            TextView txtAge = findViewById(R.id.txtMissAge);
            TextView txtHeight = findViewById(R.id.txtMissHeight);
            TextView txtName = findViewById(R.id.txtMissName);

            txtAge.setVisibility(View.VISIBLE);
            txtHeight.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.VISIBLE);

            EditText editAge = findViewById(R.id.editMissAge);
            EditText editHeight = findViewById(R.id.editMissHeight);
            EditText editName = findViewById(R.id.editMissName);

            editAge.setVisibility(View.VISIBLE);
            editHeight.setVisibility(View.VISIBLE);
            editName.setVisibility(View.VISIBLE);
        }

        if (!type.getSelectedItem().toString().equals("Injury")) {
            TextView txtInjType = findViewById(R.id.txtInjType);
            Spinner spinInjType = findViewById(R.id.spinInjType);

            txtInjType.setVisibility(View.GONE);
            spinInjType.setVisibility(View.GONE);
        }

        if (type.getSelectedItem().toString().equals("Injury")) {
            TextView txtInjType = findViewById(R.id.txtInjType);
            Spinner spinInjType = findViewById(R.id.spinInjType);

            txtInjType.setVisibility(View.VISIBLE);
            spinInjType.setVisibility(View.VISIBLE);
        }

    }

    public String getDetails(){

        if (type.getSelectedItem().toString().equals("Missing Person")) {

            EditText editAge = findViewById(R.id.editMissAge);
            EditText editHeight = findViewById(R.id.editMissHeight);
            EditText editName = findViewById(R.id.editMissName);

            EditText editDesc = findViewById(R.id.editDesc);

            return editName.getText().toString()+","+editAge.getText().toString()+","+editHeight.getText().toString()+","+editDesc.getText().toString();
        }

        if (type.getSelectedItem().toString().equals("Injury")) {


            Spinner spinInjType = findViewById(R.id.spinInjType);

            EditText editDesc = findViewById(R.id.editDesc);

            return spinInjType.getSelectedItem().toString()+","+editDesc.getText().toString();
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
