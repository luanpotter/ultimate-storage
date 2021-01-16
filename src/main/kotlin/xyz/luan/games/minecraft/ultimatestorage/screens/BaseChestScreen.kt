package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer

private val background by lazy { ResourceLocation(MOD_ID, "textures/gui/sample-container.png") }

class BaseChestScreen(
    container: BaseChestContainer,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : ContainerScreen<BaseChestContainer>(container, playerInventory, title) {
    init {
        xSize = 256
        ySize = 256
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        getMinecraft().getTextureManager().bindTexture(background)
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize)
    }
}