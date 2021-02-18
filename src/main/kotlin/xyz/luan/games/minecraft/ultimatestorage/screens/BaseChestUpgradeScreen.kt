package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.Rectangle2d
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.text.ITextComponent
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestUpgradesContainer
import xyz.luan.games.minecraft.ultimatestorage.items.ItemFilter
import xyz.luan.games.minecraft.ultimatestorage.items.ItemFilterUpgrade.Companion.getItemFilterData
import xyz.luan.games.minecraft.ultimatestorage.items.ItemFilterUpgrade.Companion.setItemFilterData

class BaseChestUpgradeScreen(
    container: BaseChestUpgradesContainer,
    playerInventory: PlayerInventory,
    title: ITextComponent,
) : BaseScreen<BaseChestUpgradesContainer>(container, playerInventory, title) {

    private val renderer = Renderer()
    private var selectedTab = -1
    private val selectables = mutableListOf<Selectable>()
    private var currentSelected: Selectable? = null

    inner class Selectable(
        // note: this is on relative coordinates!
        val rect: Rectangle2d,
        val onClick: () -> Boolean,
    )

    override fun init() {
        selectedTab = -1
        reset()
        super.init()

        container.tile.chestUpgrades.addListener { reset() }
    }

    override fun mouseMoved(x: Double, y: Double) {
        super.mouseMoved(x, y)
        val relativeX = x.toInt() - guiLeft
        val relativeY = y.toInt() - guiTop
        currentSelected = selectables.find { it.rect.contains(relativeX, relativeY) }
    }

    override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
        if (currentSelected?.let { it.onClick() } == true) {
            return true
        }
        return super.mouseClicked(x, y, button)
    }

    private fun reset() {
        renderer.prepare {
            render(BgSegment.top)

            val slotCount = container.tile.tier.upgradeSlots
            val slots = BgSegment.row.slots.take(slotCount)
            slots.forEachIndexed { idx, slot ->
                button(slot.x, slot.y + 18, 18, 8, "ˬ") { clickButton(idx) }
            }
            render(BgSegment.emptyRow, renderSlotOverlay = BgSegment.baseUpgradeOverlay, amount = slotCount)
            if (selectedTab == -1) {
                renderUpgradeSection(locked = true, height = UPGRADE_ROW_HEIGHT) {
                    text("Select an upgrade to configure", dx = 12, dy = 0)
                }
            } else {
                renderUpgradeSection(locked = false, height = UPGRADE_ROW_HEIGHT) {
                    val upgrade = container.tile.chestUpgrades.getStackInSlot(selectedTab)
                    val filters = getItemFilterData(upgrade)

                    val dx = 12
                    val dy = 14
                    val delta = 18
                    text("Configure ${upgrade.item.name.string}", dx = dx, dy = 0)
                    selectables.clear()
                    val currentFilters = filters.mapIndexed { idx, filter ->
                        drawItem(filter.item(), dx + delta * idx + 1, dy + 1)
                        val rect = renderAt(BgSegment.removeOverlay, dx + delta * idx, dy)
                        Selectable(rect, onClick = {
                            setItemFilterData(upgrade, filters.filter { it != filter })
                            StorageUpdatePacket.send(container.tile.pos, selectedTab, upgrade)
                            reset()
                            true
                        })
                    }
                    selectables.addAll(currentFilters)
                    val addNewButton = renderAt(BgSegment.plusButton, dx + delta * filters.size, dy)
                    selectables.add(
                        Selectable(addNewButton, onClick = { false })
                    )
                }
            }
            renderPlayerInventory()
        }

        xSize = renderer.getWidth()
        ySize = renderer.getHeight()

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

        val upgrade = container.tile.chestUpgrades.getStackInSlot(selectedTab)
        if (getItemFilterData(upgrade).isEmpty()) {
            setItemFilterData(
                upgrade,
                listOf(
                    ItemFilter(Item.getIdFromItem(Items.PAPER), 12),
                    ItemFilter(Item.getIdFromItem(Items.BIRCH_BOAT), 2),
                    ItemFilter(Item.getIdFromItem(Items.SLIME_BALL), 200),
                ),
            )
            StorageUpdatePacket.send(container.tile.pos, selectedTab, upgrade)
        }

        reset()
    }

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, x: Int, y: Int) {
        renderer.renderForeground(matrixStack)
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, x: Int, y: Int) {
        renderer.renderBackground(matrixStack)
        currentSelected?.let {
            BgSegment.hoverOverlay.render(this, matrixStack, guiLeft + it.rect.x, guiTop + it.rect.y)
        }
    }

    companion object {
        const val UPGRADE_ROW_HEIGHT = 36
    }
}