package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.network.PacketBuffer
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

private const val playerInvRows = 3
private const val playerInvCols = 9

class BaseChestContainer constructor(
    windowId: Int,
    private var tile: BaseChestTileEntity,
    playerInventory: PlayerInventory,
) : Container(BlockRegistry.baseChestContainer.get(), windowId) {
    val rows = tile.rows
    private val cols = tile.cols

    init {
        setup(playerInventory)
    }

    constructor(windowId: Int, playerInventory: PlayerInventory, extraData: PacketBuffer) : this(
        windowId,
        playerInventory.player.world.getTileEntity(extraData.readBlockPos()) as BaseChestTileEntity,
        playerInventory,
    )

    private fun setup(inventory: PlayerInventory) {
        // Slots for chest
        val firstBlock = 8
        for (chestRow in 0 until rows) {
            for (chestCol in 0 until cols) {
                val x = 12 + chestCol * 18
                val y = firstBlock + chestRow * 18
                addSlot(Slot(tile.chestInventory, chestRow + chestCol * cols, x, y))
            }
        }
        // Slots for the main inventory
        val secondBlock = firstBlock + 4 + rows * 18
        for (row in 0 until playerInvRows) {
            for (col in 0 until playerInvCols) {
                val x = 12 + col * 18
                val y = secondBlock + row * 18
                addSlot(Slot(inventory, col + (row + 1) * 9, x, y))
            }
        }
        // Slots for the hotbar
        val thirdBlock = secondBlock + 4 + playerInvRows * 18
        for (row in 0 until playerInvCols) {
            val x = 12 + row * 18
            addSlot(Slot(inventory, row, x, thirdBlock))
        }
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true // TODO this
    }
}