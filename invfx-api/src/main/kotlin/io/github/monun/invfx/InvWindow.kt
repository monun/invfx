/*
 * InvFX
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.monun.invfx

import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.InventoryHolder

interface InvWindow: InventoryHolder {
    fun onOpen(event: InventoryOpenEvent)

    fun onClose(event: InventoryCloseEvent)

    fun onClick(event: InventoryClickEvent)

    fun onClickBottom(event: InventoryClickEvent)

    fun onClickOutside(event: InventoryClickEvent)

    fun onDrag(event: InventoryDragEvent)

    fun onPickupItem(event: EntityPickupItemEvent)

    fun onDropItem(event: PlayerDropItemEvent)
}