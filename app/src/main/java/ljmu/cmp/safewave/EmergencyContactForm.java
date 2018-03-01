package ljmu.cmp.safewave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyContactForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_form);

        final EditText firstName = findViewById(R.id.inputEmFirstName);
        final EditText lastName = findViewById(R.id.inputEmLastName);
        final EditText phone = findViewById(R.id.inputEmContactNumber);
        final EditText email = findViewById(R.id.inputEmContactEmail);
        final Spinner relation = findViewById(R.id.inputRelation);

        if (EmergencyContact.hasEmContact == true) {

            firstName.setText(EmergencyContact.FirstName);

            lastName.setText(EmergencyContact.LastName);

            phone.setText(EmergencyContact.PhoneNum);

            email.setText(EmergencyContact.Email);

            if (EmergencyContact.Relation.equals("Parent/Carer"))
                relation.setSelection(0);
            if (EmergencyContact.Relation.equals("Next of Kin"))
                relation.setSelection(1);
            if (EmergencyContact.Relation.equals("Other"))
                relation.setSelection(2);

        }




        Button submit = findViewById(R.id.btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = User.Username;
                String fName = firstName.getText().toString();
                String lName = lastName.getText().toString();
                String fone = phone.getText().toString();
                String eM = email.getText().toString();
                String rel = relation.getSelectedItem().toString();


                BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());

                if (EmergencyContact.hasEmContact == false)
                    backgroundTask.execute("emergencyContact", username, fName, lName, fone, eM, rel);

                else
                    backgroundTask.execute("emergencyContactUpdate",username, fName, lName, fone, eM, rel);


                new EmergencyContact(username, fName, lName, fone, eM, rel);
                EmergencyContact.hasEmContact = true;

                finish();
            }
        });
    }
}
