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

package com.github.noonmaru.invfx

import com.google.common.collect.ImmutableList
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack

internal abstract class InvNodeImpl(final override val x: Int, final override val y: Int) : InvNode

internal abstract class InvRegionImpl(
    override val scene: InvSceneImpl,
    x: Int,
    y: Int,
    override val width: Int, override val height: Int
) : InvNodeImpl(x, y), InvRegion {
    abstract fun onClick(x: Int, y: Int, event: InventoryClickEvent)

    open fun onOpen(event: InventoryOpenEvent) {}
}

internal class InvPaneImpl(scene: InvSceneImpl, x: Int, y: Int, width: Int, height: Int) :
    InvRegionImpl(scene, x, y, width, height), InvPane {
    override lateinit var buttons: List<InvButtonImpl>
        private set

    private lateinit var onClick: (InvPane, Int, Int, InventoryClickEvent) -> Unit

    fun initialize(builder: InvPaneBuilder) {
        this.onClick = builder.onClick
        this.buttons = ImmutableList.copyOf(builder.buttonBuilders.map { it.build() })

        builder.runCatching { onInit() }
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        runCatching { onClick(this, x, y, event) }

        buttonAt(x, y)?.runCatching { onClick(this, event) }
    }

    override fun buttonAt(x: Int, y: Int): InvButtonImpl? {
        return buttons.find { it.x == x && it.y == y }
    }
}

internal class InvButtonImpl(
    override val pane: InvPaneImpl,
    x: Int, y: Int
) : InvNodeImpl(x, y), InvButton {
    internal lateinit var onClick: (button: InvButton, event: InventoryClickEvent) -> Unit
        private set

    fun initialize(builder: InvButtonBuilder) {
        builder.item?.let { this.item = it }
        this.onClick = builder.onClick

        builder.runCatching { onInit() }
    }
}

internal class InvListViewImpl<T>(
    scene: InvSceneImpl,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : InvRegionImpl(scene, x, y, width, height), InvListView<T> {
    override var page: Int = 0
        set(value) {
            field = updatePage(value)
        }
    override lateinit var list: List<T>
    override val displayList = ArrayList<T>(size)
    private lateinit var transform: T.() -> ItemStack

    private lateinit var onUpdatePage: (InvListView<T>, Int, List<T>) -> Unit
    private lateinit var onClickItem: (listView: InvListView<T>, x: Int, y: Int, clicked: T, event: InventoryClickEvent) -> Unit

    fun initialize(builder: InvListViewBuilder<T>) {
        this.list = builder.list
        this.transform = builder.transform
        this.onUpdatePage = builder.onUpdatePage
        this.onClickItem = builder.onClickItem

        builder.runCatching { onInit() }
        refresh()
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        val index = x + y * width
        displayList.elementAtOrNull(index)?.runCatching {
            onClickItem(this@InvListViewImpl, x, y, this, event)
        }
    }

    private fun updatePage(page: Int): Int {
        val maxPage = list.count() / size
        val update = page.coerceIn(0, maxPage)
        val offset = update * size

        val list = this.list
        val displayList = this.displayList.apply {
            clear()
        }
        val transform = this.transform

        for (i in 0 until size) {
            val item = list.elementAtOrNull(offset + i)

            if (item != null)
                displayList.add(item)

            val x = i % width
            val y = i / width

            setItem(x, y, item?.transform())
        }

        runCatching { onUpdatePage(this, update, displayList) }

        return update
    }
}

internal class InvSceneImpl(line: Int, title: String) : InvStage(line, title), InvScene {
    private lateinit var onOpen: (scene: InvScene, event: InventoryOpenEvent) -> Unit
    private lateinit var onClose: (scene: InvScene, event: InventoryCloseEvent) -> Unit
    private lateinit var onClickBottom: (scene: InvScene, event: InventoryClickEvent) -> Unit

    override lateinit var regions: List<InvRegionImpl>

    fun initialize(builder: InvSceneBuilder) {
        onOpen = builder.onOpen
        onClose = builder.onClose
        onClickBottom = builder.onClickBottom
        regions = builder.regions.map { it.build() }

        builder.runCatching { onInit() }
    }

    override fun onOpen(event: InventoryOpenEvent) {
        runCatching { onOpen(this, event) }
        regions.forEach { it.onOpen(event) }
    }

    override fun onClose(event: InventoryCloseEvent) {
        runCatching { onClose(this, event) }
    }

    override fun onClickOutside(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    override fun regionAt(x: Int, y: Int): InvRegionImpl? {
        return regions.find { it.contains(x, y) }
    }

    override fun onClickTop(event: InventoryClickEvent) {
        event.isCancelled = true

        val slot = event.slot
        val x = slot % 9
        val y = slot / 9

        regionAt(x, y)?.let { region ->
            region.onClick(x - region.x, y - region.y, event)
        }
    }

    override fun onClickBottom(event: InventoryClickEvent) {
        event.isCancelled = true
        runCatching { onClickBottom(this, event) }
    }

    override fun onDrag(event: InventoryDragEvent) {
        event.isCancelled = true
    }

    override fun onPickupItem(event: EntityPickupItemEvent) {
        event.isCancelled = true
    }

    override fun onDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }
}