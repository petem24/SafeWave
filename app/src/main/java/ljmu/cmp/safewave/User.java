package ljmu.cmp.safewave;

import android.widget.TextView;

public class User {

    public static String Username;
    public static String FirstName;
    public static String LastName;
    public static String Gender;
    public static String DOB;
    public static String Phone;
    public static boolean signedIn;
    public static char VerifyStatus;
    public static char Level;
    public static double Height;
    public static String Build;
    public static String Allergies;
    public static String AddInfo;



    public User(){

    }
    public User(String Username, String FirstName, String LastName, String Gender, String DOB, String Phone, char VerifyStatus, char Level, double Height, String Weight, String Allergies, String AddInfo){

        this.Username = Username;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Gender = Gender;
        this.DOB = DOB;
        this.Phone = Phone;
        this.VerifyStatus = VerifyStatus;
        this.Level = Level;
        this.Height = Height;
        this.Build = Weight;
        this.Allergies = Allergies;
        this.AddInfo = AddInfo;

    }

    public static String getFullName(){

        return FirstName+" "+LastName;

    }


}


