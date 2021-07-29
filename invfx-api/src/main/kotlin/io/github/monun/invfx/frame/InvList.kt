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
import org.bukkit.inventory.ItemStack
import kotlin.math.max

@InvDSL
interface InvList<T>: InvRegion {
    var index: Int

    var page: Double

    val displays: List<Pair<T, ItemStack>>

    fun onClickItem(onClickItem: (x: Int, y: Int, item: Pair<T, ItemStack>, event: InventoryClickEvent) -> Unit)

    fun onUpdate(onUpdate: (list: List<Pair<T, ItemStack>>, index: Int) -> Unit)

    fun transform(transform: (T) -> ItemStack)

    fun refresh() {
        index = index
    }

    fun first() {
        index = 0
    }

    fun last() {
        index = Int.MAX_VALUE
    }
}