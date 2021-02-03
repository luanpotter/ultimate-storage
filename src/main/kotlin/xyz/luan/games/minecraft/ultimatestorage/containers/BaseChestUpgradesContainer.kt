package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.registry.ItemRegistry
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

class BaseChestUpgradesContainer constructor(
    windowId: Int,
    val tile: BaseChestTileEntity,
    playerInventory: PlayerInventory,
) : GenericContainer(BlockRegistry.baseChestUpgradesContainer.get(), windowId) {
    override val inventorySize
        get() = tile.chestUpgrades.sizeInventory

    init {
        setup(playerInventory)
    }

    constructor(windowId: Int, playerInventory: PlayerInventory, extraData: PacketBuffer) : this(
        windowId,
        playerInventory.player.world.getTileEntity(extraData.readBlockPos()) as BaseChestTileEntity,
        playerInventory,
    )

    private fun setup(inventory: PlayerInventory) {
        val firstBlock = 8
        val secondBlock = firstBlock + BgSegment.upgrades.height + 18 + 4
        val thirdBlock = secondBlock + 4 + PLAYER_INVENTORY_ROW_COUNT * 18
        // Slots for the hotbar
        for (row in 0 until PLAYER_INVENTORY_COLUMN_COUNT) {
            val x = 12 + row * 18
            addSlot(Slot(inventory, row, x, thirdBlock))
        }
        // Slots for the main inventory
        for (row in 0 until PLAYER_INVENTORY_ROW_COUNT) {
            for (col in 0 until PLAYER_INVENTORY_COLUMN_COUNT) {
                val x = 12 + col * 18
                val y = secondBlock + row * 18
                addSlot(Slot(inventory, col + (row + 1) * 9, x, y))
            }
        }
        // main upgrade gui
        for (idx in 0 until tile.chestUpgrades.sizeInventory) {
            addSlot(UpgradeSlot(tile.chestUpgrades, idx, 12 + idx * 18, firstBlock))
        }
    }

    class UpgradeSlot(
        inventoryIn: IInventory,
        index: Int,
        xPosition: Int,
        yPosition: Int
    ) : Slot(inventoryIn, index, xPosition, yPosition) {
        override fun getSlotStackLimit(): Int {
            return 1
        }

        override fun isItemValid(stack: ItemStack): Boolean {
            return ItemRegistry.allUpgrades.any { it.get() == stack.item }
        }
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true // TODO this
    }
}