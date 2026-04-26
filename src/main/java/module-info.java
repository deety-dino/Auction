module fxml.auction {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires jdk.jsobject;
    requires org.xerial.sqlitejdbc;

    opens fxml.auction to javafx.fxml;
    exports fxml.auction;
}