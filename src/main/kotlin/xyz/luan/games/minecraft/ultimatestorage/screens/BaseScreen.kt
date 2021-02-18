package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.renderer.Rectangle2d
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import xyz.luan.games.minecraft.ultimatestorage.containers.PLAYER_INVENTORY_ROW_COUNT
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment.Companion.DEFAULT_WIDTH
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment.Companion.closedUpgradeBottom
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment.Companion.closedUpgradeMiddle
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment.Companion.closedUpgradeTop
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment.Companion.openedUpgradeBottom
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment.Companion.openedUpgradeMiddle
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment.Companion.openedUpgradeTop
import java.awt.Color

val textColor = Color(55, 55, 55).rgb

abstract class BaseScreen<T : Container>(
    container: T,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : ContainerScreen<T>(container, playerInventory, title) {

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        renderHoveredTooltip(matrixStack, mouseX, mouseY)
    }

    private inner class BgRenderer(
        private val x: Int,
        private val y: Int,
        private val segment: BgSegment,
        private val heightOverride: Int? = null,
    ) {
        fun render(matrixStack: MatrixStack) {
            segment.render(this@BaseScreen, matrixStack, guiLeft + x, guiTop + y, heightOverride = heightOverride)
        }
    }

    private inner class FgRenderer(
        private val x: Float,
        private val y: Float,
        private val text: String,
    ) {
        fun render(matrixStack: MatrixStack) {
            // font rendering is already relative to guiLeft and guiTop apparently
            getMinecraft().fontRenderer.drawString(matrixStack, text, x, y, textColor)
        }
    }

    private inner class ButtonRenderer(
        private val x: Int,
        private val y: Int,
        private val w: Int,
        private val h: Int,
        private val text: String,
        private val action: (Button) -> Unit,
    ) {
        fun add() {
            addButton(Button(guiLeft + x, guiTop + y, w, h, TranslationTextComponent(text), action))
        }
    }

    private inner class ItemRenderer(
        private val x: Int,
        private val y: Int,
        private val item: Item,
    ) {
        fun render() {
            itemRenderer.renderItemAndEffectIntoGUI(ItemStack(item), guiLeft + x, guiTop + y)
        }
    }

    protected inner class Renderer {
        private var relativeY = 0

        private var bgRenderers = mutableListOf<BgRenderer>()
        private var fgRenderers = mutableListOf<FgRenderer>()
        private val buttons = mutableListOf<ButtonRenderer>()
        private val items = mutableListOf<ItemRenderer>()

        fun getWidth(): Int {
            return DEFAULT_WIDTH
        }

        fun getHeight(): Int {
            return relativeY
        }

        fun prepare(block: Renderer.() -> Unit) {
            clear()
            block()
        }

        private fun clear() {
            relativeY = 0
            bgRenderers.clear()
            fgRenderers.clear()
            buttons.clear()
            items.clear()
        }

        fun text(text: String, dx: Int, dy: Int) {
            val x = dx.toFloat()
            val y = relativeY + dy.toFloat() + getMinecraft().fontRenderer.FONT_HEIGHT / 2
            fgRenderers.add(FgRenderer(x, y, text))
        }

        fun drawItem(item: Item, dx: Int, dy: Int) {
            items.add(ItemRenderer(dx, relativeY + dy, item))
        }

        fun skip(segment: BgSegment) {
            relativeY += segment.drawHeight
        }

        fun renderPlayerInventory() {
            render(BgSegment.divider)
            repeat(PLAYER_INVENTORY_ROW_COUNT) {
                render(BgSegment.row)
            }
            render(BgSegment.divider)
            render(BgSegment.row)
            render(BgSegment.bottom)
        }

        fun renderUpgradeSection(locked: Boolean, height: Int, lambda: Renderer.() -> Unit) {
            val top = if (locked) closedUpgradeTop else openedUpgradeTop
            val middle = if (locked) closedUpgradeMiddle else openedUpgradeMiddle
            val bottom = if (locked) closedUpgradeBottom else openedUpgradeBottom

            val middleHeight = height - top.height - bottom.height
            bgRenderers.add(BgRenderer(0, relativeY, top))
            bgRenderers.add(BgRenderer(0, relativeY + top.height, middle, heightOverride = middleHeight))
            bgRenderers.add(BgRenderer(0, relativeY + height - bottom.height, bottom))

            lambda()
            relativeY += height
        }

        fun render(
            segment: BgSegment,
            renderSlotOverlay: BgSegment? = null,
            amount: Int = 0,
            lambda: Renderer.() -> Unit = {},
        ) {
            bgRenderers.add(BgRenderer(0, relativeY, segment))
            if (renderSlotOverlay != null) {
                val slots = segment.slots.take(amount).map {
                    BgRenderer(it.x, relativeY + it.y, renderSlotOverlay)
                }
                bgRenderers.addAll(slots)
            }
            lambda()
            skip(segment)
        }

        fun renderAt(segment: BgSegment, x: Int, y: Int): Rectangle2d {
            val relativeY = relativeY + y
            bgRenderers.add(BgRenderer(x, relativeY, segment))
            return Rectangle2d(x, relativeY, segment.width, segment.height)
        }

        fun button(x: Int, y: Int, w: Int, h: Int, text: String, action: (Button) -> Unit) {
            buttons.add(ButtonRenderer(x, relativeY + y, w, h, text, action))
        }

        fun renderBackground(matrixStack: MatrixStack) {
            bgRenderers.forEach { it.render(matrixStack) }
            items.forEach { it.render() }
        }

        fun renderForeground(matrixStack: MatrixStack) {
            fgRenderers.forEach { it.render(matrixStack) }
        }

        fun initButtons() {
            buttons.forEach { it.add() }
        }
    }
}