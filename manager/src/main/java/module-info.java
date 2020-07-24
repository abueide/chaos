module com.abysl.chaos.manager {
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires kotlinx.coroutines.core;
    requires kotlinx.serialization.runtime;

    opens com.abysl.chaos.manager.controllers to javafx.fxml;
    exports com.abysl.chaos.manager;
}
