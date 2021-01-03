package xyz.luan.games.minecraft.ultimatestorage.registry

import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID

object ItemRegistry {
    private val items = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)

    fun register() {
        val bus = MOD_CONTEXT.getKEventBus()
        items.register(bus)
    }

    fun addItem(name: String, supplier: () -> Item) {
        items.register(name, supplier)
    }

    val baseUpgrade = addItem("base_upgrade") { Item(Item.Properties().group(ItemGroup.MISC)) }
}