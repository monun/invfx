package com.github.noonmaru.winventory

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

open class InvWindow(lines: Int, title: String) {
    val inventory: Inventory = Bukkit.createInventory(Holder(), lines * 9, title)

    inner class Holder : InventoryHolder {
        val window: InvWindow = this@InvWindow

        override fun getInventory(): Inventory {
            return this@InvWindow.inventory
        }
    }

    open fun onOpen(event: InventoryOpenEvent) {}

    open fun onClose(event: InventoryCloseEvent) {}

    open fun onClickOutside(event: InventoryClickEvent) {}

    open fun onClickTop(event: InventoryClickEvent) {}

    open fun onClickBottom(event: InventoryClickEvent) {}

    open fun onDrag(event: InventoryDragEvent) {}

    open fun onPickupItem(event: EntityPickupItemEvent) {}

    open fun onDropItem(event: PlayerDropItemEvent) {}
}

internal val Inventory.window: InvWindow?
    get() {
        holder?.let {
            if (it is InvWindow.Holder) return it.window
        }
        return null


    }

fun Player.openWindow(window: InvWindow): Boolean {
    return openInventory(window.inventory) != null
}