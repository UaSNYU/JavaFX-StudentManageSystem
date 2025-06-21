package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutionException;

public class AllStuUnitCon {

    @FXML
    private Label BirthLB;

    @FXML
    private Label NameLB;

    @FXML
    private Label SexLB;

    @FXML
    private Button StuDelBt;

    @FXML
    private Label StuIdLB;

    @FXML
    private Button StuInfoBt;

    private String Stuid;
    private String Name;
    private String Xingbie;
    private String Date;
    private String SumsCore;
    private String Beizhu;
    private String StuClass;

    public void Contentinit(String id,String name,String xingbie,String date,String sumscore,String beizhu,String stuclass)
    {
        Stuid=id;Name=name;Xingbie=xingbie;Date=date;SumsCore=sumscore;Beizhu=beizhu;StuClass=stuclass;
        StuIdLB.setText("学号:"+id);
        NameLB.setText("姓名:"+name);
        SexLB.setText("性别:"+xingbie);
        BirthLB.setText("出生日期:"+date);
    }

    private boolean DeleteStuDB()
    {
        Task<Boolean> StuDeleteTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("DELETE FROM Student WHERE 学号=?");
                    stmt1.setString(1,Stuid);

                    int rowdelete= stmt1.executeUpdate();

                    if(rowdelete>0)
                    {
                        return true;
                    } else {return false;}
                }
            }
        };

        StuDeleteTask.setOnSucceeded(event -> {
            if (StuDeleteTask.getValue()) {
                System.out.println("ClassInfos Change Successfully");
            }
            else {
                System.out.println("ClassInfos Change Failed");
            }
        });

        new Thread(StuDeleteTask).start();

        boolean res= false;

        try {
            res =StuDeleteTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }


    @FXML
    void StuInfoAction(ActionEvent event) {
        FXMLLoader StuInfoLorder=new FXMLLoader();
        StuInfoLorder.setLocation(getClass().getResource("StuInfosUnit.fxml"));

        AnchorPane StuInfoPane=null;
        try {
            StuInfoPane=StuInfoLorder.load();
            StuInfosCon SIC=StuInfoLorder.getController();
            SIC.ContentInit(Stuid,Name,Xingbie,Date,SumsCore,Beizhu,StuClass);
            SIC.setParentPane(new ClassUnitController());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("修改学生信息");
        stage.setScene(new Scene(StuInfoPane));
        stage.show();
    }
}
