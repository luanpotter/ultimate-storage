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
        renderer.prepare {
            render(BgSegment.top)
            val slotCount = container.tile.tier.upgradeSlots
            val slots = BgSegment.emptyRow.slots.take(slotCount)
            slots.forEachIndexed { idx, slot ->
                button(slot.x, slot.y + 18, 18, 8, "ˬ") { clickButton(idx) }
            }
            render(BgSegment.emptyRow, renderSlotOverlay = BgSegment.baseUpgradeOverlay, amount = slotCount)
            if (selectedTab == -1) {
                text("Select an upgrade to configure", dx = 18, dy = 2)
            } else {
                val upgrade = container.tile.chestUpgrades.getStackInSlot(selectedTab).item
                text("Configure ${upgrade.name.string}", dx = 18, dy = 2)
            }
            render(BgSegment.upgradesEmpty)
            render(BgSegment.bottom)
        }

        buttons.clear()
        renderer.initButtons()

        val slotCount = container.tile.tier.upgradeSlots
        val validTabs = (0 until slotCount).filter { idx ->
            val valid = container.tile.isConfigurableUpgradeSlot(idx)
            buttons[idx].active = true
            buttons[idx].visible = valid
            valid
        }
        if (selectedTab !in validTabs) {
            selectedTab = -1
        }
        if (selectedTab != -1) {
            buttons[selectedTab].active = false
        }
    }

    private fun clickButton(slot: Int) {
        selectedTab = slot
        reset()
    }

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, x: Int, y: Int) {
        renderer.renderForeground(matrixStack)
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        renderer.renderBackground(matrixStack)
    }
}