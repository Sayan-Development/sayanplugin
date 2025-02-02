package org.sayandev.sayanplugin.element.elements// Create the PluginListElement class
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import org.sayandev.sayanplugin.connection.JsonConnection
import org.sayandev.sayanplugin.element.Element
import java.awt.BorderLayout
import java.awt.Dimension
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
        dialog.preferredSize = Dimension(512, 318)
        dialog.isResizable = true

        val typeField = ComboBox(arrayOf("hangar", "modrinth", "url"))

        var selectedType = typeField.selectedItem as String
        var isHangarType = selectedType == "hangar"
        var isModrinthType = selectedType == "modrinth"

        val versionDropDown = DropDownElement("version", "Version", { fetchHangarPlugins().await()?.first()?.let { fetchHangarPluginVersions(it) } ?: CompletableDeferred(null) }, null)
        val nameDropDown = DropDownElement("name", "Name", { fetchHangarPlugins() }, null, onUserTyping = {
            versionDropDown.setValues {
                when {
                    isHangarType -> fetchHangarPluginVersions(it)
                    isModrinthType -> fetchModrinthPluginVersions(it)
                    else -> CompletableDeferred(emptyList())
                }
            }
        })
        val urlField = JTextField().apply {
            this.preferredSize = Dimension(200, 34)
        }

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
        dialog.add(nameDropDown.comboBox, constraints)

        constraints.gridx = 0
        constraints.gridy = 2
        dialog.add(versionLabel, constraints)
        constraints.gridx = 1
        dialog.add(versionDropDown.comboBox, constraints)

        constraints.gridx = 0
        constraints.gridy = 3
        dialog.add(urlLabel, constraints)
        constraints.gridx = 1
        dialog.add(urlField, constraints)

        // Initially hide the URL field
        urlLabel.isVisible = false
        urlField.isVisible = false

        typeField.addActionListener {
            selectedType = typeField.selectedItem as String
            isHangarType = selectedType == "hangar"
            isModrinthType = selectedType == "modrinth"

            val isUrlType = selectedType == "url"

            nameLabel.isVisible = !isUrlType
            nameDropDown.comboBox.isVisible = !isUrlType
            versionLabel.isVisible = !isUrlType
            versionDropDown.comboBox.isVisible = !isUrlType
            urlLabel.isVisible = isUrlType
            urlField.isVisible = isUrlType

            when {
                isHangarType -> {
                    nameDropDown.setValues({ fetchHangarPlugins() })
                }
                isModrinthType -> {
                    nameDropDown.setValues({ fetchModrinthPlugins() })
                }
            }

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
                        name = nameDropDown.comboBox.selectedItem as String,
                        version = versionDropDown.comboBox.selectedItem as String
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

    private fun fetchHangarPlugins(): CompletableDeferred<List<String>?> {
        return CompletableDeferred(JsonConnection("https://hangar.papermc.io/api/v1/projects").fetchNullable()?.asJsonObject?.get("result")?.asJsonArray?.map { it.asJsonObject["name"].asString })
    }

    private fun fetchHangarPluginVersions(plugin: String): CompletableDeferred<List<String>?> {
        return CompletableDeferred(JsonConnection("https://hangar.papermc.io/api/v1/projects/${plugin.lowercase()}/versions").fetchNullable()?.asJsonObject?.get("result")?.asJsonArray?.map { version -> version.asJsonObject["name"].asString })
    }

    private fun fetchModrinthPlugins(): CompletableDeferred<List<String>?> {
        return CompletableDeferred(JsonConnection("https://api.modrinth.com/v2/search?facets=[[%22categories:spigot%22,%22categories:paper%22]]").fetchNullable()?.asJsonObject?.get("hits")?.asJsonArray?.map { it.asJsonObject["slug"].asString })
    }

    private fun fetchModrinthPluginVersions(plugin: String): CompletableDeferred<List<String>?> {
        return CompletableDeferred(JsonConnection("https://api.modrinth.com/v2/project/${plugin.lowercase()}/version").fetchNullable()?.asJsonArray?.filter { version -> version.asJsonObject["loaders"].asJsonArray.any { listOf("paper", "spigot").contains(it.asString) } }?.map { version -> version.asJsonObject["version_number"].asString })
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