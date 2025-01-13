package org.sayandev.sayanplugin.element.elements

data class Plugin(
    val type: String,
    val name: String = "",
    val version: String = "",
    val url: String = ""
) {
    override fun toString(): String {
        return if (name == "") {
            "[${type.uppercase()}] $url"
        } else {
            "[${type.uppercase()}] $name version $version"
        }
    }
}