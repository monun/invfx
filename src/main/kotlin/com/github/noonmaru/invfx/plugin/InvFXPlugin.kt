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

package com.github.noonmaru.invfx.plugin

import com.github.noonmaru.invfx.window
import com.github.noonmaru.kommand.kommand
import com.github.noonmaru.tap.util.GitHubSupport
import com.github.noonmaru.tap.util.UpToDateException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

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
        kommand {
            register("invfx") {
                then("update") {
                    executes { update(it.sender) }
                }
//                then("test") {
//                    then("trim" to bool(), "page" to bool()) {
//                        require { this is Player }
//                        executes { context ->
//                            val trim = context.parseArgument<Boolean>("trim")
//                            val page = context.parseArgument<Boolean>("page")
//                            val player = context.sender as Player
//                            player.openWindow(createTestInv(trim, page))
//
//                        }
//                    }
//                }
            }
        }
    }

    // Update example
    private fun update(sender: CommandSender) {
        sender.sendMessage("Attempt to update.")
        update {
            onSuccess { url ->
                sender.sendMessage("Updated successfully. Applies after the server restarts.")
                sender.sendMessage(url)
            }
            onFailure { t ->
                if (t is UpToDateException) sender.sendMessage("Up to date!")
                else {
                    sender.sendMessage("Update failed. Check the console.")
                    t.printStackTrace()
                }
            }
        }
    }

    private fun update(callback: (Result<String>.() -> Unit)? = null) {
        GlobalScope.launch {
            val file = file
            val updateFile = File(file.parentFile, "update/${file.name}")
            GitHubSupport.downloadUpdate(updateFile, "noonmaru", "inv-fx", description.version, callback)
        }
    }

//    private fun createTestInv(trim: Boolean, byPage: Boolean) = InvFX.scene(4, "test") {
//        panel(0, 0, 9, 4) {
//            listView(0, 0, 3, 3, trim, "ABCDEFGHIJK".map { it.toString() }) {
//                transform = {
//                    ItemStack(Material.BOOK).also {
//                        it.itemMeta = it.itemMeta.also { meta ->
//                            meta.setDisplayName(this)
//                        }
//                    }
//                }
//                onClickItem = { listView, x, y, clicked, event ->
//                    event.whoClicked.sendMessage("x=$x y=$y item=$clicked")
//                }
//                onUpdateItems = { listView, offsetIndex, displayList ->
//                    Bukkit.broadcastMessage(displayList.toString())
//                }
//            }.run {
//                button(0, 3) {
//                    onClick = {button, event ->
//                        if (byPage) page -= 1.0 else index--
//                    }
//                }
//                button(1, 3) {
//                    onClick = { button, event ->
//                        if (byPage)  page += 1.0 else index++
//                    }
//                }
//            }
//        }
//    }
}
