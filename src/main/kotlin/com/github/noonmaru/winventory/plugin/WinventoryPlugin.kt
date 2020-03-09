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

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.openInventory.topInventory.window?.run { player.closeInventory(InventoryCloseEvent.Reason.PLUGIN) }
        }
    }

    //    debug codes
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender as Player

        val list = mutableListOf<String>()

        for (i in 1..100) {
            list += i.toString()
        }

        val window = frame(6, "TEST") {
            addListView<String>(0, 0, 9, 6) {
                setList(1, 0, 3, 3, { list }, {
                    ItemStack(Material.STICK).apply {
                        itemMeta = itemMeta.apply { setDisplayName(it) }
                    }
                }) {
                    onClickItem = { item, _ ->
                        println("CLICK ITEM $item")
                    }
                }

                addButton(0, 0) {
                    onClick = { invPanel, _, _ ->
                        invPanel as InvListView
                        invPanel.page++
                        println("PAGE UP")
                    }
                }
                addButton(0, 1) {
                    onClick = { invPanel, invButton, inventoryClickEvent ->
                        println("PAGE DOWN")
                        invPanel as InvListView
                        invPanel.page--
                    }
                }
            }
        }

        sender.openWindow(window)

        return true
    }
}