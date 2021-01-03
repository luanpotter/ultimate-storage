package xyz.luan.games.minecraft.ultimatestorage.data.server

import com.mojang.datafixers.util.Pair
import net.minecraft.block.Block
import net.minecraft.data.DataGenerator
import net.minecraft.data.LootTableProvider
import net.minecraft.data.loot.BlockLootTables
import net.minecraft.loot.*
import net.minecraft.loot.conditions.SurvivesExplosion
import net.minecraft.loot.functions.CopyName
import net.minecraft.util.ResourceLocation
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

class ModLootTableProvider(generator: DataGenerator) : LootTableProvider(generator) {
    override fun getTables(): List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> {
        return listOf(
            Pair.of(
                Supplier { Blocks() },
                LootParameterSets.BLOCK,
            )
        )
    }

    private class Blocks : BlockLootTables() {
        override fun addTables() {
            val builder = LootPool.builder()
                .name(BlockRegistry.baseChest.get().registryName.toString())
                .rolls(ConstantRange.of(1))
                .acceptCondition(SurvivesExplosion.builder())
                .addEntry(
                    ItemLootEntry.builder(BlockRegistry.baseChest.get())
                        .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                )
            this.registerLootTable(BlockRegistry.baseChest.get(), LootTable.builder().addLootPool(builder))
        }

        override fun getKnownBlocks(): Iterable<Block> {
            return listOf(BlockRegistry.baseChest.get())
        }
    }

    override fun validate(map: Map<ResourceLocation, LootTable>, validationtracker: ValidationTracker) {
        map.forEach { (name: ResourceLocation, table: LootTable) ->
            LootTableManager.validateLootTable(validationtracker, name, table)
        }
    }
}
