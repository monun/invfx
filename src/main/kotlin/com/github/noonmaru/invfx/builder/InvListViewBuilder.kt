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
import com.github.noonmaru.invfx.internal.InvListViewImpl
import com.github.noonmaru.invfx.internal.InvRegionImpl
import com.github.noonmaru.invfx.internal.InvSceneImpl
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * [InvListView]를 사전설정 할 수 있는 클래스
 */
class InvListViewBuilder<T> internal constructor(
    scene: InvSceneImpl,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    trim: Boolean,
    internal val list: List<T>
) : InvRegionBuilder() {
    /**
     * [InvListView]가 초기화 될 때 호출
     */
    var onInit: InvListView<T>.() -> Unit = { }

    /**
     * [InvListView]의 페이지가 업데이트될 때 호출
     */
    var onUpdateItems: (listView: InvListView<T>, offsetIndex: Int, displayList: List<T>) -> Unit = { _, _, _ -> }

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

    override val instance: InvListViewImpl<T> = InvListViewImpl(scene, x, y, width, height, trim)

    override fun build(): InvRegionImpl {
        return instance.apply {
            initialize(this@InvListViewBuilder)
        }
    }
}