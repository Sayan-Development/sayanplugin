package org.sayandev.sayanplugin.element.elements// Create the PluginListElement class
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.JBUI
import org.sayandev.sayanplugin.element.Element
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class PluginListElement(
    override val id: String,
    val title: String,
    override val children: List<Element> = listOf()
) : Element() {
    val plugins = mutableListOf<Plugin>()
    private val pluginListModel = DefaultListModel<Plugin>()
    private val pluginList = JList(pluginListModel)

    init {
        update()
    }

    override val components: List<JComponent> = listOf(
        JPanel(BorderLayout()).apply {
            add(JLabel(title), BorderLayout.NORTH)
            add(JScrollPane(pluginList), BorderLayout.CENTER)
            add(createButtonPanel(), BorderLayout.SOUTH)
        }
    )

    private fun createButtonPanel(): JPanel {
        return JPanel().apply {
            add(JButton("Add Plugin").apply {
                addActionListener { showAddPluginDialog() }
            })
            add(JButton("Remove Plugin").apply {
                addActionListener { removeSelectedPlugin() }
            })
        }
    }

    private fun showAddPluginDialog() {
        val dialog = JDialog()
        dialog.title = "Add Plugin"
        dialog.layout = GridBagLayout()
        dialog.isModal = true
        dialog.isResizable = false
        dialog.rootPane.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val typeField = ComboBox(arrayOf("modrinth", "hangar", "url"))
        val nameField = JTextField()
        val versionField = JTextField()
        val urlField = JTextField()

        val nameLabel = JLabel("Name:")
        val versionLabel = JLabel("Version:")
        val urlLabel = JLabel("URL:")

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            insets = JBUI.insets(5)
            anchor = GridBagConstraints.WEST
            fill = GridBagConstraints.HORIZONTAL
        }

        dialog.add(JLabel("Type:"), constraints)
        constraints.gridx = 1
        dialog.add(typeField, constraints)

        constraints.gridx = 0
        constraints.gridy = 1
        dialog.add(nameLabel, constraints)
        constraints.gridx = 1
        dialog.add(nameField, constraints)

        constraints.gridx = 0
        constraints.gridy = 2
        dialog.add(versionLabel, constraints)
        constraints.gridx = 1
        dialog.add(versionField, constraints)

        constraints.gridx = 0
        constraints.gridy = 3
        dialog.add(urlLabel, constraints)
        constraints.gridx = 1
        dialog.add(urlField, constraints)

        // Initially hide the URL field
        urlLabel.isVisible = false
        urlField.isVisible = false

        typeField.addActionListener {
            val selectedType = typeField.selectedItem as String
            val isUrlType = selectedType == "url"
            nameLabel.isVisible = !isUrlType
            nameField.isVisible = !isUrlType
            versionLabel.isVisible = !isUrlType
            versionField.isVisible = !isUrlType
            urlLabel.isVisible = isUrlType
            urlField.isVisible = isUrlType
            dialog.pack()
        }

        constraints.gridx = 0
        constraints.gridy = 4
        constraints.gridwidth = 2
        dialog.add(JButton("Add").apply {
            addActionListener {
                val plugin = if (typeField.selectedItem == "url") {
                    Plugin(
                        type = typeField.selectedItem as String,
                        url = urlField.text
                    )
                } else {
                    Plugin(
                        type = typeField.selectedItem as String,
                        name = nameField.text,
                        version = versionField.text
                    )
                }
                plugins.add(plugin)
                pluginListModel.addElement(plugin)
                dialog.dispose()
            }
        }, constraints)

        dialog.pack()
        dialog.setLocationRelativeTo(null) // Center the dialog
        dialog.isVisible = true
    }

    private fun removeSelectedPlugin() {
        val selectedIndex = pluginList.selectedIndex
        if (selectedIndex != -1) {
            plugins.removeAt(selectedIndex)
            pluginListModel.remove(selectedIndex)
        }
    }

    override fun update() {
        pluginListModel.clear()
        plugins.forEach { pluginListModel.addElement(it) }
    }
}