package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import org.apache.logging.log4j.LogManager
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

private val LOGGER = LogManager.getLogger()

class BaseChestContainer constructor(
    windowId: Int,
    private var tile: BaseChestTileEntity,
    playerInventory: PlayerInventory,
) : Container(BlockRegistry.baseChestContainer.get(), windowId) {
    val rows
        get() = tile.rows
    private val cols
        get() = tile.cols
    private val inventorySize
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

    override fun transferStackInSlot(playerIn: PlayerEntity, sourceSlotIndex: Int): ItemStack? {
        val sourceSlot = inventorySlots[sourceSlotIndex]
        if (sourceSlot == null || !sourceSlot.hasStack) return ItemStack.EMPTY

        val sourceStack = sourceSlot.stack
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!mergeItemStack(
                    sourceStack,
                    CONTAINER_FIRST_SLOT_INDEX,
                    CONTAINER_FIRST_SLOT_INDEX + inventorySize,
                    false
                )
            ) {
                return ItemStack.EMPTY
            }
        } else if (sourceSlotIndex >= CONTAINER_FIRST_SLOT_INDEX && sourceSlotIndex < CONTAINER_FIRST_SLOT_INDEX + inventorySize) {
            // This is a TE slot so merge the stack into the players inventory
            if (!mergeItemStack(
                    sourceStack,
                    VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false
                )
            ) {
                return ItemStack.EMPTY
            }
        } else {
            LOGGER.warn("Invalid slotIndex:$sourceSlotIndex")
            return ItemStack.EMPTY
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {
            sourceSlot.putStack(ItemStack.EMPTY)
        } else {
            sourceSlot.onSlotChanged()
        }

        sourceSlot.onTake(playerIn, sourceStack)
        return copyOfSourceStack
    }
}