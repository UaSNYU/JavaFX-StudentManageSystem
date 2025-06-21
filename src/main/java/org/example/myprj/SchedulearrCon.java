package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class SchedulearrCon {

    @FXML
    private Label CourseNameLB;

    @FXML
    private ChoiceBox<String> Day1;

    @FXML
    private ChoiceBox<String> Day2;

    @FXML
    private ChoiceBox<String> Day3;

    @FXML
    private ChoiceBox<String> Day4;

    @FXML
    private ChoiceBox<String> Day5;

    @FXML
    private Button StuListBt;

    private String CourseName;
    private String CourseId;
    private boolean Exist=true;

    public void initialize()
    {
        ChoiceBoxStyle(Day1);
        ChoiceBoxStyle(Day2);
        ChoiceBoxStyle(Day3);
        ChoiceBoxStyle(Day4);
        ChoiceBoxStyle(Day5);
    }

    private void ChoiceBoxStyle(ChoiceBox<String> Nood)
    {
        Nood.getItems().addAll("","12","34","56","78");
    }

    public void ContentInit(String id,String courseName)
    {
        CourseId=id;
        CourseName=courseName;
        CourseNameLB.setText("课程:"+courseName);
        SearchArrange();
    }


    @FXML
    void SchedulechagneAction(ActionEvent event) {
        if(Exist)
        {
            UpdateArrange();
        }else {
            InseratArrangetoDB();
        }
    }

    private boolean InseratArrangetoDB()
    {
        Task<Boolean> StuAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("INSERT INTO CourseArrange(课程号,周一,周二,周三,周四,周五) VALUES (?,?,?,?,?,?)");
                    stmt1.setString(1,CourseId);
                    stmt1.setString(2,Day1.getValue());
                    stmt1.setString(3,Day2.getValue());
                    stmt1.setString(4,Day3.getValue());
                    stmt1.setString(5,Day5.getValue());
                    stmt1.setString(6,Day5.getValue());
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

    private boolean UpdateArrange()
    {
        Task<Boolean> ClassUpdateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("UPDATE CourseArrange SET 周一 =?,周二=?,周三=?,周四=?,周五=? WHERE 课程号=?");
                    stmt1.setString(1,Day1.getValue());
                    stmt1.setString(2,Day2.getValue());
                    stmt1.setString(3,Day3.getValue());
                    stmt1.setString(4,Day5.getValue());
                    stmt1.setString(5,Day5.getValue());
                    stmt1.setString(6,CourseId);

                    int rowUpdate= stmt1.executeUpdate();

                    if(rowUpdate>0)
                    {
                        return true;
                    } else {return false;}
                }
            }
        };

        ClassUpdateTask.setOnSucceeded(event -> {
            if (ClassUpdateTask.getValue()) {
                System.out.println("CourseInfos Change Successfully");
            }
            else {
                System.out.println("CourseInfos Change Failed");
            }
        });

        new Thread(ClassUpdateTask).start();

        boolean res= false;

        try {
            res =ClassUpdateTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;

    }


    private boolean SearchArrange()
    {
        Task<Boolean> CourseAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM CourseArrange WHERE 课程号=?");

                    stmt1.setString(1,CourseId);

                    ResultSet rs= stmt1.executeQuery();

                    if(rs.next())
                    {
                        String day1=rs.getString("周一");
                        String day2=rs.getString("周二");
                        String day3=rs.getString("周三");
                        String day4=rs.getString("周四");
                        String day5=rs.getString("周五");

                        Day1.setValue(day1);
                        Day2.setValue(day2);
                        Day3.setValue(day3);
                        Day4.setValue(day4);
                        Day5.setValue(day5);

                    }else{Exist=false;}

                    if(GlobalProperty.Global_Zone_Class_units==0)
                    {
                        return false;
                    }else {return true;}

                }
            }
        };

        CourseAddTask.setOnSucceeded(event -> {
            if (CourseAddTask.getValue()) {
                System.out.println("Sys:://StuScoreUnits Loaded Successfully");
            }
            else {
                System.out.println("Sys:://No StuScoreUnits Found");

            }
        });

        new Thread(CourseAddTask).start();

        boolean res= false;

        try {
            res =CourseAddTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}