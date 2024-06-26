package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.api.*
import cn.inrhor.questengine.api.packet.PacketModule
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.library.configuration.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.console
import taboolib.module.lang.sendLang
import taboolib.platform.util.toBukkitLocation
import java.io.File

object PacketManager {

    val packetMap = mutableMapOf<String, PacketModule>()

    /**
     * type > packet
     */
    fun generate(packetID: String, type: String): Int {
        return "packet-$packetID-$type".hashCode()
    }

    fun register(packetID: String, packetModule: PacketModule) {
        packetMap[packetID] = packetModule
    }

    fun removeID(packetID: String) {
        packetMap.remove(packetID)
    }

    fun sendThisPacket(packetID: String, entityID: Int, sender: Player, location: Location) {
        val packetModule = packetMap[packetID]?: return
        val viewers = mutableSetOf(sender)
        if (packetModule.viewer == "all") viewers.addAll(Bukkit.getOnlinePlayers())
        val hook = packetModule.hook.lowercase()
        sendThisPacket(hook, entityID, packetModule, viewers, location)
    }

    fun sendThisPacket(packetID: String, sender: Player, location: Location) {
        val packetModule = packetMap[packetID]?: return
        val viewers = mutableSetOf(sender)
        if (packetModule.viewer == "all") viewers.addAll(Bukkit.getOnlinePlayers())
        val hook = packetModule.hook.lowercase()
        val entityID = packetModule.entityID
        sendThisPacket(hook, entityID, packetModule, viewers, location)
    }

    private fun sendThisPacket(hook: String, entityID: Int, packetModule: PacketModule, viewers: MutableSet<Player>, location: Location) {
        if (hook == "this ") {
            val id = hook[1].toString()
            val getPacketModule = packetMap[id] ?: return
            sendPacket(entityID, getPacketModule, viewers, location)
        } else if (hook == "normal") {
            sendPacket(entityID, packetModule, viewers, location)
        }
    }

    private fun sendPacket(entityID: Int, packetModule: PacketModule, viewers: MutableSet<Player>, location: Location) {
        spawnEntity(viewers, entityID, packetModule.entityType, location)
        packetModule.mate.forEach {
            val sp = it.split(" ")
            val sign = sp[0].lowercase()
            if (sign == "equip") {
                val slot = sp[1].uppercase()
                val itemID = sp[2]
                val item = ItemManager.get(itemID)
                updateEquipmentItem(viewers, entityID, EquipmentSlot.valueOf(slot), item)
            }else if (sign == "displayName") { // false 为 不显示
                val displayName = sp[1]
                updateDisplayName(viewers, entityID, displayName)
                val display = sp[2].toBoolean()
                setEntityCustomNameVisible(viewers, entityID, display)
            }else if (sign == "visible") {
                if (!sp[1].toBoolean()) {
                    isInvisible(viewers, entityID)
                }
            }
        }
    }

    /**
     * 需要在 packet 文件夹中构造数据包模块
     * packetID格式 packet-id-type[entity/item]
     * 将根据packetID检索id
     */
    fun sendPacket(packetID: String, sender: Player, location: taboolib.common.util.Location) {
        sendThisPacket(packetID, sender, location.toBukkitLocation())
    }

    fun sendPacket(entityID: Int, packetModule: PacketModule, sender: Player, location: taboolib.common.util.Location) {
        sendPacket(entityID, packetModule, mutableSetOf(sender), location.toBukkitLocation())
    }

    /**
     * 加载并注册数据包文件
     */
    fun loadPacket() {
        val packetFolder = GetFile.getFile("space/packet/example.yml", "PACKET-NO_FILES", true)
        GetFile.getFileList(packetFolder).forEach{
            checkRegPacket(it)
        }
    }

    /**
     * 检查和注册数据包
     */
    private fun checkRegPacket(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("PACKET-EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        for (packetID in yaml.getKeys(false)) {
            PacketFile.init(yaml.getConfigurationSection(packetID))
        }
    }

    fun returnItemEntityID(packetID: String, mate: MutableList<String>): MutableMap<String, Int> {
        val itemEntityID = mutableMapOf<String, Int>()

        mate.forEach {
            if (it.lowercase().startsWith("equip ")) {
                val sp = it.split(" ")
                val itemID = sp[2]
                val entityID = generate(packetID, itemID)
                itemEntityID[itemID] = entityID
            }
        }

        return itemEntityID
    }

}