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

package io.github.monun.invfx.plugin

import io.github.monun.invfx.InvFX
import io.github.monun.invfx.openWindow
import io.github.monun.invfx.window
import io.github.monun.kommand.Kommand
import io.github.monun.kommand.KommandDispatcher
import io.github.monun.kommand.internal.KommandDispatcherImpl
import io.github.monun.tap.util.updateFromGitHubMagically
import kotlinx.coroutines.DelicateCoroutinesApi
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

@DelicateCoroutinesApi
class InvFXPlugin : JavaPlugin() {
    override fun onEnable() {
        server.apply {
            pluginManager.registerEvents(InvListener(), this@InvFXPlugin)
        }
        setupCommands()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.openInventory.topInventory.window?.run { player.closeInventory(InventoryCloseEvent.Reason.PLUGIN) } //InvWindow 닫기
        }
    }

    private fun setupCommands() {
        Kommand.register("invfx") {
            then("version") {
                this.executes {
                    it.source.sender.sendMessage("${description.name} ${description.version}")
                }
            }
        }
        Kommand.register("update") {
            executes {
                updateFromGitHubMagically("monun", "invfx", "InvFX.jar", it.source.sender::sendMessage)
            }
        }
        Kommand.register("test") {
                executes {
                    require(it.source.sender is Player)
                    (it.source.sender as Player).openWindow(testWindow())
                }
        }
    }

    private fun testWindow() = InvFX.scene(5, "Example") {
        panel(0, 0, 9, 5) {
            listView(1, 1, 7, 3, false, "ABCDEFGHIJKLMNOPQRSTUVWXYZ".map { it.toString() }) {
                transform { item -> ItemStack(Material.BOOK).apply { lore(listOf(text(item))) } }
                onClickItem { _, _, _, item, event -> event.whoClicked.sendMessage(text("CLICK_ITEM $item")) }
            }.let { view ->
                button(0, 2) {
                    onClick { _, _ -> view.page-- }
                }
                button(8, 2) {
                    onClick { _, _ -> view.page++ }
                }
            }
        }
    }
}