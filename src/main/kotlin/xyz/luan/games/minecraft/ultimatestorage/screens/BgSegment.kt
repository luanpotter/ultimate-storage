package xyz.luan.games.minecraft.ultimatestorage.screens

import net.minecraft.util.ResourceLocation
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod

class BgSegment private constructor(
    val x: Int = 0,
    val y: Int,
    val width: Int = 184,
    val height: Int,
    val slots: List<SlotPos> = listOf(),
) {
    val texture by lazy { ResourceLocation(UltimateStorageMod.MOD_ID, "textures/gui/chest-gui.png") }

    class SlotPos(
        val x: Int,
        val y: Int,
    )

    companion object {
        val top = BgSegment(y = 134, height = 7)
        val row = BgSegment(y = 88, height = 18)
        val emptyRow = BgSegment(y = 107, height = 26, slots = generateSlots(y = 0, xStart = 11, xDelta = 18, n = 9))
        val bottom = BgSegment(y = 0, height = 87)
        val upgrades = BgSegment(y = 179, height = 36, slots = generateSlots(y = 14, xStart = 13, xDelta = 20, n = 8))
        val upgradesEmpty = BgSegment(y = 142, height = 36)

        val baseUpgradeOverlay = BgSegment(x = 238, y = 0, width = 18, height = 18)
    }
}

private fun generateSlots(y: Int, xStart: Int, xDelta: Int, n: Int): List<BgSegment.SlotPos> {
    return (0 until n)
        .map { xStart + it * xDelta }
        .map { BgSegment.SlotPos(it, y) }
}
