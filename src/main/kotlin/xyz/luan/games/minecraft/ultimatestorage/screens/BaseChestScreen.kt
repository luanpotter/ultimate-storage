package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer

private val bgTop by lazy { ResourceLocation(MOD_ID, "textures/gui/container-top.png") }
private const val bgTopHeight = 25
private val bgRow by lazy { ResourceLocation(MOD_ID, "textures/gui/container-row.png") }
private const val bgRowHeight = 18
private val bgBottom by lazy { ResourceLocation(MOD_ID, "textures/gui/container-bottom.png") }
private const val bgBottomHeight = 141

class BaseChestScreen(
    container: BaseChestContainer,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : ContainerScreen<BaseChestContainer>(container, playerInventory, title) {
    private val rows = container.rows

    private val middleRows: Int
        get() = rows - 2

    init {
        xSize = 184
        ySize = bgTopHeight + middleRows * bgRowHeight + bgBottomHeight
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        RenderSystem.color4f(1f, 1f, 1f, 1f)

        val renderer = Renderer(matrixStack, guiTop)
        renderer.render(bgTop, bgTopHeight)
        repeat(middleRows) {
            renderer.render(bgRow, bgRowHeight)
        }
        renderer.render(bgBottom, bgBottomHeight)
    }

    private inner class Renderer(
        val matrixStack: MatrixStack,
        var currentY: Int,
    ) {
        fun render(texture: ResourceLocation, height: Int) {
            getMinecraft().getTextureManager().bindTexture(texture)
            blit(matrixStack, guiLeft, currentY, 0, 0, xSize, height)
            currentY += height
        }
    }
}