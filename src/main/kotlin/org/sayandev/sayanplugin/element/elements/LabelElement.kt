package org.sayandev.sayanplugin.element.elements

import org.sayandev.sayanplugin.element.Element
import javax.swing.JComponent
import javax.swing.JLabel

data class LabelElement(
    override val id: String,
    val name: String,
    val size: Float? = null,
    val style: Int? = null,
    override val children: List<Element> = listOf()
) : Element() {
    val label = JLabel(name).apply {
        if (size != null) {
            font = font.deriveFont(24f)
        }
        if (style != null) {
            font = font.deriveFont(font.style or style)
        }
    }

    override val components = listOf<JComponent>(
        label,
    )

    override fun update() {

    }
}