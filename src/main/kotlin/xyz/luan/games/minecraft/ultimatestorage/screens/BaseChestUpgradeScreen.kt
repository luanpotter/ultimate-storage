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
        set(value) {
            field = value
            newFilterItemTab = -1
        }

    private val selectables = mutableListOf<Selectable>()
    private var currentSelected: Selectable? = null
    private var newFilterItemTab = -1

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
        currentSelected = null

        renderer.prepare {
            selectables.clear()
            render(BgSegment.top)

            val slotCount = container.tile.tier.upgradeSlots
            render(BgSegment.emptyRow, renderSlotOverlay = BgSegment.baseUpgradeOverlay, amount = slotCount)
            render(BgSegment.divider) {
                val slots = BgSegment.row.slots.take(slotCount)
                slots.forEachIndexed { idx, slot ->
                    button(slot.x, slot.y, 18, 6, "", "Configure this upgrade") { clickButton(idx) }
                }
            }
            when {
                newFilterItemTab != -1 -> {
                    val fullHeight = 76
                    renderUpgradeSection(locked = false, height = fullHeight) {
                        val dx = 12
                        val dy = 4
                        val height = 12
                        listOf("Item", "Name", "Tag", "Mod", "Special").forEachIndexed { idx, name ->
                            // TODO(luan) add tooltips
                            button(dx, dy + (height + 2) * idx, 50, height, name, name) {
                                newFilterItemTab = idx
                                reset()
                            }
                        }

                        val width = 40
                        button(130, dy + (height + 2) * 0, width, height, "Save", "Save changes and go back") {
                            val newFilter = ItemFilter(Item.getIdFromItem(Items.SLIME_BALL), 200)
                            val upgrade = container.tile.chestUpgrades.getStackInSlot(selectedTab)
                            val filters = getItemFilterData(upgrade) + newFilter
                            setItemFilterData(upgrade, filters)
                            StorageUpdatePacket.send(container.tile.pos, selectedTab, upgrade)
                            newFilterItemTab = -1
                            reset()
                        }
                        button(130, dy + (height + 2) * 1, width, height, "Cancel", "Ignore changes and go back") {
                            newFilterItemTab = -1
                            reset()
                        }
                    }
                    container.setup(fullHeight)
                }
                selectedTab == -1 -> {
                    renderUpgradeSection(locked = true, height = UPGRADE_ROW_HEIGHT) {
                        text("Select an upgrade to configure", dx = 12, dy = 0)
                    }
                    container.setup(UPGRADE_ROW_HEIGHT)
                }
                else -> {
                    renderUpgradeSection(locked = false, height = UPGRADE_ROW_HEIGHT) {
                        val upgrade = container.tile.chestUpgrades.getStackInSlot(selectedTab)
                        val filters = getItemFilterData(upgrade)

                        val dx = 12
                        val dy = 14
                        val delta = 18
                        text("Configure ${upgrade.item.name.string}", dx = dx, dy = 0)
                        val currentFilters = filters.mapIndexed { idx, filter ->
                            drawItem(filter.item(), dx + delta * idx + 1, dy + 1)
                            val rect = renderAt(BgSegment.removeOverlay, dx + delta * idx, dy)
                            Selectable(rect, onClick = {
                                setItemFilterData(upgrade, filters.filter { it !== filter })
                                StorageUpdatePacket.send(container.tile.pos, selectedTab, upgrade)
                                reset()
                                true
                            })
                        }
                        selectables.addAll(currentFilters)
                        val addNewButton = renderAt(BgSegment.plusButton, dx + delta * filters.size, dy)
                        selectables.add(
                            Selectable(addNewButton, onClick = {
                                newFilterItemTab = 0
                                reset()
                                true
                            })
                        )
                    }
                    container.setup(UPGRADE_ROW_HEIGHT)
                }
            }
            renderPlayerInventory()
        }

        xSize = renderer.getWidth()
        ySize = renderer.getHeight()
        guiLeft = (width - xSize) / 2
        guiTop = (height - ySize) / 2

        buttons.clear()
        renderer.initButtons()

        val slotCount = container.tile.tier.upgradeSlots
        val validTabs = (0 until slotCount).filter { idx ->
            val valid = container.tile.isConfigurableUpgradeSlot(idx)
            buttons[idx].active = true
            buttons[idx].visible = valid
            valid
        }
        buttons.drop(slotCount).forEachIndexed { idx, button ->
            button.active = newFilterItemTab != idx
            button.visible = true
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
        currentSelected?.let {
            BgSegment.hoverOverlay.render(this, matrixStack, guiLeft + it.rect.x, guiTop + it.rect.y)
        }
    }

    companion object {
        const val UPGRADE_ROW_HEIGHT = 36
    }
}