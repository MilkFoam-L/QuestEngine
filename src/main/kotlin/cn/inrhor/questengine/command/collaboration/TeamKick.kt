package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamKick {

    @CommandBody
    val kick = subCommand {
        literal("team") {
            literal("kick") {
                dynamic {
                    suggestion<ProxyPlayer> { sender, _ ->
                        TeamManager.getTeamData((sender as Player))?.members?.map {
                            (Bukkit.getPlayer(it))?.name.toString() }
                    }
                    execute<ProxyPlayer> { sender, context, _ ->
                        val args = context.args
                        val player = sender as Player
                        val pUUID = player.uniqueId
                        val teamData = TeamManager.getTeamData(pUUID)?: return@execute run {
                            player.sendLang("TEAM.NO_TEAM") }
                        if (!TeamManager.isLeader(pUUID, teamData)) return@execute run {
                            player.sendLang("TEAM.NOT_LEADER") }
                        val mName = args[0]
                        val m = Bukkit.getPlayer(mName)?: return@execute run {
                            player.sendLang("PLAYER_NOT_ONLINE") }
                        val mUUID = m.uniqueId
                        TeamManager.removeMember(mUUID, teamData)
                    }
                }
            }
        }
    }


}