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

import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.frame.InvList
import io.github.monun.invfx.util.getValue
import io.github.monun.invfx.util.lazyVal
import io.github.monun.invfx.util.setValue
import io.github.monun.invfx.util.weak
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max

class InvListImpl<T>(
    frame: InvFrame,
    minX: Int,
    minY: Int,
    maxX: Int,
    maxY: Int,
    private val trim: Boolean,
    private val items: () -> List<T>
) : AbstractInvRegion(minX, minY, maxX, maxY), InvList<T> {
    private val frame by weak(frame)

    override var index = 0
        set(value) {
            field = updateItems(value)
        }

    override var page: Double
        get() = index.toDouble() / size.toDouble()
        set(value) {
            val count = items().count()
            val size = size
            val lastPage = max(0, (count - 1)) / size
            val newPage = value.coerceIn(0.0, lastPage.toDouble())
            index = (newPage * size).toInt()
        }

    override val displays = ArrayList<Pair<T, ItemStack>>(size)

    private var onClickItem: ((x: Int, y: Int, item: Pair<T, ItemStack>, event: InventoryClickEvent) -> Unit)? by lazyVal()

    private var onUpdate: ((List<Pair<T, ItemStack>>, Int) -> Unit)? by lazyVal()

    private var transform: ((T) -> ItemStack)? by lazyVal()

    override fun onClickItem(onClickItem: (x: Int, y: Int, item: Pair<T, ItemStack>, event: InventoryClickEvent) -> Unit) {
        this.onClickItem = onClickItem
    }

    override fun onUpdate(onUpdate: (list: List<Pair<T, ItemStack>>, index: Int) -> Unit) {
        this.onUpdate = onUpdate
    }

    override fun transform(transform: (T) -> ItemStack) {
        this.transform = transform
    }

    private fun updateItems(updateIndex: Int): Int {
        val frame = frame
        val transform = transform
        val items = items()
        val itemsCount = items.count()
        val width = width
        val size = size
        val offsetIndex = updateIndex.coerceIn(0, max(0, if (trim) itemsCount - size else itemsCount - 1))
        val displays = displays.apply { clear() }

        for (i in 0 until size) {
            val item = items.getOrNull(offsetIndex + i)
            var display: ItemStack? = null

            if (item != null) {
                display = transform?.invoke(item) ?: item as ItemStack
                displays += item to display
            }

            val x = i % width
            val y = i / width

            frame.item(this.minX + x, this.minY + y, display)
        }

        onUpdate?.invoke(displays, offsetIndex)

        return offsetIndex
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        onClickItem?.runCatching {
            displays.getOrNull(x + y * width)?.let { item ->
                invoke(x, y, item, event)
            }
        }
    }
}