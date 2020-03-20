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

/**
 * [InvRegion]를 사전설정 할 수 있는 클래스
 */
abstract class InvRegionBuilder internal constructor() {

    internal abstract val instance: InvRegionImpl

    internal abstract fun build(): InvRegionImpl
}

/**
 * [InvPane]을 사전설정 할 수 있는 클래스
 */
class InvPaneBuilder internal constructor(scene: InvSceneImpl, x: Int, y: Int, width: Int, height: Int) :
    InvRegionBuilder() {
    internal val buttonBuilders = ArrayList<InvButtonBuilder>(0)

    /**
     * [InvPane]을 초기화할 때 호출
     */
    var onInit: InvPane.() -> Unit = { }

    /**
     * [org.bukkit.entity.Player]가 구역 내의 슬롯을 클릭할 때 호출됩니다.
     */
    var onClick: (pane: InvPane, x: Int, y: Int, event: InventoryClickEvent) -> Unit = { _, _, _, _ -> }

    override val instance: InvPaneImpl = InvPaneImpl(scene, x, y, width, height)

    /**
     * [InvButton]을 추가합니다.
     */
    fun button(x: Int, y: Int, init: (InvButtonBuilder.() -> Unit)? = null): InvButton {
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

/**
 * [InvButton]을 사전설정할 수 있는 클래스
 */
class InvButtonBuilder internal constructor(pane: InvPaneImpl, x: Int, y: Int) {
    /**
     * [InvButton] 슬롯에 초기화될 아이템
     */
    var item: ItemStack? = null

    /**
     * [InvButton]이 초기화될 때 호출
     */
    var onInit: InvButton.() -> Unit = { }

    /**
     * [org.bukkit.entity.Player]가 클릭할 때 호출
     */
    var onClick: (button: InvButton, event: InventoryClickEvent) -> Unit = { _, _ -> }

    internal val instance: InvButtonImpl = InvButtonImpl(pane, x, y)

    internal fun build(): InvButtonImpl {
        return instance.apply {
            initialize(this@InvButtonBuilder)
        }
    }
}

/**
 * [InvListView]를 사전설정 할 수 있는 클래스
 */
class InvListViewBuilder<T> internal constructor(
    scene: InvSceneImpl,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    internal val list: List<T>
) : InvRegionBuilder() {
    /**
     * [InvListView]가 초기화 될 때 호출
     */
    var onInit: InvListView<T>.() -> Unit = { }

    /**
     * [InvListView]의 페이지가 업데이트될 때 호출
     */
    var onUpdatePage: (listView: InvListView<T>, page: Int, displayList: List<T>) -> Unit = { _, _, _ -> }

    /**
     * [InvListView]의 아이템을 클릭할때 호출
     */
    var onClickItem: (listView: InvListView<T>, x: Int, y: Int, clicked: T, event: InventoryClickEvent) -> Unit =
        { _, _, _, _, _ -> }

    /**
     * 아이템을 표시할 [ItemStack]으로 변환
     */
    var transform: T.() -> ItemStack = {
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

/**
 * [InvScene]를 사전설정할 수 있는 클래스
 */
class InvSceneBuilder internal constructor(private val line: Int, title: String) {
    internal val regions = ArrayList<InvRegionBuilder>(0)

    /**
     * [InvScene]이 초기화될 때 호출
     */
    var onInit: InvScene.() -> Unit = { }

    /**
     * [org.bukkit.entity.Player]가 [InvScene]을 열때 호출
     */
    var onOpen: (scene: InvScene, event: InventoryOpenEvent) -> Unit = { _, _ -> }

    /**
     * [org.bukkit.entity.Player]가 [InvScene]을 닫을때 호출
     */
    var onClose: (scene: InvScene, event: InventoryCloseEvent) -> Unit = { _, _ -> }

    /**
     * [org.bukkit.entity.Player]가 자신의 인벤토리를 클릭했을때 호출
     */
    var onClickBottom: (scene: InvScene, event: InventoryClickEvent) -> Unit = { _, _ -> }

    private val instance: InvSceneImpl = InvSceneImpl(line, title)

    private fun checkRegion(x: Int, y: Int, width: Int, height: Int) {
        require(x in 0..8) { "X must be between 0 and 8 ($x)" }
        require(y in 0 until line) { "Y must be between 0 and 5 ($y)" }
        require(width in 1..9) { "Width must be between 1 and 9 ($width)" }
        require(height in 1..line) { "Height must be between 1 and $line ($height)" }

        val maxX = x + width - 1
        val maxY = y + height - 1

        require(maxX in x until 9 && maxY in y until line) { "Out of range args(x=$x, y=$y, width=$width, height=$height)" }
        regions.find { it.instance.overlaps(x, y, maxX, maxY) }?.let {
            throw IllegalArgumentException("Overlaps with other region  args=[$x, $y - $maxX, $maxY] overlaps=${it.instance.regionString}")
        }
    }

    /**
     * [InvScene]에 [InvPane]을 추가하기 위한 [InvPaneBuilder]를 생성합니다.
     */
    fun panel(
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

    /**
     * [InvScene]에 [InvListView]를 추가하기 위한 [InvListViewBuilder]를 생성합니다.
     */
    fun <T> listView(
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

/**
 * [InvScene]을 생성하기 위한 [InvSceneBuilder]를 생성합니다.
 */
fun invScene(line: Int, title: String, init: InvSceneBuilder.() -> Unit): InvScene {
    return InvSceneBuilder(line, title).apply(init).build()
}