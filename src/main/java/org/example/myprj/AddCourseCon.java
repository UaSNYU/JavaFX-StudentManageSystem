package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class AddCourseCon {

    @FXML
    private Label ArrangeLB;

    @FXML
    private ChoiceBox<String> CourseDateChoice;

    @FXML
    private TextField CourseDuration_Tx;

    @FXML
    private TextField CourseId_Tx;

    @FXML
    private Label CourseLB;

    @FXML
    private Label CourseNameLB;

    @FXML
    private TextField CourseName_Tx;

    @FXML
    private Label CourseScLB;

    @FXML
    private TextField CourseScore_Tx;

    @FXML
    private Button StuDelBt;


    @FXML
    private Label MessageLB;

    @FXML
    private Label durationLB;

    MainPageController ParentPane;
    String CourseId;
    String CourseName;
    String CourseDate;
    String CourseDuration;
    String CourseScore;

    public void initialize()
    {
        CourseDateChoice.getItems().addAll("1","2","3","4","5","6","7","8");
    }

    public void setParentPane(MainPageController parentPane)
    {
        this.ParentPane=parentPane;
    }



    @FXML
    void CourseAddAction(ActionEvent event) {
        CourseId=CourseId_Tx.getText();
        CourseName=CourseName_Tx.getText();
        CourseDate=CourseDateChoice.getValue();
        CourseDuration=CourseDuration_Tx.getText();
        CourseScore=CourseScore_Tx.getText();
        if(AddNewStu())
        {
            MessageLB.setText("√ 添加成功");
            MessageLB.setVisible(true);
            AddCourseToDB();
            ParentPane.Flush_CourseList();
        }else {
            MessageLB.setText("X 信息有误请重新填写");
            MessageLB.setVisible(true);
        }
    }

    private boolean AddNewStu() {
        if(CourseId.isEmpty()||CourseName.isEmpty()||CourseDate.isEmpty()||CourseDuration.isEmpty()||CourseScore.isEmpty())
        {
            return false;
        }
        if(AddCourseCheck())
        {
            return false;
        }

        return true;
    }


    private boolean AddCourseCheck() {
        Task<Boolean> StuAddCheck = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM StuCource WHERE 课程号=?");
                    stmt1.setString(1,CourseId);
                    ResultSet rs= stmt1.executeQuery();

                    if(rs.next())
                    {
                        return true;
                    }else {return false;}
                }
            }
        };

        StuAddCheck.setOnSucceeded(event -> {
            if (StuAddCheck.getValue()) {
                System.out.println("Sys:://null");
            }
        });

        new Thread(StuAddCheck).start();

        boolean res= false;

        try {
            res =StuAddCheck.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private boolean AddCourseToDB() {
        Task<Boolean> StuAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("INSERT INTO StuCource(课程号,课程名,开课学期,学时,学分) VALUES (?,?,?,?,?)");
                    stmt1.setString(1,CourseId);
                    stmt1.setString(2,CourseName);
                    stmt1.setString(3,CourseDate);
                    stmt1.setString(4,CourseDuration);
                    stmt1.setString(5,CourseScore);
                    int rs= stmt1.executeUpdate();

                    if(rs>0)
                    {
                        return true;
                    }else {return false;}
                }
            }
        };

        StuAddTask.setOnSucceeded(event -> {
            if (StuAddTask.getValue()) {
                System.out.println("Sys:://null");
            }else {
                System.out.println("Sys:://null");
            }
        });

        new Thread(StuAddTask).start();

        boolean res= false;

        try {
            res =StuAddTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;

    }


}