package %package%

import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.pluginDirectory
import org.sayandev.stickynote.core.configuration.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.io.File

@ConfigSerializable
data class SettingsConfig(
    val general: General = General(),
) : Config(pluginDirectory, FILE_NAME) {
    @ConfigSerializable
    data class General(
        val language: String = LanguageConfig.Language.EN_US.id,
    )

    companion object {
        private const val FILE_NAME = "settings.yml"

        lateinit var config: SettingsConfig

        @JvmStatic
        fun get(): SettingsConfig {
            if (!::config.isInitialized) {
                reload()
            }
            return config
        }

        @JvmStatic
        fun defaultConfig(): SettingsConfig {
            return SettingsConfig().also { config ->
                StickyNote.log("generating $FILE_NAME configuration...")
                config.save()
            }
        }

        @JvmStatic
        fun fromConfig(): SettingsConfig? {
            return fromConfig<SettingsConfig>(File(pluginDirectory, FILE_NAME))
        }

        fun reload() {
            config = fromConfig() ?: defaultConfig()
        }
    }
}
