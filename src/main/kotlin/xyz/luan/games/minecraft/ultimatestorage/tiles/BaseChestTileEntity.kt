package xyz.luan.games.minecraft.ultimatestorage.tiles

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer
import xyz.luan.games.minecraft.ultimatestorage.registry.BlockRegistry

private const val CONTENTS_INVENTORY_TAG = "chest-contents"

class BaseChestTileEntity : TileEntity(BlockRegistry.baseChestTileEntity.get()), INamedContainerProvider {
    val rows = 5
    val cols = 9

    val inventorySize: Int
        get() = rows * cols

    val chestInventory = Inventory(inventorySize)

    override fun createMenu(windowId: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity?): Container {
        return BaseChestContainer(windowId, this, playerInventory)
    }

    override fun getDisplayName(): ITextComponent {
        return StringTextComponent("Base Chest")
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        super.write(compound)
        compound.put(CONTENTS_INVENTORY_TAG, chestInventory.write())
        return compound
    }

    override fun read(state: BlockState, nbt: CompoundNBT) {
        super.read(blockState, nbt)
        val inventoryNBT = nbt.getList(CONTENTS_INVENTORY_TAG, 10)
        chestInventory.read(inventoryNBT)
    }
}