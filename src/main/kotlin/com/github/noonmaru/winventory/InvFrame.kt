package com.github.noonmaru.winventory

import com.google.common.collect.ImmutableList
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack

class InvFrameBuilder(lines: Int, val title: String) : PanelBuilder(0, 0, 9, lines) {
    fun build(): InvFrame {
        return InvFrame(this)
    }
}

fun frameBuilder(lines: Int, title: String, init: InvFrameBuilder.() -> Unit): InvFrameBuilder {
    return InvFrameBuilder(lines, title).apply(init)
}

fun frame(lines: Int, title: String, init: InvFrameBuilder.() -> Unit): InvFrame {
    return frameBuilder(lines, title, init).build()
}

open class PanelBuilder(x: Int, y: Int, internal val width: Int, internal val height: Int) {
    internal val minX = x
    internal val minY = y
    internal val maxX = x + width - 1
    internal val maxY = y + height - 1

    internal val panels = mutableListOf<PanelBuilder>()
    internal val buttons = mutableListOf<ButtonBuilder>()

    internal var receiver: ((InvPanel) -> Unit)? = null
    internal var clickListener: ((InventoryClickEvent) -> Unit)? = null

    fun receive(receiver: (InvPanel) -> Unit) {
        this.receiver = receiver
    }

    fun onClick(clickListener: (InventoryClickEvent) -> Unit) {
        this.clickListener = clickListener
    }

    fun panel(x: Int, y: Int, width: Int, height: Int, init: (PanelBuilder.() -> Unit)? = null) {
        check(x, y, width, height)

        val builder = PanelBuilder(x, y, width, height)
        panels += builder

        init?.invoke(builder)
    }

    private fun check(x: Int, y: Int, width: Int, height: Int) {
        require(width > 0 && height > 0) { "Zero size" }

        val minX = x + this.minX
        val minY = y + this.minY
        val maxX = minX + width - 1
        val maxY = minY + height - 1

        require(this.minX <= minX && this.maxX >= maxX && this.minY <= minY && this.maxY >= maxY) { "Out of range" }
        require(panels.find { it.minX <= maxX && it.maxX >= minX && it.minY <= maxY && it.maxY >= minY } == null) { "Overlap with other panels" }
    }

    fun button(x: Int, y: Int, item: ItemStack? = null, init: (ButtonBuilder.() -> Unit)? = null) {
        require(x >= 0 && y >= 0 && x <= width && y <= height) { "Out of range" }
        require(buttons.find { it.x == x && it.y == y } == null) { "Overlap with other buttons" }

        val builder = ButtonBuilder(x, y, item)
        buttons += builder

        init?.invoke(builder)
    }
}

class ButtonBuilder(val x: Int, val y: Int, val item: ItemStack?) {
    internal var receiver: ((InvButton) -> Unit)? = null

    internal var clickListener: ((InventoryClickEvent) -> Unit)? = null

    fun receive(receiver: (InvButton) -> Unit) {
        this.receiver = receiver
    }

    fun onClick(clickListener: (InventoryClickEvent) -> Unit) {
        this.clickListener = clickListener
    }
}

class InvFrame(builder: InvFrameBuilder) : InvWindow(builder.height, builder.title) {
    private inner class Panel(val parent: Panel?, builder: PanelBuilder) : InvPanel {
        override val minX = builder.minX
        override val minY = builder.minY
        override val maxX = builder.maxX
        override val maxY = builder.maxY
        internal val clickListener = builder.clickListener

        override val panels: List<Panel>
        override val buttons: List<Button>

        val indexOffset: Int
            get() {
                var offset = minX + minY * 9
                parent?.let { offset += it.indexOffset }

                return offset
            }

        init {
            this.panels = ImmutableList.copyOf(builder.panels.map { Panel(this, it) })
            this.buttons = ImmutableList.copyOf(builder.buttons.map { Button(this, it) })

            builder.receiver?.invoke(this)
        }

        override fun panelAt(x: Int, y: Int): Panel? {
            return panels.find { it.minX <= x && it.maxX >= x && it.minY <= y && it.maxY >= y }
        }

        override fun buttonAt(x: Int, y: Int): Button? {
            return buttons.find { x == it.x && y == it.y }
        }

        fun onClick(x: Int, y: Int, event: InventoryClickEvent) {
            clickListener?.invoke(event)
            buttonAt(x, y)?.clickListener?.invoke(event)

            panelAt(x, y)?.run {
                val relX = x - minX
                val relY = y - minY

                onClick(relX, relY, event)
            }

            toIndex(x, y)
        }

        override fun getItem(x: Int, y: Int): ItemStack? {
            return inventory.getItem(toIndex(x, y))
        }

        override fun setItem(x: Int, y: Int, item: ItemStack?) {
            inventory.setItem(toIndex(x, y), item)
        }

        private fun toIndex(x: Int, y: Int): Int {
            require(x >= 0 && y >= 0 && x <= maxX - minX && y <= maxY - minY) { "Out of range" }

            return indexOffset + x + y * 9
        }
    }

    private inner class Button(val panel: Panel, builder: ButtonBuilder) : InvButton {
        override val x = builder.x
        override val y = builder.y
        internal val clickListener: ((InventoryClickEvent) -> Unit)? = builder.clickListener

        override var item: ItemStack?
            get() = panel.getItem(x, y)
            set(value) {
                panel.setItem(x, y, value)
            }

        init {
            builder.item?.let { this.item = it }
            builder.receiver?.invoke(this)
        }
    }

    private val rootPanel = Panel(null, builder)

    override fun onClickOutside(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    override fun onClickTop(event: InventoryClickEvent) {
        event.isCancelled = true

        val slot = event.slot
        val x = slot % 9
        val y = slot / 9
        rootPanel.onClick(x, y, event)
    }

    override fun onClickBottom(event: InventoryClickEvent) {
        event.isCancelled = true
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

interface InvPanel {
    val minX: Int
    val minY: Int
    val maxX: Int
    val maxY: Int
    val panels: List<InvPanel>
    val buttons: List<InvButton>

    fun panelAt(x: Int, y: Int): InvPanel?

    fun buttonAt(x: Int, y: Int): InvButton?

    fun getItem(x: Int, y: Int): ItemStack?

    fun setItem(x: Int, y: Int, item: ItemStack?)
}

interface InvButton {
    val x: Int
    val y: Int
    var item: ItemStack?
}