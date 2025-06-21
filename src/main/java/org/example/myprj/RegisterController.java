package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterController {
    @FXML
    private Label ResSuc;


    @FXML
    private Label InputNull;

    @FXML
    private Label MessageLabel;

    @FXML
    private TextField IdInput;

    @FXML
    private Label UserIdRegistered;

    @FXML
    private Button LoginButton;

    @FXML
    private Button LoginButton1;

    @FXML
    private PasswordField PasswordInput;

    @FXML
    private PasswordField PasswordInput1;

    @FXML
    private ToggleButton ToggleStyle;

    @FXML
    private AnchorPane archor;

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
        MessageLabel.setVisible(false);
        UserIdRegistered.setVisible(false);
        InputNull.setVisible(false);
        ResSuc.setVisible(false);

    }


    @FXML
    void BackButton(ActionEvent event) throws IOException {
        FXMLLoader RegisterLoader=new FXMLLoader();
        RegisterLoader.setLocation(getClass().getResource("LoginPage.fxml"));
        Parent NewPane=RegisterLoader.load();
        archor.getChildren().setAll(NewPane);
    }

    @FXML
    void ConfirmPasswordScanner(ActionEvent event) {

    }

    @FXML
    void IdScanner(ActionEvent event) {

    }

    @FXML
    void PasswordScanner(ActionEvent event) {

    }

    @FXML
    void RegisterAction(ActionEvent event) {
        String id=IdInput.getText();
        String password0=PasswordInput.getText();
        String password1=PasswordInput1.getText();

        if(RegisterCheck(id,password0,password1))
        {
            ResSuc.setVisible(true);
            UserIdRegistered.setVisible(false);
            MessageLabel.setVisible(false);
            InputNull.setVisible(false);
            Registerhandle(id,password0);
        }


    }

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

    private boolean RegisterCheck(String id,String password0,String password1)
    {
        if(RegistterCheckhandle(id))
        {
            UserIdRegistered.setVisible(true);
            return false;
        }
        else if(!password0.equals(password1))
        {
            MessageLabel.setVisible(true);
            return false;
        }
        else if(id.isEmpty() || password0.isEmpty() ||password1.isEmpty())
        {
            InputNull.setVisible(true);
            return false;
        }

        return true;
    }


    private boolean RegistterCheckhandle(String Id) {
        Task<Boolean> RegisterChecktask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt=conn.prepareStatement("SELECT * FROM LoginUser WHERE 账号Id=?");
                    stmt.setString(1,Id);
                    ResultSet rs= stmt.executeQuery();

                    return  rs.next();
                }
            }
        };

        RegisterChecktask.setOnSucceeded(event -> {
            if (RegisterChecktask.getValue()) {
                System.out.println("Sys:://User:"+Id+"Duplicate usernames");

            }
        });

        new Thread(RegisterChecktask).start();

        boolean res= false;

        try {
            res = RegisterChecktask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }



    private void Registerhandle(String Id,String Password)
    {
        Task<Boolean> Registertask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt=conn.prepareStatement("INSERT INTO LoginUser (账号Id,账号Password,账号Permission) VALUES (?,?,?)");
                    stmt.setString(1,Id);
                    stmt.setString(2,Password);
                    stmt.setString(3,"Tea");
                    stmt.executeUpdate();

                    return  true;
                }
            }
        };

        Registertask.setOnSucceeded(event -> {
            if (Registertask.getValue()) {
                System.out.println("Sys:://User:"+Id+" Register successful");
            }
        });

        new Thread(Registertask).start();

    }


    void BG_set(String Model)
    {
        if(Model.equals("Night")) {
            archor.setStyle("-fx-background-color: #BBFFFF");
            IdInput.setStyle("-fx-background-color: WHITE;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15");
            PasswordInput.setStyle("-fx-background-color: WHITE;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15");
            PasswordInput1.setStyle("-fx-background-color: WHITE;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15");
            ToggleStyle.setGraphic(dayicon);
        }else if(Model.equals("Day"))
        {
            archor.setStyle("-fx-background-color: #0d4142");
            IdInput.setStyle("-fx-background-color: #5F9EA0;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15;-fx-background-radius: 15");
            PasswordInput.setStyle("-fx-background-color: #5F9EA0;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15;-fx-background-radius: 15");
            PasswordInput1.setStyle("-fx-background-color: #5F9EA0;-fx-border-color: BLACK;" +
                    "-fx-border-width: 0.2;-fx-border-radius: 15;-fx-background-radius: 15");
            ToggleStyle.setGraphic(nighticon);
        }
    }



}
