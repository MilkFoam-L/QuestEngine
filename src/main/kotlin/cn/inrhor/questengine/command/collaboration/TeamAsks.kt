package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.CommandBody
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.subCommand
import taboolib.platform.util.sendLang

object TeamAsks {

    @CommandBody
    val asks = subCommand {
        literal("team") {
            literal("asks") {
                literal("open") {
                    dynamic {
                        execute<ProxyPlayer> { sender, _, _ ->
                            val player = sender as Player
                            val pUUID = player.uniqueId
                            val tData = TeamManager.getTeamData(pUUID)?: return@execute run {
                                sender.sendLang("TEAM.NO_TEAM") }
                            if (!TeamManager.isLeader(pUUID, tData)) return@execute run {
                                sender.sendLang("TEAM.NOT_LEADER") }
                            HasTeam.openAsks(player)
                        }
                    }
                }
                literal("agree") {
                    dynamic {
                        execute<ProxyPlayer> { sender, context, _ ->
                            val args = context.args
                            val player = sender as Player
                            manager(player, args)
                            return@execute
                        }
                    }
                }
                literal("reject") {
                    dynamic {
                        execute<ProxyPlayer> { sender, context, _ ->
                            val args = context.args
                            val player = sender as Player
                            manager(player, args, false)
                            return@execute
                        }
                    }
                }
            }
        }
    }

    private fun manager(player: Player, args: Array<String>, agree: Boolean = true) {
        val pUUID = player.uniqueId
        val tData = TeamManager.getTeamData(pUUID)?: return run {
            player.sendLang("TEAM.NO_TEAM") }
        if (!TeamManager.isLeader(pUUID, tData)) return run {
            player.sendLang("TEAM.NOT_LEADER") }
        val mName = args[0]
        val m = Bukkit.getPlayer(mName)?: return run {
            player.sendLang("PLAYER_NOT_ONLINE") }
        val mUUID = m.uniqueId
        TeamManager.removeAsk(mUUID, tData)
        if (agree) {
            TeamManager.addMember(mUUID, tData)
        }
    }

}