package org.sayandev.sayanplugin.element.elements

import com.intellij.openapi.ui.ComboBox
import kotlinx.coroutines.*
import org.sayandev.sayanplugin.DataManager.asyncDispatcher
import org.sayandev.sayanplugin.element.Element
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

@OptIn(ExperimentalCoroutinesApi::class)
data class DropDownElement(
    override val id: String,
    val name: String,
    val values: suspend () -> Deferred<List<String>?>,
    val defaultValue: String?,
    var enabled: Boolean = true,
    var editable: Boolean = true,
    private val onUserTyping: suspend (String) -> Unit = {},
    override val children: List<Element> = listOf()
) : Element() {
    val currentValues = mutableListOf<String>()
    var loading = true
    private var debounceJob: Job? = null

    val label = JLabel(name)
    val comboBox = ComboBox(arrayOf<String>()).apply {
        this.preferredSize = Dimension(200, 35)
        this.isEditable = true
    }

    init {
        update()
        listenForTyping()
        GlobalScope.launch {
            withContext(asyncDispatcher) {
                val values = values()
                values.invokeOnCompletion {
                    val completedValues = values.getCompleted() ?: return@invokeOnCompletion
                    loading = false
                    currentValues.clear()
                    currentValues.addAll(completedValues)
                    update()
                }
            }
        }
    }

    private fun listenForTyping() {
        val editorComponent = comboBox.editor.editorComponent
        if (editorComponent is JTextField) {
            editorComponent.document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) = debounceSearch(e.document.getText(0, e.document.length))
                override fun removeUpdate(e: DocumentEvent) = debounceSearch(e.document.getText(0, e.document.length))
                override fun changedUpdate(e: DocumentEvent) = debounceSearch(e.document.getText(0, e.document.length))
            })
        }
    }

    private fun debounceSearch(content: String) {
        debounceJob?.cancel()
        debounceJob = GlobalScope.launch {
            delay(500)
            onUserTyping(content)
        }
    }

    fun setValues(values: suspend () -> Deferred<List<String>?>): Deferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        loading = true
        update()
        GlobalScope.launch {
            withContext(asyncDispatcher) {
                val newValues = values().await() ?: return@withContext
                loading = false
                currentValues.clear()
                currentValues.addAll(newValues)
                update()
                deferred.complete(Unit)
            }
        }
        return deferred
    }

    override val components = listOf<JComponent>(
        label,
        comboBox
    )

    override fun update() {
        SwingUtilities.invokeLater {
            comboBox.apply {
                this.removeAllItems()
                if (defaultValue != null) {
                    this.selectedItem = defaultValue
                } else {
                    if (loading) {
                        this.addItem(LOADING_TEXT)
                    }
                }
                currentValues.forEach { value -> this.addItem(value) }
                this.isEditable = enabled && editable && !loading
                this.isEnabled = !loading && enabled
            }
        }
    }

    companion object {
        const val LOADING_TEXT = "Loading..."
    }
}
