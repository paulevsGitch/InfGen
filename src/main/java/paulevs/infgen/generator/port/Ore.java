package paulevs.infgen.generator.port;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.IWorld;

public class Ore
{
	protected static final Mutable POS = new Mutable();
	protected BlockState ore;
	
	public Ore(BlockState ore)
	{
		this.ore = ore;
	}

	public boolean generate(IWorld world, Random random, int x, int y, int z)
	{
		float sinX = random.nextFloat() * 3.1415927F;
		float sin = MathHelper.sin(sinX) * 2.0F;
		float cos = MathHelper.cos(sinX) * 2.0F;
		double xa = (float) (x + 8) + sin;
		double xb = (float) (x + 8) - sin;
		double za = (float) (z + 8) + cos;
		double zb = (float) (z + 8) - cos;
		double ya = y + random.nextInt(3) + 2;
		double yb = y + random.nextInt(3) + 2;

		for (int i = 0; i <= 16; ++i)
		{
			double delta = (double) i / 16.0D;
			double cx = xa + (xb - xa) * delta;
			double cy = ya + (yb - ya) * delta;
			double cz = za + (zb - za) * delta;
			double var26 = random.nextDouble();
			double radH = ((MathHelper.sin((float) delta * 3.1415927F) + 1.0F) * var26 + 1.0D) / 2.0;
			double radV = ((MathHelper.sin((float) delta * 3.1415927F) + 1.0F) * var26 + 1.0D) / 2.0;

			for(int px = (int) (cx - radH); px <= (int) (cx + radH); ++px)
			{
				POS.setX(px);
				for(int py = (int) (cy - radV); py <= (int) (cy + radV); ++py)
				{
					POS.setY(py);
					for(int pz = (int) (cz - radH); pz <= (int) (cz + radH); ++pz)
					{
						POS.setZ(pz);
						double spX = (px + 0.5 - cx) / radH;
						double spY = (py + 0.5 - cy) / radV;
						double spZ = (pz + 0.5 - cz) / radH;
						
						if (spX * spX + spY * spY + spZ * spZ < 1.0D && world.getBlockState(POS).getBlock() == Blocks.STONE)
						{
							world.setBlockState(POS, ore, 19);
						}
					}
				}
			}
		}

		return true;
	}
}
