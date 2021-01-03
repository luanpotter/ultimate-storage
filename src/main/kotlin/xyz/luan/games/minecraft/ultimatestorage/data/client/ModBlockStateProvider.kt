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
        simpleBlock(BlockRegistry.baseChest.get())
    }
}