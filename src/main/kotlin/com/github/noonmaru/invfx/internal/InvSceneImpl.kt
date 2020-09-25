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

import com.github.noonmaru.invfx.InvScene
import com.github.noonmaru.invfx.InvStage
import com.github.noonmaru.invfx.builder.InvSceneBuilder
import com.google.common.collect.ImmutableList
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent

internal class InvSceneImpl(line: Int, title: String) : InvStage(line, title), InvScene {
    private lateinit var openActions: List<(scene: InvScene, event: InventoryOpenEvent) -> Unit>
    private lateinit var closeActions: List<(scene: InvScene, event: InventoryCloseEvent) -> Unit>
    private lateinit var clickBottomActions: List<(scene: InvScene, event: InventoryClickEvent) -> Unit>

    override lateinit var regions: List<InvRegionImpl>

    fun initialize(builder: InvSceneBuilder) {
        openActions = ImmutableList.copyOf(builder.openActions)
        closeActions = ImmutableList.copyOf(builder.closeActions)
        clickBottomActions = ImmutableList.copyOf(builder.clickBottomActions)
        regions = ImmutableList.copyOf(builder.regions.map { it.build() })
        builder.initActions.forEach { kotlin.runCatching { it(this) } }
    }

    override fun onOpen(event: InventoryOpenEvent) {
        openActions.forEachInvokeSafety { it(this, event) }
        regions.forEach { it.onOpen(event) }
    }

    override fun onClose(event: InventoryCloseEvent) {
        closeActions.forEachInvokeSafety { it(this, event) }
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
        clickBottomActions.forEachInvokeSafety { it(this, event) }
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