module com.example.mosaicifier {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires jdk.jfr;
    requires javafx.swing;


    opens com.example.mosaicifier to javafx.fxml;
    exports com.example.mosaicifier;
}