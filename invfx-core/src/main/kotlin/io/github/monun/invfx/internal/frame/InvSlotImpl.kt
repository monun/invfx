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

package io.github.monun.invfx.internal.frame

import io.github.monun.invfx.frame.InvSlot
import io.github.monun.invfx.util.getValue
import io.github.monun.invfx.util.lazyVal
import io.github.monun.invfx.util.setValue
import io.github.monun.invfx.util.weak
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class InvSlotImpl(
    frame: InvFrameImpl,
    override val x: Int,
    override val y: Int
) : InvSlot {
    private val frame by weak(frame)

    internal var onClick: ((InventoryClickEvent) -> Unit)? by lazyVal()
        private set

    override var item: ItemStack?
        get() = frame.item(x, y)
        set(value) {
            frame.item(x, y, value)
        }

    override fun onClick(onClick: (InventoryClickEvent) -> Unit) {
        this.onClick = onClick
    }
}