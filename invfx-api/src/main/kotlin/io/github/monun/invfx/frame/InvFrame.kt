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

package io.github.monun.invfx.frame

import io.github.monun.invfx.InvDSL
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

@InvDSL
interface InvFrame:  InvSpace {
    fun slot(x: Int, y: Int, init: InvSlot.() -> Unit): InvSlot

    fun pane(x: Int, y: Int, width: Int, height: Int, init: InvPane.() -> Unit): InvPane

    fun <T> list(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        trim: Boolean,
        item: () -> List<T>,
        init: (InvList<T>.() -> Unit)? = null
    ): InvList<T>

    fun onOpen(onOpen: (InventoryOpenEvent) -> Unit)

    fun onClose(onClose: (InventoryCloseEvent) -> Unit)

    fun onClickBottom(onClickBottom: (InventoryClickEvent) -> Unit)

    fun onClickOutside(onClickOutside: (InventoryClickEvent) -> Unit)
}