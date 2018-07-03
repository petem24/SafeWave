package ljmu.cmp.safewave;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.Time;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Register extends AppCompatActivity{

    Context context = this;
    private final int IMG_REQUEST = 1;
    private static Bitmap bitmap;
    private ImageView upload;
    private String name;
    EditText dob;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        upload = findViewById(R.id.imgProfileUpload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        dob = findViewById(R.id.InputDOB);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Register.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.DAY_OF_MONTH), myCalendar.get(Calendar.MONTH)).show();
            }
        });

        Button btn = findViewById(R.id.btnRegister);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //Gets all details from the text boxes and puts them in variables
                BackgroundTask backgroundTask = new BackgroundTask(context);

                EditText user = findViewById(R.id.InputUserR);
                String struser = user.getText().toString();

                EditText pass = findViewById(R.id.InputPasswordR);
                String strpass = pass.getText().toString();

                EditText firstName = findViewById(R.id.InputFirstName);
                String strfirstname = firstName.getText().toString();

                EditText lastName = findViewById(R.id.InputLastName);
                String strlastname = lastName.getText().toString();

                Spinner gender = findViewById(R.id.InputGender);
                String strgender = gender.getSelectedItem().toString();
                
                String strdob = dob.getText().toString();

                EditText phone = findViewById(R.id.InputPhoneChange);
                String strphone = phone.getText().toString();

                EditText height = findViewById(R.id.inputHeight);
                String strheight = height.getText().toString();

                Spinner build = findViewById(R.id.inputBuild);
                String strbuild = build.getSelectedItem().toString();

                EditText allergies = findViewById(R.id.inputAllergies);
                String strallergies = allergies.getText().toString();

                name = struser;

                //Adds the user to the database
                backgroundTask.execute("register", struser, strpass, strfirstname, strlastname, strgender, strdob, strphone, strheight, strbuild, strallergies);

                uploadImage();

                Toast.makeText(context, "Registration Successful",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null){
            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                upload.setImageBitmap(bitmap);
                upload.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Uploads the base64 string to the database
    private void uploadImage() {
        if (bitmap != null) {
            if (!bitmap.toString().equals(null)) {
                String image = imageToString(bitmap);
                BackgroundTask backgroundTask = new BackgroundTask(context);
                backgroundTask.execute("uploadImage", name, image);
            }

        }
    }

    //Returns bitmap to base64 string
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    //Updates the label when data is picked
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        dob.setText(sdf.format(myCalendar.getTime()));
    }


}
