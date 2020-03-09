package com.github.noonmaru.winventory

import com.google.common.collect.ImmutableList
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

interface InvPanel {
    val parent: InvPanel?
    val minX: Int
    val minY: Int
    val maxX: Int
    val maxY: Int
    val panels: List<InvPanel>
    val buttons: List<InvButton>
    val indexOffset: Int

    fun panelAt(x: Int, y: Int): InvPanel?

    fun buttonAt(x: Int, y: Int): InvButton?

    fun toIndex(x: Int, y: Int): Int

    fun getItem(x: Int, y: Int): ItemStack?

    fun setItem(x: Int, y: Int, item: ItemStack?)
}

interface InvListView : InvPanel {
    val listX: Int
    val listY: Int
    val listWidth: Int
    val listHeight: Int
    val listSize: Int
        get() = listWidth * listHeight
    var page: Int

    fun first() {
        page = 0
    }

    fun last() {
        page = Int.MAX_VALUE
    }
}

interface InvButton {
    val parent: InvPanel
    val x: Int
    val y: Int
    var item: ItemStack?
        get() = parent.getItem(x, y)
        set(value) = parent.setItem(x, y, value)
}

class FrameBuilder(val line: Int, val title: String) : PanelBuilder(0, 0, 9, line) {
    var onClickBottom: (InvFrame, PlayerInventory, InventoryClickEvent) -> Unit = { _, _, _ -> }

    fun build(): InvFrame {
        return InvFrame(this)
    }
}

open class PanelBuilder(internal val x: Int, internal val y: Int, internal val width: Int, internal val height: Int) {
    var onInit: (InvPanel) -> Unit = { }
    var onOpen: (InvPanel, InventoryOpenEvent) -> Unit = { _, _ -> }
    var onClose: (InvPanel, InventoryCloseEvent) -> Unit = { _, _ -> }
    var onClick: (panel: InvPanel, x: Int, y: Int, event: InventoryClickEvent) -> Unit = { _, _, _, _ -> }

    internal val panels = ArrayList<PanelBuilder>(0)
    internal val buttons = ArrayList<ButtonBuilder>(0)

    fun addPanel(x: Int, y: Int, width: Int, height: Int, init: PanelBuilder.() -> Unit) {
        this.panels += PanelBuilder(x, y, width, height).apply(init)
    }

    fun <T> addListView(x: Int, y: Int, width: Int, height: Int, init: ListViewBuilder<T>.() -> Unit) {
        panels += ListViewBuilder<T>(x, y, width, height).apply(init)
    }

    fun addButton(x: Int, y: Int, init: ButtonBuilder.() -> Unit) {
        this.buttons += ButtonBuilder(x, y).apply(init)
    }

    internal open fun build(inventory: Inventory, parent: InvPanel?): InvPanelImpl {
        return InvPanelImpl(this, inventory, parent)
    }
}

class ListViewBuilder<T>(x: Int, y: Int, width: Int, height: Int) : PanelBuilder(x, y, width, height) {

    internal var list: ListBuilder<T>? = null

    fun setList(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        list: () -> List<T>,
        transform: (T) -> ItemStack,
        init: ListBuilder<T>.() -> Unit
    ) {
        this.list = ListBuilder(x, y, width, height, list, transform).apply(init)
    }

    override fun build(inventory: Inventory, parent: InvPanel?): InvPanelImpl {
        val list = this.list ?: return InvPanelImpl(this, inventory, parent)

        return InvListViewImpl(this, list, inventory, parent)
    }
}

class ListBuilder<T>(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val list: () -> List<T>,
    val transform: (T) -> ItemStack
) {
    var onClickItem: (T, InventoryClickEvent) -> Unit = { _, _ -> }
}

class ButtonBuilder(val x: Int, val y: Int) {
    var onInit: (InvButton) -> Unit = { }
    var onClick: (InvPanel, InvButton, InventoryClickEvent) -> Unit = { _, _, _ -> }

    internal fun build(parent: InvPanelImpl): InvButtonImpl {
        return InvButtonImpl(parent, this)
    }
}

fun frame(line: Int, title: String, init: FrameBuilder.() -> Unit): InvFrame {
    return FrameBuilder(line, title).apply(init).build()
}

class InvFrame(builder: FrameBuilder) : InvWindow(builder.line, builder.title) {
    private val onClickBottom = builder.onClickBottom
    private val _rootPanel = builder.build(this.inventory, null)
    val rootPanel: InvPanel
        get() = _rootPanel

    override fun onOpen(event: InventoryOpenEvent) {
        _rootPanel.onOpen(event)
    }

    override fun onClose(event: InventoryCloseEvent) {
        _rootPanel.onClose(event)
    }

    override fun onClickOutside(event: InventoryClickEvent) {
        event.isCancelled = true

    }

    override fun onClickTop(event: InventoryClickEvent) {
        event.isCancelled = true

        val slot = event.slot
        val x = slot % 9
        val y = slot / 9
        _rootPanel.onClick(x, y, event)
    }

    override fun onClickBottom(event: InventoryClickEvent) {
        event.isCancelled = true

        runCatching { onClickBottom(this, event.whoClicked.inventory, event) }
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

internal open class InvPanelImpl(
    builder: PanelBuilder,
    protected val inventory: Inventory,
    override val parent: InvPanel?
) : InvPanel {
    final override val minX: Int = builder.x
    final override val minY: Int = builder.y
    final override val maxX: Int = minX + builder.width - 1
    final override val maxY: Int = minY + builder.height - 1

    private val onOpen = builder.onOpen
    private val onClose = builder.onClose
    private val onClick = builder.onClick

    final override val panels: List<InvPanelImpl>
    final override val buttons: List<InvButtonImpl>

    override val indexOffset: Int
        get() {
            var offset = minX + minY * 9
            parent?.let { offset += it.indexOffset }

            return offset
        }

    init {
        panels = ImmutableList.copyOf(builder.panels.map { it.build(inventory, this) })
        buttons = ImmutableList.copyOf(builder.buttons.map { it.build(this) })

        runCatching { builder.onInit(this) }
    }

    override fun panelAt(x: Int, y: Int): InvPanelImpl? {
        return panels.find { it.minX <= x && it.maxX >= x && it.minY <= y && it.maxY >= y }
    }

    override fun buttonAt(x: Int, y: Int): InvButtonImpl? {
        return buttons.find { it.x == x && it.y == y }
    }

    override fun getItem(x: Int, y: Int): ItemStack? {
        return inventory.getItem(toIndex(x, y))
    }

    override fun setItem(x: Int, y: Int, item: ItemStack?) {
        inventory.setItem(toIndex(x, y), item)
    }

    override fun toIndex(x: Int, y: Int): Int {
        require(x >= 0 && y >= 0 && x <= maxX - minX && y <= maxY - minY) { "Out of range" }

        return indexOffset + x + y * 9
    }

    open fun onOpen(event: InventoryOpenEvent) {
        runCatching { onOpen(this, event) }

        panels.forEach {
            runCatching { it.onOpen(event) }
        }
    }

    open fun onClose(event: InventoryCloseEvent) {
        runCatching { onClose(this, event) }

        panels.forEach {
            runCatching { it.onClose(event) }
        }
    }

    open fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        runCatching { onClick(this, x, y, event) }
        buttonAt(x, y)?.let { it.onClick(this, event) }
        panelAt(x, y)?.let { it.onClick(x - it.minX, y - it.minY, event) }
    }
}

internal class InvListViewImpl<T>(
    builder: ListViewBuilder<T>,
    listBuilder: ListBuilder<T>,
    inventory: Inventory,
    parent: InvPanel?
) : InvPanelImpl(
    builder,
    inventory,
    parent
), InvListView {
    override val listX: Int = listBuilder.x
    override val listY: Int = listBuilder.y
    override val listWidth: Int = listBuilder.width
    override val listHeight: Int = listBuilder.height
    private val list: () -> List<T> = listBuilder.list
    private val transform: T.() -> ItemStack = listBuilder.transform
    override var page: Int = 0
        set(value) {
            field = updatePage(value)
        }

    private val onClickItem: (T, InventoryClickEvent) -> Unit = listBuilder.onClickItem
    private val slots = arrayOfNulls<Any>(listSize)

    private fun updatePage(page: Int): Int {
        slots.fill(null)

        val list = list()
        val updatePage = page.coerceIn(0, list.count() / listSize)
        val offset = updatePage * listSize

        for (i in 0 until listSize) {
            val index = i + offset
            val item = list.elementAtOrNull(index)
            slots[i] = item
            val x = index % listWidth
            val y = index / listWidth
            inventory.setItem(toIndex(x + listX, y + listY), item?.transform())
        }

        return updatePage
    }

    override fun onOpen(event: InventoryOpenEvent) {
        super.onOpen(event)
        first()
    }

    override fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
        super.onClick(x, y, event)

        val index = (x - listX + y * listWidth)
        list().elementAtOrNull(index)?.runCatching {
            onClickItem(this, event)
        }
    }
}

internal class InvButtonImpl(override val parent: InvPanel, builder: ButtonBuilder) : InvButton {
    override val x: Int = builder.x
    override val y: Int = builder.y

    private val onClick = builder.onClick

    init {
        runCatching { builder.onInit(this) }
    }

    fun onClick(pnl: InvPanelImpl, event: InventoryClickEvent) {
        runCatching { onClick(pnl, this, event) }
    }
}