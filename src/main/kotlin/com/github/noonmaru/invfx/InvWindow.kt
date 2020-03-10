package com.github.noonmaru.invfx

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

interface InvWindow : InventoryHolder {
    fun onOpen(event: InventoryOpenEvent)
    fun onClose(event: InventoryCloseEvent)
    fun onClickOutside(event: InventoryClickEvent)
    fun onClickTop(event: InventoryClickEvent)
    fun onClickBottom(event: InventoryClickEvent)
    fun onDrag(event: InventoryDragEvent)
    fun onPickupItem(event: EntityPickupItemEvent)
    fun onDropItem(event: PlayerDropItemEvent)
}

open class InvStage(lines: Int, title: String) : InvWindow {
    @Suppress("LeakingThis")
    private val inv: Inventory = Bukkit.createInventory(this, lines * 9, title)

    final override fun getInventory(): Inventory {
        return inv
    }

    override fun onOpen(event: InventoryOpenEvent) {}
    override fun onClose(event: InventoryCloseEvent) {}
    override fun onClickOutside(event: InventoryClickEvent) {}
    override fun onClickTop(event: InventoryClickEvent) {}
    override fun onClickBottom(event: InventoryClickEvent) {}
    override fun onDrag(event: InventoryDragEvent) {}
    override fun onPickupItem(event: EntityPickupItemEvent) {}
    override fun onDropItem(event: PlayerDropItemEvent) {}
}

internal val Inventory.window: InvWindow?
    get() {
        return holder?.run {
            if (this is InvWindow) this else null
        }
    }

fun Player.openWindow(window: InvWindow): Boolean {
    return openInventory(window.inventory) != null
}