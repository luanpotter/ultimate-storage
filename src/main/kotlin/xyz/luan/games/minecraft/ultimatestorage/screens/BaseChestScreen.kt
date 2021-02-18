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

    private val renderer = Renderer()

    override fun init() {
        renderer.prepare {
            render(BgSegment.top)
            repeat(rows) {
                render(BgSegment.row)
            }
            renderPlayerInventory()
        }

        xSize = renderer.getWidth()
        ySize = renderer.getHeight()

        super.init()
    }

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, x: Int, y: Int) {
        renderer.renderForeground(matrixStack)
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        renderer.renderBackground(matrixStack)
    }
}