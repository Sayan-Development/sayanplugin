package org.sayandev.sayanplugin.element.elements

import org.sayandev.sayanplugin.element.ClickableElement
import org.sayandev.sayanplugin.element.Element
import java.awt.event.ItemEvent
import javax.swing.JCheckBox
import javax.swing.JComponent

data class CheckBoxElement(
    override val id: String,
    val name: String,
    var enabled: Boolean = true,
    var selected: Boolean = false,
    val onClick: (event: ItemEvent, component: JComponent, element: Element) -> Unit = { a, b, c -> },
    override val children: List<Element> = listOf()
) : ClickableElement() {
    val checkBox = JCheckBox(name).apply {
        this.addItemListener { event -> onClick(event, this) }
    }

    init {
        update()
    }

    override val components = listOf<JComponent>(
        checkBox
    )

    override fun onClick(event: ItemEvent, component: JComponent) {
        enabled = checkBox.isEnabled
        selected = checkBox.isSelected
        onClick.invoke(event, component, this)
        super.onClick(event, component)
    }

    override fun update() {
        checkBox.apply {
            this.isEnabled = enabled
            this.isSelected = selected
        }

        for (element in children) {
            when (element) {
                is CheckBoxElement -> {
                    element.enabled = selected
                    if (!this.selected) {
                        element.selected = false
                    }
                }
                is DropDownElement -> {
                    element.editable = selected
                    element.enabled = selected
                }
            }
            element.update()
        }
    }
}