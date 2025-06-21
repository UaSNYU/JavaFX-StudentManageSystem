package org.example.myprj;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutionException;

public class LoginController {


    @FXML
    private Label IdLB;

    @FXML
    private Label PwLB;

    @FXML
    private ProgressIndicator LodingAnimation;

    @FXML
    private Label LodingLB;

    @FXML
    private AnchorPane archor;

    @FXML
    private ToggleButton ToggleStyle;

    @FXML
    private Circle ci2;

    @FXML
    private ImageView HeaderImage;

    @FXML
    private TextField IdInput;

    @FXML
    private Button LoginButton;

    @FXML
    private PasswordField PasswordInput;

    @FXML
    private Button RegisterButton;

    @FXML
    private Label MessageLB;

    private ImageView nighticon=new ImageView();
    private ImageView dayicon=new ImageView();

    public void initialize()
    {
         BG_set(GlobalProperty.Global_BG);
         dayicon.setImage(new Image(getClass().getResource("Headerimage/NightIcon.png").toExternalForm()));
         nighticon.setImage(new Image(getClass().getResource("Headerimage/DaylightIcon.png").toExternalForm()));

         dayicon.setFitWidth(30);
         dayicon.setFitHeight(30);
         nighticon.setFitWidth(30);
         nighticon.setFitHeight(30);
         ToggleStyle.setGraphic(nighticon);

         MessageLB.setVisible(false);
    }


    //切换背景颜色
    @FXML
    void ToggleAction(ActionEvent event) {
        if(ToggleStyle.isSelected())
        {
            GlobalProperty.Global_BG="Night";
            BG_set("Night");
        }
        else
        {
            GlobalProperty.Global_BG="Day";
            BG_set("Day");
        }
    }

    @FXML
    void LoginCheck(ActionEvent event) throws IOException {
        //handleLogin() true
        if(handleLogin()) {
            //头像
            Circle ci=new Circle(65);
            ci.setCenterX(HeaderImage.getFitWidth() / 2);
            ci.setCenterY(HeaderImage.getFitHeight() / 2);
            HeaderImage.setClip(ci);
            GlobalProperty.setGlobal_UserHdImage(LoginUserInfo.getUserId());
            HeaderImage.setImage(GlobalProperty.UserHdImage);
            //LodingAnimation
            LodingAnimation();
        }else
        {
            MessageLB.setVisible(true);
        }
    }

    private void LodingAnimation()
    {
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    MessageLB.setVisible(false);
                    IdInput.setVisible(false);
                    PasswordInput.setVisible(false);
                    LoginButton.setVisible(false);
                    RegisterButton.setVisible(false);
                    ToggleStyle.setVisible(false);
                    IdLB.setVisible(false);
                    PwLB.setVisible(false);
                    LodingLB.setVisible(true);
                    LodingAnimation.setVisible(true);
                });

                Thread.sleep(1500);
                return null;
            }

            @Override
            protected void succeeded() {
                // 加载完成后，回到 JavaFX 主线程执行下面这段
                Platform.runLater(() -> {
                    FXMLLoader MainPagelorder = new FXMLLoader();
                    MainPagelorder.setLocation(getClass().getResource("MainPage.fxml"));
                    Parent MainPageroot = null;
                    try {
                        MainPageroot = MainPagelorder.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    MessageLB.setVisible(false);
                    Stage MainPageStage = new Stage();
                    MainPageStage.setTitle("学生管理系统(xx端)");
                    MainPageStage.setScene(new Scene(MainPageroot));
                    MainPageStage.show();
                    Stage stage = (Stage) LoginButton.getScene().getWindow();
                    stage.close();
                });
            }
        };


        new Thread(loadTask).start(); // 启动任务
    }

    @FXML
    void Register(ActionEvent event) throws IOException {
        FXMLLoader RegisterLoader=new FXMLLoader();
        RegisterLoader.setLocation(getClass().getResource("RegisterPage.fxml"));
        Parent NewPane=RegisterLoader.load();
        archor.getChildren().setAll(NewPane);
    }

   

    private boolean handleLogin()
    {
        String Id=IdInput.getText();
        String Password=PasswordInput.getText();
        System.out.println("Sys:://Use" + Id+" Login");

        if(Id.isEmpty()||Password.isEmpty())
        {
            return false;
        }

        Task<Boolean> LoginChecktask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("SELECT * FROM LoginUser WHERE 账号ID=? AND 账号Password=?");
                    stmt1.setString(1,Id);
                    stmt1.setString(2,Password);
                    ResultSet rs= stmt1.executeQuery();

                    PreparedStatement stmt2=conn.prepareStatement("SELECT * FROM TeaInfo WHERE 账号ID=?");
                    stmt2.setString(1,Id);
                    ResultSet rs1=stmt2.executeQuery();


                    if(rs.next())
                    {
                        if(rs1.next())
                        {
                            String UserId=Id;
                            String TeaID=rs1.getString("教师工号");
                            String TeaName=rs1.getString("教师姓名");
                            String faculties= rs1.getString("所属院系");
                            String Permission=rs.getString("账号Permission");
                            LoginUserInfo.Init(UserId,Permission,TeaID,TeaName,faculties);
                        }

                        GlobalProperty.Global_permissions=rs.getString("账号Permission");
                        return true;
                    }else {return false;}
                }
            }
        };

        LoginChecktask.setOnSucceeded(event -> {
            if (LoginChecktask.getValue()) {
                System.out.println("Sys:://User:"+Id+" Login Successfully;---Permission:"+GlobalProperty.Global_permissions);
            }
        });

        new Thread(LoginChecktask).start();

        boolean res= false;

        try {
            res =LoginChecktask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private void BG_set(String Model)
    {
        if(Model.equals("Night")) {
            archor.setStyle("-fx-background-color: #BBFFFF");
            IdInput.setStyle("-fx-background-color: WHITE;-fx-border-color: BLACK;-fx-border-width: 0.2;-fx-border-radius: 15");
            PasswordInput.setStyle("-fx-background-color: WHITE;-fx-border-color: BLACK;-fx-border-width: 0.2;-fx-border-radius: 15");
            ToggleStyle.setGraphic(dayicon);
        }else if(Model.equals("Day"))
        {
            archor.setStyle("-fx-background-color: #0d4142");
            IdInput.setStyle("-fx-background-color: #5F9EA0;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15;-fx-background-radius: 15");
            PasswordInput.setStyle("-fx-background-color: #5F9EA0;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15;-fx-background-radius: 15");
            ToggleStyle.setGraphic(nighticon);
        }
    }

}
