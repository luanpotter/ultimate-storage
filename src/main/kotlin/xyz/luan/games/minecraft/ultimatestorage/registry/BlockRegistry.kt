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
import xyz.luan.games.minecraft.ultimatestorage.Tier
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.blocks.BaseChestBlock
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity


object BlockRegistry {
    private val blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
    private val tileEntities = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID)
    private val containers = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID)

    class Registry(
        val tier: Tier,
        private val blockRegistry: RegistryObject<Block>,
        private val tileEntityRegistry: RegistryObject<TileEntityType<BaseChestTileEntity>>,
    ) {
        fun block(): Block {
            return blockRegistry.get()
        }

        fun tileEntity(): TileEntityType<BaseChestTileEntity> {
            return tileEntityRegistry.get()
        }
    }

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

    private fun registerTierChest(tier: Tier): Registry {
        val name = tier.name.toLowerCase()
        val chest = addBlock("${name}_tier_chest") { BaseChestBlock(tier) }
        val tileEntity = tileEntities.register("${name}_tier_chest_tile") {
            TileEntityType.Builder.create(
                { BaseChestTileEntity(tier, forTier(tier).tileEntity()) },
                chest.get(),
            ).build(null)
        }
        return Registry(tier, chest, tileEntity)
    }

    val tiers = Tier.values().map { registerTierChest(it) }

    fun forTier(tier: Tier): Registry {
        return tiers.single { it.tier == tier }
    }

    val baseChestContainer: RegistryObject<ContainerType<BaseChestContainer>> =
        containers.register("base_chest_container") { IForgeContainerType.create(::BaseChestContainer) }
}