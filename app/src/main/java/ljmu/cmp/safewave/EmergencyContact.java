package ljmu.cmp.safewave;

/**
 * Created by adammaddocks on 09/02/2018.
 */

public class EmergencyContact {


    static String FirstName;
    static String LastName;
    static String Username;
    static String PhoneNum;
    static String Email;
    static String Relation;
    static Boolean hasEmContact = false;

    public EmergencyContact(String username, String firstName, String lastName, String phoneNum, String email, String relation) {

        Username = username;
        FirstName = firstName;
        LastName = lastName;
        PhoneNum = phoneNum;
        Email = email;
        Relation = relation;
    }

}
