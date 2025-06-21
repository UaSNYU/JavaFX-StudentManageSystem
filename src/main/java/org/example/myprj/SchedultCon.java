package org.example.myprj;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SchedultCon {

    @FXML
    private GridPane gridpane;

    private String ClassId;
    List<ScheduleUnitInfo> Sche=new ArrayList<>();

    public void ContentInit(String classId)
    {
        ClassId=classId;
        LoginCoursearrInfo();
        scheUI();
    }

    private void scheUI()
    {
        for(int i=0;i<Sche.size();i++)
        {
            ScheduleUnitInfo temp=Sche.get(i);
            String Course= temp.CourseName;
            AddtoGridPane(Course,0,temp.Day1);
            AddtoGridPane(Course,1,temp.Day2);
            AddtoGridPane(Course,2,temp.Day3);
            AddtoGridPane(Course,3,temp.Day4);
            AddtoGridPane(Course,4,temp.Day5);
        }
    }

    private boolean AddtoGridPane(String CourseName,int column,String row)
    {
        if (row == null||row=="0")
        {
            return false;
        }

        Label LB=new Label(CourseName);
        LB.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // 占满单元格
        LB.setAlignment(Pos.CENTER); // 文字居中
        switch (row)
        {
            case "12":
                gridpane.add(LB,0,column);
                break;
            case "34":
                gridpane.add(LB,1,column);
                break;
            case "56":
                gridpane.add(LB,2,column);
                break;
            case "78":
                gridpane.add(LB,3,column);
                break;
        }

        return true;
    }

    private boolean LoginCoursearrInfo(){
        Task<Boolean> CourseAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM v_ClassCourse WHERE 班级代号 =?");

                    stmt1.setString(1,ClassId);

                    ResultSet rs= stmt1.executeQuery();

                    while (rs.next()) {
                        String course=rs.getString("课程名");
                        String day1=rs.getString("周一");
                        String day2=rs.getString("周二");
                        String day3=rs.getString("周三");
                        String day4=rs.getString("周四");
                        String day5=rs.getString("周五");

                       Sche.add(new ScheduleUnitInfo(course,day1,day2,day3,day4,day5));
                    }

                    if(GlobalProperty.Global_Zone_Class_units==0)
                    {
                        return false;
                    }else {return true;}

                }
            }
        };

        CourseAddTask.setOnSucceeded(event -> {
            if (CourseAddTask.getValue()) {
                System.out.println("Sys:://null");
            }
            else {
                System.out.println("Sys:://null");

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
