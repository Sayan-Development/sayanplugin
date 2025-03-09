package org.sayandev.sayanplugin.template

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.CheckBoxElement
import org.sayandev.sayanplugin.element.elements.InputElement

class ClassMainBukkitTemplate {
    val groupInput = DataManager.getTypedElement<InputElement>("group")!!

    val settingsConfigCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_settings_config")!!
    val languageConfigCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_language_config")!!
    val mainCommandCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_main_command")!!
    val dependencyPlaceholderAPIPapiExpansionCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_papiexpansion")!!

    val template = """
        package ${groupInput.field.text}.${DataManager.context.projectName.lowercase()}
        
        ${
            if (settingsConfigCheckBox.selected) {
                "import ${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.config.SettingsConfig"
            } else { "<empty>" }
        }
        ${
            if (languageConfigCheckBox.selected) {
                "import ${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.config.LanguageConfig"
            } else { "<empty>" }
        }
        ${
            if (mainCommandCheckBox.selected) {
                "import ${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.command.${DataManager.context.projectName}Command"
            } else { "<empty>" }
        }
        ${
            if (dependencyPlaceholderAPIPapiExpansionCheckBox.selected) {
                """
                    import org.sayandev.stickynote.bukkit.log
                    import org.sayandev.stickynote.bukkit.hasPlugin
                    import org.sayandev.stickynote.bukkit.hook.PlaceholderAPIHook
                    import ${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.hook.PAPIExpansion
                """.trimIndent()
            } else { "<empty>" }
        }
        import org.bukkit.plugin.java.JavaPlugin
        import org.sayandev.stickynote.loader.bukkit.StickyNoteBukkitLoader
            
        class ${DataManager.context.projectName}Plugin : JavaPlugin() {
            override fun onEnable() {
                StickyNoteBukkitLoader(this)
            
            ${
                if (settingsConfigCheckBox.selected) {
                    "SettingsConfig.reload()"
                } else { "<empty>" }
            }
            ${
                if (languageConfigCheckBox.selected) {
                    "LanguageConfig.reload()"
                } else { "<empty>" }
            }
            ${
                if (mainCommandCheckBox.selected) {
                    "${DataManager.context.projectName}Command"
                } else { "<empty>" }
            }
            ${
                if (dependencyPlaceholderAPIPapiExpansionCheckBox.selected) {
                    """
                        if (hasPlugin("PlaceholderAPI")) {
                            log("PlaceholderAPI found. Enabling PlaceholderAPI hook..")
                            PlaceholderAPIHook.injectComponent()
                            PAPIExpansion().register()
                        }
                    """.trimIndent()
                } else { "<empty>" }
            }
            }
        }
    """.trimIndent()
}