package com.abysl.chaos.manager.controllers

import com.abysl.chaos.manager.data.Account
import com.abysl.chaos.manager.Main
import com.abysl.chaos.manager.data.Settings
import com.abysl.chaos.manager.windows.AccountEdit
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.net.URL
import java.util.*


class MainController : Initializable {
    @FXML
    private lateinit var settingsBox: VBox

    @FXML
    private lateinit var exaltPath: TextField

    @FXML
    private lateinit var startProxyCheck: CheckBox

    @FXML
    private lateinit var injectChaosCheck: CheckBox

    @FXML
    private lateinit var customDllCheck: CheckBox

    @FXML
    private lateinit var customDllBox: HBox

    @FXML
    private lateinit var dllList: ListView<String>

    @FXML
    private lateinit var accountList: ListView<Account>

    private var exaltDir: SimpleObjectProperty<File> =
        SimpleObjectProperty(File(System.getProperty("user.home") + "/Documents/RealmOfTheMadGod/Production"))
    private val exaltExe: String = "RotMG Exalt.exe"
    private val exaltDll = File("bin/chaoshook.dll")
    private val injectExe = File("bin/inject.exe")
    private val settingsFile = File("settings.json")

    private val processes: MutableMap<Account, Process> = mutableMapOf()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        customDllBox.isVisible = false
        loadSettings()
        onCustomDllToggle()

        accountList.selectionModel.selectionMode = SelectionMode.MULTIPLE
        if (accountList.items.size > 0) {
            accountList.selectionModel.select(0);
        }
        if (dllList.items.size > 0) {
            dllList.selectionModel.select(0)
        }

        exaltPath.text = exaltDir.get().absolutePath

        exaltDir.addListener { _, _, newValue ->
            exaltPath.text = newValue.absolutePath
        }
        exaltPath.textProperty().addListener { _, _, newValue ->
            exaltDir.set(File(newValue))
        }
    }

    fun exit() {
        saveSettings()
    }

    private fun loadSettings() {
        if (settingsFile.exists()) {
            val parseSettings: Settings = Json(JsonConfiguration.Stable)
                .parse(Settings.serializer(), String(settingsFile.readBytes()))
            setSettings(parseSettings)
        }
    }

    private fun setSettings(settings: Settings) {
        startProxyCheck.isSelected = settings.startProxy
        injectChaosCheck.isSelected = settings.injectChaos
        customDllCheck.isSelected = settings.injectDlls
        accountList.items = FXCollections.observableArrayList(settings.accounts)
        dllList.items = FXCollections.observableArrayList(settings.dlls)
    }

    private fun saveSettings() {
        if (!settingsFile.exists()) {
            settingsFile.createNewFile()
        }
        val settings = Settings(
            startProxyCheck.isSelected,
            injectChaosCheck.isSelected,
            customDllCheck.isSelected,
            accountList.items,
            dllList.items
        )
        val json = Json(JsonConfiguration.Stable).stringify(Settings.serializer(), settings)
        settingsFile.writeText(json)
    }

    private fun inject(pid: Long, dllPath: String) {
        var fixedPath = dllPath
        if (dllPath.startsWith("/")) {
            fixedPath = dllPath.substring(1)
        }

        val inject = injectExe.absolutePath + " $pid \"" + fixedPath + "\""
        println(inject);
        val process = Runtime.getRuntime().exec(inject)

        GlobalScope.launch {
            while (true) {
                val input = String(process.inputStream.readAllBytes())
                val error = String(process.errorStream.readAllBytes())
                if (input.isNotBlank()) {
                    println(input)
                }
                if (error.isNotBlank()) {
                    println(error)
                }
            }
        }
    }

    @FXML
    fun onCustomDllToggle() {
        customDllBox.isVisible = customDllCheck.isSelected
    }

    @FXML
    fun onDllBrowse() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select dll(s) to inject..."
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Dll File", "*.dll"))

        val files: List<File>? = fileChooser.showOpenMultipleDialog(settingsBox.scene.window)
        files?.let { dllList.items = FXCollections.observableList(files.map { it.absolutePath }) }
    }

    @FXML
    fun onExaltBrowse() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Select RotMG Directory..."
        val directory: File? = directoryChooser.showDialog(settingsBox.scene.window)

        directory?.let { dir ->
            if (dir.list()?.any { it.contains(exaltExe) } == true) {
                exaltDir.set(dir)
            } else {
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Invalid Directory"
                alert.headerText = "Invalid Directory"
                alert.contentText = "Couldn't find RotMG Exalt.exe in the specified directory."
                alert.showAndWait()
            }
        }
    }

    @FXML
    fun onLaunch() {
        processes.keys.forEach {
            if (!processes[it]!!.isAlive) {
                processes.remove(it)
            }
        }
        accountList.selectionModel.selectedItems
            .filter { !processes.containsKey(it) }
            .forEach { account ->
                val execCommand =
                            "\"${exaltPath.text}/$exaltExe\"" +
                            " data:{platform:Deca,password:${account.getBase64Password()},guid:${account.getBase64Email()},env:4}"
                val process: Process = Runtime.getRuntime().exec(execCommand)
                processes[account] = process

                if (injectChaosCheck.isSelected) {
                    inject(process.pid(), exaltDll.absolutePath)
                }

                if (customDllCheck.isSelected) {
                    dllList.items.forEach { path ->
                        inject(process.pid(), path)
                    }
                }
            }
    }

    @FXML
    fun onAddAccount() {
        AccountEdit(accountList.items).show()
    }

    @FXML
    fun onDeleteAccount() {
        accountList.items.remove(accountList.selectionModel.selectedItem)
    }

    @FXML
    fun onEditAccount() {
        val item: Account? = accountList.selectionModel.selectedItem
        item?.let {
            AccountEdit(it, accountList.items).show()
        }
    }

    @FXML
    fun onExportAccounts() {
    }


}


