package xyz.luan.games.minecraft.ultimatestorage

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT

// The regular Inventory.read/write only writes non-empty slots and thus the ordering is lost

fun Inventory.writeOrdered(): ListNBT {
    return getContents().mapIndexed { index, itemStack ->
        CompoundNBT().apply {
            putInt("slot", index)
            put("content", itemStack.write(CompoundNBT()))
        }
    }.toNBTList()
}

fun Inventory.readOrdered(list: ListNBT) {
    list.forEach { element ->
        val nbt = element as CompoundNBT
        val slot = nbt.getInt("slot")
        val stack = ItemStack.read(nbt.get("content") as CompoundNBT)
        this.setInventorySlotContents(slot, stack)
    }
}

fun Inventory.getContents(): List<ItemStack> {
    return (0 until sizeInventory).map { getStackInSlot(it) }
}

fun List<CompoundNBT>.toNBTList(): ListNBT {
    val list = ListNBT()
    forEach { list.add(it) }
    return list
}