package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class AddStuController {

    @FXML
    private Label MessageLB;

    @FXML
    private AnchorPane AddClassSet;

    @FXML
    private TextArea Beizhu_Tx;

    @FXML
    private ChoiceBox<String> SexChoice;

    @FXML
    private Label StuIdLB;

    @FXML
    private Button StuLAddBt;

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

    private ClassUnitController ClassParentPane;
    private String ClassName;
    private String StuID;
    private String StuName;
    private String StuSex;
    private String StuDate;
    private String StuZy;
    private String StuBeizhu;

    public void setParentPane(ClassUnitController parentPane) {
        this.ClassParentPane = parentPane;
        zhuanye_tx.setText(ClassParentPane.ZY);
    }

    @FXML
    void StuAddAction(ActionEvent event) {
        ClassName=ClassParentPane.NAME;
        StuID=stuid_tx.getText();
        StuName=name_tx.getText();
        StuSex=SexChoice.getValue();
        StuDate=date_tx.getText();
        StuZy=zhuanye_tx.getText();
        StuBeizhu=Beizhu_Tx.getText();
        if(AddNewStu())
        {
            MessageLB.setText("√ 添加成功");
            MessageLB.setVisible(true);
            AddStuToDB();
            ClassParentPane.Flush_StuList();
        }else {
            MessageLB.setText("X 信息有误请重新填写");
            MessageLB.setVisible(true);
        }

    }

     public void initialize()
    {
        SexChoice.getItems().addAll("男","女");
    }

    private boolean AddNewStu() {
        if(StuName.isEmpty()||StuID.isEmpty()||StuSex.isEmpty()||StuDate.isEmpty()||StuZy.isEmpty())
        {
            return false;
        }
        if(AddStuCheck())
        {
            return false;
        }
        
        return true;
    }


    private boolean AddStuCheck() {
        Task<Boolean> StuAddCheck = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM Student WHERE 学号=?");
                    stmt1.setString(1,StuID);
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
                System.out.println("Sys:://StuId:" +StuID+"has already existed");
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
    
    private boolean AddStuToDB() {
        Task<Boolean> StuAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("INSERT INTO Student(学号,姓名,性别,出生时间,总学分,备注,班级) VALUES (?,?,?,?,?,?,?)");
                    stmt1.setString(1,StuID);
                    stmt1.setString(2,StuName);
                    stmt1.setString(3,StuSex);
                    stmt1.setString(4,StuDate);
                    stmt1.setString(5,"0");
                    stmt1.setString(6,StuBeizhu);
                    stmt1.setString(7,ClassName);
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
                System.out.println("Sys:://ClassId:" +StuID+"insert successfully");
            }else {
                System.out.println("Sys:://ClassId:" +StuID+"insert failed");
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
