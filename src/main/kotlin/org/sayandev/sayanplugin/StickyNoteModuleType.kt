package org.sayandev.sayanplugin

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import javax.swing.Icon

class StickyNoteModuleType : ModuleType<StickyNoteModuleBuilder>(ID) {
    override fun createModuleBuilder(): StickyNoteModuleBuilder {
        return StickyNoteModuleBuilder()
    }

    override fun getName(): String {
        return "StickyNote Module"
    }

    override fun getDescription(): String {
        return "Module for creating StickyNote projects"
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return StickyNoteIcons.STICKY_NOTE
    }

    companion object {
        const val ID = "STICKY_NOTE_MODULE"
        val INSTANCE: StickyNoteModuleType
            get() = ModuleTypeManager.getInstance().findByID(ID) as StickyNoteModuleType
    }
}