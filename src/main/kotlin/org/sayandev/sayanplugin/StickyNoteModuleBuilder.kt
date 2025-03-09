package org.sayandev.sayanplugin

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.utils.vfs.getDirectory
import okio.ByteString.Companion.toByteString
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
            WriteCommandAction.runWriteCommandAction(modifiableRootModel.project) {
                reformatDirectory(sourceRoot, modifiableRootModel.project)
            }
        }

        modifiableRootModel.sdk = ProjectJdkTable.getInstance().allJdks.firstOrNull { it.name.contains("17") } ?: ProjectJdkTable.getInstance().allJdks.firstOrNull()
        ProjectRootManager.getInstance(modifiableRootModel.project).projectSdk = modifiableRootModel.sdk

        val project = modifiableRootModel.project

        project.save()
        FileDocumentManager.getInstance().saveAllDocuments()

        ExternalSystemUtil.refreshProject(
            project,
            ProjectSystemId("GRADLE"),
            project.basePath!!,
            false,
            ProgressExecutionMode.IN_BACKGROUND_ASYNC,
        )
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

        val mainDirectory = sourceRoot.createChildDirectory(this, "main")
        val kotlinDirectory = mainDirectory.createChildDirectory(this, "kotlin")
        val groupInput = DataManager.getTypedElement<InputElement>("group")!!
        var codePackage: VirtualFile? = null
        for (part in groupInput.field.text.plus(".${DataManager.context.projectName.lowercase()}").split(".")) {
            codePackage = (codePackage?.createChildDirectory(this, part) ?: kotlinDirectory.createChildDirectory(this, part))
        }
        codePackage!!

        val stickyNoteBukkitCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_bukkit")!!
        val stickyNoteBungeeCordCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_bungeecord")!!
        val stickyNoteVelocityCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_velocity")!!

        val settingsConfigCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_settings_config")!!
        val languageConfigCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_language_config")!!
        val commandConfigCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_main_command")!!
        val dependencyPlaceholderAPIPapiExpansionCheckBox = DataManager.getTypedElement<CheckBoxElement>("generate_papiexpansion")!!

        if (settingsConfigCheckBox.selected) {
            val configDirectory = codePackage.findDirectory("config") ?: codePackage.createChildDirectory(this, "config")
            val settingsConfigFile = javaClass.getResource("/templates/SettingsConfig.kt").readText().replace("%package%", "${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.config")
            configDirectory.createChildData(this, "SettingsConfig.kt").setBinaryContent(settingsConfigFile.toByteArray())
        }

        if (languageConfigCheckBox.selected) {
            val configDirectory = codePackage.findDirectory("config") ?: codePackage.createChildDirectory(this, "config")
            val languageConfigFile = javaClass.getResource("/templates/LanguageConfig.kt").readText().replace("%package%", "${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.config")
            configDirectory.createChildData(this, "LanguageConfig.kt").setBinaryContent(languageConfigFile.toByteArray())
        }

        if (commandConfigCheckBox.selected) {
            val commandDirectory = codePackage.findDirectory("command") ?: codePackage.createChildDirectory(this, "command")
            val mainCommandFile = javaClass.getResource("/templates/PluginCommand.kt").readText()
                .replace("%package%", "${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.command")
                .replace("%plugin_name%", DataManager.context.projectName)
            commandDirectory.createChildData(this, "${DataManager.context.projectName}Command.kt").setBinaryContent(mainCommandFile.toByteArray())
        }

        if (dependencyPlaceholderAPIPapiExpansionCheckBox.selected) {
            val hookDirectory = codePackage.findDirectory("hook") ?: codePackage.createChildDirectory(this, "hook")
            val papiExpansionFile = javaClass.getResource("/templates/PAPIExpansion.kt").readText()
                .replace("%package%", "${groupInput.field.text}.${DataManager.context.projectName.lowercase()}.hook")
            hookDirectory.createChildData(this, "PAPIExpansion.kt").setBinaryContent(papiExpansionFile.toByteArray())
        }

        if (stickyNoteBukkitCheckBox.selected) {
            val bukkitMainFile = ClassMainBukkitTemplate().template
            codePackage.createChildData(this, "${DataManager.context.projectName}Plugin.kt").setBinaryContent(bukkitMainFile.toByteArray())
        }

        if (stickyNoteBungeeCordCheckBox.selected) {
            val bungeeMainFile = ClassMainBungeeTemplate().template
            codePackage.createChildData(this, "${DataManager.context.projectName}Plugin.kt").setBinaryContent(bungeeMainFile.toByteArray())
        }

        if (stickyNoteVelocityCheckBox.selected) {
            val velocityMainFile = ClassMainVelocityTemplate().template
            codePackage.createChildData(this, "${DataManager.context.projectName}Plugin.kt").setBinaryContent(velocityMainFile.toByteArray())
        }
    }

    private fun reformatFile(file: VirtualFile, project: Project) {
        val psiFile: PsiFile? = PsiManager.getInstance(project).findFile(file)
        if (psiFile != null) {
            CodeStyleManager.getInstance(project).reformat(psiFile)
        }
    }

    private fun reformatDirectory(directory: VirtualFile, project: Project) {
        for (file in directory.children) {
            if (file.isDirectory) {
                reformatDirectory(file, project)
            } else {
                reformatFile(file, project)
            }
        }
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep {
        return StickyNoteModuleWizardStep(context)
    }
}