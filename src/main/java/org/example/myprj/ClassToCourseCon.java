package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClassToCourseCon {

    @FXML
    private TextField ClassId_Tx;

    @FXML
    private TextField CourseId_Tx;

    @FXML
    private VBox InScrollCourse;

    private String CLASSID;
    private String CourseId;
    List<Node> Coursearr=new ArrayList<>();
    int SumCourse=0;

    @FXML
    void ClassToCoursearrAction(ActionEvent event) {
        CourseId=CourseId_Tx.getText();
        AddclassCourseTodB();
    }

    private boolean AddclassCourseTodB()
    {
        Task<Boolean> StuAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("INSERT INTO ClassCoursearr(班级代号,课程号) VALUES (?,?)");
                    stmt1.setString(1,CLASSID);
                    stmt1.setString(2,CourseId);
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


    public void ContentInit(String ClassId)
    {
        CLASSID=ClassId;
        ClassId_Tx.setText(CLASSID);
        Flush_List();
    }

    private void Flush_List()
    {
        Coursearr.clear();
        SearchClassArr();
        InScrollCourse.getChildren().clear();
        InScrollCourse.getChildren().addAll(Coursearr);
    }


    private boolean SearchClassArr()
    {

        Task<Boolean> ClassLodingTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM v_ClassCourse WHERE 班级代号=?");
                    stmt1.setString(1,CLASSID);
                    ResultSet rs= stmt1.executeQuery();

                    while (rs.next())
                    {
                        SumCourse++;
                        String CourseName=rs.getString("课程名");

                        Coursearr.add(AddCourse(CourseName));
                    }

                    if(SumCourse==0)
                    {
                        return false;
                    }else{return true;}
                }
            }
        };

        ClassLodingTask.setOnSucceeded(event -> {
            if (ClassLodingTask.getValue()) {
                System.out.println("StuUnits Loaded Successfully---Units:"+SumCourse);
            }
            else {
                System.out.println("StuUnits Loaded Failed");
            }
        });

        new Thread(ClassLodingTask).start();

        boolean res= false;

        try {
            res =ClassLodingTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;

    }

    private Node AddCourse(String coursename)
    {
        FXMLLoader StuScoreAddLoader = new FXMLLoader();
        StuScoreAddLoader.setLocation(getClass().getResource("CoursearrUnit.fxml"));

        AnchorPane Classunit = null;
        try {
            Classunit = StuScoreAddLoader.load();
            CoursearrUnitCon SSC= StuScoreAddLoader.getController();
            SSC.ContentInit(coursename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return Classunit;

    }



}
