package ljmu.cmp.safewave;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PersonalDetailsActivity extends AppCompatActivity {

    private final int IMG_REQUEST = 1;
    private static Bitmap bitmap;
    private ImageView upload;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        upload = findViewById(R.id.imgProfileUpload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        EditText firstName = findViewById(R.id.InputFirstNameChange);
        firstName.setText(User.FirstName);


        EditText lastName = findViewById(R.id.InputLastNameChange);
        lastName.setText(User.LastName);


        Spinner gender = findViewById(R.id.InputGenderChange);

            if(User.Gender.equals("M"))
                gender.setSelection(0);

            if(User.Gender.equals("F"))
                gender.setSelection(1);

            if(User.Gender.equals("O"))
                gender.setSelection(2);



        EditText dob = findViewById(R.id.InputDOBChange);
        dob.setText(User.DOB);


        EditText phone = findViewById(R.id.InputPhoneChange);
        phone.setText(User.Phone);


        EditText height = findViewById(R.id.inputHeightChange);
        String a = String.valueOf(User.Height);
        height.setText(a);


            if(User.Build.equals("Large"))
                gender.setSelection(0);

            if(User.Build.equals("Average"))
                gender.setSelection(1);

            if(User.Build.equals("Small"))
                gender.setSelection(2);


        TextView allergies = findViewById(R.id.inputAllergiesChange);
        allergies.setText(User.Allergies);

        name = User.Username;

        Button submit = findViewById(R.id.btnChange);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                Toast.makeText(getApplicationContext(), "Updated Successfully",
                        Toast.LENGTH_SHORT).show();
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
                ProgressBar progressBar = findViewById(R.id.progressBarUpload);

                BackgroundTaskDialog backgroundTaskDialog = new BackgroundTaskDialog(getApplicationContext(), progressBar);
                try {
                    backgroundTaskDialog.execute("decodeImage", imageToString(bitmap)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                upload.setImageBitmap(ProfileFragment.bitmap);
                upload.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (bitmap != null) {
            if (!bitmap.toString().equals(null)) {
                String image = imageToString(ProfileFragment.bitmap);
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute("updateImage", name, image);
            }

        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
}
