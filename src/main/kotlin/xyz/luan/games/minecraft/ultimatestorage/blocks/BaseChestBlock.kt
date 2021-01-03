package xyz.luan.games.minecraft.ultimatestorage.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.ContainerBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.common.ToolType
import net.minecraftforge.fml.network.NetworkHooks
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity


class BaseChestBlock: ContainerBlock(
    Properties
        .create(Material.WOOD)
        .harvestTool(ToolType.AXE)
        .sound(SoundType.WOOD)
        .hardnessAndResistance(2F, 10F)
) {
    override fun createNewTileEntity(worldIn: IBlockReader): TileEntity? {
        return BlockRegistry.baseChestTileEntity.get().create()
    }

    override fun hasTileEntity(state: BlockState?): Boolean = true

    override fun onBlockActivated(
        state: BlockState?,
        worldIn: World,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        blockRayTraceResult: BlockRayTraceResult?
    ): ActionResultType {
        if (worldIn.isRemote) return ActionResultType.SUCCESS

        val serverPlayer = player as? ServerPlayerEntity ?: return ActionResultType.PASS
        val tileEntity = pos?.let { worldIn.getTileEntity(it) } ?: return ActionResultType.PASS

        if (tileEntity !is BaseChestTileEntity) {
            return ActionResultType.FAIL
        }
        NetworkHooks.openGui(serverPlayer, tileEntity, pos)
        return ActionResultType.SUCCESS
    }
}