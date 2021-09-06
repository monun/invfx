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

package io.github.monun.invfx.internal.frame

import io.github.monun.invfx.InvWindow
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.frame.InvList
import io.github.monun.invfx.frame.InvPane
import io.github.monun.invfx.frame.InvSlot
import io.github.monun.invfx.util.getValue
import io.github.monun.invfx.util.lazyVal
import io.github.monun.invfx.util.setValue
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InvFrameImpl(
    lines: Int,
    title: Component
) : InvFrame, InvWindow {
    private val inv = Bukkit.createInventory(this, lines * 9, title)

    private val slots = arrayListOf<InvSlotImpl>()

    private val regions = arrayListOf<AbstractInvRegion>()

    private var onOpen: ((InventoryOpenEvent) -> Unit)? by lazyVal()

    private var onClose: ((InventoryCloseEvent) -> Unit)? by lazyVal()

    private var onClick: ((Int, Int, InventoryClickEvent) -> Unit)? by lazyVal()

    private var onClickBottom: ((InventoryClickEvent) -> Unit)? by lazyVal()

    private var onClickOutside: ((InventoryClickEvent) -> Unit)? by lazyVal()

    override fun getInventory(): Inventory = inv

    private fun checkItemSlot(x: Int, y: Int) {
        val lines = inv.size / 9
        require(x in 0 until 9) { "require 0 <= x <= 8 ($x)" }
        require(y in 0 until lines) { "require 0 <= y <= $lines ($y)" }
    }

    override fun slot(x: Int, y: Int, init: InvSlot.() -> Unit): InvSlotImpl {
        checkItemSlot(x, y)
        require(slots.find { it.x == x && it.y == y } == null) { "Overlaps with other slot" }
        require(regions.find { it.contains(x, y) } == null) { "Overlaps with other region" }

        return InvSlotImpl(this, x, y).apply(init).also { slots += it }
    }

    private fun checkRegion(x: Int, y: Int, width: Int, height: Int) {
        val lines = inv.size / 9
        require(x in 0 until 9) { "require 0 <= x <= 8 ($x)" }
        require(y in 0 until lines) { "require 0 <= y <= $lines ($y)" }
        require(x + width <= 9)
        require(y + height <= lines)
        require(regions.find { it.overlaps(x, y, x + width, y + width) } == null) { "Overlaps with other slot" }
        require(slots.find { it.x in x until x + width && it.y in y until y + height } == null) { "Overlaps with other region" }
    }

    override fun pane(x: Int, y: Int, width: Int, height: Int, init: InvPane.() -> Unit): InvPane {
        checkRegion(x, y, width, height)

        return InvPaneImpl(this, x, y, width, height).apply(init).also {
            regions += it
        }
    }

    override fun <T> list(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        trim: Boolean,
        item: () -> List<T>,
        init: (InvList<T>.() -> Unit)?
    ): InvListImpl<T> {
        checkRegion(x, y, width, height)

        return InvListImpl(this, x, y, width, height, trim, item).apply {
            init?.let { it() }
        }.also {
            regions += it
        }
    }

    override fun onOpen(onOpen: (InventoryOpenEvent) -> Unit) {
        this.onOpen = onOpen
    }

    override fun onClose(onClose: (InventoryCloseEvent) -> Unit) {
        this.onClose = onClose
    }

    override fun onClick(onClick: (x: Int, y: Int, event: InventoryClickEvent) -> Unit) {
        this.onClick = onClick
    }

    override fun onClickBottom(onClickBottom: (InventoryClickEvent) -> Unit) {
        this.onClickBottom = onClickBottom
    }

    override fun onClickOutside(onClickOutside: (InventoryClickEvent) -> Unit) {
        this.onClickOutside = onClickOutside
    }

    fun trim() {
        regions.trimToSize()
        slots.trimToSize()
    }

    override fun item(x: Int, y: Int): ItemStack? {
        checkItemSlot(x, y)
        return inv.getItem(x + y * 9)
    }

    override fun item(x: Int, y: Int, item: ItemStack?) {
        checkItemSlot(x, y)
        inv.setItem(x + y * 9, item)
    }

    override fun onOpen(event: InventoryOpenEvent) {
        onOpen?.runCatching { invoke(event) }

        regions.forEach { region ->
            if (region is InvListImpl<*>) {
                region.refresh()
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        onClose?.runCatching { invoke(event) }
    }

    override fun onClick(event: InventoryClickEvent) {
        event.cancel()

        val slot = event.slot
        val x = slot % 9
        val y = slot / 9

        onClick?.runCatching { invoke(x, y, event) }

        regions.find { it.contains(x, y) }?.let {
            it.onClick(x - it.x, y - it.y, event)
        }

        slots.find { it.x == x && it.y == y }?.let {
            it.onClick?.runCatching { invoke(event) }
        }
    }

    override fun onClickBottom(event: InventoryClickEvent) {
        event.cancel()

        onClickBottom?.runCatching { invoke(event) }
    }

    override fun onClickOutside(event: InventoryClickEvent) {
        event.cancel()

        onClickOutside?.runCatching { invoke(event) }
    }

    override fun onDrag(event: InventoryDragEvent) {
        event.cancel()
    }

    override fun onPickupItem(event: EntityPickupItemEvent) {
        event.cancel()
    }

    override fun onDropItem(event: PlayerDropItemEvent) {
        event.cancel()
    }
}

private fun Cancellable.cancel() {
    isCancelled = true
}