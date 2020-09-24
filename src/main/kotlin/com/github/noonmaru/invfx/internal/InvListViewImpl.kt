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

package com.github.noonmaru.invfx.internal

import com.github.noonmaru.invfx.InvListView
import com.github.noonmaru.invfx.builder.InvListViewBuilder
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max

internal class InvListViewImpl<T>(
    scene: InvSceneImpl,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    override val trim: Boolean
) : InvRegionImpl(scene, x, y, width, height), InvListView<T> {
    override var index: Int = 0
        set(value) {
            field = updateItems(value)
        }
    override lateinit var items: List<T>
    override val displayItems = ArrayList<T>(size)
    private lateinit var transform: T.() -> ItemStack

    private lateinit var onUpdateItems: (InvListView<T>, Int, List<T>) -> Unit
    private lateinit var onClickItem: (listView: InvListView<T>, x: Int, y: Int, clicked: T, event: InventoryClickEvent) -> Unit

    fun initialize(builder: InvListViewBuilder<T>) {
        this.items = builder.list
        this.transform = builder.transform
        this.onUpdateItems = builder.onUpdateItems
        this.onClickItem = builder.onClickItem

        builder.runCatching { onInit() }
        refresh()
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        val index = x + y * width
        displayItems.elementAtOrNull(index)?.runCatching {
            onClickItem(this@InvListViewImpl, x, y, this, event)
        }
    }

    private fun updateItems(updateIndex: Int): Int {
        val items = items
        val itemsCount = items.count()
        val width = width
        val size = size

        val offsetIndex = updateIndex.coerceIn(0, max(0, if (trim) itemsCount - size else itemsCount - 1))
//            if (trim) {
//            updateIndex.coerceIn(0, itemsCount - size)
//        } else {
//            updateIndex.coerceIn(0, itemsCount - 1)
//        }

        val displayItems = displayItems.apply { clear() }

        for (i in 0 until size) {
            val item = items.elementAtOrNull(offsetIndex + i)

            if (item != null)
                displayItems.add(item)

            val x = i % width
            val y = i / width

            setItem(x, y, item?.transform())
        }

        runCatching { onUpdateItems(this, offsetIndex, displayItems) }

        return offsetIndex
    }
}