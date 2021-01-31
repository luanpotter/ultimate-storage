package xyz.luan.games.minecraft.ultimatestorage.data.client

import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.data.ExistingFileHelper
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry


class ModBlockStateProvider(
    generator: DataGenerator,
    existingFileHelper: ExistingFileHelper,
) : BlockStateProvider(generator, MOD_ID, existingFileHelper) {
    override fun registerStatesAndModels() {
        BlockRegistry.tiers.forEach { registerTextureForTier(it) }
    }

    private fun registerTextureForTier(block: BlockRegistry.Registry) {
        val name = block.tier.name.toLowerCase()
        horizontalBlock(
            block.block(),
            models().orientableWithBottom(
                block.block().registryName!!.path,
                modLoc("block/gen/$name-side"),
                modLoc("block/gen/$name-front"),
                modLoc("block/gen/$name-bottom"),
                modLoc("block/gen/$name-top"),
            ).texture("particle", modLoc("block/gen/$name-side")),
        )
    }
}