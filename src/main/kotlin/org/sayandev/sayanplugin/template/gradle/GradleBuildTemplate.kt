package org.sayandev.sayanplugin.template.gradle

import org.sayandev.sayanplugin.DataManager
import org.sayandev.sayanplugin.element.elements.CheckBoxElement
import org.sayandev.sayanplugin.element.elements.DropDownElement
import org.sayandev.sayanplugin.element.elements.InputElement
import org.sayandev.sayanplugin.element.elements.PluginListElement

class GradleBuildTemplate {
    val groupInput = DataManager.getTypedElement<InputElement>("group")!!
    val versionInput = DataManager.getTypedElement<InputElement>("version")!!
    val descriptionInput = DataManager.getTypedElement<InputElement>("description")!!
    val kotlinVersionDropDown = DataManager.getTypedElement<DropDownElement>("kotlin_version")!!
    val websiteInput = DataManager.getTypedElement<InputElement>("website")!!
    val authorsInput = DataManager.getTypedElement<InputElement>("authors")!!
    val javaVersionDropDown = DataManager.getTypedElement<DropDownElement>("java_version")!!

    //val stickyNoteCoreModuleCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_core")!!
    val stickyNoteLoaderModuleCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_loader")!!

    val stickyNoteBukkitModuleCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_bukkit")!!
    val stickyNoteBukkitNMSModuleCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_bukkit_nms")!!
    val paperVersionDropDown = DataManager.getTypedElement<DropDownElement>("paper_version")!!
    val paperWeightCheckBox = DataManager.getTypedElement<CheckBoxElement>("use_paperweight")!!
    val paperWeightVersionDropDown = DataManager.getTypedElement<DropDownElement>("paperweight_version")!!
    val foliaSupportedCheckBox = DataManager.getTypedElement<CheckBoxElement>("folia_supported")!!
    val mappingDropDown = DataManager.getTypedElement<DropDownElement>("mapping")!!
    val runPaperCheckBox = DataManager.getTypedElement<CheckBoxElement>("add_runpaper")!!
    val debugToolsCheckBox = DataManager.getTypedElement<CheckBoxElement>("debug_tools")!!
    val runPaperVersionDropDown = DataManager.getTypedElement<DropDownElement>("runpaper_version")!!
    val runPaperMinecraftVersionDropDown = DataManager.getTypedElement<DropDownElement>("minecraft_version")!!
    val allowLegacyFormattingCheckBox = DataManager.getTypedElement<CheckBoxElement>("allow_legacy_formatting")!!
    val runPaperPluginList = DataManager.getTypedElement<PluginListElement>("runpaper_plugins")!!

    val addPluginYamlBukkitCheckBox = DataManager.getTypedElement<CheckBoxElement>("add_plugin_yaml_bukkit")!!
    val addPluginYamlBukkitDropDown = DataManager.getTypedElement<DropDownElement>("add_plugin_yaml_bukkit_version")!!
    val runTaskJavaVersion = DataManager.getTypedElement<DropDownElement>("run_task_java_version")!!

    val stickyNoteProxyModuleCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy")!!
    val stickyNoteVelocityModuleCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_velocity")!!
    val stickyNoteBungeeCordModuleCheckBox = DataManager.getTypedElement<CheckBoxElement>("stickynote_proxy_bungeecord")!!

    val processResources = DataManager.getTypedElement<CheckBoxElement>("process_resources")!!

    val template = StringBuilder().apply {
        appendLine("import org.sayandev.plugin.StickyNoteModules")

        appendLine("""
plugins {
    java
    kotlin("jvm") version "${kotlinVersionDropDown.comboBox.selectedItem as String}"
    ${
        if (runPaperCheckBox.selected) {
            "id(\"xyz.jpenilla.run-paper\") version \"${runPaperVersionDropDown.comboBox.selectedItem as String}\""
        } else { "<empty>" }
    }
    ${
        if (addPluginYamlBukkitCheckBox.selected) {
            "id(\"de.eldoria.plugin-yml.bukkit\") version \"${addPluginYamlBukkitDropDown.comboBox.selectedItem as String}\""
        } else { "<empty>" }
    }
    ${
        if (paperWeightCheckBox.selected) {
            "id(\"io.papermc.paperweight.userdev\") version \"${paperWeightVersionDropDown.comboBox.selectedItem as String}\""
        } else { "<empty>" }
    }
    id("org.sayandev.stickynote.project")
}
""")

        appendLine("val slug = rootProject.name.lowercase()")
        appendLine("group = \"${groupInput.field.text}\"")
        appendLine("version = \"${versionInput.field.text}\"")
        if (descriptionInput.field.text.isNotEmpty()) {
            appendLine("description = \"${descriptionInput.field.text}\"")
        }

        val modules = mutableListOf<String>()
        // core is there by default
        /*if (stickyNoteCoreModuleCheckBox.selected) {
            modules.add("StickyNoteModules.CORE")
        }*/
        if (stickyNoteBukkitModuleCheckBox.selected) {
            modules.add("StickyNoteModules.BUKKIT")
        }
        if (stickyNoteBukkitNMSModuleCheckBox.selected) {
            modules.add("StickyNoteModules.BUKKIT_NMS")
        }
        if (stickyNoteProxyModuleCheckBox.selected) {
            modules.add("StickyNoteModules.PROXY")
        }
        if (stickyNoteVelocityModuleCheckBox.selected) {
            modules.add("StickyNoteModules.PROXY_VELOCITY")
        }
        if (stickyNoteBungeeCordModuleCheckBox.selected) {
            modules.add("StickyNoteModules.PROXY_BUNGEECORD")
        }

        appendLine("""
stickynote {
    modules(${modules.joinToString(", ") { it }})
}""")

        appendLine("""
repositories {
    mavenCentral()
    mavenLocal()
    
    maven("https://repo.sayandev.org/snapshots")
    maven("https://repo.sayandev.org/releases")
    maven("https://repo.sayandev.org/private")
}""")

        appendLine("""
dependencies {
    ${
        if (stickyNoteBukkitModuleCheckBox.selected && !paperWeightCheckBox.selected) {
            "compileOnly(\"io.papermc.paper:paper-api:${paperVersionDropDown.comboBox.selectedItem as String}-R0.1-SNAPSHOT\")"
        } else { "<empty>" }
    }
    ${
        if (paperWeightCheckBox.selected) {
            "paperweight.paperDevBundle(\"${paperVersionDropDown.comboBox.selectedItem as String}-R0.1-SNAPSHOT\")"
        } else { "<empty>" }
    }
    compileOnly(fileTree("libs"))
}""")

        appendLine("""
tasks {
    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "${mappingDropDown.comboBox.selectedItem as String}"
        }
    }

    shadowJar {
        archiveFileName.set("${'$'}{rootProject.name}-${'$'}{version}.jar")
        archiveClassifier.set(null as String?)
        destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
        manifest {
            attributes["paperweight-mappings-namespace"] = "${mappingDropDown.comboBox.selectedItem as String}"
        }
    }
    
    ${
        if (runPaperCheckBox.selected) {
"""
    runServer {
        minecraftVersion("${runPaperMinecraftVersionDropDown.comboBox.selectedItem as String}")
        ${
            if (debugToolsCheckBox.selected) {
        """
        javaLauncher = project.javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of("${runTaskJavaVersion.comboBox.selectedItem as String}")
        }"""
            } else { "<empty>" }
        }
        ${
            """
                ${
                    if (runPaperPluginList.plugins.isNotEmpty()) {
        """
        downloadPlugins {
${
    runPaperPluginList.plugins.map { plugin -> 
        when (plugin.type) {
            "modrinth" -> {
                "            modrinth(\"${plugin.name}\", \"${plugin.version}\")"
            }
            "hangar" -> {
                "            hangar(\"${plugin.name}\", \"${plugin.version}\")"
            }
            "url" -> {
                "            url(\"${plugin.url}\")"
            }
            else -> { }
        }
    }.joinToString("\n")
}
        }"""
                    } else { "<empty>" }
                }"""
        }
        jvmArgs(${
            buildList {
                if (allowLegacyFormattingCheckBox.selected) {
                    add("\"-Dnet.kyori.adventure.text.warnWhenLegacyFormattingDetected=false\"")
                }
                if (debugToolsCheckBox.selected) {
                    add("\"-XX:+AllowEnhancedClassRedefinition\"")
                }
            }.joinToString(", ")
        })
    }"""
    } else { "<empty>" }
    }
    
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    
    configurations {
        create("compileOnlyApiResolved") {
            isCanBeResolved = true
            extendsFrom(configurations.getByName("compileOnlyApi"))
        }
    }

    val publicationShadowJar by registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        from(sourceSets.main.get().output)
        configurations = listOf(*configurations.toTypedArray(), project.configurations["compileOnlyApiResolved"])
        archiveClassifier.set("sticky")
        destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
    }

    build {
        dependsOn(shadowJar)
        dependsOn(publicationShadowJar)
    }
    ${
        if (processResources.selected) {
"""
    processResources {
        filesMatching(listOf("**plugin.yml", "**plugin.json")) {
            expand(
                "version" to project.version as String,
                "slug" to slug,
                "name" to rootProject.name,
                "description" to project.description
            )
        }
    }
}"""
        } else { "<empty>" }
    }
    ${
        if (addPluginYamlBukkitCheckBox.selected) {
"""
bukkit {
    main = "${'$'}group.${'$'}{slug}.${'$'}{rootProject.name}Plugin"
    version = rootProject.version.toString()
    ${
        if (websiteInput.field.text.isNotEmpty()) {
            "website = \"${websiteInput.field.text}\""
        } else { "<empty>" }
    }
    
    ${
        if (foliaSupportedCheckBox.selected) {
            "foliaSupported = true"
        } else { "<empty>" }
    }
    
    apiVersion = "1.13"
    
    depend = listOf()
    
    authors = listOf(${authorsInput.field.text.split(",").joinToString(", ") { "\"${it.trim()}\"" }})
    prefix = rootProject.name
}"""
        } else { "<empty>" }
    }
    
java {
    disableAutoTargetJvm()
    if (gradle.startParameter.getTaskNames().any { it.startsWith("runServer") || it.startsWith("runFolia") || it.startsWith("runVelocity") }) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(${runTaskJavaVersion.comboBox.selectedItem as String}))
    } else {
        toolchain.languageVersion.set(JavaLanguageVersion.of(${javaVersionDropDown.comboBox.selectedItem as String}))
    }
}""")
    }.toString().lines().filter { !it.contains("<empty>") }.joinToString("\n").trimIndent()
}