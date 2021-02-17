package xyz.luan.games.minecraft.ultimatestorage.screens

import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkEvent
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.MAIN_CHANNEL
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

class StorageUpdatePacket(
    private val pos: BlockPos,
    private val updateSlot: Int,
    private val itemStack: ItemStack,
) {
    fun encode(message: PacketBuffer) {
        message.writeBlockPos(pos)
        message.writeInt(updateSlot)
        message.writeItemStack(itemStack)
    }

    fun consume(ctx: NetworkEvent.Context) {
        val player = ctx.sender ?: return
        val world = player.world ?: return
        val tileEntity = world.getTileEntity(pos) as? BaseChestTileEntity ?: return
        tileEntity.chestUpgrades.setInventorySlotContents(updateSlot, itemStack)
    }

    companion object {
        fun send(pos: BlockPos, updatedSlot: Int, itemStack: ItemStack) {
            MAIN_CHANNEL.sendToServer(
                StorageUpdatePacket(pos, updatedSlot, itemStack),
            )
        }

        fun decode(message: PacketBuffer): StorageUpdatePacket {
            return StorageUpdatePacket(
                pos = message.readBlockPos(),
                updateSlot = message.readInt(),
                itemStack = message.readItemStack(),
            )
        }

        fun consume(message: StorageUpdatePacket, ctx: NetworkEvent.Context) {
            ctx.enqueueWork { message.consume(ctx) }
            ctx.packetHandled = true
        }
    }
}