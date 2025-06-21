package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class AddStuScoreCon {
    @FXML
    private Label MessageLabel;
    @FXML
    private AnchorPane AddClassSet;

    @FXML
    private TextField ClassId_tx;

    @FXML
    private TextField Courseid_tx;

    @FXML
    private Label MessageLB;

    @FXML
    private TextField Score_Tx;

    @FXML
    private TextField Stuid_tx;
    MainPageController ParentPane;


    public void setParentPane(MainPageController parentPane) {
        this.ParentPane = parentPane;
    }

    private void Flush_ScoreList()
    {
        ParentPane.Flush_ScoreList();
    }

    @FXML
    void ScoreAddAction(ActionEvent event) {
        String StuId=Stuid_tx.getText();
        String Course=Courseid_tx.getText();
        String Score=Score_Tx.getText();
        if(AddnewScore(StuId,Course,Score))
        {
            MessageLabel.setText("√ 添加成功");
            MessageLabel.setVisible(true);
            AddScoretoDB(StuId,Course,Score);
            Flush_ScoreList();
        }else
        {
            MessageLabel.setText("成绩已存在或信息有误");
            MessageLabel.setVisible(true);
        }
    }

    private boolean AddnewScore(String StuId,String Course,String Score)
    {


        if(StuId.isEmpty()||Course.isEmpty()||Score.isEmpty())
        {
            return false;
        }
        if(StuCcoreInfoCheck(StuId,Course))
        {
            return false;
        }

        return true;
    }

    private boolean StuCcoreInfoCheck(String Id,String Course) {
        Task<Boolean> ScoreAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;", "Aminuos", "123456");) {
                    PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM StuScore WHERE 学号=? AND 课程号=?");
                    stmt1.setString(1, Id);
                    stmt1.setString(2, Course);
                    ResultSet rs = stmt1.executeQuery();

                    if (rs.next()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };

        ScoreAddTask.setOnSucceeded(event -> {
            if (ScoreAddTask.getValue()) {
                System.out.println("Sys:://StuId:" + Id + "Score has already existed");
            }
        });

        new Thread(ScoreAddTask).start();

        boolean res = false;

        try {
            res = ScoreAddTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private boolean AddScoretoDB(String StuId,String Course,String Score)
    {
        Task<Boolean> ScoreAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("INSERT INTO StuScore(学号,课程号,成绩) VALUES (?,?,?)");
                    stmt1.setString(1,StuId);
                    stmt1.setString(2,Course);
                    stmt1.setString(3,Score);

                    int rs= stmt1.executeUpdate();

                    if(rs>0)
                    {
                        return true;
                    }else {return false;}
                }
            }
        };

        ScoreAddTask.setOnSucceeded(event -> {
            if (ScoreAddTask.getValue()) {
                System.out.println("Sys:://StuId:" +StuId+"Score insert successfully");
            }else {
                System.out.println("Sys:://StuId:" +StuId+"Score insert failed");
            }
        });

        new Thread(ScoreAddTask).start();

        boolean res= false;

        try {
            res =ScoreAddTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;

    }
}
