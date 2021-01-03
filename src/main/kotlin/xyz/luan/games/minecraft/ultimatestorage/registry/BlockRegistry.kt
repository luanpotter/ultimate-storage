package xyz.luan.games.minecraft.ultimatestorage.registry

import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID

object BlockRegistry {
    private val blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)

    fun register() {
        val bus = MOD_CONTEXT.getKEventBus()
        blocks.register(bus)
    }

    private fun addBlock(name: String, block: () -> Block): RegistryObject<Block> {
        return blocks.register(name, block).also {
            ItemRegistry.addItem(name) { BlockItem(it.get(), Item.Properties().group(ItemGroup.MISC)) }
        }
    }

    val baseChest = addBlock("base_chest") {
        Block(
            AbstractBlock.Properties
                .create(Material.WOOD)
                .hardnessAndResistance(3F, 10F)
                .sound(SoundType.WOOD)
        )
    }
}