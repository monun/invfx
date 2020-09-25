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
import com.github.noonmaru.invfx.internal.InvButtonImpl
import com.github.noonmaru.invfx.internal.InvPaneImpl
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * [InvButton]을 사전설정할 수 있는 클래스
 */
class InvButtonBuilder internal constructor(pane: InvPaneImpl, x: Int, y: Int) {

    internal val instance: InvButtonImpl = InvButtonImpl(pane, x, y)

    internal val initActions = ArrayList<(InvButton) -> Unit>(0)

    internal val clickActions = ArrayList<(InvButton, InventoryClickEvent) -> Unit>(0)

    internal fun build(): InvButtonImpl {
        return instance.apply {
            initialize(this@InvButtonBuilder)
        }
    }

    /**
     * [InvButton]이 초기화될 때 호출됩니다.
     */
    fun onInit(action: (button: InvButton) -> Unit) {
        initActions += action
    }

    /**
     * [org.bukkit.entity.Player]가 [InvButton]을 클릭할 때 호출됩니다.
     */
    fun onClick(action: (button: InvButton, event: InventoryClickEvent) -> Unit) {
        clickActions += action
    }
}