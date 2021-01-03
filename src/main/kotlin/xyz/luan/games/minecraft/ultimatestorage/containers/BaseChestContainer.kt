package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.network.PacketBuffer
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity


class BaseChestContainer : Container {
    private val rows = 10
    private val cols = 10
    private var tile: BaseChestTileEntity

    constructor(windowId: Int, playerInventory: PlayerInventory, extraData: PacketBuffer) : this(
        windowId,
        playerInventory.player.world.getTileEntity(extraData.readBlockPos()) as BaseChestTileEntity,
        playerInventory,
    )

    constructor(
        windowId: Int,
        tile: BaseChestTileEntity,
        playerInventory: PlayerInventory,
    ) : super(BlockRegistry.baseChestContainer.get(), windowId) {
        this.tile = tile
        this.setup(playerInventory);
    }

    private fun setup(inventory: PlayerInventory) {
        // Slots for the hotbar
        for (row in 0..8) {
            val x = 8 + row * 18
            val y = 56 + 86
            addSlot(Slot(inventory, row, x, y))
        }
        // Slots for the main inventory
        for (row in 1..3) {
            for (col in 0..8) {
                val x = 8 + col * 18
                val y = row * 18 + (56 + 10)
                addSlot(Slot(inventory, col + row * 9, x, y))
            }
        }
        // Slots for chest
        for (chestRow in 0 until rows) {
            for (chestCol in 0 until cols) {
                addSlot(
                    Slot(
                        tile.chestInventory,
                        chestCol + chestRow * rows,
                        12 + chestCol * 18,
                        8 + chestRow * 18,
                    ),
                )
            }
        }
    }

    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true // TODO this
    }
}