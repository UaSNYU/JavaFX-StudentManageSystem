package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StuInfosCon {

    @FXML
    private TextField SumScore_Tx;

    @FXML
    private AnchorPane AddStuSet;

    @FXML
    private TextArea Beizhu_Tx;

    @FXML
    private Label MessageLB;

    @FXML
    private ChoiceBox<String> SexChoice;

    @FXML
    private Label StuIdLB;

    @FXML
    private Label StuIdLB1;

    @FXML
    private Button StuLAddBt;

    @FXML
    private VBox StuScorePane;

    @FXML
    private Label beizhuLB;

    @FXML
    private Label dateLB;

    @FXML
    private TextField date_tx;

    @FXML
    private Label nameLB;

    @FXML
    private TextField name_tx;

    @FXML
    private TextField stuid_tx;

    @FXML
    private Label zhuanyeLB;

    @FXML
    private TextField zhuanye_tx;

    String Stuid;
    String StuName;
    String StuClass;
    String Studate;
    String StuSex;
    String StuBeizhu;
    String SumScore;
    List<Node> StuScoreList=new ArrayList<>();
    private ClassUnitController ClassParentPane;
    public void setParentPane(ClassUnitController parentPane) {
        this.ClassParentPane = parentPane;
    }

    public void initialize()
    {
        SexChoice.getItems().addAll("男","女");
    }


    public void ContentInit(String id,String name,String xingbie,String date,String sumscore,String beizhu,String stuclass)
    {
        Stuid=id;StuName=name;StuSex=xingbie;StuClass=stuclass;Studate=date;StuBeizhu=beizhu;SumScore=sumscore;
        stuid_tx.setText(Stuid);
        name_tx.setText(StuName);
        zhuanye_tx.setText(StuClass);
        date_tx.setText(Studate);
        Beizhu_Tx.setText(StuBeizhu);
        SumScore_Tx.setText(SumScore);
        SexChoice.setValue(StuSex);


        Flush_ScoreList();
    }


    @FXML
    void StuchangeAction(ActionEvent event) {
        ChangeInfoDB();
        ClassParentPane.Flush_StuList();

    }

    public void Flush_ScoreList()
    {
        StuScoreList.clear();
        StuScorePane.getChildren().clear();
        LodingScoreunit(Stuid);
        StuScorePane.getChildren().addAll(StuScoreList);
    }


    boolean ChangeInfoDB()
    {
        Task<Boolean> ClassUpdateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("UPDATE Student SET 学号 =?,姓名=?,性别=?,出生时间=?,备注=? WHERE 学号=?");
                    stmt1.setString(1,stuid_tx.getText());
                    stmt1.setString(2,name_tx.getText());
                    stmt1.setString(3,SexChoice.getValue());
                    stmt1.setString(4,date_tx.getText());
                    stmt1.setString(5,Beizhu_Tx.getText());
                    stmt1.setString(6,Stuid);

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
                System.out.println("StuInfos Change Successfully");
            }
            else {
                System.out.println("StuInfos Change Failed");
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

    private boolean LodingScoreunit(String Stuid)
    {
        Task<Boolean> StuScoreAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM v_StuScore WHERE 学号 =?");

                    stmt1.setString(1,Stuid);

                    ResultSet rs= stmt1.executeQuery();

                    while (rs.next()) {
                        String StuId=rs.getString("学号");
                        String StuName=rs.getString("姓名");
                        String StuCourse=rs.getString("课程名");
                        String StuScore=rs.getString("成绩");

                        StuScoreList.add(AddStuScoreunit(StuId,StuName,StuCourse,StuScore));
                    }

                    if(GlobalProperty.Global_Zone_Class_units==0)
                    {
                        return false;
                    }else {return true;}

                }
            }
        };

        StuScoreAddTask.setOnSucceeded(event -> {
            if (StuScoreAddTask.getValue()) {
                System.out.println("Sys:://StuScoreUnits Loaded Successfully");
            }
            else {
                System.out.println("Sys:://No StuScoreUnits Found");

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

    private Node AddStuScoreunit(String StuId,String StuName,String StuCourse,String StuScore)
    {
        FXMLLoader StuScoreAddLoader = new FXMLLoader();
        StuScoreAddLoader.setLocation(getClass().getResource("StuInfoscoreUnit.fxml"));

        AnchorPane Classunit = null;
        try {
            Classunit = StuScoreAddLoader.load();
            StuInfoScCon SSC= StuScoreAddLoader.getController();
            SSC.Contentinit(StuId,StuName,StuCourse,StuScore);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return Classunit;
    }

}
