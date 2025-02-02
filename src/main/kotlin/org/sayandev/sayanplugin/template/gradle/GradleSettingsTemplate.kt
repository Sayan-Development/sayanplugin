package org.sayandev.sayanplugin.template.gradle

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.CheckBoxElement
import org.sayandev.sayanplugin.element.elements.DropDownElement

class GradleSettingsTemplate {
    val stickyNoteVersion = DataManager.getTypedElement<DropDownElement>("stickynote_version")!!

    val plugins = mutableListOf(
        "id(\"org.sayandev.stickynote.settings\") version \"${stickyNoteVersion.comboBox.selectedItem as String}\""
    ).apply {
        if (DataManager.getTypedElement<CheckBoxElement>("debug_tools")!!.selected) {
            add("id(\"org.gradle.toolchains.foojay-resolver-convention\") version \"0.9.0\"")
        }
    }

    val template = """
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.sayandev.org/snapshots")
    }
}

plugins {
    ${plugins.joinToString("\n")}
}

rootProject.name = "${DataManager.context.projectName}""""
}