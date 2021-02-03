package xyz.luan.games.minecraft.ultimatestorage.tiles

import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SUpdateTileEntityPacket
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.Direction
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.InvWrapper
import xyz.luan.games.minecraft.ultimatestorage.Tier
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestContainer
import xyz.luan.games.minecraft.ultimatestorage.containers.BaseChestUpgradesContainer
import xyz.luan.games.minecraft.ultimatestorage.getContents
import xyz.luan.games.minecraft.ultimatestorage.readOrdered
import xyz.luan.games.minecraft.ultimatestorage.registry.ItemRegistry
import xyz.luan.games.minecraft.ultimatestorage.writeOrdered

class BaseChestTileEntity(
    val tier: Tier,
    tileEntityType: TileEntityType<BaseChestTileEntity>,
) : TileEntity(tileEntityType), INamedContainerProvider, ICapabilityProvider {
    val chestUpgrades = Inventory(tier.upgradeSlots)

    var rows: Int = 3
    var cols: Int = 9

    var inventorySize: Int = rows * cols
    var chestInventory = Inventory(inventorySize)
    var chestHandler: LazyOptional<IItemHandlerModifiable>? = null

    init {
        updateRowCount()
        chestUpgrades.addListener { updateRowCount() }
    }

    private fun updateRowCount() {
        val capacityUpgrades = chestUpgrades.getContents().count { it.item == ItemRegistry.capacityUpgrade.get() }
        val newRows = 3 + capacityUpgrades
        if (newRows == rows) return

        rows = newRows
        cols = 9
        inventorySize = rows * cols

        val previousContents = chestInventory.getContents()
        chestInventory = Inventory(inventorySize)
        previousContents.take(inventorySize).forEachIndexed { slot, item ->
            chestInventory.setInventorySlotContents(slot, item)
        }
        world?.let { worldIn ->
            previousContents.drop(inventorySize).forEach {
                InventoryHelper.spawnItemStack(worldIn, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), it)
            }
        }

        chestHandler = null
    }

    override fun createMenu(windowId: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity?): Container {
        updateRowCount()
        return BaseChestContainer(windowId, this, playerInventory)
    }

    fun createUpgradeMenu(windowId: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity?): Container {
        return BaseChestUpgradesContainer(windowId, this, playerInventory)
    }

    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (!removed && cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.chestHandler == null) {
                this.chestHandler = LazyOptional.of { createHandler() }
            }
            return this.chestHandler!!.cast()
        }
        return super<TileEntity>.getCapability(cap, side)
    }

    private fun createHandler(): IItemHandlerModifiable {
        return InvWrapper(chestInventory)
    }

    override fun getDisplayName(): ITextComponent {
        return StringTextComponent("${tier.name.toLowerCase().capitalize()} Chest")
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        compound.put("chest-upgrades", chestUpgrades.writeOrdered())
        compound.put("chest-contents", chestInventory.writeOrdered())
        return super.write(compound)
    }

    override fun read(state: BlockState, nbt: CompoundNBT) {
        super.read(state, nbt)
        chestUpgrades.readOrdered(nbt.getList("chest-upgrades", 10))
        updateRowCount()
        chestInventory.readOrdered(nbt.getList("chest-contents", 10))
    }

    override fun handleUpdateTag(stateIn: BlockState, tag: CompoundNBT) {
        read(stateIn, tag)
    }

    override fun onDataPacket(net: NetworkManager, pkt: SUpdateTileEntityPacket) {
        read(blockState, pkt.nbtCompound)
    }
}
