package xyz.luan.games.minecraft.ultimatestorage

import net.minecraft.client.gui.ScreenManager
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.registry.ItemRegistry
import xyz.luan.games.minecraft.ultimatestorage.screens.BaseChestScreen
import xyz.luan.games.minecraft.ultimatestorage.screens.BaseChestUpgradeScreen


@Mod(MOD_ID)
object UltimateStorageMod {
    const val MOD_ID = "ultimatestorage"
    val LOGGER: Logger = LogManager.getLogger()

    init {
        LOGGER.info("Ultimate Storage initialization")
        ItemRegistry.register()
        BlockRegistry.register()

        EVENT_BUS.register(this)
        MOD_BUS.addListener(::onClientSetup)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        ScreenManager.registerFactory(BlockRegistry.baseChestContainer.get(), ::BaseChestScreen)
        ScreenManager.registerFactory(BlockRegistry.baseChestUpgradesContainer.get(), ::BaseChestUpgradeScreen)
    }

    val itemGroup = object : ItemGroup("ultimatestorage") {
        override fun createIcon(): ItemStack {
            return ItemStack(BlockRegistry.tiers.last().block().asItem())
        }
    }
}