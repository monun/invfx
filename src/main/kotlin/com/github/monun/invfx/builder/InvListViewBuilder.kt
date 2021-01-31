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

package com.github.monun.invfx.builder

import com.github.monun.invfx.InvListView
import com.github.monun.invfx.internal.InvListViewImpl
import com.github.monun.invfx.internal.InvRegionImpl
import com.github.monun.invfx.internal.InvSceneImpl
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * [InvListView]를 사전설정 할 수 있는 클래스입니다.
 */
class InvListViewBuilder<T> internal constructor(
    scene: InvSceneImpl,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    trim: Boolean,
    internal val items: List<T>
) : InvRegionBuilder() {

    override val instance: InvListViewImpl<T> = InvListViewImpl(scene, x, y, width, height, trim)

    internal var transformer: (item: T) -> ItemStack = { item ->
        if (item is ItemStack) item
        else {
            val toString = item.toString()
            ItemStack(Material.BOOK).apply {
                itemMeta = itemMeta.apply {
                    setDisplayName(toString)
                }
            }
        }
    }

    internal val initActions = ArrayList<(InvListView<T>) -> Unit>(0)

    internal val updateItemsActions = ArrayList<(InvListView<T>, Int, List<T>) -> Unit>(0)

    internal val clickItemAction = ArrayList<(InvListView<T>, Int, Int, T, InventoryClickEvent) -> Unit>(0)

    override fun build(): InvRegionImpl {
        return instance.apply {
            initialize(this@InvListViewBuilder)
        }
    }

    /**
     * 아이템을 표시할 [ItemStack]으로 변환합니다.
     */
    fun transform(transformer: (item: T) -> ItemStack) {
        this.transformer = transformer
    }

    /**
     * [InvListView]가 초기화 될 때 호출합니다.
     */
    fun onInit(action: (view: InvListView<T>) -> Unit) {
        initActions += action
    }

    /**
     * [InvListView]의 페이지가 업데이트될 때 호출합니다.
     */
    fun onUpdateItems(action: (view: InvListView<T>, offsetIndex: Int, displayList: List<T>) -> Unit) {
        updateItemsActions += action
    }

    /**
     * [InvListView]의 아이템을 클릭할때 호출합니다.
     */
    fun onClickItem(action: (view: InvListView<T>, x: Int, y: Int, item: T, event: InventoryClickEvent) -> Unit) {
        clickItemAction += action
    }
}