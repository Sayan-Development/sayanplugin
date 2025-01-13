package org.sayandev.sayanplugin

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import org.sayandev.sayanplugin.category.GeneralCategory
import org.sayandev.sayanplugin.category.ModuleCategory
import org.sayandev.sayanplugin.element.Element
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.swing.JPanel
import kotlin.coroutines.CoroutineContext

object DataManager {
    val asyncDispatcher = object : CoroutineDispatcher() {
        val threadPool: ExecutorService = Executors.newFixedThreadPool(
            5,
            ThreadFactoryBuilder().setNameFormat("stickynote-%d").build()
        )

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            threadPool.submit(block)
        }
    }

    val panel = JPanel(GridBagLayout())
    val elements = mutableListOf<Element>()

    val constraints = GridBagConstraints().apply {
        anchor = GridBagConstraints.WEST
        fill = GridBagConstraints.NONE
        gridx = 0
        gridy = GridBagConstraints.RELATIVE
        insets = JBUI.insets(2)
    }

    lateinit var context: WizardContext

    fun initialize(context: WizardContext) {
        if (elements.isNotEmpty()) return
        this.context = context

        panel.layout = GridBagLayout()

        elements.addAll(listOf(
            *GeneralCategory.toTypedArray(),
            *ModuleCategory.toTypedArray(),
        ))

        update()
    }

    fun allElements(): List<Element> {
        val result = mutableListOf<Element>()
        fun addElements(elements: List<Element>) {
            for (element in elements) {
                result.add(element)
                if (element.children.isNotEmpty()) {
                    addElements(element.children)
                }
            }
        }
        addElements(elements)
        return result
    }

    fun getElement(elementId: String): Element? {
        return allElements().find { it.id == elementId }
    }

    fun <T: Element> getTypedElement(elementId: String): T? {
        return getElement(elementId) as? T?
    }

    fun update() {
        panel.removeAll()
        constraints.gridy = 0
        for (element in elements) {
            constraints.gridy++
            element.components.forEach { component ->
                if (element.children.isNotEmpty()) {
                    val elementPanel = JPanel(GridBagLayout())
                    elementPanel.add(component, constraints)
                    addBorderToElement(element, 0, constraints, elementPanel)
                    panel.add(elementPanel, constraints)
                } else {
                    panel.add(component, constraints)
                    constraints.gridx++
                }
            }
            constraints.gridx = 0
        }
    }

    fun addBorderToElement(element: Element, previousMargin: Int = 0, constraints: GridBagConstraints, panel: JPanel) {
        element.children.forEach { subElement ->
            constraints.gridy++
            subElement.components.forEach { subComponent ->
                constraints.gridy++
                panel.add(JPanel().apply {
                    add(JPanel().apply { preferredSize = Dimension(previousMargin, 0) })
                    add(subComponent)
                }, constraints)
//                panel.add(JPanel().apply { preferredSize = Dimension(previousMargin + 25, 0) }, constraints)
//                subComponent.border = JBUI.Borders.emptyLeft(previousMargin + 25)
//                panel.add(subComponent, constraints)
            }
            if (subElement.children.isNotEmpty()) {
                addBorderToElement(subElement, previousMargin + 25, constraints, panel)
            }
        }
    }
}