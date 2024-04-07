module com.la3.task3.la3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires opencv;
    //requires org.bytedeco.opencv;

    opens com.la3.task3.la3 to javafx.fxml;
    exports com.la3.task3.la3;
}