/*
 * Copyright (c) 2020 Noonmaru
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.monun.invfx.plugin

import io.github.monun.invfx.window
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent

class InvListener : Listener {
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