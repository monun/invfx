/*
 *
 *  * Copyright (c) 2020 Noonmaru
 *  *
 *  * Licensed under the General Public License, Version 3.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * https://opensource.org/licenses/gpl-3.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package com.github.noonmaru.invfx

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

abstract class InvRegionBuilder internal constructor() {

    internal abstract val instance: InvRegionImpl

    internal abstract fun build(): InvRegionImpl
}

class InvPaneBuilder internal constructor(scene: InvSceneImpl, x: Int, y: Int, width: Int, height: Int) :
    InvRegionBuilder() {
    internal val buttonBuilders = ArrayList<InvButtonBuilder>(0)

    var onInit: InvPane.() -> Unit = { }
    var onClick: (pane: InvPane, x: Int, y: Int, event: InventoryClickEvent) -> Unit = { _, _, _, _ -> }

    override val instance: InvPaneImpl = InvPaneImpl(scene, x, y, width, height)

    fun addButton(x: Int, y: Int, init: (InvButtonBuilder.() -> Unit)? = null): InvButton {
        instance.let {
            require(x in 0 until it.width && y in 0 until it.height) { "Out of range  args=(x=$x y=$y) region=${it.regionString})" }
        }

        return InvButtonBuilder(instance, x, y).apply {
            init?.invoke(this)
            buttonBuilders += this
        }.instance
    }

    override fun build(): InvPaneImpl {
        return instance.apply {
            initialize(this@InvPaneBuilder)
        }
    }
}

class InvButtonBuilder internal constructor(pane: InvPaneImpl, x: Int, y: Int) {
    var item: ItemStack? = null
    var onInit: InvButton.() -> Unit = { }
    var onClick: (button: InvButton, event: InventoryClickEvent) -> Unit = { _, _ -> }

    internal val instance: InvButtonImpl = InvButtonImpl(pane, x, y)

    internal fun build(): InvButtonImpl {
        return instance.apply {
            initialize(this@InvButtonBuilder)
        }
    }
}

class InvListViewBuilder<T> internal constructor(
    scene: InvSceneImpl,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    internal val list: List<T>
) : InvRegionBuilder() {
    var onInit: InvListView<T>.() -> Unit = { }
    var onUpdatePage: (listView: InvListView<T>, page: Int, displayList: List<T>) -> Unit = { _, _, _ -> }
    var onClickItem: (listView: InvListView<T>, x: Int, y: Int, clicked: T, event: InventoryClickEvent) -> Unit =
        { _, _, _, _, _ -> }
    val transform: T.() -> ItemStack = {
        if (this is ItemStack) this
        else {
            val toString = toString()
            ItemStack(Material.BOOK).apply {
                itemMeta = itemMeta.apply {
                    setDisplayName(toString)
                }
            }
        }
    }

    override val instance: InvListViewImpl<T> = InvListViewImpl(scene, x, y, width, height)

    override fun build(): InvRegionImpl {
        return instance.apply {
            initialize(this@InvListViewBuilder)
        }
    }
}

class InvSceneBuilder internal constructor(private val line: Int, title: String) {
    internal val regions = ArrayList<InvRegionBuilder>(0)

    var onInit: InvScene.() -> Unit = { }
    var onOpen: (scene: InvScene, event: InventoryOpenEvent) -> Unit = { _, _ -> }
    var onClose: (scene: InvScene, event: InventoryCloseEvent) -> Unit = { _, _ -> }
    var onClickBottom: (scene: InvScene, event: InventoryClickEvent) -> Unit = { _, _ -> }

    private val instance: InvSceneImpl = InvSceneImpl(line, title)

    private fun checkRegion(x: Int, y: Int, width: Int, height: Int) {
        require(x in 0..8) { "X must be between 0 and 8 ($x)" }
        require(y in 0..5) { "Y must be between 0 and 5 ($y)" }
        require(width in 1..9) { "Width must be between 1 and 9 ($width)" }
        require(height in 1..line) { "Height must be between 1 and $line ($height)" }

        val maxX = x + width - 1
        val maxY = y + height - 1

        require(maxX in x until 9 && maxY in y until line) { "Out of range args(x=$x, y=$y, width=$width, height=$height)" }
        regions.find { it.instance.overlaps(x, y, maxX, maxY) }?.let {
            throw IllegalArgumentException("Overlaps with other region  args=[$x, $y - $maxX, $maxY] overlaps=${it.instance.regionString}")
        }
    }

    fun addPanel(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        init: (InvPaneBuilder.() -> Unit)? = null
    ): InvPane {
        checkRegion(x, y, width, height)

        return InvPaneBuilder(instance, x, y, width, height).apply {
            init?.invoke(this)
            regions += this
        }.instance
    }

    fun <T> addListView(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        list: List<T>,
        init: (InvListViewBuilder<T>.() -> Unit)? = null
    ): InvListView<T> {
        checkRegion(x, y, width, height)

        return InvListViewBuilder(instance, x, y, width, height, list).apply {
            init?.invoke(this)
            regions += this
        }.instance
    }

    internal fun build(): InvScene {
        return instance.apply {
            initialize(this@InvSceneBuilder)
        }
    }
}

private val InvRegionImpl.regionString: String
    get() {
        return "[$minX, $minY - $maxX, $maxY]"
    }

fun invScene(line: Int, title: String, init: InvSceneBuilder.() -> Unit): InvScene {
    return InvSceneBuilder(line, title).apply(init).build()
}