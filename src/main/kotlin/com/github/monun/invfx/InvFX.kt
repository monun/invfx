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

package com.github.monun.invfx

import com.github.monun.invfx.builder.InvSceneBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory


object InvFX {
    /**
     * [InvScene]을 생성하기 위한 [InvSceneBuilder]를 생성합니다.
     */
    fun scene(line: Int, title: String, init: InvSceneBuilder.() -> Unit): InvScene {
        return InvSceneBuilder(line, title).apply(init).build()
    }
}

internal val Inventory.window: InvWindow?
    get() {
        return holder?.run {
            if (this is InvWindow) this else null
        }
    }

/**
 * 플레이어에게 [InvWindow]를 보여줍니다.
 */
fun Player.openWindow(window: InvWindow): Boolean {
    return openInventory(window.inventory) != null
}