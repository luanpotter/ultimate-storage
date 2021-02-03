package xyz.luan.games.minecraft.ultimatestorage.items

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.item.Item
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResultType
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.common.ToolType
import net.minecraftforge.fml.network.NetworkHooks
import xyz.luan.games.minecraft.ultimatestorage.UltimateStorageMod.itemGroup
import xyz.luan.games.minecraft.ultimatestorage.tiles.BaseChestTileEntity

class ItemWrench : Item(
    Properties()
        .addToolType(ToolType.get("wrench"), 1)
        .group(itemGroup)
) {
    override fun onItemUse(context: ItemUseContext): ActionResultType {
        val pos = context.pos
        val player = context.player
        val serverPlayer = player as? ServerPlayerEntity ?: return ActionResultType.PASS

        val tileEntity = context.world.getTileEntity(pos)
        if (tileEntity is BaseChestTileEntity) {
            if (player.isSneaking) {
                NetworkHooks.openGui(serverPlayer, object : INamedContainerProvider {
                    override fun createMenu(
                        windowId: Int,
                        playerInv: PlayerInventory,
                        player: PlayerEntity,
                    ): Container {
                        return tileEntity.createUpgradeMenu(windowId, playerInv, player)
                    }

                    override fun getDisplayName(): ITextComponent {
                        return StringTextComponent("Chest Upgrades")
                    }
                }, pos)
                return ActionResultType.SUCCESS
            }
        }
        // handled by the chest block
        return ActionResultType.PASS
    }
}