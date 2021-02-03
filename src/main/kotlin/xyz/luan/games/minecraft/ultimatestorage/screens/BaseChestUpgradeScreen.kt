package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestUpgradesContainer

class BaseChestUpgradeScreen(
    container: BaseChestUpgradesContainer,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : BaseScreen<BaseChestUpgradesContainer>(container, playerInventory, title) {
    init {
        xSize = 184
        ySize = BgSegment.top.height + BgSegment.row.height + BgSegment.upgrades.height + BgSegment.bottom.height
    }

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, x: Int, y: Int) {
        // no titles
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        val renderer = Renderer(matrixStack, guiTop)

        renderer.render(BgSegment.top)
        renderer.render(BgSegment.row, renderSlotOverlay = BgSegment.baseUpgradeOverlay)
        renderer.render(BgSegment.upgradesEmpty)
        renderer.render(BgSegment.bottom)
    }
}