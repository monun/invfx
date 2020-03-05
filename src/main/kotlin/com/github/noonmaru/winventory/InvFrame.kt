package com.github.noonmaru.winventory

import com.google.common.collect.ImmutableList
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class FrameBuilder private constructor(val lines: Int, val title: String, internal val panelBuilder: PanelBuilderImpl) :
    PanelBuilder by panelBuilder {
    constructor(lines: Int, title: String) : this(lines, title, PanelBuilderImpl(null, 0, 0, 9, lines))

    fun build(): InvFrame {
        return InvFrame(this)
    }
}

interface PanelBuilder {
    fun panel(x: Int, y: Int, width: Int, height: Int, init: (PanelBuilder.() -> Unit)? = null)
    fun button(x: Int, y: Int, item: ItemStack?, clickListener: ((event: InventoryClickEvent) -> Unit))
}

open class PanelBuilderImpl internal constructor(
    val parent: PanelBuilderImpl?,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) : PanelBuilder {
    internal var panels: MutableList<PanelBuilderImpl>? = null

    internal var buttons: MutableList<ButtonBuilder>? = null

    val clickListener: ((InventoryClickEvent) -> Unit)? = null

    private val area: Area by lazy(LazyThreadSafetyMode.NONE) {
        var minX = x
        var minY = y

        parent?.let { parent ->
            minX += parent.x
            minY += parent.y
        }

        val maxX = minX + width
        val maxY = minY + height

        Area(minX, minY, maxX, maxY)
    }

    override fun panel(x: Int, y: Int, width: Int, height: Int, init: (PanelBuilder.() -> Unit)?) {
        require(x >= 0 && y >= 0 && width <= this.width && height <= this.height) { "Out of range" }

        (this.panels ?: mutableListOf<PanelBuilderImpl>().apply { panels = this }).let { panels ->
            panels.forEach { require(it.area.overlaps(x, y, x + width, y + height)) { "Overlap with other panels" } }

            panels += PanelBuilderImpl(this, x, y, width, height).apply { init?.invoke(this) }
        }
    }

    override fun button(x: Int, y: Int, item: ItemStack?, clickListener: (event: InventoryClickEvent) -> Unit) {
        require(x >= 0 && y >= 0 && x < this.width && y < this.height) { "Out of range" }

        (this.buttons ?: mutableListOf<ButtonBuilder>().apply { buttons = this }).let { buttons ->
            buttons.forEach { require(it.x != x && it.y != y) { "Duplicated index" } }

            buttons += ButtonBuilder(x, y, item, clickListener)
        }
    }

    private class Area(val minX: Int, val minY: Int, val maxX: Int, val maxY: Int) {
        fun overlaps(
            minX: Int,
            minY: Int,
            maxX: Int,
            maxY: Int
        ): Boolean {
            return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY
        }
    }
}

class ButtonBuilder internal constructor(
    val x: Int,
    val y: Int,
    val item: ItemStack?,
    val clickListener: (InventoryClickEvent) -> Unit
)

class InvFrame internal constructor(builder: FrameBuilder) : InvWindow(builder.lines * 9, builder.title) {

    val rootPanel = Panel(this, null, builder.panelBuilder)

    override fun onClickTop(event: InventoryClickEvent) {
        val slot = event.rawSlot
        val x = slot % 9
        val y = slot / 9

        rootPanel.onClick(x, y, event)
    }

    class Panel internal constructor(val frame: InvFrame, val parent: Panel?, builder: PanelBuilderImpl) {

        val width: Int = builder.width

        val height: Int = builder.height

        val minX: Int = builder.x

        val minY: Int = builder.y

        val maxX: Int = minX + width - 1

        val maxY: Int = minY + height - 1

        val clickListener: ((InventoryClickEvent) -> Unit)? = builder.clickListener

        val panels: List<Panel>

        val buttons: List<Button>

        private val indexOffset: Int
            get() {
                var index = minX + minY * 9
                parent?.let { index += parent.indexOffset }

                return index
            }

        init {
            this.panels = builder.panels?.let {
                val list = ArrayList<Panel>(it.count())
                it.forEach { panelBuilder ->
                    list += Panel(frame, this, panelBuilder)
                }
                ImmutableList.copyOf(list)
            } ?: ImmutableList.of()
            this.buttons = builder.buttons?.let {
                val list = ArrayList<Button>(it.count())
                it.forEach { buttonBuilder ->
                    list += Button(buttonBuilder)
                }
                ImmutableList.copyOf(list)
            } ?: ImmutableList.of()
        }

        private fun toIndex(x: Int, y: Int): Int {
            return indexOffset + x + y * 9
        }

        internal fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
            clickListener?.invoke(event)
            getButtonAt(x, y)?.clickListener?.invoke(event)

            getPanelAt(x, y)?.let { panel ->
                val relativeX = x - panel.minX
                val relativeY = y - panel.minY

                onClick(relativeX, relativeY, event)
            }
        }

        fun setItem(x: Int, y: Int, item: ItemStack?) {
            require(this.minX <= x && this.maxX >= x && this.minY <= y && this.maxY >= y)

            frame.inventory.setItem(toIndex(x, y), item)
        }

        fun getButtonAt(x: Int, y: Int): Button? {
            return buttons.find { it.x == x && it.y == y }
        }

        fun getPanelAt(x: Int, y: Int): Panel? {
            return panels.find { it.minX <= x && it.maxX >= x && it.minY <= x && it.maxY >= y }
        }
    }

    class Button(builder: ButtonBuilder) {
        val x = builder.x
        val y = builder.y
        val item = builder.item
        val clickListener = builder.clickListener
    }
}