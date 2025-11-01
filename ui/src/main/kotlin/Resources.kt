package cz.pocolor.game.ui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Text(val appName: String)

object Resource {
    var text: Text

    init {
        val textResource = ::javaClass.javaClass.classLoader.getResource("text.json")
        text = Json.decodeFromString<Text>(textResource!!.readText())
    }
}
