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

public class CourseUnitCon {

    @FXML
    private Label ArrangeLB;

    @FXML
    private Label CourseLB;

    @FXML
    private Label CourseNameLB;

    @FXML
    private Label CourseScLB;

    @FXML
    private Button StuDelBt;

    @FXML
    private Button StuInfoBt;

    @FXML
    private Label durationLB;

    MainPageController ParentPane;
    String CourseId;
    String CourseName;
    String CourseDate;
    String CourseDuration;
    String CourseScore;

    public void setParentPane(MainPageController parentPane)
    {
        this.ParentPane=parentPane;
    }

    public void Contentinit(String courseId,String courseName,String courseDate,String courseDuration,String courseScore)
    {
        CourseId=courseId;CourseName=courseName;CourseDate=courseDate;CourseDuration=courseDuration;CourseScore=courseScore;
        CourseLB.setText("课程号:"+CourseId);
        CourseNameLB.setText("课程名:"+CourseName);
        ArrangeLB.setText("开课学期:"+CourseDate);
        durationLB.setText("学时:"+CourseDuration);
        CourseScLB.setText("学分:"+CourseScore);
    }


    @FXML
    void CourseArrangeAction(ActionEvent event) {
        FXMLLoader CourseArrangeLorder=new FXMLLoader();
        CourseArrangeLorder.setLocation(getClass().getResource("ScheduleArrangeUnit.fxml"));

        AnchorPane StuInfoPane=null;
        try {
            StuInfoPane=CourseArrangeLorder.load();
            SchedulearrCon SAC=CourseArrangeLorder.getController();
            SAC.ContentInit(CourseId,CourseName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("课程安排");
        stage.setScene(new Scene(StuInfoPane));
        stage.show();
    }

    @FXML
    void CourseDelAction(ActionEvent event) {
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("确认删除课程信息!!!");

        ButtonType result=alert.showAndWait().orElse(ButtonType.CANCEL);

        if(result==ButtonType.OK)
        {
            DeleteStuDB();
            ParentPane.Flush_CourseList();
        }
    }


    private boolean DeleteStuDB()
    {
        Task<Boolean> StuDeleteTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("DELETE FROM StuCourse WHERE 课程号=?");
                    stmt1.setString(1,CourseId);

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
                System.out.println("CourseInfos Change Successfully");
            }
            else {
                System.out.println("CourseInfos Change Failed");
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
}