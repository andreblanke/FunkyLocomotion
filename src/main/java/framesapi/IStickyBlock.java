package framesapi;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStickyBlock {
	public boolean isStickySide(World world, BlockPos pos, EnumFacing side);
}
