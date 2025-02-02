package %package%

import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.pluginDirectory
import org.sayandev.stickynote.core.configuration.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.io.File

@ConfigSerializable
data class LanguageConfig(
    val general: General = General(),
) : Config(languagesDirectory, "${SettingsConfig.get().general.language}.yml") {
    @ConfigSerializable
    data class General(
        val reloaded: String = "<green>Plugin successfully reloaded. <red>Please note that some changes may require a server restart to take effect. Subsequent reloads may cause issues.",
    )

    enum class Language(val id: String) {
        EN_US("en_US"),
    }

    companion object {
        private val languagesDirectory = File(pluginDirectory, "languages").apply {
            if (!this.exists()) {
                this.mkdirs()
            }
        }
        lateinit var config: LanguageConfig

        @JvmStatic
        fun get(): LanguageConfig {
            if (!::config.isInitialized) {
                reload()
            }
            return config
        }

        fun reload() {
            config = getOrDefault()
        }

        @JvmStatic
        private fun defaultConfig(): LanguageConfig {
            return LanguageConfig().also { config ->
                StickyNote.log("generating ${config.file.name} configuration...")
                config.save()
            }
        }

        @JvmStatic
        private fun fromConfig(): LanguageConfig? {
            return fromConfig<LanguageConfig>(File(languagesDirectory, "${SettingsConfig.get().general.language}.yml"))
        }

        @JvmStatic
        private fun getOrDefault(): LanguageConfig {
            return fromConfig() ?: defaultConfig()
        }
    }
}
