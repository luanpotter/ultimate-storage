package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer

class BaseChestScreen(
    container: BaseChestContainer,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : BaseScreen<BaseChestContainer>(container, playerInventory, title) {
    private val rows: Int
        get() = container.rows

    init {
        xSize = 184
        ySize = BgSegment.top.height + rows * BgSegment.row.height + BgSegment.bottom.height
    }

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, x: Int, y: Int) {
        // no titles
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        val renderer = Renderer(matrixStack, guiTop)
        renderer.render(BgSegment.top)
        repeat(rows) {
            renderer.render(BgSegment.row)
        }
        renderer.render(BgSegment.bottom)
    }
}