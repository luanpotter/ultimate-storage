package xyz.luan.games.minecraft.ultimatestorage.data

import com.sun.xml.internal.ws.spi.db.BindingContextFactory
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.data.client.ModBlockStateProvider
import xyz.luan.games.minecraft.ultimatestorage.data.client.ModItemModelProvider

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        BindingContextFactory.LOGGER.info("Gathering data...")
        val gen = event.generator
        val existingFileHelper = event.existingFileHelper
        gen.addProvider(ModBlockStateProvider(gen, existingFileHelper))
        gen.addProvider(ModItemModelProvider(gen, existingFileHelper))
    }
}