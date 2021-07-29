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

import io.github.monun.invfx.frame.InvRegion
import io.github.monun.invfx.util.getValue
import io.github.monun.invfx.util.lazyVal
import io.github.monun.invfx.util.setValue
import org.bukkit.event.inventory.InventoryClickEvent

abstract class AbstractInvRegion(
    final override val x: Int,
    final override val y: Int,
    final override val width: Int,
    final override val height: Int
) : InvRegion {
    abstract fun onClick(x: Int, y: Int, event: InventoryClickEvent)
}
