module com.example.petsafeapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.json;
    requires javafx.web;
    requires jdk.jsobject;


    opens com.example.petsafeapp to javafx.fxml;
    exports com.example.petsafeapp;
}