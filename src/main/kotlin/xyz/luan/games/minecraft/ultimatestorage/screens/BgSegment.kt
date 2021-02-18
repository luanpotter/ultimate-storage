package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.ResourceLocation
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod

private const val TEXTURE_FILE_SIZE = 256

class BgSegment private constructor(
    val x: Int = 0,
    val y: Int,
    val width: Int = DEFAULT_WIDTH,
    val height: Int,
    val drawHeight: Int = height,
    val slots: List<SlotPos> = listOf(),
) {
    private val texture by lazy { ResourceLocation(UltimateStorageMod.MOD_ID, "textures/gui/chest-gui.png") }

    class SlotPos(
        val x: Int,
        val y: Int,
    )

    fun render(gui: Screen, matrixStack: MatrixStack, dx: Int, dy: Int, heightOverride: Int? = null) {
        gui.minecraft.textureManager.bindTexture(texture)
        CustomRenderer.blit(
            matrixStack = matrixStack,
            x1 = dx,
            x2 = dx + width,
            y1 = dy,
            y2 = dy + (heightOverride ?: drawHeight),
            blitOffset = gui.blitOffset,
            uWidth = width,
            vHeight = height,
            uOffset = x.toFloat(),
            vOffset = y.toFloat(),
            textureWidth = TEXTURE_FILE_SIZE,
            textureHeight = TEXTURE_FILE_SIZE,
        )
    }

    companion object {
        const val DEFAULT_WIDTH = 184

        val top = BgSegment(y = 0, height = 7)
        val divider = BgSegment(y = 8, height = 4)
        val row = BgSegment(y = 13, height = 18, slots = generateSlots(y = 0, xStart = 11, xDelta = 18, n = 9))
        val emptyRow = BgSegment(y = 8, height = 4, drawHeight = 18, slots = row.slots)
        val bottom = BgSegment(y = 32, height = 7)

        // closed upgrade
        val closedUpgradeTop = BgSegment(y = 40, height = 5)
        val closedUpgradeMiddle = BgSegment(y = 46, height = 5)
        val closedUpgradeBottom = BgSegment(y = 52, height = 5)

        // opened upgrade
        val openedUpgradeTop = BgSegment(y = 58, height = 5)
        val openedUpgradeMiddle = BgSegment(y = 64, height = 5)
        val openedUpgradeBottom = BgSegment(y = 70, height = 5)

        // slots
        val baseUpgradeOverlay = BgSegment(x = 238, y = 0, width = 18, height = 18)
        val plusButton = BgSegment(x = 238, y = 18, width = 18, height = 18)
        val removeOverlay = BgSegment(x = 238, y = 36, width = 18, height = 18)
        val hoverOverlay = BgSegment(x = 238, y = 54, width = 18, height = 18)
    }
}

private fun generateSlots(y: Int, xStart: Int, xDelta: Int, n: Int): List<BgSegment.SlotPos> {
    return (0 until n)
        .map { xStart + it * xDelta }
        .map { BgSegment.SlotPos(it, y) }
}
