package ljmu.cmp.safewave;

/**
 * Created by Pete on 26/01/2018.
 */


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLite extends SQLiteOpenHelper{


    private static final String DB_NAME = "Information";
    public static String DB_FILEPATH = "/data/data/ljmu.cmp.safewave/databases";
    private SQLiteDatabase myDataBase;
    Context myContext;

    public static List<String> titleArray = new ArrayList<>();
    public static List<String> descArray = new ArrayList<>();
    public static List<String> imgArray = new ArrayList<>();
    public static List<String> fullDescArray = new ArrayList<>();

    public SQLite(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }


    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            myContext.deleteDatabase(DB_NAME);
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }

        }

    }


    //Check if the database already exist to avoid re-copying the file each time you open the application.

    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_FILEPATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }


    //This copies that database from the assets into the application database
    private void copyDataBase() throws IOException {

        //Open your asset db as an input stream
        InputStream myInput = myContext.getAssets().open("Databases/"+DB_NAME);


        String outFileName = DB_FILEPATH + DB_NAME;


        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }


        myOutput.flush();
        myOutput.close();
        myInput.close();

    }


    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_FILEPATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    // needed because class is extending SQLiteOpenHelper
    @Override
    public void onCreate(SQLiteDatabase db) {



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void writeTitles(String page) throws SQLException{

        openDataBase();
        titleArray.clear();
        Cursor res = null;

        if(page.equals("Info"))
            res = myDataBase.rawQuery("SELECT title FROM Information", null);
        if(page.equals("Beach"))
            res = myDataBase.rawQuery("SELECT title FROM Beach", null);

            for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            titleArray.add(res.getString(0));

        }

        res.close();


    }

    public void writeDesc(String page) throws SQLException{

        openDataBase();

        descArray.clear();
        Cursor res = null;

        if(page.equals("Info"))
            res = myDataBase.rawQuery("SELECT BriefDesc FROM Information", null);
        if(page.equals("Beach"))
            res = myDataBase.rawQuery("SELECT BriefDesc FROM Beach", null);

        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            descArray.add(res.getString(0));

        }

        res.close();


    }

    public void writeImg(String page) throws SQLException{

        openDataBase();

        imgArray.clear();
        Cursor res = null;

        if(page.equals("Info"))
            res = myDataBase.rawQuery("SELECT img FROM Information", null);
        if(page.equals("Beach"))
            res = myDataBase.rawQuery("SELECT img FROM Beach", null);

        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            imgArray.add(res.getString(0));

        }

        res.close();

    }

    public void writeFullDesc(String page) throws SQLException{

        openDataBase();

        fullDescArray.clear();
        Cursor res = null;

        if(page.equals("Beach"))
            res = myDataBase.rawQuery("SELECT fullDesc FROM Beach", null);
        if(page.equals("Info"))
            res = myDataBase.rawQuery("SELECT fullDesc FROM Information", null);

        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            fullDescArray.add(res.getString(0));

        }

        res.close();

    }


}
