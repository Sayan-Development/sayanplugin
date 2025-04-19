package %package%

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.sayandev.stickynote.bukkit.StickyNote

class PAPIExpansion: PlaceholderExpansion() {

    override fun getIdentifier(): String {
        return StickyNote.plugin().name
    }

    override fun getAuthor(): String {
        return StickyNote.plugin().description.authors.joinToString(", ")
    }

    override fun getVersion(): String {
        return StickyNote.plugin().description.version
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer, params: String): String? {
        return null
    }

}