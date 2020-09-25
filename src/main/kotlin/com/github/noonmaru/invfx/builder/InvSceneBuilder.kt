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

package com.github.noonmaru.invfx.builder

import com.github.noonmaru.invfx.InvListView
import com.github.noonmaru.invfx.InvPane
import com.github.noonmaru.invfx.InvScene
import com.github.noonmaru.invfx.internal.InvRegionImpl
import com.github.noonmaru.invfx.internal.InvSceneImpl
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

/**
 * [InvScene]를 사전설정할 수 있는 클래스
 */
class InvSceneBuilder internal constructor(private val line: Int, title: String) {

    private val instance: InvSceneImpl = InvSceneImpl(line, title)

    internal val regions = ArrayList<InvRegionBuilder>(0)

    internal val initActions = ArrayList<(InvScene) -> Unit>(0)

    internal val openActions = ArrayList<(InvScene, InventoryOpenEvent) -> Unit>(0)

    internal val closeActions = ArrayList<(InvScene, InventoryCloseEvent) -> Unit>(0)

    internal val clickBottomActions = ArrayList<(InvScene, InventoryClickEvent) -> Unit>(0)

    internal fun build(): InvScene {
        return instance.apply {
            initialize(this@InvSceneBuilder)
        }
    }

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
        trim: Boolean,
        list: List<T>,
        init: (InvListViewBuilder<T>.() -> Unit)? = null
    ): InvListView<T> {
        checkRegion(x, y, width, height)

        return InvListViewBuilder(instance, x, y, width, height, trim, list).apply {
            init?.invoke(this)
            regions += this
        }.instance
    }


    /**
     * [InvScene]이 초기화될 때 호출
     */
    fun onInit(action: (scene: InvScene) -> Unit) {
        initActions += action
    }

    /**
     * [org.bukkit.entity.Player]가 [InvScene]을 열때 호출
     */
    fun onOpen(action: (scene: InvScene, event: InventoryOpenEvent) -> Unit) {
        openActions += action
    }

    /**
     * [org.bukkit.entity.Player]가 [InvScene]을 닫을때 호출
     */
    fun onClose(action: (scene: InvScene, event: InventoryCloseEvent) -> Unit) {
        closeActions += action
    }

    /**
     * [org.bukkit.entity.Player]가 자신의 인벤토리를 클릭했을때 호출
     */
    fun onClickBottom(action: (scene: InvScene, event: InventoryClickEvent) -> Unit) {
        clickBottomActions += action
    }
}

internal val InvRegionImpl.regionString: String
    get() {
        return "[$minX, $minY - $maxX, $maxY]"
    }