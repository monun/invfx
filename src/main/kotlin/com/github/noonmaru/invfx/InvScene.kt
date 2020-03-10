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

    fun contains(x: Int, y: Int): Boolean {
        return x in minX..maxX && y in minY..maxY
    }

    fun setItem(x: Int, y: Int, item: ItemStack?) {
        return scene.inventory.setItem(toInvIndex(x, y), item)
    }

    fun toInvIndex(x: Int, y: Int): Int {
        require(x >= 0 && y >= 0 && x < width && y < width) { "Out of range" }

        return minX + x + y * 9
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

interface InvListView : InvRegion {
    val list: List<*>
    var page: Int
    val displayList: List<*>

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

    fun regionAt(x: Int, y: Int): InvRegion? {
        return regions.find { it.contains(x, y) }
    }
}