package xyz.luan.games.minecraft.ultimatestorage.data.server

import net.minecraft.data.*
import net.minecraft.item.Items
import net.minecraftforge.common.Tags
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import java.util.function.Consumer

class ModRecipeProvider(generator: DataGenerator) : RecipeProvider(generator) {
    override fun registerRecipes(consumer: Consumer<IFinishedRecipe>) {
        ShapedRecipeBuilder
            .shapedRecipe(BlockRegistry.baseChest.get())
            .patternLine(" H ")
            .patternLine("PCP")
            .patternLine(" R ")
            .key('H', Items.HOPPER)
            .key('P', Items.STICKY_PISTON)
            .key('C', Items.CHEST)
            .key('R', Items.REDSTONE)
            .addCriterion("has_chests", hasItem(Tags.Items.CHESTS))
            .build(consumer)
    }

}