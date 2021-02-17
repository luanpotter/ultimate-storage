package xyz.luan.games.minecraft.ultimatestorage.screens

import net.minecraft.network.PacketBuffer

class StorageUpdatePacket {

    companion object {
        fun encode(message: PacketBuffer) {
            //
        }

        fun decode(message: PacketBuffer): StorageUpdatePacket {
            TODO("luan")
        }

        fun consume(message: StorageUpdatePacket) {
            //
        }
    }
}