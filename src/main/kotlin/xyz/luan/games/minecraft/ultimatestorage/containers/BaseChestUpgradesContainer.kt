package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.network.PacketBuffer
import org.apache.logging.log4j.LogManager
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

private val LOGGER = LogManager.getLogger()

class BaseChestUpgradesContainer constructor(
    windowId: Int,
    private var tile: BaseChestTileEntity,
    playerInventory: PlayerInventory,
) : Container(BlockRegistry.baseChestUpgradesContainer.get(), windowId) {
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
        val secondBlock = firstBlock + 400
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
        // main gui
        // TODO(luan) this
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true // TODO this
    }
}