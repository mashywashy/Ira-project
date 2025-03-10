module org.ira.iraeval {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.xml;

    opens org.ira.iraeval to javafx.fxml;
    exports org.ira.iraeval;

    opens org.ira.iraeval.Process to javafx.base;
}