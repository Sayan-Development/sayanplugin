package %package%

import org.incendo.cloud.kotlin.MutableCommandBuilder
import org.incendo.cloud.parser.standard.StringParser
import org.sayandev.stickynote.bukkit.command.BukkitCommand
import org.sayandev.stickynote.bukkit.command.BukkitSender
import org.sayandev.stickynote.bukkit.command.literalWithPermission
import org.sayandev.stickynote.bukkit.command.required
import org.sayandev.stickynote.bukkit.extension.sendComponent
import org.sayandev.stickynote.bukkit.plugin

object %plugin_name%Command : BukkitCommand(plugin.name.lowercase()) {

    init {
        rawCommandBuilder().registerCopy {
            literalWithPermission("foo")
            required("bar", StringParser.stringParser())
            handler { context ->
                val sender = context.sender().platformSender()
                val bar = context.get<String>("bar")
                sender.sendComponent("<rainbow>Little literal for bar $bar :)")
            }
        }
    }
}
