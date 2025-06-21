package org.example.myprj;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StuInfoScCon {

    @FXML
    private Label CourseLB;

    @FXML
    private Label NameLB;

    @FXML
    private Label ScoreLB;

    @FXML
    private Label StuIdLB1;

    private String Stuid,StuName,StuCourse,StuScore;

    public void initialize()
    {

    }

    public void Contentinit(String stuid,String stuname,String stucourse,String stuscore)
    {
        Stuid=stuid;StuName=stuname;StuCourse=stucourse;StuScore=stuscore;
        StuIdLB1.setText("学号:"+Stuid);
        NameLB.setText("姓名:"+StuName);
        CourseLB.setText("课程名:"+StuCourse);
        ScoreLB.setText("分数:"+StuScore);
    }
}
