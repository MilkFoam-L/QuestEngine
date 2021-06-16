package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.location.LocationTool
import cn.inrhor.questengine.utlis.public.MsgUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * 全息对话管理
 */
class HoloDialog(
    var dialogModule: DialogModule,
    var npcLoc: Location,
    var viewers: MutableSet<Player>,
    var frame: Int) {

    constructor(dialogModule: DialogModule, npcLoc: Location, viewers: MutableSet<Player>) :
            this(dialogModule, npcLoc, viewers, 0)

    fun run() {
        var holoLoc = npcLoc
        var nextY = 0.0
        var textIndex = 0
        var itemIndex = 0
        for (i in dialogModule.dialog) {
            val iUc = i.uppercase(Locale.getDefault())
            when {
                iUc.startsWith("INITLOC") -> {
                    MsgUtil.send("iiiiwqeqwe  $i")
                    holoLoc = LocationTool().getFixedLoc(npcLoc, KetherHandler.evalFixedLoc(i))
                }
                iUc.startsWith("ADDLOC") -> {
                    MsgUtil.send("rrrr  $i")
                    holoLoc = LocationTool().getFixedLoc(holoLoc, KetherHandler.evalFixedLoc(i))
                }
                iUc.startsWith("NEXTY") -> {
                    MsgUtil.send("qweeee  $i")
                    val get = i.substring(0, iUc.indexOf(" "))
                    nextY = i.substring(get.length+1, i.length).toDouble()
                    holoLoc.add(0.0, nextY, 0.0)
                }
                iUc.startsWith("TEXT") -> {
                    val playText = dialogModule.playText[textIndex]
                    val holoID = playText.holoID
                    textIndex++
                    HoloDisplay.spawnAS(holoID, viewers, holoLoc)
                    HoloAnimationText(viewers, playText).runTask()
                }
            }
        }
    }
}