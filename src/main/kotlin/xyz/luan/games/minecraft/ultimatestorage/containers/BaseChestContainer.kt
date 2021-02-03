package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Slot
import net.minecraft.network.PacketBuffer
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

class BaseChestContainer constructor(
    windowId: Int,
    private var tile: BaseChestTileEntity,
    playerInventory: PlayerInventory,
) : GenericContainer(BlockRegistry.baseChestContainer.get(), windowId) {
    val rows
        get() = tile.rows
    private val cols
        get() = tile.cols

    override val inventorySize
        get() = tile.inventorySize

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
        val secondBlock = firstBlock + 4 + rows * 18
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
        // Slots for chest
        for (chestRow in 0 until rows) {
            for (chestCol in 0 until cols) {
                val x = 12 + chestCol * 18
                val y = firstBlock + chestRow * 18
                addSlot(Slot(tile.chestInventory, chestCol + chestRow * cols, x, y))
            }
        }
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true // TODO this
    }
}