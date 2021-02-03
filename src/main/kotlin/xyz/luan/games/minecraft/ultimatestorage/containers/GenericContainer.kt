package xyz.luan.games.minecraft.ultimatestorage.containers

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.ItemStack
import org.apache.logging.log4j.LogManager

private val LOGGER = LogManager.getLogger()

abstract class GenericContainer(type: ContainerType<*>, windowId: Int) : Container(type, windowId) {
    abstract val inventorySize: Int

    abstract fun canAcceptItemStack(sourceStack: ItemStack): Boolean

    override fun transferStackInSlot(playerIn: PlayerEntity, sourceSlotIndex: Int): ItemStack? {
        val sourceSlot = inventorySlots[sourceSlotIndex]
        if (sourceSlot == null || !sourceSlot.hasStack) return ItemStack.EMPTY

        val sourceStack = sourceSlot.stack
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!canAcceptItemStack(sourceStack)) {
                return ItemStack.EMPTY
            }
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
            LOGGER.warn("Invalid slotIndex: $sourceSlotIndex")
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