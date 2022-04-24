module com.example.mosaicifier {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mosaicifier to javafx.fxml;
    exports com.example.mosaicifier;
}