package idealindustrial.blocks.ores;

import idealindustrial.II_Core;
import idealindustrial.autogen.material.II_Material;
import idealindustrial.autogen.material.II_Materials;
import idealindustrial.autogen.material.Prefixes;
import idealindustrial.blocks.base.Tile32kBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockOres extends Tile32kBlock<TileOres> {


    public BlockOres() {
        super("block.ores", Material.rock);
        setCreativeTab(II_Core.II_MATERIAL_TAB);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.stone.getIcon(side, meta);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        Prefixes[] prefixes = new Prefixes[]{Prefixes.ore, Prefixes.oreSmall};
        for (II_Material material : II_Materials.allMaterials) {
            for (Prefixes prefix : prefixes) {
                if (material.isEnabled(prefix)) {
                    list.add(new ItemStack(item, 1, TileOres.getMeta(material, prefix)));
                }
            }
        }
    }

    //todo remove, x-ray fix for debug
    @Override
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        return true;
    }

    @Override
    protected Class<TileOres> getTileClass() {
        return TileOres.class;
    }
}
