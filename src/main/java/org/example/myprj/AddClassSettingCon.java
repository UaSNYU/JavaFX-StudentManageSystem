package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class AddClassSettingCon {
    @FXML
    private TextField Grade_Tx;

    @FXML
    private TextField ClassId_tx;

    @FXML
    private Label Grade;

    @FXML
    private Label MessageLB;

    @FXML
    private AnchorPane AddClassSet;

    @FXML
    private Label ClassId;

    @FXML
    private Button StuListBt;

    @FXML
    private TextField Yx_tx;

    @FXML
    private TextField Zy_tx;

    @FXML
    private Label daoyuanLB;

    @FXML
    private TextField doayuan_tx;

    @FXML
    private Label yuanxiLB;

    @FXML
    private Label zhuanyeLB;

    @FXML
    private Label ClassName;

    @FXML
    private TextField Classname_tx1;

    public MainPageController ParentPane;

    public void setParentPane(MainPageController parentPane) {
        this.ParentPane = parentPane;
    }

    @FXML
    void StuListAction(ActionEvent event) {
        String grade=Grade_Tx.getText();
        String classId = ClassId_tx.getText();
        String zhuanye= Zy_tx.getText();
        String yuanxi=Yx_tx.getText();
        String daoyuan=doayuan_tx.getText();
        String classname=Classname_tx1.getText();
        if(AddnewClass(grade,classId,zhuanye,yuanxi,daoyuan,classname))
        {
            MessageLB.setText("√ 添加成功");
            MessageLB.setVisible(true);
            AddClasstoDB(classId,zhuanye,daoyuan,classname,grade);
            Flush_classlist();
        } else {
            MessageLB.setText("X 信息有误请重新填写");
            MessageLB.setVisible(true);
        }
    }

    private void Flush_classlist()
    {
        ParentPane.Flush_ClassList();
    }

    private boolean AddnewClass(String grade,String classId,String zhuanye,String yuanxi,String daoyuan,String classname)
    {


        if(grade.isEmpty()||classId.isEmpty()||zhuanye.isEmpty()||yuanxi.isEmpty()||daoyuan.isEmpty()||classname.isEmpty())
        {
            return false;
        }
        if(ClassInfoCheck(classId))
        {
            return false;
        }

        return true;
    }


    private boolean ClassInfoCheck(String Id)
    {
        Task<Boolean> ClassAddtask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM ClassInfos WHERE 班级代号=?");
                    stmt1.setString(1,Id);
                    ResultSet rs= stmt1.executeQuery();

                    if(rs.next())
                    {
                        return true;
                    }else {return false;}
                }
            }
        };

        ClassAddtask.setOnSucceeded(event -> {
            if (ClassAddtask.getValue()) {
                System.out.println("Sys:://ClassId:" +Id+"has already existed");
            }
        });

        new Thread(ClassAddtask).start();

        boolean res= false;

        try {
            res =ClassAddtask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private boolean AddClasstoDB(String classId,String zhuanye,String daoyuan,String classname,String grade)
    {
        Task<Boolean> ClassAddtask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("INSERT INTO ClassInfos(班级代号,班级名称,班级专业,班级人数,班级管理员,班级年级) VALUES (?,?,?,?,?,?)");
                    stmt1.setString(1,classId);
                    stmt1.setString(2,classname);
                    stmt1.setString(3,zhuanye);
                    stmt1.setString(4,"0");
                    stmt1.setString(5,daoyuan);
                    stmt1.setString(6,grade);
                    int rs= stmt1.executeUpdate();

                    if(rs>0)
                    {
                        return true;
                    }else {return false;}
                }
            }
        };

        ClassAddtask.setOnSucceeded(event -> {
            if (ClassAddtask.getValue()) {
                System.out.println("Sys:://ClassId:" +classId+"insert successfully");
            }else {
                System.out.println("Sys:://ClassId:" +classId+"insert failed");
            }
        });

        new Thread(ClassAddtask).start();

        boolean res= false;

        try {
            res =ClassAddtask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;

    }

}
