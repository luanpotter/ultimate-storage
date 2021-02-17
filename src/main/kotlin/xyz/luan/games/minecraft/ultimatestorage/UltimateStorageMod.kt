package xyz.luan.games.minecraft.ultimatestorage

import net.minecraft.client.gui.ScreenManager
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MOD_ID
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.registry.ItemRegistry
import xyz.luan.games.minecraft.ultimatestorage.screens.BaseChestScreen
import xyz.luan.games.minecraft.ultimatestorage.screens.BaseChestUpgradeScreen
import xyz.luan.games.minecraft.ultimatestorage.screens.StorageUpdatePacket
import java.util.function.BiConsumer

@Mod(MOD_ID)
object UltimateStorageMod {
    const val MOD_ID = "ultimatestorage"
    private const val PROTOCOL_VERSION = "1"
    const val TILE_ENTITY_TYPE = 42

    val LOGGER: Logger = LogManager.getLogger()

    val MAIN_CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation(MOD_ID, "main"),
        { PROTOCOL_VERSION },
        { PROTOCOL_VERSION == it },
        { PROTOCOL_VERSION == it },
    )

    init {
        LOGGER.info("Ultimate Storage initialization")
        ItemRegistry.register()
        BlockRegistry.register()

        EVENT_BUS.register(this)
        MOD_BUS.addListener(::onClientSetup)


        MAIN_CHANNEL.messageBuilder(
            StorageUpdatePacket::class.java,
            0,
            NetworkDirection.PLAY_TO_SERVER,
        )
            .encoder { msg, bytes -> msg.encode(bytes) }
            .decoder { StorageUpdatePacket.decode(it) }
            .consumer(BiConsumer { msg, ctx -> StorageUpdatePacket.consume(msg, ctx.get()) })
            .add()
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