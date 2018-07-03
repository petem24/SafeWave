package ljmu.cmp.safewave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Pete on 23/02/2018.
 */

public class BackgroundTaskDialog extends AsyncTask<String,Integer,String> {

    ProgressBar progressBar;
    Context context;
    String result = "";
    String line;




    BackgroundTaskDialog(Context ctx, ProgressBar prog) {
        this.context = ctx;
        this.progressBar = prog;

    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.progressBar != null) {
            progressBar.setProgress(values[0]);
        }
    }



    @SuppressLint("MissingPermission")
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String getImage_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/getImage.php";

        if(type.equals("getImage")){
            try {

                URL url = new URL(getImage_url);

                HttpURLConnection HttpURLConnection = (HttpURLConnection) url.openConnection();
                HttpURLConnection.setRequestMethod("POST");
                HttpURLConnection.setDoOutput(true);
                HttpURLConnection.setDoInput(true);

                OutputStream outputStream = HttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(User.Username, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = HttpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                HttpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(type.equals("decodeImage")) {
            String imgString = params[1];

            byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            int targetWidth = 200;
            int targetHeight = 200;
            Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(targetBitmap);
            Path path = new Path();
            path.addCircle(((float) targetWidth - 1) / 2,
                    ((float) targetHeight - 1) / 2,
                    (Math.min(((float) targetWidth),
                            ((float) targetHeight)) / 2),
                    Path.Direction.CCW);

            canvas.clipPath(path);
            Bitmap sourceBitmap = bitmap;
            canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, targetWidth, targetHeight), null);
            ProfileFragment.bitmap = targetBitmap;
        }


        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);
    }
}
