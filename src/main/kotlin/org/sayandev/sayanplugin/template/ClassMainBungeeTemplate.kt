package org.sayandev.sayanplugin.template

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.InputElement

class ClassMainBungeeTemplate {
    val groupInput = DataManager.getTypedElement<InputElement>("group")!!

    val template = """
        package ${groupInput.field.text}.${DataManager.context.projectName.lowercase()}
        
        import net.md_5.bungee.api.plugin.Plugin
        import org.sayandev.stickynote.bungeecord.StickyNote
        import org.sayandev.stickynote.loader.bungee.StickyNoteBungeeLoader
        
        class ${DataManager.context.projectName}Plugin : Plugin() {
            override fun onEnable() {
                StickyNoteBungeeLoader(this)
            }
        }
    """.trimIndent()
}