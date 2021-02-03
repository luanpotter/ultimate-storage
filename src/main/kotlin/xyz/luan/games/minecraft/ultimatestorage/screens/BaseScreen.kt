package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent

abstract class BaseScreen<T : Container>(
    container: T,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : ContainerScreen<T>(container, playerInventory, title) {
    protected inner class Renderer(
        private val matrixStack: MatrixStack,
        private var currentY: Int,
    ) {
        fun render(segment: BgSegment, renderSlotOverlay: BgSegment? = null) {
            bindTexture(segment.texture)
            segment.render(guiLeft, currentY)
            if (renderSlotOverlay != null) {
                bindTexture(renderSlotOverlay.texture)
                segment.slots.forEach { renderSlotOverlay.render(guiLeft + it.x + 1, currentY + it.y + 1) }
            }
            currentY += segment.height
        }

        private fun BgSegment.render(x: Int, y: Int) {
            blit(matrixStack, x, y, this.x, this.y, width, height)
        }

        private fun bindTexture(texture: ResourceLocation) {
            getMinecraft().getTextureManager().bindTexture(texture)
        }
    }
}