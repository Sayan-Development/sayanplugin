package org.sayandev.sayanplugin.element.elements

import org.sayandev.sayanplugin.element.Element
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField

data class InputElement(
    override val id: String,
    val name: String,
    val defaultValue: String?,
    override val children: List<Element> = listOf()
) : Element() {
    val label = JLabel(name)
    val field = JTextField(defaultValue).apply {
        this.preferredSize = Dimension(200, 35)
    }

    override val components = listOf<JComponent>(
        label,
        field
    )

    override fun update() {

    }
}