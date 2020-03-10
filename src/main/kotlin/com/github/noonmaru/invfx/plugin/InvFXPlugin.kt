package com.github.noonmaru.invfx.plugin

import com.github.noonmaru.invfx.*
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

        val list = mutableListOf<Int>()

        for (i in 0..100) {
            list += i
        }

        val scene = invScene(6, "Test") {
            addPanel(0, 0, 3, 3) {
                onInit = {
                    println("Panel INIT")
                }
                onClick = { _, x, y, _ ->
                    println("Panel CLICK $x $y")
                }
                addButton(1, 1) {
                    onInit = {
                        item = ItemStack(Material.STONE)
                        println("Button INIT")
                    }
                    onClick = { _, _ ->
                        println("Button CLICK")
                    }
                }
            }

            var listView: InvListView? = null

            addListView(3, 0, 3, 3, list) {
                onInit = {
                    println("ListView INIT")
                    listView = this
                }
                onClickItem = { _, x, y, clicked, _ ->
                    println("ListView CLICK $x $y $clicked")
                }
            }
            addPanel(6, 0, 3, 3) {
                addButton(0, 0) {
                    onInit = {
                        item = ItemStack(Material.STICK)
                    }
                    onClick = { _, _ ->
                        listView!!.next()
                    }
                }
                addButton(1, 0) {
                    onInit = {
                        item = ItemStack(Material.STICK)
                    }
                    onClick = { _, _ ->
                        listView!!.previous()
                    }
                }
            }
        }

        sender.openWindow(scene)

        return true
    }
}