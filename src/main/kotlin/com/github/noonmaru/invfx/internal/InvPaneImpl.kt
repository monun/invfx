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

import com.github.noonmaru.invfx.InvPane
import com.github.noonmaru.invfx.builder.InvPaneBuilder
import com.google.common.collect.ImmutableList
import org.bukkit.event.inventory.InventoryClickEvent

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