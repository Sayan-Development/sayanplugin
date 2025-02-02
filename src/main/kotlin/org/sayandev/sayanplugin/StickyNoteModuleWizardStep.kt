package org.sayandev.sayanplugin

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sayandev.sayanplugin.element.elements.DropDownElement
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel

@OptIn(DelicateCoroutinesApi::class)
class StickyNoteModuleWizardStep(
    private val context: WizardContext
) : ModuleWizardStep() {

    init {
        GlobalScope.launch {
            DataManager.initialize(context)
        }
    }

    override fun validate(): Boolean {
        val isValid = DataManager.elements.filterIsInstance<DropDownElement>().all { !it.loading }
        if (!isValid) {
            Messages.showErrorDialog("Please wait for the data to load", "LOADING DATA")
        }
        return isValid
    }

    override fun getComponent(): JComponent {
        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(DataManager.panel, BorderLayout.WEST)
        val scrollPane = JBScrollPane(mainPanel)
        scrollPane.border = BorderFactory.createEmptyBorder(0, 20, 0, 0)
        scrollPane.preferredSize = Dimension(800, 600)
        return scrollPane
    }

    override fun updateDataModel() {
        DataManager.initialize(context)
    }
}