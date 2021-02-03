package xyz.luan.games.minecraft.ultimatestorage.blocks

import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.BlockItemUseContext
import net.minecraft.state.StateContainer
import net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorld
import net.minecraft.world.World
import net.minecraftforge.common.ToolType
import net.minecraftforge.fml.network.NetworkHooks
import net.minecraftforge.items.CapabilityItemHandler
import xyz.luan.games.minecraft.ultimatestorage.Tier
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry
import xyz.luan.games.minecraft.ultimatestorage.registry.ItemRegistry
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity


class BaseChestBlock(
    private val tier: Tier,
) : ContainerBlock(
    Properties
        .create(Material.WOOD)
        .harvestTool(ToolType.AXE)
        .sound(SoundType.WOOD)
        .hardnessAndResistance(2F, 10F)
) {
    init {
        defaultState = stateContainer.baseState.with(HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
        super.fillStateContainer(builder)
        builder.add(HORIZONTAL_FACING)
    }

    override fun createNewTileEntity(worldIn: IBlockReader): TileEntity? {
        return BlockRegistry.tiers.single { it.tier == tier }.tileEntity().create()
    }

    override fun hasTileEntity(state: BlockState?): Boolean = true

    override fun onBlockActivated(
        state: BlockState,
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

        if (isUsingWrench(player, blockRayTraceResult)) {
            if (player.isSneaking) {
                return ActionResultType.PASS // handled by the ItemWrench
            } else if (blockRayTraceResult != null) {
                worldIn.setBlockState(pos, state.with(HORIZONTAL_FACING, blockRayTraceResult.face))
            }
        } else {
            NetworkHooks.openGui(serverPlayer, tileEntity, pos)
        }
        return ActionResultType.SUCCESS
    }

    private fun isUsingWrench(player: PlayerEntity?, traceResult: RayTraceResult?): Boolean {
        if (player == null || traceResult == null) {
            return false
        }

        return sequenceOf(Hand.MAIN_HAND, Hand.OFF_HAND)
            .map { player.getHeldItem(it) }
            .mapNotNull { it.takeUnless { it.isEmpty }?.item }
            .contains(ItemRegistry.wrench.get())
    }

    override fun onReplaced(state: BlockState, worldIn: World, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (newState.block !== this) {
            val tileEntity = worldIn.getTileEntity(pos)
            val cap = tileEntity?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            cap?.ifPresent { handler ->
                for (i in 0 until handler.slots) {
                    InventoryHelper.spawnItemStack(
                        worldIn,
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble(),
                        handler.getStackInSlot(i),
                    )
                }
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving)
        }
    }

    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)))
    }

    override fun rotate(state: BlockState, world: IWorld, pos: BlockPos, rotation: Rotation): BlockState {
        return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)))
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState {
        return defaultState.with(HORIZONTAL_FACING, context.placementHorizontalFacing.opposite)
    }
}