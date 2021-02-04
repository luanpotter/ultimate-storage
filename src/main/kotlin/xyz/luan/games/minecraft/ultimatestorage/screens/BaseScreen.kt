package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import java.awt.Color

val textColor = Color(55, 55, 55).rgb

abstract class BaseScreen<T : Container>(
    container: T,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : ContainerScreen<T>(container, playerInventory, title) {
    protected inner class Renderer(
        private val matrixStack: MatrixStack,
        private var currentY: Int,
    ) {
        fun text(text: String, dx: Int, dy: Int) {
            val x = dx.toFloat()
            val y = (currentY - guiTop) + dy.toFloat() + getMinecraft().fontRenderer.FONT_HEIGHT
            getMinecraft().fontRenderer.drawString(matrixStack, text, x, y, textColor)
        }

        fun skip(segment: BgSegment) {
            currentY += segment.height
        }

        fun render(segment: BgSegment, renderSlotOverlay: BgSegment? = null, amount: Int = 0) {
            bindTexture(segment.texture)
            segment.render(guiLeft, currentY)
            if (renderSlotOverlay != null) {
                bindTexture(renderSlotOverlay.texture)
                segment.slots.take(amount).forEach { renderSlotOverlay.render(guiLeft + it.x, currentY + it.y) }
            }
            skip(segment)
        }

        private fun BgSegment.render(dx: Int, dy: Int) {
            blit(matrixStack, dx, dy, this.x, this.y, width, height)
        }

        private fun bindTexture(texture: ResourceLocation) {
            getMinecraft().textureManager.bindTexture(texture)
        }
    }
}