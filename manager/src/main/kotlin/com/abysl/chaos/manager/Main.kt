package com.abysl.chaos.manager

import com.abysl.chaos.manager.controllers.MainController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import java.io.File


class Main: Application() {
    override fun start(stage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("fxml/main.fxml"));
        val root: Parent = loader.load()
        val controller = loader.getController<MainController>()
        val scene = Scene(root)
        scene.stylesheets.add(javaClass.getResource("themes/Dark.css").toExternalForm())

        stage.title = "RotMG Chaos v1.0"
        stage.icons.add(Image(javaClass.getResourceAsStream("images/chaos.png")))
        stage.scene = scene
        stage.show()

        stage.setOnCloseRequest {
            controller.exit()
        }
    }
}

fun main(){
    Application.launch(Main::class.java)
}


