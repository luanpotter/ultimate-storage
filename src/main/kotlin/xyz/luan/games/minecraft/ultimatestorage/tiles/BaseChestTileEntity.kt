package xyz.luan.games.minecraft.ultimatestorage.tiles

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry


class BaseChestTileEntity : TileEntity(BlockRegistry.baseChestTileEntity.get()), INamedContainerProvider {
    val chestInventory = Inventory(100)

    override fun createMenu(windowId: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity?): Container {
        return BaseChestContainer(windowId, this, playerInventory)
    }

    override fun getDisplayName(): ITextComponent {
        return StringTextComponent("Base Chest");
    }
}