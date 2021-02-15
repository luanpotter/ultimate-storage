package xyz.luan.games.minecraft.ultimatestorage.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.itemGroup
import xyz.luan.games.minecraft.ultimatestorage.toNBTList

data class ItemFilter(
    private val itemId: Int,
    private val itemCount: Int,
) {
    fun write(): CompoundNBT {
        return CompoundNBT().apply {
            putInt("item_id", itemId)
            putInt("item_count", itemCount)
        }
    }

    fun tooltip(): String {
        return "${itemCount}x ${item().name.string}"
    }

    fun item(): Item {
        return Item.getItemById(itemId)
    }

    companion object {
        fun parse(nbt: CompoundNBT): ItemFilter {
            val itemId = nbt.getInt("item_id")
            val itemCount = nbt.getInt("item_count")
            return ItemFilter(itemId, itemCount)
        }
    }
}

class ItemFilterUpgrade : Item(
    Properties().group(itemGroup),
) {
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<ITextComponent>,
        flagIn: ITooltipFlag
    ) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        val nbt = getItemFilterData(stack)
        if (nbt.isNotEmpty()) {
            tooltip.add(StringTextComponent("Filters").mergeStyle(TextFormatting.GRAY))
            nbt.forEach { tooltip.add(StringTextComponent(" - ${it.tooltip()}").mergeStyle(TextFormatting.GRAY)) }
        } else {
            tooltip.add(StringTextComponent("Empty").mergeStyle(TextFormatting.GRAY))
        }
    }

    companion object {
        fun setItemFilterData(itemStack: ItemStack, items: List<ItemFilter>) {
            val nbtData = getOrCreateNBT(itemStack)
            nbtData.put("filters", items.map { it.write() }.toNBTList())
            itemStack.tag = nbtData
        }

        fun getItemFilterData(itemStack: ItemStack): List<ItemFilter> {
            val nbtData = getOrCreateNBT(itemStack)
            val nbtList = (nbtData["filters"] as ListNBT?) ?: ListNBT()
            return nbtList.map { ItemFilter.parse(it as CompoundNBT) }
        }

        private fun getOrCreateNBT(itemStack: ItemStack): CompoundNBT {
            return itemStack.tag ?: CompoundNBT()
        }
    }
}