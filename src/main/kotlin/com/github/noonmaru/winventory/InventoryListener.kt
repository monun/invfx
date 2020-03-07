package com.github.noonmaru.winventory

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent

class InventoryListener : Listener {
    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        event.inventory.window?.onOpen(event)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        event.inventory.window?.onClose(event)
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        event.inventory.window?.run {
            val slot = event.rawSlot

            when {
                slot < 0 -> {
                    onClickOutside(event)
                }
                slot < inventory.size -> {
                    onClickTop(event)
                }
                else -> {
                    onClickBottom(event)
                }
            }
        }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        event.inventory.window?.onDrag(event)
    }

    @EventHandler
    fun onPickupItem(event: EntityPickupItemEvent) {
        val entity = event.entity

        if (entity is Player) {
            entity.openInventory.topInventory.window?.onPickupItem(event)
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        event.player.openInventory.topInventory.window?.onDropItem(event)
    }
}