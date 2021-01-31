package xyz.luan.games.minecraft.ultimatestorage.screens

import net.minecraft.util.ResourceLocation
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod

class BgSegment private constructor(
    val fileName: String,
    val height: Int,
    val width: Int = 184,
    val slots: List<SlotPos> = listOf(),
) {
    val texture by lazy { ResourceLocation(UltimateStorageMod.MOD_ID, "textures/gui/$fileName.png") }

    class SlotPos(
        private val x: Int,
        private val y: Int,
    )

    companion object {
        val top = BgSegment("container-top", height = 7)
        val row = BgSegment("container-row", height = 18)
        val bottom = BgSegment("container-bottom", height = 87)
        val upgrades = BgSegment("container-upgrades", height = 36)
        val upgradesEmpty = BgSegment("container-upgrades-empty", height = 36)
    }
}
