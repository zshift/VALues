package me.peterfaria.values

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UIUtil
import org.intellij.lang.annotations.JdkConstants
import javax.swing.JComponent
import javax.swing.JPanel

class ValComponent {
    val panel: JPanel
    private val cbAutoFold = JBCheckBox("Automatically fold `final var` into `val`.", true)
    private val cbGroupFolds = JBCheckBox("Group all folds together.", false)

    init {
        cbGroupFolds.toolTipText = "Folding and unfolding happens to all `final var` segments in the file at once."
        panel = FormBuilder.createFormBuilder()
            .addComponent(cbAutoFold)
            .addComponent(cbGroupFolds)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = cbAutoFold

    var groupFolds: Boolean
        get() = cbGroupFolds.isSelected
        set(newStatus) {
            cbGroupFolds.isSelected = newStatus
        }

    var autoFold: Boolean
        get() = cbAutoFold.isSelected
        set(newStatus) {
            cbAutoFold.isSelected = newStatus
        }
}