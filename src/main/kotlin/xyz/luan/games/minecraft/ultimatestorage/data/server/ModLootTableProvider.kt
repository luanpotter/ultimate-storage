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
            BlockRegistry.tiers.forEach { addLootTable(it.block()) }
        }

        private fun addLootTable(block: Block) {
            val builder = LootPool.builder()
                .name(block.registryName.toString())
                .rolls(ConstantRange.of(1))
                .acceptCondition(SurvivesExplosion.builder())
                .addEntry(
                    ItemLootEntry
                        .builder(block)
                        .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                )
            registerLootTable(block, LootTable.builder().addLootPool(builder))
        }

        override fun getKnownBlocks(): Iterable<Block> {
            return BlockRegistry.tiers.map { it.block() }
        }
    }

    override fun validate(map: Map<ResourceLocation, LootTable>, validationtracker: ValidationTracker) {
        map.forEach { (name, table) -> LootTableManager.validateLootTable(validationtracker, name, table) }
    }
}
