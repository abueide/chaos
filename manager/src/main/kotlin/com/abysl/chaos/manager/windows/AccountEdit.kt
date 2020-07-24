package com.abysl.chaos.manager.windows

import com.abysl.chaos.manager.data.Account
import com.abysl.chaos.manager.Main
import com.abysl.chaos.manager.controllers.AccountEditController
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.IOException


class AccountEdit(val account: Account, val accountList: ObservableList<Account>) {
    constructor(accountList: ObservableList<Account>) : this(Account(), accountList)



    fun update(account: Account) {
        accountList.remove(account)
        accountList.add(account)
    }

    fun show(): Unit {
        try {
            //Load second scene
            val loader = FXMLLoader(Main::class.java.getResource("fxml/account.fxml"))
            val root = loader.load<Parent>()

            //Get controller of scene2
            val controller: AccountEditController = loader.getController()
            controller.callback = ::update
            controller.setAccount(account)
            //Pass whatever data you want. You can have multiple method calls here
            //Show scene 2 in new window
            val stage = Stage()
            stage.scene = Scene(root)
            stage.title = "Edit account"
            stage.isAlwaysOnTop = true;
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.show()
        } catch (ex: IOException) {
            System.err.println(ex)
        }
    }

}