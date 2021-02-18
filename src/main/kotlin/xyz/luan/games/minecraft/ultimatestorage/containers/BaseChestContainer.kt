package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Slot
import net.minecraft.network.PacketBuffer
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.screens.BgSegment
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

class BaseChestContainer constructor(
    windowId: Int,
    private val tile: BaseChestTileEntity,
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
        val firstBlock = BgSegment.top.height + 1
        val secondBlock = firstBlock + BgSegment.divider.height + rows * 18

        addPlayerInventorySlots(inventory, secondBlock)

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