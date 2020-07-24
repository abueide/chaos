package com.abysl.chaos.manager.data

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.collections.ObservableList
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val startProxy: Boolean,
    val injectChaos: Boolean,
    val injectDlls: Boolean,
    val accounts: List<Account>,
    val dlls: List<String>
){

}