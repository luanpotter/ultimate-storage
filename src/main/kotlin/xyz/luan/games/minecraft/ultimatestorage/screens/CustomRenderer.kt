package xyz.luan.games.minecraft.ultimatestorage.screens

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldVertexBufferUploader
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.vector.Matrix4f

object CustomRenderer {
    fun blit(
        matrixStack: MatrixStack,
        x1: Int,
        x2: Int,
        y1: Int,
        y2: Int,
        blitOffset: Int,
        uWidth: Int,
        vHeight: Int,
        uOffset: Float,
        vOffset: Float,
        textureWidth: Int,
        textureHeight: Int
    ) {
        blit(
            matrixStack.last.matrix,
            x1,
            x2,
            y1,
            y2,
            blitOffset,
            (uOffset + 0.0f) / textureWidth.toFloat(),
            (uOffset + uWidth.toFloat()) / textureWidth.toFloat(),
            (vOffset + 0.0f) / textureHeight.toFloat(),
            (vOffset + vHeight.toFloat()) / textureHeight.toFloat(),
        )
    }

    private fun blit(
        matrix: Matrix4f,
        x1: Int,
        x2: Int,
        y1: Int,
        y2: Int,
        blitOffset: Int,
        minU: Float,
        maxU: Float,
        minV: Float,
        maxV: Float
    ) {
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferBuilder.pos(matrix, x1.toFloat(), y2.toFloat(), blitOffset.toFloat()).tex(minU, maxV).endVertex()
        bufferBuilder.pos(matrix, x2.toFloat(), y2.toFloat(), blitOffset.toFloat()).tex(maxU, maxV).endVertex()
        bufferBuilder.pos(matrix, x2.toFloat(), y1.toFloat(), blitOffset.toFloat()).tex(maxU, minV).endVertex()
        bufferBuilder.pos(matrix, x1.toFloat(), y1.toFloat(), blitOffset.toFloat()).tex(minU, minV).endVertex()
        bufferBuilder.finishDrawing()
        RenderSystem.enableAlphaTest()
        WorldVertexBufferUploader.draw(bufferBuilder)
    }
}