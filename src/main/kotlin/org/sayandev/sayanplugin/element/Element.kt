package org.sayandev.sayanplugin.element

import javax.swing.JComponent

abstract class Element {
    abstract val id: String

    abstract val components: List<JComponent>
    abstract val children: List<Element>

    open fun update() { }
}