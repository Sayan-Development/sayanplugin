package org.sayandev.sayanplugin.category

import kotlinx.coroutines.CompletableDeferred
import org.sayandev.sayanplugin.element.Element
import org.sayandev.sayanplugin.connection.JsonConnection
import org.sayandev.sayanplugin.element.elements.DropDownElement
import org.sayandev.sayanplugin.element.elements.InputElement
import org.sayandev.sayanplugin.element.elements.LabelElement
import java.awt.Font

object GeneralCategory: List<Element> by listOf<Element>(
    LabelElement("general_label", "General Properties", 16f, Font.BOLD),
    DropDownElement("java_version", "Java Version", {
        CompletableDeferred(mutableListOf("17", "21"))
    }, "17"),
    DropDownElement("kotlin_version", "Kotlin Version", {
        CompletableDeferred(
            JsonConnection("https://api.github.com/repos/JetBrains/kotlin/releases").fetch().asJsonArray
                .mapNotNull { element ->
                    val jsonObject = element.asJsonObject
                    if (!jsonObject.get("prerelease").asBoolean) jsonObject.get("tag_name").asString?.removePrefix("v") else null
                }
        )
    }, null),
    DropDownElement("gradle_version", "Gradle Version", {
        CompletableDeferred(
            JsonConnection("https://services.gradle.org/versions/all").fetch().asJsonArray
                .mapNotNull { element ->
                    val jsonObject = element.asJsonObject
                    val version = jsonObject.get("version").asString
                    if (!jsonObject.get("snapshot").asBoolean && !version.contains("milestone") && !version.contains("rc")) version else null
                }
        )
    }, null),
    InputElement("group", "Group", "org.sayandev"),
    InputElement("version", "Version", "1.0.0-SNAPSHOT"),
    InputElement("description", "Description", null),
    InputElement("website", "Website", "sayandev.org"),
    InputElement("authors", "Authors", "SayanDevelopment"),
)