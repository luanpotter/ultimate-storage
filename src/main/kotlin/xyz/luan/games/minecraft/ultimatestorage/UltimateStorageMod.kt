package xyz.luan.games.minecraft.ultimatestorage

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.registry.ItemRegistry

@Mod(MOD_ID)
object UltimateStorageMod {
    const val MOD_ID = "ultimatestorage"
    val LOGGER: Logger = LogManager.getLogger()

    init {
        LOGGER.info("Ultimate Storage initialization")
        ItemRegistry.register()
        BlockRegistry.register()

        MinecraftForge.EVENT_BUS.register(this)
    }
}