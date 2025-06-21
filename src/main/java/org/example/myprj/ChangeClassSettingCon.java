package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class ChangeClassSettingCon {

        @FXML
        private AnchorPane AddClassSet;

        @FXML
        private Label ClassId;

        @FXML
        private TextField ClassId_tx;

        @FXML
        private Button DeleteClass;

        @FXML
        private Label Grade;

        @FXML
        private TextField Grade_Tx;

        @FXML
        private Label MessageLB;

        @FXML
        private Button StuListBt;

        @FXML
        private TextField Yx_tx;

        @FXML
        private TextField Zy_tx;

        @FXML
        private TextField Name_Tx1;

        @FXML
        private TextField doayuan_tx;

        @FXML
        private Label daoyuanLB;

        @FXML
        private Label yuanxiLB;

        @FXML
        private Label zhuanyeLB;

        public MainPageController ParentPane;

        public void setParentPane(MainPageController ParentPane)
        {
            this.ParentPane=ParentPane;
        }

        public void initialize()
        {

        }

        public void GetClassInfo(String Name,String grade,String id,String zhuanye,String yuanxiL,String daoyuan)
        {
                Name_Tx1.setText(Name);
                Grade_Tx.setText(grade);
                ClassId_tx.setText(id);
                Zy_tx.setText(zhuanye);
                Yx_tx.setText(yuanxiL);
                doayuan_tx.setText(daoyuan);
        }

        private void Flush_classlist()
        {
            ParentPane.Flush_ClassList();
        }

        @FXML
        void DeleteAction(ActionEvent event) {
                Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("确认删除");
                alert.setHeaderText("确认删除班级信息!!!");

                ButtonType result=alert.showAndWait().orElse(ButtonType.CANCEL);

                if(result==ButtonType.OK)
                {
                        DeleteInfoDB();
                        Flush_classlist();
                }

                Stage stage =(Stage) AddClassSet.getScene().getWindow();
                stage.close();
        }

        @FXML
        void StuListAction(ActionEvent event) {
              ChangeInfoDB();
              Flush_classlist();
        }

        boolean ChangeInfoDB()
        {
                Task<Boolean> ClassUpdateTask = new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                                        PreparedStatement stmt1=conn.prepareStatement("UPDATE ClassInfos SET 班级名称 =?,班级专业=?,班级管理员=?,班级年级=? WHERE 班级代号=?");
                                        stmt1.setString(1,Name_Tx1.getText());
                                        stmt1.setString(2,Zy_tx.getText());
                                        stmt1.setString(3,doayuan_tx.getText());
                                        stmt1.setString(4,Grade_Tx.getText());
                                        stmt1.setString(5,ClassId_tx.getText());

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
                                System.out.println("ClassInfos Change Successfully");
                        }
                        else {
                                System.out.println("ClassInfos Change Failed");
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

        boolean DeleteInfoDB()
        {
                Task<Boolean> ClassDeleteTask = new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                                        PreparedStatement stmt1=conn.prepareStatement("DELETE FROM ClassInfos WHERE 班级代号=?");
                                        stmt1.setString(1,ClassId_tx.getText());

                                        int rowdelete= stmt1.executeUpdate();

                                        if(rowdelete>0)
                                        {
                                                return true;
                                        } else {return false;}
                                }
                        }
                };

                ClassDeleteTask.setOnSucceeded(event -> {
                        if (ClassDeleteTask.getValue()) {
                                System.out.println("ClassInfos Change Successfully");
                        }
                        else {
                                System.out.println("ClassInfos Change Failed");
                        }
                });

                new Thread(ClassDeleteTask).start();

                boolean res= false;

                try {
                        res =ClassDeleteTask.get();
                } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                }

                return res;
        }

}
