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
import com.google.common.collect.ImmutableList
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

    private lateinit var transformer: (T) -> ItemStack

    private lateinit var updateItemsActions: List<(InvListView<T>, Int, List<T>) -> Unit>

    private lateinit var clickItemActions: List<(InvListView<T>, Int, Int, T, InventoryClickEvent) -> Unit>

    fun initialize(builder: InvListViewBuilder<T>) {
        this.items = builder.items
        this.transformer = builder.transformer
        this.updateItemsActions = ImmutableList.copyOf(builder.updateItemsActions)
        this.clickItemActions = ImmutableList.copyOf(builder.clickItemAction)
        builder.initActions.forEach { it.runCatching { invoke(this@InvListViewImpl) } }
        refresh()
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        val index = x + y * width
        displayItems.elementAtOrNull(index)?.let { item ->
            clickItemActions.forEachInvokeSafety { it(this, x, y, item, event) }
        }
    }

    private fun updateItems(updateIndex: Int): Int {
        val items = items
        val itemsCount = items.count()
        val width = width
        val size = size
        val offsetIndex = updateIndex.coerceIn(0, max(0, if (trim) itemsCount - size else itemsCount - 1))
        val displayItems = displayItems.apply { clear() }

        for (i in 0 until size) {
            val item = items.elementAtOrNull(offsetIndex + i)

            if (item != null)
                displayItems.add(item)

            val x = i % width
            val y = i / width

            setItem(x, y, item?.let(transformer))
        }

        updateItemsActions.forEachInvokeSafety { it(this, offsetIndex, displayItems) }
        return offsetIndex
    }
}