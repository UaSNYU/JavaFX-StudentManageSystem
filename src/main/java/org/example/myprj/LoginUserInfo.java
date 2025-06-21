package org.example.myprj;

public class LoginUserInfo {
    private static String UserId=null;
    private static String UserPermission=null;
    private static String CID=null;
    private static String UserName=null;
    private static String Faculties=null;

    public static void Init(String id,String userpermission,String cid,String username,String faculties)
    {
        UserId=id;
        UserPermission=userpermission;
        CID=cid;
        UserName=username;
        Faculties=faculties;
    }


    public static String getCID() {
        return CID;
    }

    public static String getUserName() {
        return UserName;
    }

    public static String getFaculties() {
        return Faculties;
    }

    public static String getUserPermission() {
        return UserPermission;
    }

    public static String getUserId() {
        return UserId;
    }




}
