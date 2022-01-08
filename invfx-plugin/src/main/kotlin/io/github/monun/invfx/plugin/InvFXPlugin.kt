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

package io.github.monun.invfx.plugin

import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class InvFXPlugin : JavaPlugin() {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as Player
        val strings = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList().map { it.toString() }

        InvFX.frame(3, text("TEST")) {
            list(1, 0, 3, 3, true, { strings }) {
                transform { s ->
                    ItemStack(Material.BOOK).apply {
                        editMeta { it.displayName(text(s)) }
                    }
                }
                onClickItem { x, y, item, _ ->
                    println("$x $y $item")
                }
            }.let { list ->
                slot(0, 1) {
                    item = ItemStack(Material.SLIME_BALL)
                    onClick {
                        list.page -= 1
                    }
                }
                slot(4, 1) {
                    item = ItemStack(Material.MAGMA_CREAM)
                    onClick {
                        list.page += 1
                    }
                }
            }

            pane(5, 0, 3, 3) {
                item(1, 1, ItemStack(Material.EMERALD))

                onClick { x, y, _ ->
                    println("$x $y ${item(x, y)}")
                }
            }

        }.let {
            player.openFrame(it)
        }

        return true
    }
}