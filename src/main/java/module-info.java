module at.fhv.mme.gameoflife {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.fhv.mme.gameoflife to javafx.fxml;
    exports at.fhv.mme.gameoflife;
}