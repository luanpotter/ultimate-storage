package xyz.luan.games.minecraft.ultimatestorage.data.client

import net.minecraft.block.Block
import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile
import net.minecraftforge.common.data.ExistingFileHelper
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry


class ModItemModelProvider(
    generator: DataGenerator,
    existingFileHelper: ExistingFileHelper,
) : ItemModelProvider(generator, MOD_ID, existingFileHelper) {
    override fun registerModels() {
        val itemGenerated = getExistingFile(mcLoc("item/generated"))
        getBuilder("base_upgrade").parent(itemGenerated).texture("layer0", "item/base_upgrade")

        // block items
        registerBlockModel(BlockRegistry.baseChest.get())
    }

    private fun registerBlockModel(block: Block) {
        val path = block.registryName!!.path
        getBuilder(path).parent(UncheckedModelFile(modLoc("block/$path")))
    }
}