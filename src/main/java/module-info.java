module com.adil.filereader {
    requires javafx.controls;
    requires java.xml;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.adil.filereader to javafx.fxml;
    opens com.adil.filereader.model to javafx.base;
    exports com.adil.filereader;
    exports com.adil.filereader.model;
}