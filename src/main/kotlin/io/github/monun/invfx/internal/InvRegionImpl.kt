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

package io.github.monun.invfx.internal

import io.github.monun.invfx.InvRegion
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent

internal abstract class InvRegionImpl(
    override val scene: InvSceneImpl,
    x: Int,
    y: Int,
    override val width: Int, override val height: Int
) : InvNodeImpl(x, y), InvRegion {
    abstract fun onClick(x: Int, y: Int, event: InventoryClickEvent)

    open fun onOpen(event: InventoryOpenEvent) {}
}