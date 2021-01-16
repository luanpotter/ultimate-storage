package xyz.luan.games.minecraft.ultimatestorage.registry

import net.minecraft.block.Block
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.blocks.BaseChestBlock
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity


object BlockRegistry {
    private val blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
    private val tileEntities = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID)
    private val containers = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID)

    fun register() {
        val bus = MOD_CONTEXT.getKEventBus()
        blocks.register(bus)
        tileEntities.register(bus)
        containers.register(bus)
    }

    private fun addBlock(name: String, block: () -> Block): RegistryObject<Block> {
        return blocks.register(name, block).also {
            ItemRegistry.addItem(name) { BlockItem(it.get(), Item.Properties().group(ItemGroup.MISC)) }
        }
    }

    val baseChest = addBlock("base_chest") { BaseChestBlock() }

    val baseChestTileEntity: RegistryObject<TileEntityType<BaseChestTileEntity>> =
        tileEntities.register("base_chest_tile") {
            TileEntityType.Builder.create(
                { BaseChestTileEntity() },
                baseChest.get()
            ).build(null)
        }
    val baseChestContainer: RegistryObject<ContainerType<BaseChestContainer>> =
        containers.register("base_chest_container") { IForgeContainerType.create(::BaseChestContainer) }
}