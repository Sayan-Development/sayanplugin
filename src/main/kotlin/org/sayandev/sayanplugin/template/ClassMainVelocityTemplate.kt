package org.sayandev.sayanplugin.template

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.InputElement

class ClassMainVelocityTemplate {
    val groupInput = DataManager.getTypedElement<InputElement>("group")!!

    val template = """
        package ${groupInput.field.text}.${DataManager.context.projectName.lowercase()}
        
        import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
        import com.google.inject.Inject
        import com.velocitypowered.api.event.Subscribe
        import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
        import com.velocitypowered.api.plugin.annotation.DataDirectory
        import com.velocitypowered.api.proxy.ProxyServer
        import org.sayandev.stickynote.loader.velocity.StickyNoteVelocityLoader
        import org.sayandev.stickynote.velocity.StickyNote
        import org.slf4j.Logger
        import java.io.File
        import java.nio.file.Path
        
        class ${DataManager.context.projectName}Plugin @Inject constructor(
            val suspendingPluginContainer: SuspendingPluginContainer
        ) {
        
            @Inject
            lateinit var server: ProxyServer
        
            @Inject
            lateinit var logger: Logger
        
            @Inject
            @DataDirectory lateinit var dataDirectory: Path
        
            @Subscribe
            fun onProxyInitialize(event: ProxyInitializeEvent) {
                StickyNoteVelocityLoader(this, PLUGIN_ID, server, logger, dataDirectory)
                suspendingPluginContainer.initialize(this)
            }
        
            companion object {
                const val PLUGIN_ID = "${DataManager.context.projectName.lowercase()}"
            }
        }
    """.trimIndent()
}