package com.github.noonmaru.winventory.plugin

import com.github.noonmaru.winventory.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
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

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender as Player

        val window = frame(5, "test") {
            panel(0, 0, 2, 2) {
                onClick {
                    sender.sendMessage("FIRST PANEL")
                }

                button(1, 1, ItemStack(Material.STICK)) {
                    onClick {
                        sender.sendMessage("FIRST BUTTON")
                    }
                }
            }
            var button: InvButton? = null

            panel(2, 2, 3, 3) {
                onClick {
                    sender.sendMessage("SECOND PANEL")
                }
                button(1, 1, ItemStack(Material.STONE)) {
                    receive {
                        button = it
                    }
                    onClick {
                        sender.sendMessage("SECOND BUTTON")
                        button!!.item = ItemStack(Material.values().random())
                    }
                }
            }
        }

        sender.openWindow(window)
        return true
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.openInventory.topInventory.window?.run { player.closeInventory(InventoryCloseEvent.Reason.PLUGIN) }
        }
    }
}