package org.example.myprj;

import javafx.scene.image.Image;

import java.net.URL;

public class GlobalProperty {
    static String Global_BG="Night";
    static String Global_permissions= "Tea";
    static int Global_Zone_Class_units=0;

    static String ImageId="default.png";
    static Image UserHdImage=new Image(GlobalProperty.class.getResource("Headerimage/UserImage/"+ImageId).toExternalForm(),false);

    public static void setGlobal_UserHdImage(String imageId){
        URL url=GlobalProperty.class.getResource("Headerimage/UserImage/"+imageId+".png");
        System.out.println("Headerimage/UserImage/"+imageId+".png");
        if(url==null)
        {
            System.out.println("Sys:://UserImage not found");
        }else
        {
            UserHdImage=new Image(url.toExternalForm());
        }

    }
}
