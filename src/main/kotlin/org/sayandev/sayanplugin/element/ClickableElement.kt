package org.sayandev.sayanplugin.element

import java.awt.event.ItemEvent
import javax.swing.JComponent

abstract class ClickableElement: Element() {
    open fun onClick(event: ItemEvent, component: JComponent) {
        update()
    }
}