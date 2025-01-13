package org.sayandev.sayanplugin.category

import kotlinx.coroutines.CompletableDeferred
import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.connection.JsonConnection
import org.sayandev.sayanplugin.connection.XMLConnection
import org.sayandev.sayanplugin.element.Element
import org.sayandev.sayanplugin.element.elements.CheckBoxElement
import org.sayandev.sayanplugin.element.elements.DropDownElement
import org.sayandev.sayanplugin.element.elements.LabelElement
import org.sayandev.sayanplugin.element.elements.PluginListElement
import java.awt.Font

object ModuleCategory : List<Element> by listOf<Element>(
    LabelElement("general_label", "Module Properties", 16f, Font.BOLD),
    DropDownElement("stickynote_version", "StickyNote Version", {
        CompletableDeferred(
            XMLConnection("https://repo.sayandev.org/snapshots/org/sayandev/stickynote-core/maven-metadata.xml").fetch()
                .getElementsByTagName("version").let { versions ->
                    (0 until versions.length).mapNotNull { versions.item(it).textContent }.reversed()
                }
        )
    }, null),
    CheckBoxElement("stickynote_core", "StickyNote Core", enabled = false, selected = true),
    CheckBoxElement("stickynote_loader", "StickyNote Loader", enabled = false, selected = true),
    CheckBoxElement(
        "stickynote_bukkit", "StickyNote Bukkit", onClick = { event, component, element ->
            if ((element as CheckBoxElement).selected) {
                DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_velocity")?.let {
                    it.selected = false
                    it.update()
                }
                DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_bungeecord")?.let {
                    it.selected = false
                    it.update()
                }
            }
            element.update()
        }, children = listOf(
            CheckBoxElement("stickynote_bukkit_nms", "StickyNote Bukkit NMS"),
            DropDownElement("paper_version", "Paper Version", {
                CompletableDeferred(
                    JsonConnection("https://api.papermc.io/v2/projects/paper").fetch().asJsonObject.get("versions")
                        .asJsonArray.map { it.asString.removePrefix("v") }.reversed()
                )
            }, null),
            CheckBoxElement("use_paperweight", "Use Paperweight", children = listOf(
                DropDownElement("paperweight_version", "Paperweight Version", {
                    CompletableDeferred(
                        JsonConnection("https://api.github.com/repos/PaperMC/paperweight/releases").fetch()
                            .asJsonArray.mapNotNull { it.asJsonObject.get("tag_name")?.asString?.removePrefix("v") }
                    )
                }, null)
            )),
            CheckBoxElement("folia_supported", "Folia Supported", selected = true),
            DropDownElement("mapping", "Mapping", { CompletableDeferred(listOf("mojang", "spigot")) }, "mojang", editable = false),
            CheckBoxElement(
                "add_runpaper", "Add RunPaper", children = listOf(
                    CheckBoxElement("debug_tools", "Debug Tools"),
                    DropDownElement("runpaper_version", "RunPaper Version", {
                        CompletableDeferred(
                            JsonConnection("https://api.github.com/repos/jpenilla/run-task/releases").fetch()
                                .asJsonArray.mapNotNull { it.asJsonObject.get("tag_name")?.asString?.removePrefix("v") }
                        )
                    }, null, true),
                    DropDownElement("minecraft_version", "Minecraft Version", {
                        CompletableDeferred(
                            JsonConnection("https://api.papermc.io/v2/projects/paper").fetch().asJsonObject.get("versions")
                                .asJsonArray.map { it.asString }.reversed()
                        )
                    }, null, true),
                    CheckBoxElement("allow_legacy_formatting", "Allow Legacy Formatting", selected = true),
                    PluginListElement("runpaper_plugins", "Plugins")
                )
            ),
            CheckBoxElement(
                "add_plugin_yaml_bukkit", "Add Yaml Bukkit", children = listOf(
                    DropDownElement("add_plugin_yaml_bukkit_version", "Yaml Bukkit Version", {
                        CompletableDeferred(
                            JsonConnection("https://api.github.com/repos/eldoriarpg/plugin-yml/releases").fetch()
                                .asJsonArray.mapNotNull { it.asJsonObject.get("tag_name")?.asString?.removePrefix("v") }
                        )
                    }, null, true)
                )
            ),
            DropDownElement("run_task_java_version", "Java Version", {
                CompletableDeferred(mutableListOf("17", "21"))
            }, "17"),
        )
    ),
    CheckBoxElement(
        "stickynote_proxy", "StickyNote Proxy", onClick = { event, component, element ->
            element.update()
        }, children = listOf(
            CheckBoxElement("stickynote_proxy_velocity", "StickyNote Proxy Velocity", onClick = { event, component, element ->
                if ((element as CheckBoxElement).selected) {
                    DataManager.getTypedElement<CheckBoxElement>("stickynote_bukkit")?.let {
                        it.selected = false
                        it.update()
                    }
                    DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_bungeecord")?.let {
                        it.selected = false
                        it.update()
                    }
                }
            }),
            CheckBoxElement("stickynote_proxy_bungeecord", "StickyNote Proxy BungeeCord", onClick = { event, component, element ->
                if ((element as CheckBoxElement).selected) {
                    DataManager.getTypedElement<CheckBoxElement>("stickynote_bukkit")?.let {
                        it.selected = false
                        it.update()
                    }
                    DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_velocity")?.let {
                        it.selected = false
                        it.update()
                    }
                }
            })
        )
    ),
    CheckBoxElement("process_resources", "Process Resources", selected = true),
    CheckBoxElement("disable_autotjvm", "Disable AutoTargetJvm", selected = true),
)