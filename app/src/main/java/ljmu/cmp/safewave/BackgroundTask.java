package ljmu.cmp.safewave;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Pete on 27/01/2018.
 */

public class BackgroundTask extends AsyncTask<String, String, String> {
    Context context;
    AlertDialog alertDialog;
    String result = "";
    String line;
    public static String[] inBeach;


    User user = new User();

    BackgroundTask(Context ctx) {
        context = ctx;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Status");


    }

    @SuppressLint("MissingPermission")
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String login_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/login.php";
        String register_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/register.php";
        String emergencyFullSigned_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/emergencyFullSigned.php";
        String emergencyFull_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/emergencyFull.php";
        String emergencyEmpty_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/emergencyEmpty.php";
        String emergencyEmptySigned_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/emergencyEmptySigned.php";
        String getMarkers_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/getMarkers.php";
        String checkIn_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/checkIn.php";
        String checkInVerify_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/checkInVerify.php";
        String checkOut_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/checkOut.php";
        String checkOutVerify_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/checkOutVerify.php";
        String completeEmergency_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/completeEmergency.php";
        String emergencyContact_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/emergencyContact.php";
        String getEmergency_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/getEmergency.php";
        String uploadImage_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/imageUpload.php";
        String updateLocation_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/updateLocation.php";
        String getEmergencyContact_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/getEmergencyContact.php";
        String emergencyContactUpdate_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/emergencyContactUpdate.php";
        String updateImage_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/updateImage.php";
        String getLife_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/getLife.php";
        String sendMissingMessage_url = "http://cmpproj.cms.livjm.ac.uk/cmppmahe/sendMissingMessage.php";

        if (type.equals("login")) {
            try {

                String userName = params[1];
                String passWord = params[2];
                URL url = new URL(login_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData = URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&"
                        + URLEncoder.encode("passWord", "UTF-8") + "=" + URLEncoder.encode(passWord, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                if (!result.equals("Login Failed")) {

                    String[] parts = result.split(",");
                    User.Username = parts[0];
                    User.FirstName = parts[1];
                    User.LastName = parts[2];
                    User.Gender = parts[3];
                    User.DOB = parts[4];
                    User.Phone = parts[5];
                    User.Level = parts[6].charAt(0);
                    User.Height = Double.parseDouble(parts[7]);
                    User.Build = parts[8];
                    User.Allergies = parts[9];
                    User.VerifyStatus = parts[10].charAt(0);
                    User.signedIn = true;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        if (type.equals("register")) {
            try {

                String userName = params[1];
                String passWord = params[2];

                String firstName = params[3];
                String lastName = params[4];
                String gender = params[5];
                String DOB = params[6];
                String phone = params[7];
                String level = "s";
                String height = params[8];
                String build = params[9];
                String allergies = params[10];
                String verifycheck = "n";


                URL url = new URL(register_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&"
                                + URLEncoder.encode("passWord", "UTF-8") + "=" + URLEncoder.encode(passWord, "UTF-8") + "&"
                                + URLEncoder.encode("firstName", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") + "&"
                                + URLEncoder.encode("lastName", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&"
                                + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8") + "&"
                                + URLEncoder.encode("DOB", "UTF-8") + "=" + URLEncoder.encode(DOB, "UTF-8") + "&"
                                + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                                + URLEncoder.encode("level", "UTF-8") + "=" + URLEncoder.encode(level, "UTF-8") + "&"
                                + URLEncoder.encode("height", "UTF-8") + "=" + URLEncoder.encode(height, "UTF-8") + "&"
                                + URLEncoder.encode("build", "UTF-8") + "=" + URLEncoder.encode(build, "UTF-8") + "&"
                                + URLEncoder.encode("allergies", "UTF-8") + "=" + URLEncoder.encode(allergies, "UTF-8") + "&"
                                + URLEncoder.encode("verifycheck", "UTF-8") + "=" + URLEncoder.encode(verifycheck, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (type.equals("emergencyFullSigned")) {

            try {


                String beach = params[1];
                String slocation = params[2];
                String location = slocation.substring(10, slocation.length() - 1);
                String emgtype = params[3];
                String details = params[4];

                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();

                URL url = new URL(emergencyFullSigned_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =

                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(User.Username, "UTF-8") + "&"
                                + URLEncoder.encode("beach", "UTF-8") + "=" + URLEncoder.encode(beach, "UTF-8") + "&"
                                + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&"
                                + URLEncoder.encode("cTime", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&"
                                + URLEncoder.encode("emgtype", "UTF-8") + "=" + URLEncoder.encode(emgtype, "UTF-8") + "&"
                                + URLEncoder.encode("details", "UTF-8") + "=" + URLEncoder.encode(details, "UTF-8") + "&"
                                + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(User.Phone, "UTF-8") + "&"
                                + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(User.getFullName(), "UTF-8") + "&"
                                + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(User.Gender, "UTF-8") + "&"
                                + URLEncoder.encode("DOB", "UTF-8") + "=" + URLEncoder.encode(User.DOB, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                String[] a = result.split(",");
                Emergency.id = a[1];

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (type.equals("emergencyFull")) {
            try {


                String beach = params[1];
                String slocation = params[2];
                String location = slocation.substring(10, slocation.length() - 1);
                String emgtype = params[3];
                String details = params[4];

                URL url = new URL(emergencyFull_url);

                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =

                        URLEncoder.encode("beach", "UTF-8") + "=" + URLEncoder.encode(beach, "UTF-8") + "&"
                                + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&"
                                + URLEncoder.encode("cTime", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&"
                                + URLEncoder.encode("emgtype", "UTF-8") + "=" + URLEncoder.encode(emgtype, "UTF-8") + "&"
                                + URLEncoder.encode("details", "UTF-8") + "=" + URLEncoder.encode(details, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                String[] a = result.split(",");
                Emergency.id = a[1];

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("emergencyEmpty")) {
            try {


                String beach = params[1];
                String slocation = params[2];
                String location = slocation.substring(10, slocation.length() - 1);

                URL url = new URL(emergencyEmpty_url);

                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =

                        URLEncoder.encode("beach", "UTF-8") + "=" + URLEncoder.encode(beach, "UTF-8") + "&"
                                + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&"
                                + URLEncoder.encode("cTime", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                String[] a = result.split(",");
                Emergency.id = a[1];

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("emergencyEmptySigned")) {
            try {


                String beach = params[1];
                String slocation = params[2];
                String location = slocation.substring(10, slocation.length() - 1);

                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();

                URL url = new URL(emergencyEmptySigned_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =

                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(User.Username, "UTF-8") + "&"
                                + URLEncoder.encode("beach", "UTF-8") + "=" + URLEncoder.encode(beach, "UTF-8") + "&"
                                + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&"
                                + URLEncoder.encode("cTime", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&"
                                + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(User.Phone, "UTF-8") + "&"
                                + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(User.getFullName(), "UTF-8") + "&"
                                + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(User.Gender, "UTF-8") + "&"
                                + URLEncoder.encode("DOB", "UTF-8") + "=" + URLEncoder.encode(User.DOB, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                String[] a = result.split(",");
                Emergency.id = a[1];

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (type.equals("getBeachMarkers")) {
            try {

                URL url = new URL(getMarkers_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                inBeach = result.split("<br>");

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("setCheckIn")) {
            try {


                String beach = params[1];
                String slocation = params[2];
                String location = slocation.substring(10, slocation.length() - 1);


                URL url = new URL(checkIn_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =


                        URLEncoder.encode("beach", "UTF-8") + "=" + URLEncoder.encode(beach, "UTF-8") + "&"
                                + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&"
                                 + URLEncoder.encode("nToken", "UTF-8") + "=" + URLEncoder.encode(SplashScreen.nToken, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("setCheckInVerify")) {
            try {


                String beach = params[1];
                String slocation = params[2];
                String location = slocation.substring(10, slocation.length() - 1);

                URL url = new URL(checkInVerify_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =


                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(User.Username, "UTF-8") + "&"
                         +URLEncoder.encode("beach", "UTF-8") + "=" + URLEncoder.encode(beach, "UTF-8") + "&"
                        +URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&"
                                + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(User.getFullName(), "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("setCheckOut")) {
            try {


                String id = ProfileFragment.checkInId;

                URL url = new URL(checkOut_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =


                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("completeEmergency")) {
            try {

                String reason = params[1];
                String notes = params[2];
                URL url = new URL(completeEmergency_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Emergency.id, "UTF-8") + "&"
                                + URLEncoder.encode("reason", "UTF-8") + "=" + URLEncoder.encode(reason, "UTF-8") + "&"
                                + URLEncoder.encode("notes", "UTF-8") + "=" + URLEncoder.encode(notes, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("emergencyContact")) {
            try {

                String userName = params[1];
                String firstName = params[2];
                String lastName = params[3];
                String phone = params[4];
                String email = params[5];
                String relation = params[6];

                URL url = new URL(emergencyContact_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&"
                                + URLEncoder.encode("firstName", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") + "&"
                                + URLEncoder.encode("lastName", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&"
                                + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                                + URLEncoder.encode("relation", "UTF-8") + "=" + URLEncoder.encode(relation, "UTF-8") + "&"
                                + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (type.equals("getEmergency")) {
            try {

                URL url = new URL(getEmergency_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);


                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("uploadImage")) {
            try {

                URL url = new URL(uploadImage_url);

                String name = params[1];
                String image = params[2];


                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                                + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("updateImage")) {
            try {

                URL url = new URL(updateImage_url);

                String name = params[1];
                String image = params[2];


                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                                + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("updateLocation")) {
            try {

                URL url = new URL(updateLocation_url);

                String slocation = ProfileFragment.currentLocation.toString();
                String location = slocation.substring(10, slocation.length() - 1);

                String level = Character.toString(User.Level);

                if(!User.signedIn) {
                    User.Username = "temp";
                    level = "x";
                }

                else {
                    if(level.equals("a") || level.equals("v"))
                        ProfileFragment.checkInId = "temp";

                }



                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(ProfileFragment.checkInId, "UTF-8")+ "&"
                                + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8")+ "&"
                                + URLEncoder.encode("emId", "UTF-8") + "=" + URLEncoder.encode(Emergency.id, "UTF-8")+ "&"
                                + URLEncoder.encode("level", "UTF-8") + "=" + URLEncoder.encode(level, "UTF-8")+ "&"
                                + URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(User.Username, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("getEmergencyContact")) {
            try {

                URL url = new URL(getEmergencyContact_url);


                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(User.Username, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                if (!result.equals("")) {

                    String[] a = result.split(";");

                    new EmergencyContact(User.Username, a[0], a[1], a[2], a[3], a[4]);
                    EmergencyContact.hasEmContact = true;
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("emergencyContactUpdate")) {
            try {

                String userName = params[1];
                String firstName = params[2];
                String lastName = params[3];
                String phone = params[4];
                String email = params[5];
                String relation = params[6];


                URL url = new URL(emergencyContactUpdate_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&"
                                + URLEncoder.encode("firstName", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") + "&"
                                + URLEncoder.encode("lastName", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&"
                                + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                                + URLEncoder.encode("relation", "UTF-8") + "=" + URLEncoder.encode(relation, "UTF-8") + "&"
                                + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");


                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("getLife")) {
            try {


                URL url = new URL(getLife_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("setCheckOutVerify")) {
            try {

                URL url = new URL(checkOutVerify_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(User.Username, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (type.equals("sendMissingMessage")) {
            try {

                URL url = new URL(sendMissingMessage_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String postData =
                        URLEncoder.encode("missing", "UTF-8") + "=" + URLEncoder.encode("Missing Person", "UTF-8") + "&"
                                +URLEncoder.encode("beach", "UTF-8") + "=" + URLEncoder.encode(Beach.name, "UTF-8");

                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





        return null;
    }


    @Override
    protected void onPostExecute(String s) {


    }


    public String sha1Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }


    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
