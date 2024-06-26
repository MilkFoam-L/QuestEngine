package cn.inrhor.questengine.utlis

import cn.inrhor.questengine.QuestEngine
import java.util.*

object UtilString {

    fun updateLang(): MutableList<String> = QuestEngine.config.getStringList("update.lang")

    const val pluginTag = "&7&l[ &c&li &7&l]&7&l[ &3&lQuestEngine &7&l]"

    fun getJsonStr(list: MutableList<String>): String {
        var content = ""
        list.forEach {
            if (content.isEmpty()) {
                content = it
                return@forEach
            }
            content = "$content&r\n$it"
        }
        return content
    }
}

/**
 * 截取特殊字符之后的字符串
 */
fun String.subAfter(meta: String): String {
    return this.substring(this.indexOf(meta)+1)
}