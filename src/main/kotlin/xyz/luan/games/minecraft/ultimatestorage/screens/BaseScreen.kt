package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.text.ITextComponent

abstract class BaseScreen<T : Container>(
    container: T,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : ContainerScreen<T>(container, playerInventory, title) {
    protected inner class Renderer(
        val matrixStack: MatrixStack,
        var currentY: Int,
    ) {
        fun render(segment: BgSegment) {
            getMinecraft().getTextureManager().bindTexture(segment.texture)
            blit(matrixStack, guiLeft, currentY, 0, 0, segment.width, segment.height)
            currentY += segment.height
        }
    }
}