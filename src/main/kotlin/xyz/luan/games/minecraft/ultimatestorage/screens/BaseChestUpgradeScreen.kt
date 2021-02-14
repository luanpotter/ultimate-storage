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

    private val renderer = Renderer()
    private var selectedTab = -1

    override fun init() {
        super.init()

        selectedTab = -1
        container.tile.chestUpgrades.addListener { reset() }
        reset()
    }

    private fun reset() {
        val validTabs = mutableListOf<Int>()
        renderer.prepare {
            render(BgSegment.top)
            val slotCount = container.tile.tier.upgradeSlots
            val slots = BgSegment.emptyRow.slots.take(slotCount)
            slots.forEachIndexed { idx, slot ->
                if (container.tile.isConfigurableUpgradeSlot(idx)) {
                    validTabs.add(idx)
                    button(slot.x, slot.y + 18, 18, 8, "ˬ") {
                        selectedTab = idx
                        reset()
                    }
                }
            }
            render(BgSegment.emptyRow, renderSlotOverlay = BgSegment.baseUpgradeOverlay, amount = slotCount)
            text("Select an upgrade to configure", dx = 18, dy = 2)
            render(BgSegment.upgradesEmpty)
            render(BgSegment.bottom)
        }
        if (selectedTab !in validTabs) {
            selectedTab = -1
        }

        buttons.clear()
        renderer.initButtons()
    }

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, x: Int, y: Int) {
        renderer.renderForeground(matrixStack)
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        renderer.renderBackground(matrixStack)
    }
}