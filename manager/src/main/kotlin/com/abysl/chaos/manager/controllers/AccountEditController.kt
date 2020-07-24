package com.abysl.chaos.manager.controllers

import com.abysl.chaos.manager.data.Account
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.net.URL
import java.util.*
import java.util.regex.Pattern


class AccountEditController : Initializable {


    @FXML
    lateinit var nickName: TextField

    @FXML
    lateinit var email: TextField

    @FXML
    lateinit var password: PasswordField

    lateinit var callback: (Account) -> Unit

    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    fun checkEmail(email: String): Boolean {
        return pattern.matcher(email).matches()
    }

    @FXML
    fun onCancel(event: ActionEvent) {
        close()
    }

    @FXML
    fun onSave(event: ActionEvent) {
        if (checkEmail(email.text)) {
            val account =
                if (nickName.text.isBlank())
                    Account(email.text, password.text)
                else
                    Account(nickName.text, email.text, password.text)
            callback.invoke(account)
            close()
        } else {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Invalid Email"
            alert.headerText = "Invalid Email"
            alert.contentText = "Please enter a valid email"
            alert.showAndWait()
        }
    }

    fun setAccount(account: Account){
        nickName.text = account.nickname
        email.text = account.email
        password.text = account.password
    }

    private fun close() {
        val stage = nickName.scene.window as Stage

        stage.close()
    }


    companion object {
        val pattern: Pattern = Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        )
    }


}
