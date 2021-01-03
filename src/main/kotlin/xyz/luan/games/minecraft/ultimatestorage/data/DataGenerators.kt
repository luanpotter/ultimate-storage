package xyz.luan.games.minecraft.ultimatestorage.data

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.LOGGER
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.data.client.ModBlockStateProvider
import xyz.luan.games.minecraft.ultimatestorage.data.client.ModItemModelProvider
import xyz.luan.games.minecraft.ultimatestorage.data.server.ModLootTableProvider
import xyz.luan.games.minecraft.ultimatestorage.data.server.ModRecipeProvider

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        LOGGER.info("Gathering data...")

        val gen = event.generator
        val existingFileHelper = event.existingFileHelper

        if (event.includeServer()) {
            gen.addProvider(ModRecipeProvider(gen))
            gen.addProvider(ModLootTableProvider(gen))
        }

        if (event.includeClient()) {
            gen.addProvider(ModBlockStateProvider(gen, existingFileHelper))
            gen.addProvider(ModItemModelProvider(gen, existingFileHelper))
        }
    }
}