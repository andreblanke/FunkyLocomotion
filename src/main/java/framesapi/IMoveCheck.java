package framesapi;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMoveCheck {
	public boolean canMove(IBlockState state, World worldObj, BlockPos pos);
}
