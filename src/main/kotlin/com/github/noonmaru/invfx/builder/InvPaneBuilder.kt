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

import com.github.noonmaru.invfx.InvButton
import com.github.noonmaru.invfx.InvPane
import com.github.noonmaru.invfx.internal.InvPaneImpl
import com.github.noonmaru.invfx.internal.InvSceneImpl
import org.bukkit.event.inventory.InventoryClickEvent

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