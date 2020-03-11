/*
 *
 *  * Copyright (c) 2020 Noonmaru
 *  *
 *  * Licensed under the General Public License, Version 3.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * https://opensource.org/licenses/gpl-3.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package com.github.noonmaru.invfx

import org.bukkit.inventory.ItemStack

interface InvNode {
    val x: Int
    val y: Int
}

interface InvRegion : InvNode {
    val scene: InvScene
    val width: Int
    val height: Int
    val minX: Int
        get() = x
    val minY: Int
        get() = y
    val maxX: Int
        get() = minX + width - 1
    val maxY: Int
        get() = minY + height - 1
    val size: Int
        get() = width * height

    fun overlaps(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        return this.minX <= maxX && this.maxX >= minX && this.minY <= maxY && this.maxY >= minY
    }

    fun contains(x: Int, y: Int): Boolean {
        return x in minX..maxX && y in minY..maxY
    }

    fun setItem(x: Int, y: Int, item: ItemStack?) {
        return scene.inventory.setItem(toInvIndex(x, y), item)
    }

    fun toInvIndex(x: Int, y: Int): Int {
        require(x in 0 until width && y in 0 until height) { "Out of range" }

        return x + y * 9 + minX + minY * 9
    }
}

interface InvPane : InvRegion {
    val buttons: List<InvButton>

    fun buttonAt(x: Int, y: Int): InvButton?

    fun getItem(x: Int, y: Int): ItemStack? {
        return scene.inventory.getItem(toInvIndex(x, y))
    }
}

interface InvButton : InvNode {
    val pane: InvPane
    var item: ItemStack?
        get() = pane.getItem(x, y)
        set(value) = pane.setItem(x, y, value)
}

interface InvListView<T> : InvRegion {
    val list: List<T>
    var page: Int
    val displayList: List<T>

    fun refresh() {
        page = page
    }

    fun next() {
        page++
    }

    fun previous() {
        page--
    }

    fun first() {
        page = 0
    }

    fun last() {
        page = Int.MAX_VALUE
    }
}

interface InvScene : InvWindow {
    val regions: List<InvRegion>

    fun regionAt(x: Int, y: Int): InvRegion?
}