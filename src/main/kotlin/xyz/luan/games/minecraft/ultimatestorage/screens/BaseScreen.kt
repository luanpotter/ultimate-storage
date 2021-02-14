package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TranslationTextComponent
import java.awt.Color

val textColor = Color(55, 55, 55).rgb

abstract class BaseScreen<T : Container>(
    container: T,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : ContainerScreen<T>(container, playerInventory, title) {

    private inner class BgRenderer(
        private val x: Int,
        private val y: Int,
        private val segment: BgSegment,
    ) {
        fun render(matrixStack: MatrixStack) {
            bindTexture(segment.texture)
            segment.render(matrixStack, x, y)
        }

        private fun BgSegment.render(matrixStack: MatrixStack, dx: Int, dy: Int) {
            blit(matrixStack, dx, dy, this.x, this.y, width, height)
        }

        private fun bindTexture(texture: ResourceLocation) {
            getMinecraft().textureManager.bindTexture(texture)
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

    protected inner class Renderer {
        var relativeY = 0

        private var bgRenderers = mutableListOf<BgRenderer>()
        private var fgRenderers = mutableListOf<FgRenderer>()
        private val buttons = mutableListOf<ButtonRenderer>()

        fun prepare(block: Renderer.() -> Unit) {
            clear()
            block()
        }

        private fun clear() {
            relativeY = 0
            bgRenderers.clear()
            fgRenderers.clear()
        }

        fun text(text: String, dx: Int, dy: Int) {
            val x = dx.toFloat()
            val y = relativeY + dy.toFloat() + getMinecraft().fontRenderer.FONT_HEIGHT
            fgRenderers.add(FgRenderer(x, y, text))
        }

        fun skip(segment: BgSegment) {
            relativeY += segment.height
        }

        fun render(segment: BgSegment, renderSlotOverlay: BgSegment? = null, amount: Int = 0) {
            bgRenderers.add(BgRenderer(guiLeft, guiTop + relativeY, segment))
            if (renderSlotOverlay != null) {
                val slots = segment.slots.take(amount).map {
                    BgRenderer(guiLeft + it.x, guiTop + relativeY + it.y, renderSlotOverlay)
                }
                bgRenderers.addAll(slots)
            }
            skip(segment)
        }

        fun button(x: Int, y: Int, w: Int, h: Int, text: String, action: (Button) -> Unit) {
            buttons.add(ButtonRenderer(guiLeft + x, guiTop + relativeY + y, w, h, text, action))
        }

        fun renderBackground(matrixStack: MatrixStack) {
            bgRenderers.forEach { it.render(matrixStack) }
        }

        fun renderForeground(matrixStack: MatrixStack) {
            fgRenderers.forEach { it.render(matrixStack) }
        }

        fun initButtons() {
            buttons.forEach { it.add() }
        }
    }
}