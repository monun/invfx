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

package io.github.monun.invfx

import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.util.LibraryLoader
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object InvFX {
    internal val support = LibraryLoader.loadImplement(InvFXSupport::class.java)

    fun frame(lines: Int, title: Component, init: InvFrame.() -> Unit): InvFrame {
        return support.newInvFrame(lines, title, init)
    }
}

interface InvFXSupport {
    fun newInvFrame(lines: Int, title: Component, init: InvFrame.() -> Unit): InvFrame

    fun openFrame(player: Player, frame: InvFrame)
}

fun Player.openWindow(window: InvWindow) {
    openInventory(window.inventory)
}

fun Player.openFrame(frame: InvFrame) {
    InvFX.support.openFrame(this, frame)
}

@DslMarker
annotation class InvDSL