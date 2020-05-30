package me.peterfaria.values

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


@State(
    name = "me.peterfaria.values.AppSettingsState",
    storages = [Storage("VALuesPlugin.xml")]
)
class ValState(var autoFold: Boolean = true, var groupFolds: Boolean = false) :
    PersistentStateComponent<ValState> {
    companion object {
        fun getInstance(): ValState {
            return ServiceManager.getService(ValState::class.java)
        }
    }

    override fun getState(): ValState? {
        return this
    }

    override fun loadState(state: ValState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}