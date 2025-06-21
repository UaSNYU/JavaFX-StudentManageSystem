package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClassUnitController {

    @FXML
    private Label NameLB;

    @FXML
    private Label Grade;

    @FXML
    private Label ClassId;

    @FXML
    private AnchorPane BackGround;

    @FXML
    private Button ClassSetBt;

    @FXML
    private Button StuListBt;

    @FXML
    private Label daoyuanLB;

    @FXML
    private Label renshuLB;

    @FXML
    private Label yuanxiLB;

    @FXML
    private Label zhuanyeLB;


    private int SumStu=0;
    private MainPageController ParentPane;
    List<Node> Stunodes=new ArrayList<>();

    private String GRADE;
    private String ID;
    public String ZY;
    private String YUXANXI;
    private String RENSHU;
    private String DAOYUAN;
    public String NAME;


    public void setParentPane(MainPageController parentPane) {
        this.ParentPane = parentPane;
    }
    public void Contentinit(String Name,String grade,String id,String zhuanye,String yuanxiL,String renshu,String daoyuan)
    {


        ZY=zhuanye;GRADE=grade;ID=id;YUXANXI=yuanxiL;RENSHU=renshu;DAOYUAN=daoyuan;NAME=Name;
        Grade.setText("年级:"+grade+"级");
        ClassId.setText("班级编号:"+id);
        zhuanyeLB.setText("专业:"+zhuanye);
        yuanxiLB.setText("院系:"+yuanxiL);
        renshuLB.setText("班级人数:"+renshu);
        daoyuanLB.setText("辅导员:"+daoyuan);
        NameLB.setText("班级名称:"+Name);
        LodingStuList();
    }


    @FXML
    void ClassSetAction(ActionEvent event) {
        FXMLLoader ClassSetLoder=new FXMLLoader();
        ClassSetLoder.setLocation(getClass().getResource("ChangeClassUnit.fxml"));

        AnchorPane ClassSetPane=null;
        try {
            ClassSetPane=ClassSetLoder.load();
            ChangeClassSettingCon CCSC=ClassSetLoder.getController();
            CCSC.GetClassInfo(NAME,GRADE,ID,ZY,YUXANXI,DAOYUAN);
            CCSC.setParentPane(ParentPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("修改班级信息");
        stage.setScene(new Scene(ClassSetPane));
        stage.show();
    }
    
    @FXML
    void StuListAction(ActionEvent event) {
        ParentPane.ClassUnit_Now=this;
        Flush_StuList();
    }

    @FXML
    void ScheduleAddAction(ActionEvent event) {
        FXMLLoader SchedultInfoPage=new FXMLLoader();
        SchedultInfoPage.setLocation(getClass().getResource("ClassCoursearr.fxml"));

        AnchorPane SchedultInfoPane=null;
        try {
            SchedultInfoPane=SchedultInfoPage.load();
            ClassToCourseCon CTCC=SchedultInfoPage.getController();
            CTCC.ContentInit(ID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("班级课程添加");
        stage.setScene(new Scene(SchedultInfoPane));
        stage.show();
    }



    @FXML
    void ScheduleAction(ActionEvent event) {
        FXMLLoader SchedultInfoPage=new FXMLLoader();
        SchedultInfoPage.setLocation(getClass().getResource("ScheduleUnit.fxml"));

        AnchorPane SchedultInfoPane=null;
        try {
            SchedultInfoPane=SchedultInfoPage.load();
            SchedultCon SC=SchedultInfoPage.getController();
            SC.ContentInit(ID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("班级课表");
        stage.setScene(new Scene(SchedultInfoPane));
        stage.show();
    }


    public void Flush_StuList()
    {
        Stunodes.clear();
        LodingStuList();
        ParentPane.InStuScrollPane.getChildren().clear();
        ParentPane.UnitClose();
        ParentPane.InStuScrollPane.getChildren().addAll(Stunodes);
        ParentPane.InnerPaneStu.setVisible(true);
    }

    private boolean LodingStuList()
    {
        Task<Boolean> ClassLodingTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM Student WHERE 班级=?");
                    stmt1.setString(1,NAME);
                    ResultSet rs= stmt1.executeQuery();

                    while (rs.next())
                    {
                        SumStu++;
                        String id=rs.getString("学号");
                        String name=rs.getString("姓名");
                        String xingbie=rs.getString("性别");
                        String date=rs.getString("出生时间");
                        String Sumscore=rs.getString("总学分");
                        String beizhu=rs.getString("备注");
                        String StuClass= rs.getString("班级");

                        Stunodes.add(AddStuunit(id,name,xingbie,date,Sumscore,beizhu,StuClass));
                    }
                    
                    if(SumStu==0)
                    {
                        return false;
                    }else{return true;}
                }
            }
        };

        ClassLodingTask.setOnSucceeded(event -> {
            if (ClassLodingTask.getValue()) {
                System.out.println("StuUnits Loaded Successfully---Units:"+SumStu);
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


    private Node AddStuunit(String id,String name,String xingbie,String date,String Sumscore,String beizhu,String StuClass)
    {
        FXMLLoader StuPagelorder = new FXMLLoader();
        StuPagelorder.setLocation(getClass().getResource("StuUnit.fxml"));
        AnchorPane Stuunit = null;


        try {
            Stuunit = StuPagelorder.load();
            StuUnitController cuc= StuPagelorder.getController();
            cuc.Contentinit(id,name,xingbie,date,Sumscore,beizhu,StuClass);
            cuc.setParentPane(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Stuunit;
    }
}
