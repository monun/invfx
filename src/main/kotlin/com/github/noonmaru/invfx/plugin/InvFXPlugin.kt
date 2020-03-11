package com.github.noonmaru.invfx.plugin

import com.github.noonmaru.invfx.InventoryListener
import com.github.noonmaru.invfx.invScene
import com.github.noonmaru.invfx.openWindow
import com.github.noonmaru.invfx.window
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
class InvFXPlugin : JavaPlugin() {
    override fun onEnable() {
        server.apply {
            pluginManager.registerEvents(InventoryListener(), this@InvFXPlugin)
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



        val scene = invScene(6, "Test") {
            val list = mutableListOf<Int>()

            for (i in 0..100) {
                list += i
            }

            val listView = addListView(0, 0, 9, 5, list) {
                onInit = {
                    println("ListView INIT")
                }
                onUpdatePage = { listView, page, displayList ->
                    println("ListView UPDATE PAGE $page ${displayList.joinToString()}")
                }
                onClickItem = { listView, x, y, clicked, event ->
                    println("ListView CLICK $x $y $clicked")
                }
            }
            addPanel(0, 5, 9, 1) {
                onInit = {
                    println("Panel INIT")
                }
                onClick = { pane, x, y, event ->
                    println("Panel CLICK")
                }
                addButton(3, 0) {
                    item = ItemStack(Material.STONE)
                    onInit = {
                        println("Button(PREV) INIT")
                    }
                    onClick = { button, event ->
                        println("Button(PREV) CLICK")
                        listView.previous()
                    }
                }
                addButton(5, 0) {
                    item = ItemStack(Material.GRASS_BLOCK)
                    onInit = {
                        println("Button(NEXT) INIT")
                    }
                    onClick = { button, event ->
                        println("Button(NEXT) CLICK")
                        listView.next()
                    }
                }
            }
        }

        sender.openWindow(scene)

        return true
    }
}