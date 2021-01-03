package xyz.luan.games.minecraft.ultimatestorage.data.client

import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID

class ModItemModelProvider(
    generator: DataGenerator,
    existingFileHelper: ExistingFileHelper,
) : ItemModelProvider(generator, MOD_ID, existingFileHelper) {
    override fun registerModels() {
        val itemGenerated = getExistingFile(mcLoc("item/generated"))
        getBuilder("base_upgrade").parent(itemGenerated).texture("layer0", "item/base_upgrade")

        // withExistingParent("base_chest", modLoc("block/base_chest"))
    }
}