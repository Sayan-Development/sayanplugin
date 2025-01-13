package org.sayandev.sayanplugin.template

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.InputElement

class ClassMainBukkitTemplate {
    val groupInput = DataManager.getTypedElement<InputElement>("group")!!

    val template = """
        package ${groupInput.field.text}
        
        import org.bukkit.plugin.java.JavaPlugin
        import org.sayandev.stickynote.loader.bukkit.StickyNoteBukkitLoader
            
        class ${DataManager.context.projectName}Plugin : JavaPlugin() {
            override fun onEnable() {
                StickyNoteBukkitLoader(this)
            }
        }
    """.trimIndent()
}