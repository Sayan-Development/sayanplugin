package org.sayandev.sayanplugin.element.elements

import com.intellij.openapi.ui.ComboBox
import kotlinx.coroutines.*
import org.sayandev.sayanplugin.DataManager.asyncDispatcher
import org.sayandev.sayanplugin.element.Element
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel

@OptIn(ExperimentalCoroutinesApi::class)
data class DropDownElement(
    override val id: String,
    val name: String,
    val values: suspend () -> Deferred<List<String>>,
    val defaultValue: String?,
    var enabled: Boolean = true,
    var editable: Boolean = true,
    override val children: List<Element> = listOf()
) : Element() {
    val currentValues = mutableListOf<String>()
    var loading = true

    val label = JLabel(name)
    val comboBox = ComboBox(arrayOf<String>()).apply {
        this.preferredSize = Dimension(200, 35)
    }

    init {
        update()
        GlobalScope.launch {
            withContext(asyncDispatcher) {
                val values = values()
                values.invokeOnCompletion {
                    loading = false
                    currentValues.clear()
                    currentValues.addAll(values.getCompleted())
                    update()
                }
            }
        }
    }

    override val components = listOf<JComponent>(
        label,
        comboBox
    )

    override fun update() {
        comboBox.apply {
            this.removeAllItems()
            if (defaultValue != null) {
                this.selectedItem = defaultValue
            } else {
                if (loading) {
                    this.addItem("Loading...")
                }
            }
            currentValues.forEach { value -> this.addItem(value) }
            this.isEditable = enabled && editable && !loading
            this.isEnabled = !loading && enabled
        }
    }
}