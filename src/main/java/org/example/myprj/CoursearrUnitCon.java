package org.example.myprj;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CoursearrUnitCon {

    @FXML
    private Label CourseName;

    public void ContentInit(String coursename)
    {
        CourseName.setText("课程名:"+coursename);
    }
}
