package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical;
import micdoodle8.mods.galacticraft.core.items.IShiftDescription;
import micdoodle8.mods.galacticraft.core.tile.IMachineSides;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class BlockMachineBase extends BlockTileGC implements IShiftDescription, ISortableBlock
{
    public static final int METADATA_MASK = 0x0c; //Used to select the machine type from metadata
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockMachineBase(String assetName)
    {
        super(GCBlocks.machine);
        this.setHardness(1.0F);
        this.setSoundType(SoundType.METAL);
        this.setUnlocalizedName(assetName);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        int metadata = getMetaFromState(state);

        final int angle = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int change = EnumFacing.getHorizontal(angle).getOpposite().getHorizontalIndex();

        worldIn.setBlockState(pos, this.getStateFromMeta((metadata & BlockMachineBase.METADATA_MASK) + change), 3);
    }

    /**
     * Called when the block is right clicked by the player
     */
    @Override
    public boolean onMachineActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            entityPlayer.openGui(GalacticraftCore.instance, -1, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public boolean onUseWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        TileBaseUniversalElectrical.onUseWrenchBlock(state, world, pos, state.getValue(FACING));
        return true;
    }
    
    @Override
    public boolean onSneakUseWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IMachineSides)
        {
            ((IMachineSides)tile).nextSideConfiguration(tile);
            return true;
        }
        return false;
    }
    
    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state) & BlockMachineBase.METADATA_MASK;
    }

    public abstract String getUnlocalizedName(int typenum);

    @Override
    public boolean showDescription(int meta)
    {
        return true;
    }

    public static EnumFacing getFront(IBlockState state)
    {
        if (state.getBlock() instanceof BlockMachineBase)
        {
            return (state.getValue(BlockMachineBase.FACING));
        }
        return EnumFacing.NORTH;
    }

    @Override
    public EnumSortCategoryBlock getCategory(int meta)
    {
        return EnumSortCategoryBlock.MACHINE;
    }
}