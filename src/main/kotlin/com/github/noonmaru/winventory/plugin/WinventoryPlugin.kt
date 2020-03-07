package com.github.noonmaru.winventory.plugin

import com.github.noonmaru.winventory.InventoryListener
import com.github.noonmaru.winventory.window
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nemo
 */
class WinventoryPlugin : JavaPlugin() {
    override fun onEnable() {
        server.apply {
            pluginManager.registerEvents(InventoryListener(), this@WinventoryPlugin)
        }
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.openInventory.topInventory.window?.run { player.closeInventory(InventoryCloseEvent.Reason.PLUGIN) }
        }
    }

    //debug codes
//    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
//        sender as Player
//
//        sender.openWindow(frame(5, "test") {
//            onInitPanel { sender.sendMessage("INIT") }
//            onOpen { sender.sendMessage("OPEN") }
//            onClose { sender.sendMessage("CLOSE") }
//            onClickBottom { sender.sendMessage("CLICK BOTTOM") }
//
//            var button: InvButton? = null
//
//            panel(0, 0, 3, 3) {
//                onInitPanel { sender.sendMessage("INIT PANEL") }
//                onClickPanel { sender.sendMessage("CLICK PANEL") }
//                button(1, 1, ItemStack(Material.STICK)) {
//                    onInitButton {
//                        button = it
//                    }
//
//                    onClickPanel {
//                        if (it != button)
//                            button?.item = ItemStack(Material.values().random())
//                    }
//                }
//            }
//        })
//        return true
//    }
}