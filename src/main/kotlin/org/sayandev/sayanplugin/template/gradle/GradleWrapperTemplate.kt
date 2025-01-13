package org.sayandev.sayanplugin.template.gradle

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.DropDownElement

class GradleWrapperTemplate {
    val gradleVersion = DataManager.getTypedElement<DropDownElement>("gradle_version")!!

    val template = """
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-${gradleVersion.comboBox.selectedItem as String}-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"""
}