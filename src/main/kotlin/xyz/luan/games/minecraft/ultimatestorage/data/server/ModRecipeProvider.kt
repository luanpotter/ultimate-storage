package xyz.luan.games.minecraft.ultimatestorage.data.server

import net.minecraft.data.DataGenerator
import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.RecipeProvider
import net.minecraft.data.ShapedRecipeBuilder
import net.minecraft.item.Items
import net.minecraft.tags.ItemTags
import net.minecraftforge.common.Tags
import xyz.luan.games.minecraft.ultimatestorage.Tier
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.registry.ItemRegistry
import java.util.function.Consumer

class ModRecipeProvider(generator: DataGenerator) : RecipeProvider(generator) {
    override fun registerRecipes(consumer: Consumer<IFinishedRecipe>) {
        ShapedRecipeBuilder
            .shapedRecipe(BlockRegistry.forTier(Tier.WOOD).block())
            .patternLine(" H ")
            .patternLine("PCP")
            .patternLine(" R ")
            .key('H', Items.HOPPER)
            .key('P', Items.STICKY_PISTON)
            .key('C', Items.CHEST)
            .key('R', Items.REDSTONE)
            .addCriterion("has_chests", hasItem(Tags.Items.CHESTS))
            .build(consumer)

        ShapedRecipeBuilder
            .shapedRecipe(BlockRegistry.forTier(Tier.IRON).block())
            .patternLine("III")
            .patternLine("ICI")
            .patternLine("III")
            .key('C', BlockRegistry.forTier(Tier.WOOD).block())
            .key('I', Items.IRON_INGOT)
            .addCriterion("has_ultimate_chests", hasItem(BlockRegistry.forTier(Tier.WOOD).block()))
            .build(consumer)

        ShapedRecipeBuilder
            .shapedRecipe(BlockRegistry.forTier(Tier.GOLD).block())
            .patternLine("GGG")
            .patternLine("GCG")
            .patternLine("GGG")
            .key('C', BlockRegistry.forTier(Tier.IRON).block())
            .key('G', Items.GOLD_INGOT)
            .addCriterion("has_ultimate_chests", hasItem(BlockRegistry.forTier(Tier.IRON).block()))
            .build(consumer)

        ShapedRecipeBuilder
            .shapedRecipe(BlockRegistry.forTier(Tier.DIAMOND).block())
            .patternLine("DDD")
            .patternLine("DCD")
            .patternLine("DDD")
            .key('C', BlockRegistry.forTier(Tier.GOLD).block())
            .key('D', Items.DIAMOND)
            .addCriterion("has_ultimate_chests", hasItem(BlockRegistry.forTier(Tier.GOLD).block()))
            .build(consumer)

        ShapedRecipeBuilder
            .shapedRecipe(ItemRegistry.baseUpgrade.get(), 16)
            .patternLine("WIW")
            .patternLine("SBS")
            .patternLine("PRP")
            .key('W', ItemTags.LOGS)
            .key('I', Items.IRON_INGOT)
            .key('S', ItemTags.SLABS)
            .key('B', Items.SLIME_BALL)
            .key('P', Items.PAPER)
            .key('R', Items.REDSTONE)
            .addCriterion("has_ultimate_chests", hasItem(BlockRegistry.forTier(Tier.WOOD).block()))
            .build(consumer)

        ShapedRecipeBuilder
            .shapedRecipe(ItemRegistry.wrench.get())
            .patternLine("INI")
            .patternLine(" U ")
            .patternLine(" I ")
            .key('I', Items.IRON_INGOT)
            .key('N', Items.IRON_NUGGET)
            .key('U', ItemRegistry.baseUpgrade.get())
            .addCriterion("has_upgrades", hasItem(ItemRegistry.baseUpgrade.get()))
            .build(consumer)

        ShapedRecipeBuilder
            .shapedRecipe(ItemRegistry.capacityUpgrade.get())
            .patternLine("CSC")
            .patternLine("HUH")
            .patternLine("CSC")
            .key('C', Items.CHEST)
            .key('S', ItemTags.SLABS)
            .key('H', Items.HOPPER)
            .key('U', ItemRegistry.baseUpgrade.get())
            .addCriterion("has_upgrades", hasItem(ItemRegistry.baseUpgrade.get()))
            .build(consumer)

        ShapedRecipeBuilder
            .shapedRecipe(ItemRegistry.filterUpgrade.get())
            .patternLine(" R ")
            .patternLine("PUP")
            .patternLine(" R ")
            .key('R', Items.REDSTONE)
            .key('P', Items.PAPER)
            .key('U', ItemRegistry.baseUpgrade.get())
            .addCriterion("has_upgrades", hasItem(ItemRegistry.baseUpgrade.get()))
            .build(consumer)
    }

}