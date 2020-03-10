package com.github.noonmaru.invfx

import com.google.common.collect.ImmutableList
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent

internal abstract class InvNodeImpl(final override val x: Int, final override val y: Int) : InvNode

internal abstract class InvRegionImpl(
    override val scene: InvScene,
    builder: InvRegionBuilder
) : InvNodeImpl(builder.x, builder.y), InvRegion {
    override val width: Int = builder.width
    override val height: Int = builder.height

    abstract fun onClick(x: Int, y: Int, event: InventoryClickEvent)
    open fun onOpen(event: InventoryOpenEvent) {}
}

internal class InvPaneImpl(
    scene: InvScene,
    builder: InvPaneBuilder
) : InvRegionImpl(
    scene,
    builder
), InvPane {
    override val buttons: List<InvButtonImpl> = ImmutableList.copyOf(builder.buttons.map { it.build(this) })

    private val onClick = builder.onClick

    init {
        builder.runCatching { onInit() }
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        runCatching { onClick(this, x, y, event) }

        buttonAt(x, y)?.runCatching { onClick(this, event) }
    }

    override fun buttonAt(x: Int, y: Int): InvButtonImpl? {
        return buttons.find { it.x == x && it.y == y }
    }
}

internal class InvListViewImpl<T>(scene: InvScene, builder: InvListViewBuilder<T>) :
    InvRegionImpl(
        scene,
        builder
    ), InvListView {
    override var page: Int = 0
        set(value) {
            field = updatePage(value)
        }

    override val list: List<T> = builder.list
    override val displayList = ArrayList<T>(0)

    private val onClickItem = builder.onClickItem
    private val transform = builder.transform

    init {
        refresh()
        builder.runCatching { onInit() }
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        val index = x + y * width
        displayList.elementAtOrNull(index)?.runCatching {
            onClickItem(this@InvListViewImpl, x, y, this, event)
        }
    }

    private fun updatePage(page: Int): Int {
        val maxPage = list.count() / size
        val update = page.coerceIn(0, maxPage)
        val offset = update * size

        displayList.clear()

        for (i in 0 until size) {
            val item = list.elementAtOrNull(offset + i)

            if (item != null)
                displayList.add(item)

            val x = i % width
            val y = i / width

            setItem(x, y, item?.transform())
        }
        return update
    }
}

internal class InvButtonImpl(
    override val pane: InvPane,
    builder: InvButtonBuilder
) : InvNodeImpl(builder.x, builder.y),
    InvButton {
    internal val onClick = builder.onClick

    init {
        runCatching { builder.onInit(this) }
    }
}

internal class InvSceneImpl(builder: InvSceneBuilder) : InvStage(builder.line, builder.title), InvScene {
    private val onOpen = builder.onOpen
    private val onClose = builder.onClose
    private val onClickBottom = builder.onClickBottom

    override val regions: List<InvRegionImpl> = builder.regions.map { it.build(this) }

    init {
        builder.runCatching { onInit() }
    }

    override fun onOpen(event: InventoryOpenEvent) {
        runCatching { onOpen(this, event) }
        regions.forEach { it.onOpen(event) }
    }

    override fun onClose(event: InventoryCloseEvent) {
        runCatching { onClose(this, event) }
    }

    override fun onClickOutside(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    override fun onClickTop(event: InventoryClickEvent) {
        event.isCancelled = true

        val slot = event.slot
        val x = slot % 9
        val y = slot / 9

        regions.find { it.contains(x, y) }?.let { region ->
            region.onClick(x - region.x, y - region.y, event)
        }
    }

    override fun onClickBottom(event: InventoryClickEvent) {
        event.isCancelled = true
        runCatching { onClickBottom(this, event) }
    }

    override fun onDrag(event: InventoryDragEvent) {
        event.isCancelled = true
    }

    override fun onPickupItem(event: EntityPickupItemEvent) {
        event.isCancelled = true
    }

    override fun onDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }
}