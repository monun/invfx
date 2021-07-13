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

package io.github.monun.invfx

import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.InventoryHolder

/**
 * 윈도우화된 인벤토리의 최상위 인터페이스입니다.
 */
interface InvWindow : InventoryHolder {
    fun onOpen(event: InventoryOpenEvent)
    fun onClose(event: InventoryCloseEvent)
    fun onClickOutside(event: InventoryClickEvent)
    fun onClickTop(event: InventoryClickEvent)
    fun onClickBottom(event: InventoryClickEvent)
    fun onDrag(event: InventoryDragEvent)
    fun onPickupItem(event: EntityPickupItemEvent)
    fun onDropItem(event: PlayerDropItemEvent)
}