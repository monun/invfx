/*
 * InvFX
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.monun.invfx.internal

import io.github.monun.invfx.InvFXSupport
import io.github.monun.invfx.InvWindow
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.internal.frame.InvFrameImpl
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.PluginClassLoader

class InvFXSupportImpl : InvFXSupport, Listener {
    private var plugin: Plugin? = null

    override fun newInvFrame(lines: Int, title: Component, init: InvFrame.() -> Unit): InvFrame {
        if (plugin == null) {
            val pluginClassLoader = init.javaClass.classLoader as PluginClassLoader
            val plugin = pluginClassLoader.plugin
            require(plugin.isEnabled)
            plugin.server.pluginManager.registerEvents(this, plugin)
            this.plugin = plugin
            plugin.logger.info("InvFX is ENABLED.")
        }

        return InvFrameImpl(lines, title).apply(init).apply { trim() }
    }

    override fun openFrame(player: Player, frame: InvFrame) {
        require(frame is InvWindow)

        player.openInventory(frame.inventory)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDisable(event: PluginDisableEvent) {
        val plugin = event.plugin
        if (event.plugin === this.plugin) {
            plugin.logger.info("InvFX is DISABLED.")
            this.plugin = null

            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.openInventory.topInventory.holder is InvWindow) {
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onOpen(event: InventoryOpenEvent) {
        event.inventory.window?.onOpen(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onClose(event: InventoryCloseEvent) {
        event.inventory.window?.onClose(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        event.inventory.window?.run {
            val slot = event.rawSlot

            when {
                slot < 0 -> {
                    onClickOutside(event)
                }
                slot < inventory.size -> {
                    onClick(event)
                }
                else -> {
                    onClickBottom(event)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrag(event: InventoryDragEvent) {
        event.inventory.window?.onDrag(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPickupItem(event: EntityPickupItemEvent) {
        val entity = event.entity

        if (entity is Player) {
            entity.openInventory.topInventory.window?.onPickupItem(event)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDropItem(event: PlayerDropItemEvent) {
        event.player.openInventory.topInventory.window?.onDropItem(event)
    }
}



private val Inventory.window: InvWindow?
    get() = holder.takeIf { it is InvWindow } as? InvWindow