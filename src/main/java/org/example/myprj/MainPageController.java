package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainPageController {
    @FXML
    private TextField CourseName_Tx;

    @FXML
    private TextField CourseId_Tx;

    @FXML
    private Button AddClassBt;

    @FXML
    private Button AddStuBt;

    @FXML
    private Button BackBt;

    @FXML
    private Button BingjiaButton;

    @FXML
    private Button ClassButton;

    @FXML
    private ScrollPane ClassScrollPane;

    @FXML
    private TextField CourseInput_Tx;

    @FXML
    private Button CourseSetButton;

    @FXML
    private Circle HeaderClip;

    @FXML
    public ImageView HeaderImage;

    @FXML
    private Label IDLB;

    @FXML
    private TextField IdInput2;

    @FXML
    private TextField IdInput21;

    @FXML
    private TextField IdInput_Tx;

    @FXML
    private ImageView ImageVV;

    @FXML
    private ImageView ImageVV1;

    @FXML
    private ImageView ImageVV2;

    @FXML
    private VBox InClassScrollPane;

    @FXML
    private VBox InScrollAllstu;

    @FXML
    public VBox InStuScrollPane;

    @FXML
    public AnchorPane InnerPaneBj;

    @FXML
    private AnchorPane InnerPaneClass;

    @FXML
    private AnchorPane InnerPaneCourse;

    @FXML
    private AnchorPane InnerPaneMain;

    @FXML
    public AnchorPane InnerPaneStu;

    @FXML
    private AnchorPane InnerPaneStuScore;

    @FXML
    private AnchorPane LeftSplitPane;

    @FXML
    private Button MainButton;

    @FXML
    private Label MajorLB;

    @FXML
    private AnchorPane MenuPane;

    @FXML
    private TextField NameInput_Tx;

    @FXML
    private Label NameLB;

    @FXML
    private Label PermissionLB;

    @FXML
    private AnchorPane RightSplitPane;

    @FXML
    private Button StuScoreButton;

    @FXML
    private ScrollPane StuScrollPane;

    @FXML
    private AnchorPane anchor;

    @FXML
    private SplitPane splitpane;

    @FXML
    private VBox InScoreScrollPan;

    @FXML
    private VBox InCourseScrollPane;

    @FXML
    private TextField Classid;

    @FXML
    private TextField Stuid;


    @FXML
    private WebView WebVV=new WebView();

    /// //////////////////////////////////////////////// ///

    List<Node> CourseList=new ArrayList<>();
    List<Node> ClassList=new ArrayList<>();
    List<Node> StuScoreList=new ArrayList<>();
    List<Node> AllStunodes=new ArrayList<>();
    ClassUnitController ClassUnit_Now;


    public void initialize()
    {
        UIhandle();
        UserInfoHandle();
        Flush_ClassList();
        Flush_ScoreList();
        Flush_CourseList();
        Flush_AllStuList();
    }

    //Flush_Nodes
    public void Flush_AllStuList()
    {
        AllStunodes.clear();
        InScrollAllstu.getChildren().clear();
        LodingAllStuList("%","%");
        InScrollAllstu.getChildren().addAll(AllStunodes);
    }
    
    public void Flush_ScoreList()
    {
        StuScoreList.clear();
        InScoreScrollPan.getChildren().clear();
        LodingScoreunit("%","%","%");
        InScoreScrollPan.getChildren().addAll(StuScoreList);
    }

    public void Flush_CourseList()
    {
        CourseList.clear();
        InCourseScrollPane.getChildren().clear();
        LodingCourseunit("%","%");
        InCourseScrollPane.getChildren().addAll(CourseList);
    }
    

    public void Flush_ClassList()
    {
        ClassList.clear();
        InClassScrollPane.getChildren().clear();
        LodingClassunit();
        InClassScrollPane.getChildren().addAll(ClassList);
    }


    private void UserInfoHandle()
    {
        NameLB.setText("姓名:"+LoginUserInfo.getUserName());
        IDLB.setText("工号:"+LoginUserInfo.getCID());
        PermissionLB.setText("身份:"+LoginUserInfo.getUserPermission());
        MajorLB.setText("院系:"+LoginUserInfo.getFaculties());
    }

    private void UIhandle()
    {
        ImageVV.setImage(new Image(getClass().getResource("Headerimage/Setting.png").toExternalForm()));
        ImageVV.setSmooth(true);
        ImageVV1.setImage(new Image(getClass().getResource("Headerimage/default.png").toExternalForm()));
        ImageVV1.setSmooth(true);
        ImageVV2.setImage(new Image(getClass().getResource("Headerimage/NightIcon.png").toExternalForm()));
        ImageVV2.setSmooth(true);

        Circle clip = new Circle();
        clip.setRadius(HeaderClip.getRadius()-4);

        clip.setCenterX(HeaderImage.getFitWidth() / 2);
        clip.setCenterY(HeaderImage.getFitHeight() / 2);

        HeaderImage.setClip(clip);
        HeaderImage.setImage(GlobalProperty.UserHdImage);
        HeaderImage.setFitWidth(108);
        HeaderImage.setFitHeight(108);

        WebEngine webengine =WebVV.getEngine();
        webengine.load("https://www.nuist.edu.cn/");
    }
    
    //DBInit___
    private boolean LodingCourseunit(String Courseid,String CourseName)
    {
        Task<Boolean> CourseAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM StuCource WHERE 课程号 LIKE ? AND 课程名 LIKE ?");
                    stmt1.setString(1,Courseid);
                    stmt1.setString(2,CourseName);
                    ResultSet rs= stmt1.executeQuery();



                    while (rs.next()) {
                        String CourseId=rs.getString("课程号");
                        String CourseName=rs.getString("课程名");
                        String CourseDate=rs.getString("开课学期");
                        String CourseDuration=rs.getString("学时");
                        String CourseScore=rs.getString("学分");


                        CourseList.add(AddCourseunit(CourseId,CourseName,CourseDate,CourseDuration,CourseScore));
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
                System.out.println("Sys:://CourseUnits Loaded Successfully---Units:"+GlobalProperty.Global_Zone_Class_units);
            }
            else {
                System.out.println("Sys:://No CourseUnits Found");

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
    
    private boolean LodingClassunit()
    {
        Task<Boolean> CourseAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM v_ClassInfos WHERE 班级管理员=?");
                    stmt1.setString(1,LoginUserInfo.getUserName());
                    ResultSet rs= stmt1.executeQuery();



                    while (rs.next()) {
                        GlobalProperty.Global_Zone_Class_units++;
                        String grade=rs.getString("班级年级");
                        String id=rs.getString("班级代号");
                        String zhuanye=rs.getString("班级专业");
                        String Name=rs.getString("班级名称");
                        String renshu=rs.getString("班级人数");
                        String daoyuan=rs.getString("班级管理员");
                        String yuanxiL=rs.getString("所属院系");

                        ClassList.add(AddClassunit(Name,grade,id,zhuanye,yuanxiL,renshu,daoyuan));
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
                System.out.println("Sys:://ClassUnits Loaded Successfully---Units:"+GlobalProperty.Global_Zone_Class_units);
            }
            else {
                System.out.println("Sys:://No ClassUnits Found");

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

    private boolean LodingScoreunit(String Stuid,String StuName,String StuCourse)
    {
        Task<Boolean> CourseAddTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM v_StuScore WHERE 学号 LIKE ? AND 姓名 LIKE ? AND 课程名 LIKE ?");

                        stmt1.setString(1,Stuid);
                        stmt1.setString(2,StuName);
                        stmt1.setString(3,StuCourse);

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

    private boolean LodingAllStuList(String stuid,String classid)
    {
        int SumStu=0;
        Task<Boolean> ClassLodingTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM Student WHERE 学号 LIKE ? AND 班级 LIKE ?");
                    stmt1.setString(1,stuid);
                    stmt1.setString(2,classid);
                    ResultSet rs= stmt1.executeQuery();


                    while (rs.next())
                    {
                        String id=rs.getString("学号");
                        String name=rs.getString("姓名");
                        String xingbie=rs.getString("性别");
                        String date=rs.getString("出生时间");
                        String Sumscore=rs.getString("总学分");
                        String beizhu=rs.getString("备注");
                        String StuClass= rs.getString("班级");

                        AllStunodes.add(AddStuunit(id,name,xingbie,date,Sumscore,beizhu,StuClass));
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
        StuPagelorder.setLocation(getClass().getResource("AllStuUnit.fxml"));
        AnchorPane Stuunit = null;


        try {
            Stuunit = StuPagelorder.load();
            AllStuUnitCon cuc= StuPagelorder.getController();
            cuc.Contentinit(id,name,xingbie,date,Sumscore,beizhu,StuClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Stuunit;
    }

    private Node AddCourseunit(String courseId,String courseName,String courseDate,String courseDuration,String courseScore)
    {
        FXMLLoader CourseAddLoader = new FXMLLoader();
        CourseAddLoader.setLocation(getClass().getResource("CourseUnit.fxml"));

        AnchorPane Courseunit = null;
        try {
            Courseunit = CourseAddLoader.load();
            CourseUnitCon SSC= CourseAddLoader.getController();
            SSC.setParentPane(this);
            SSC.Contentinit(courseId,courseName,courseDate,courseDuration,courseScore);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return Courseunit;
    }


    private Node AddStuScoreunit(String StuId,String StuName,String StuCourse,String StuScore)
    {
        FXMLLoader StuScoreAddLoader = new FXMLLoader();
        StuScoreAddLoader.setLocation(getClass().getResource("StuScore.fxml"));

        AnchorPane Classunit = null;
        try {
            Classunit = StuScoreAddLoader.load();
            StuScoreCon SSC= StuScoreAddLoader.getController();
            SSC.setParentPane(this);
            SSC.Contentinit(StuId,StuName,StuCourse,StuScore);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return Classunit;
    }

    private Node AddClassunit(String Name,String grade,String id,String zhuanye,String yuanxi,String renshu,String daoyuan)
    {
        FXMLLoader StuScoreAddLoader = new FXMLLoader();
        StuScoreAddLoader.setLocation(getClass().getResource("ClassUnit.fxml"));

        AnchorPane Classunit = null;
        try {
            Classunit = StuScoreAddLoader.load();
            ClassUnitController SSC= StuScoreAddLoader.getController();
            SSC.setParentPane(this);
            SSC.Contentinit(Name,grade,id,zhuanye,yuanxi,renshu,daoyuan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return Classunit;
    }

    public void UnitClose()
    {
        InnerPaneMain.setVisible(false);
        InnerPaneClass.setVisible(false);
        InnerPaneStu.setVisible(false);
        InnerPaneStuScore.setVisible(false);
        InnerPaneCourse.setVisible(false);
        InnerPaneBj.setVisible(false);
    }

    //MiddleMenu
    @FXML
    void MainBtAction(ActionEvent event) {
        UnitClose();
        InnerPaneMain.setVisible(true);
    }

    @FXML
    void ClassBtAction(ActionEvent event) {
        UnitClose();
        InnerPaneClass.setVisible(true);
    }

    @FXML
    void StuSCBtAction(ActionEvent event){
        UnitClose();
        InnerPaneStuScore.setVisible(true);
    }

    @FXML
    void CourseAction(ActionEvent event) {
        UnitClose();
        InnerPaneCourse.setVisible(true);
    }

    @FXML
    void AllStuAction(ActionEvent event) {
        UnitClose();
        InnerPaneBj.setVisible(true);
    }

    //TopMenu5-
    @FXML
    void StuSearchAction(ActionEvent event) {
        String StuId=Stuid.getText()+"%";
        String CLASSNAME=Classid.getText()+"%";

        AllStunodes.clear();
        InScrollAllstu.getChildren().clear();
        LodingAllStuList(StuId,CLASSNAME);
        InScrollAllstu.getChildren().addAll(AllStunodes);
    }

    @FXML
    void CourseAddAction(ActionEvent event) {
        FXMLLoader CourseAddPage=new FXMLLoader();
        CourseAddPage.setLocation(getClass().getResource("CourseAddUnit.fxml"));
        AnchorPane CourseAddPane=null;
        try {
            CourseAddPane=CourseAddPage.load();
            AddCourseCon ASC=CourseAddPage.getController();
            ASC.setParentPane(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("添加课程");
        stage.setScene(new Scene(CourseAddPane));
        stage.show();
    }

    @FXML
    void CourseSearchAction(ActionEvent event) {
        String CourseId=CourseId_Tx.getText()+"%";
        String CourseName=CourseName_Tx.getText()+"%";

        CourseList.clear();
        InCourseScrollPane.getChildren().clear();
        LodingCourseunit(CourseId,CourseName);
        InCourseScrollPane.getChildren().addAll(CourseList);
    }

    @FXML
    void ScoreAddAction(ActionEvent event) {
        FXMLLoader StuScoreAddPage=new FXMLLoader();
        StuScoreAddPage.setLocation(getClass().getResource("AddStuScore.fxml"));

        AnchorPane StuScoreAddPane=null;
        try {
            StuScoreAddPane=StuScoreAddPage.load();
            AddStuScoreCon ASCC=StuScoreAddPage.getController();
            ASCC.setParentPane(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("添加班级");
        stage.setScene(new Scene(StuScoreAddPane));
        stage.show();
    }

    @FXML
    void ScoreSearchAction(ActionEvent event) {
        String ID=IdInput_Tx.getText()+"%";
        String Name=NameInput_Tx.getText()+"%";
        String Course=CourseInput_Tx.getText()+"%";

        StuScoreList.clear();
        InScoreScrollPan.getChildren().clear();
        LodingScoreunit(ID,Name,Course);
        InScoreScrollPan.getChildren().addAll(StuScoreList);
    }

    @FXML
    void AddClassAction(ActionEvent event) {
        FXMLLoader StuScoreAddPage=new FXMLLoader();
        StuScoreAddPage.setLocation(getClass().getResource("AddClassSetting.fxml"));

        AnchorPane StuScoreAddPane=null;
        try {
            StuScoreAddPane=StuScoreAddPage.load();
            AddClassSettingCon ACC=StuScoreAddPage.getController();
            ACC.setParentPane(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("添加班级");
        stage.setScene(new Scene(StuScoreAddPane));
        stage.show();
    }


    @FXML
    void AddStuAction(ActionEvent event) {
        FXMLLoader StuScoreAddPage=new FXMLLoader();
        StuScoreAddPage.setLocation(getClass().getResource("AddStu.fxml"));
        AnchorPane StuScoreAddPane=null;
        try {
            StuScoreAddPane=StuScoreAddPage.load();
            AddStuController ASC=StuScoreAddPage.getController();
            ASC.setParentPane(this.ClassUnit_Now);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setTitle("添加学生");
        stage.setScene(new Scene(StuScoreAddPane));
        stage.show();
    }

    @FXML
    void BackBtAction(ActionEvent event) {
        UnitClose();
        InnerPaneClass.setVisible(true);
    }

    //LeftMenu

    //Setting
    @FXML
    void ImageClick(MouseEvent event) {

    }

    //UserSetting
    @FXML
    void ImageClick1(MouseEvent event) {
        FXMLLoader StuScoreAddLoaderr=new FXMLLoader();
        StuScoreAddLoaderr.setLocation(getClass().getResource("UserSetting.fxml"));
        Parent UserSettingroot = null;
        try {
            UserSettingroot = StuScoreAddLoaderr.load();
            UserSettingController USC=StuScoreAddLoaderr.getController();
            USC.setParentPane(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage UserSettingStage = new Stage();
        UserSettingStage.setScene(new Scene(UserSettingroot));
        UserSettingStage.show();


    }

    //StyleSetting
    @FXML
    void ImageClick2(MouseEvent event) {

    }

    //ButtonStyle
    @FXML
    void MouseEntered1(MouseEvent event) {
        MainButton.setStyle("-fx-background-color: #7b7bec");
    }

    @FXML
    void MouseEntered3(MouseEvent event) {
        ClassButton.setStyle("-fx-background-color: #7b7bec");
    }

    @FXML
    void MouseEntered4(MouseEvent event) {
        BingjiaButton.setStyle("-fx-background-color: #7b7bec");
    }

    @FXML
    void MouseEntered5(MouseEvent event) {
        StuScoreButton.setStyle("-fx-background-color: #7b7bec");
    }

    @FXML
    void MouseEntered6(MouseEvent event) {
        CourseSetButton.setStyle("-fx-background-color: #7b7bec");
    }

    @FXML
    void MouseExited1(MouseEvent event) {
        MainButton.setStyle("-fx-background-color:#F0F8FF;-fx-border-width: 0 0 1 0;-fx-border-color: gray");
    }

    @FXML
    void MouseExited3(MouseEvent event) {
        ClassButton.setStyle("-fx-background-color:#F0F8FF;-fx-border-width: 0 0 1 0;-fx-border-color: gray");

    }

    @FXML
    void MouseExited4(MouseEvent event) {
        BingjiaButton.setStyle("-fx-background-color:#F0F8FF;-fx-border-width: 0 0 1 0;-fx-border-color: gray");

    }

    @FXML
    void MouseExited5(MouseEvent event) {
        StuScoreButton.setStyle("-fx-background-color:#F0F8FF;-fx-border-width: 0 0 1 0;-fx-border-color: gray");

    }

    @FXML
    void MouseExited6(MouseEvent event) {
        CourseSetButton.setStyle("-fx-background-color:#F0F8FF;-fx-border-width: 0 0 1 0;-fx-border-color: gray");

    }
}
