package org.sayandev.sayanplugin.template.gradle

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.DropDownElement

class GradleSettingsTemplate {
    val stickyNoteVersion = DataManager.getTypedElement<DropDownElement>("stickynote_version")!!

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
    id("org.sayandev.stickynote.settings") version "${stickyNoteVersion.comboBox.selectedItem as String}"
}

rootProject.name = "${DataManager.context.projectName}""""
}