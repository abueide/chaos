module com.abysl.chaos.manager {
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires kotlinx.coroutines.core;

    opens com.abysl.chaos.manager.controllers to javafx.fxml;
    exports com.abysl.chaos.manager;
}
