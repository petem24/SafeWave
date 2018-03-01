package ljmu.cmp.safewave;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class BeachFullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String beachName = getIntent().getStringExtra("BeachName");
        setTitle(beachName);
        setContentView(R.layout.activity_beach_full);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView txtDesc = findViewById(R.id.txtDesc);
        txtDesc.setText(getIntent().getStringExtra("BeachDesc"));

        Drawable d = null;
        Drawable c = null;

        String[] a = beachName.split(" ");
        String shortName = a[0];

        try {
            d = Drawable.createFromStream(getAssets().open("Images/"+shortName.toLowerCase()+".jpg"), null);
            c = Drawable.createFromStream(getAssets().open("Images/"+shortName.toLowerCase()+"-body.jpg"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView background = findViewById(R.id.main_backdrop);
        background.setImageDrawable(d);

        ImageView body = findViewById(R.id.imgBeach);
        body.setImageDrawable(c);

    }


}
