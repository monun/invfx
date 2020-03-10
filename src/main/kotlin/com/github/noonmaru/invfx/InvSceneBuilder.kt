package com.github.noonmaru.invfx

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

abstract class InvRegionBuilder internal constructor(val x: Int, val y: Int, val width: Int, val height: Int) {
    private val minX: Int
        get() = x
    private val minY: Int
        get() = y
    private val maxX: Int
        get() = minX + width - 1
    private val maxY: Int
        get() = minY + height - 1

    fun overlaps(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        return this.minX <= maxX && this.maxX >= minX && this.minY <= maxY && this.maxY >= minY
    }

    internal abstract fun build(scene: InvSceneImpl): InvRegionImpl
}

class InvPaneBuilder internal constructor(x: Int, y: Int, width: Int, height: Int) :
    InvRegionBuilder(x, y, width, height) {
    internal val buttons = ArrayList<InvButtonBuilder>(0)
    var onInit: InvPane.() -> Unit = { }
    var onClick: (pane: InvPane, x: Int, y: Int, event: InventoryClickEvent) -> Unit = { _, _, _, _ -> }

    fun addButton(x: Int, y: Int, init: InvButtonBuilder.() -> Unit) {
        require(x in 0..width && y in 0..height) { "Out of range" }

        buttons += InvButtonBuilder(x, y).apply(init)
    }

    override fun build(scene: InvSceneImpl): InvRegionImpl {
        return InvPaneImpl(scene, this)
    }
}

class InvListViewBuilder<T> internal constructor(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    internal val list: List<T>
) : InvRegionBuilder(x, y, width, height) {
    var onInit: InvListView.() -> Unit = { }
    var onClickItem: (listView: InvListView, x: Int, y: Int, clicked: T, event: InventoryClickEvent) -> Unit =
        { _, _, _, _, _ -> }
    val transform: T.() -> ItemStack = {
        if (this is ItemStack) this
        else {
            val toString = toString()
            ItemStack(Material.BOOK).apply {
                itemMeta = itemMeta.apply {
                    setDisplayName(toString)
                }
            }
        }
    }

    override fun build(scene: InvSceneImpl): InvRegionImpl {
        return InvListViewImpl(scene, this)
    }
}

class InvButtonBuilder internal constructor(val x: Int, val y: Int) {
    var onInit: InvButton.() -> Unit = { }
    var onClick: (button: InvButton, event: InventoryClickEvent) -> Unit = { _, _ -> }

    internal fun build(pane: InvPaneImpl): InvButtonImpl {
        return InvButtonImpl(pane, this)
    }
}

class InvSceneBuilder internal constructor(internal val line: Int, internal val title: String) {
    var onInit: InvScene.() -> Unit = { }
    var onOpen: (scene: InvScene, event: InventoryOpenEvent) -> Unit = { _, _ -> }
    var onClose: (scene: InvScene, event: InventoryCloseEvent) -> Unit = { _, _ -> }
    var onClickBottom: (scene: InvScene, event: InventoryClickEvent) -> Unit = { _, _ -> }

    internal val regions = ArrayList<InvRegionBuilder>(0)

    private fun checkOverlaps(x: Int, y: Int, width: Int, height: Int) {
        require(regions.find { it.overlaps(x, y, x + width, y + height) } == null) { "Overlaps with other region " }
    }

    fun addPanel(x: Int, y: Int, width: Int, height: Int, init: InvPaneBuilder.() -> Unit) {
        checkOverlaps(x, y, width, height)

        regions += InvPaneBuilder(x, y, width, height).apply(init)
    }

    fun <T> addListView(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        list: List<T>,
        init: InvListViewBuilder<T>.() -> Unit
    ) {
        checkOverlaps(x, y, width, height)

        regions += InvListViewBuilder(x, y, width, height, list).apply(init)
    }

    internal fun build(): InvScene {
        return InvSceneImpl(this)
    }
}

fun invScene(line: Int, title: String, init: InvSceneBuilder.() -> Unit): InvScene {
    return InvSceneBuilder(line, title).apply(init).build()
}