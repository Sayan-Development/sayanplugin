package org.sayandev.sayanplugin

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.VirtualFile
import org.sayandev.sayanplugin.element.elements.CheckBoxElement
import org.sayandev.sayanplugin.element.elements.InputElement
import org.sayandev.sayanplugin.template.ClassMainBukkitTemplate
import org.sayandev.sayanplugin.template.ClassMainBungeeTemplate
import org.sayandev.sayanplugin.template.ClassMainVelocityTemplate
import org.sayandev.sayanplugin.template.gradle.GradleBuildTemplate
import org.sayandev.sayanplugin.template.gradle.GradleSettingsTemplate
import org.sayandev.sayanplugin.template.gradle.GradleWrapperTemplate

class StickyNoteModuleBuilder : ModuleBuilder() {
    override fun getModuleType(): ModuleType<*> {
        return StickyNoteModuleType.INSTANCE
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val contentEntry = doAddContentEntry(modifiableRootModel)
        val sourceRoot: VirtualFile? = contentEntry?.file?.createChildDirectory(this, "src")
        if (sourceRoot != null) {
            contentEntry.addSourceFolder(sourceRoot, false)
            generateFiles(sourceRoot)
        }
    }

    private fun generateFiles(sourceRoot: VirtualFile) {
        val buildGradleFile = GradleBuildTemplate().template
        sourceRoot.parent.createChildData(this, "build.gradle.kts").setBinaryContent(buildGradleFile.toByteArray())
        val settingsGradleFile = GradleSettingsTemplate().template
        sourceRoot.parent.createChildData(this, "settings.gradle.kts").setBinaryContent(settingsGradleFile.toByteArray())
        val weapperGradleFile = GradleWrapperTemplate().template
        val gradleDirectory = sourceRoot.parent.createChildDirectory(this, "gradle")
        val wrapperDirectory = gradleDirectory.createChildDirectory(this, "wrapper")
        wrapperDirectory.createChildData(this, "gradle-wrapper.properties").setBinaryContent(weapperGradleFile.toByteArray())
        val groupInput = DataManager.getTypedElement<InputElement>("group")!!
        var lastPart: VirtualFile? = null
        for (part in groupInput.field.text.split(".")) {
            lastPart = (lastPart?.createChildDirectory(this, part) ?: sourceRoot.createChildDirectory(this, part))
        }
        lastPart!!

        val stickyNoteBukkitCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_bukkit")!!
        val stickyNoteBungeeCordCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_bungeecord")!!
        val stickyNoteVelocityCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_velocity")!!

        if (stickyNoteBukkitCheckBox.selected) {
            val bukkitMainFile = ClassMainBukkitTemplate().template
            lastPart.createChildData(this, "${DataManager.context.projectName}Plugin.kt").setBinaryContent(bukkitMainFile.toByteArray())
        }

        if (stickyNoteBungeeCordCheckBox.selected) {
            val bungeeMainFile = ClassMainBungeeTemplate().template
            lastPart.createChildData(this, "${DataManager.context.projectName}Plugin.kt").setBinaryContent(bungeeMainFile.toByteArray())
        }

        if (stickyNoteVelocityCheckBox.selected) {
            val velocityMainFile = ClassMainVelocityTemplate().template
            lastPart.createChildData(this, "${DataManager.context.projectName}Plugin.kt").setBinaryContent(velocityMainFile.toByteArray())
        }
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep {
        return StickyNoteModuleWizardStep(context)
    }
}