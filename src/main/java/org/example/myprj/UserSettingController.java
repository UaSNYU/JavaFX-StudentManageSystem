package org.example.myprj;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutionException;

public class UserSettingController {
    @FXML
    private Circle HeaderClip;

    @FXML
    private ImageView HeaderImage;

    @FXML
    private PasswordField PasswordInput;

    @FXML
    private PasswordField PasswordInput1;

    @FXML
    private Button PasswordchangeBt;

    @FXML
    private Label MessageLB;

    @FXML
    private Button UpdateImage;

    public MainPageController ParentPane;

    public void setParentPane(MainPageController ParentPane)
    {
        this.ParentPane=ParentPane;
    }

    public void initialize()
    {
        Circle clip = new Circle();
        clip.setRadius(HeaderClip.getRadius()-4);

        clip.setCenterX(HeaderImage.getFitWidth() / 2);
        clip.setCenterY(HeaderImage.getFitHeight() / 2);

        HeaderImage.setClip(clip);
        HeaderImage.setImage(GlobalProperty.UserHdImage);
        HeaderImage.setFitWidth(108);
        HeaderImage.setFitHeight(108);
    }


    @FXML
    void PasswordchangeAc(ActionEvent event) {
        String Pass1 =PasswordInput.getText();
        String Pass2=PasswordInput1.getText();
        if(Pass1.equals(Pass2))
        {
            MessageLB.setText("修改成功");
            MessageLB.setVisible(true);
            UpadatePasswordDB(Pass1);
        }else
        {
            MessageLB.setVisible(true);
        }
        
    }
    
    private boolean UpadatePasswordDB(String pass1)
    {
        Task<Boolean> PasswordUpdateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=StudentDB;encrypt=true;trustServerCertificate=true;","Aminuos", "123456");){
                    PreparedStatement stmt1=conn.prepareStatement("UPDATE LoginUser SET 账号Password=? WHERE 账号ID=?");
                    stmt1.setString(1,pass1);
                    stmt1.setString(2,LoginUserInfo.getUserId());

                    int rowUpdate= stmt1.executeUpdate();

                    if(rowUpdate>0)
                    {
                        return true;
                    } else {return false;}
                }
            }
        };

        PasswordUpdateTask.setOnSucceeded(event -> {
            if (PasswordUpdateTask.getValue()) {
                System.out.println("Sys:://password Change Successfully");
            }
            else {
                System.out.println("Sys:://password Change Failed");
            }
        });

        new Thread(PasswordUpdateTask).start();

        boolean res= false;

        try {
            res =PasswordUpdateTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    @FXML
    boolean UpdateImageAction(ActionEvent event) {
        Stage FileStage=new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择头像图片");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".PNG", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(FileStage);
        System.out.println(selectedFile);
        if (selectedFile==null)return false;

        Image image=null;

        if (selectedFile != null) {
            try {
                image = new Image(selectedFile.toURI().toString());
                HeaderImage.setImage(image);
                System.out.println("sys:://头像已设置: " + selectedFile.getAbsolutePath());

            } catch (Exception e) {
                System.err.println("sys:://加载图片失败: " + e.getMessage());
            }
        } else {
            System.out.println("sys:://File not found");
        }

        Path targetDir=Paths.get("E:\\IJ Java U\\java projevt\\Myprj\\src\\main\\resources\\org\\example\\myprj\\Headerimage\\UserImage");
        Path targetPath=targetDir.resolve(LoginUserInfo.getUserId()+".png");

        try {
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            Thread.sleep(1000);
            ParentPane.HeaderImage.setImage(null);
            ParentPane.HeaderImage.setImage(image);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
