package xyz.luan.games.minecraft.ultimatestorage.registry

import net.minecraft.item.Item
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.itemGroup
import xyz.luan.games.minecraft.ultimatestorage.items.ItemWrench

object ItemRegistry {
    private val items = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)

    fun register() {
        val bus = MOD_CONTEXT.getKEventBus()
        items.register(bus)
    }

    fun addItem(name: String, supplier: () -> Item): RegistryObject<Item> {
        return items.register(name, supplier)
    }

    val wrench = addItem("wrench") { ItemWrench() }

    val baseUpgrade = addItem("base_upgrade") { Item(Item.Properties().group(itemGroup)) }
    val capacityUpgrade = addItem("capacity_upgrade") { Item(Item.Properties().group(itemGroup)) }
    val filterUpgrade = addItem("filter_upgrade") { Item(Item.Properties().group(itemGroup)) }

    val allUpgrades = listOf(capacityUpgrade, filterUpgrade)
    val configurableUpgrades = listOf(filterUpgrade)
}