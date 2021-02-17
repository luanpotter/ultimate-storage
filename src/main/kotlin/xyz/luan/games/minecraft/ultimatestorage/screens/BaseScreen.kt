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
    ) {
        fun render(matrixStack: MatrixStack) {
            segment.render(this@BaseScreen, matrixStack, x, y)
        }
    }

    private inner class FgRenderer(
        private val x: Float,
        private val y: Float,
        private val text: String,
    ) {
        fun render(matrixStack: MatrixStack) {
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
            addButton(Button(x, y, w, h, TranslationTextComponent(text), action))
        }
    }

    private inner class ItemRenderer(
        private val x: Int,
        private val y: Int,
        private val item: Item,
    ) {
        fun render() {
            itemRenderer.renderItemAndEffectIntoGUI(ItemStack(item), x, y)
        }
    }

    protected inner class Renderer {
        private var relativeY = 0

        private var bgRenderers = mutableListOf<BgRenderer>()
        private var fgRenderers = mutableListOf<FgRenderer>()
        private val buttons = mutableListOf<ButtonRenderer>()
        private val items = mutableListOf<ItemRenderer>()

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
            items.add(ItemRenderer(guiLeft + dx, guiTop + relativeY + dy, item))
        }

        fun skip(segment: BgSegment) {
            relativeY += segment.height
        }

        fun render(
            segment: BgSegment,
            renderSlotOverlay: BgSegment? = null,
            amount: Int = 0,
            lambda: Renderer.() -> Unit = {},
        ) {
            bgRenderers.add(BgRenderer(guiLeft, guiTop + relativeY, segment))
            if (renderSlotOverlay != null) {
                val slots = segment.slots.take(amount).map {
                    BgRenderer(guiLeft + it.x, guiTop + relativeY + it.y, renderSlotOverlay)
                }
                bgRenderers.addAll(slots)
            }
            lambda()
            skip(segment)
        }

        fun renderAt(segment: BgSegment, x: Int, y: Int): Rectangle2d {
            val screenX = guiLeft + x
            val screenY = guiTop + relativeY + y
            bgRenderers.add(BgRenderer(screenX, screenY, segment))
            return Rectangle2d(screenX, screenY, segment.width, segment.height)
        }

        fun button(x: Int, y: Int, w: Int, h: Int, text: String, action: (Button) -> Unit) {
            buttons.add(ButtonRenderer(guiLeft + x, guiTop + relativeY + y, w, h, text, action))
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