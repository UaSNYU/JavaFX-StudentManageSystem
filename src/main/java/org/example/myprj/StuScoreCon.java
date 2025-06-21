package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class StuScoreCon {

    @FXML
    private Label CourseLB;

    @FXML
    private Label NameLB;

    @FXML
    private Label ScoreLB;

    @FXML
    private Label StuIdLB;

    @FXML
    private Button StuScoreDelBt;

    MainPageController ParentPane;
    String Stuid;
    String StuName;
    String StuCourse;
    String StuScore;
    String StuCourseid;

    public void setParentPane(MainPageController parentPane) {
        this.ParentPane = parentPane;
    }

    private void Flush_ScoreList()
    {
        ParentPane.Flush_ScoreList();
    }

    public void Contentinit(String stuid,String stuname,String stucourse,String stuscore)
    {
        Stuid=stuid;StuName=stuname;StuCourse=stucourse;StuScore=stuscore;
        StuIdLB.setText("学号:"+Stuid);
        NameLB.setText("姓名:"+StuName);
        CourseLB.setText("课程名:"+StuCourse);
        ScoreLB.setText("分数:"+StuScore);
        SearchCourseid();
    }

    @FXML
    void StuDelAction(ActionEvent event) {
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("确认删除成绩信息!!!");

        ButtonType result=alert.showAndWait().orElse(ButtonType.CANCEL);

        if(result==ButtonType.OK)
        {
            DeleteStuDB();
            Flush_ScoreList();
        }
    }

    private boolean DeleteStuDB()
    {
        Task<Boolean> StuDeleteTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("DELETE FROM StuScore WHERE 学号=? AND 课程号=?");
                    stmt1.setString(1,Stuid);
                    stmt1.setString(2,StuCourseid);

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
                System.out.println("ScoreInfos Change Successfully");
            }
            else {
                System.out.println("ScoreInfos Change Failed");
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

    private boolean SearchCourseid()
    {
        Task<Boolean> StuScoreAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM StuCource WHERE 课程名 =?");

                    stmt1.setString(1,StuCourse);
                    ResultSet rs= stmt1.executeQuery();

                    if(rs.next()) {
                        StuCourseid=rs.getString("课程号");
                        return true;
                    }

                    return false;
                }
            }
        };

        StuScoreAddTask.setOnSucceeded(event -> {
            if (StuScoreAddTask.getValue()) {
                System.out.println("Sys:://null");
            }
            else {
                System.out.println("Sys:://null");

            }
        });

        new Thread(StuScoreAddTask).start();

        boolean res= false;

        try {
            res =StuScoreAddTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
