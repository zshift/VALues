package me.peterfaria.values

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class ValConfigurable : Configurable {

    private var valComponent: ValComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String = "VALues Settings"

    override fun getPreferredFocusedComponent(): JComponent? = valComponent?.preferredFocusedComponent

    override fun isModified(): Boolean {
        val settings = ValState.getInstance()
        return (valComponent?.autoFold != settings.autoFold)
                || (valComponent?.groupFolds != settings.groupFolds)
    }

    override fun apply() {
        val settings = ValState.getInstance()
        settings.autoFold = valComponent?.autoFold ?: true
        settings.groupFolds = valComponent?.groupFolds ?: false
    }

    override fun createComponent(): JComponent {
        valComponent = ValComponent()
        return valComponent?.panel!!
    }

    override fun reset() {
        val settings = ValState.getInstance()
        valComponent?.autoFold = settings.autoFold
        valComponent?.groupFolds = settings.groupFolds
    }

    override fun disposeUIResources() {
        valComponent = null;
    }

}