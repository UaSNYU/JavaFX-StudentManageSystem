module org.example.myprj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;


    opens org.example.myprj to javafx.fxml;
    exports org.example.myprj;
}