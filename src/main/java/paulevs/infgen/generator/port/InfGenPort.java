package paulevs.infgen.generator.port;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.chunk.Chunk;
import paulevs.infgen.IBiomeArray;

public class InfGenPort
{
	private static final Random RANDOM = new Random();
	private static final long RND_X = 341873128712L;
	private static final long RND_Z = 132897987541L;
	private static final byte ID_AIR = 0; // Air
	private static final byte ID_GRASS = 2; // Grass
	private static final byte ID_DIRT = 3; // Dirt
	private static final byte ID_WATER = 9; // Water
	private static final byte ID_STONE = 1; // Stone
	private static final byte ID_GRAVEL = 13; // Gravel
	private static final byte ID_SAND = 12; // Sand
	private ValueNoiseOctaved noiseA;
	private ValueNoiseOctaved noiseB;
	private ValueNoiseOctaved noiseC;
	private ValueNoiseOctaved noiseSand;
	private ValueNoiseOctaved noiseHeightmap;
	
	private static final Mutable POS = new Mutable();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState SAND = Blocks.SAND.getDefaultState();
	
	private static final byte[] BLOCKS = new byte[32768];
	private int lastX = Integer.MAX_VALUE;
	private int lastZ = Integer.MAX_VALUE;

	public InfGenPort(long paramLong)
	{
		RANDOM.setSeed(paramLong);
		this.noiseA = new ValueNoiseOctaved(RANDOM, 16);
		this.noiseB = new ValueNoiseOctaved(RANDOM, 16);
		this.noiseC = new ValueNoiseOctaved(RANDOM, 8);
		this.noiseSand = new ValueNoiseOctaved(RANDOM, 4);
		this.noiseHeightmap = new ValueNoiseOctaved(RANDOM, 4);
	}

	public void makeChunk(int chunkX, int chunkZ, Chunk chunk, BiomeLayerSampler oceanBiomes)
	{
		if (!isCached(chunkX, chunkZ))
			fillArray(chunkX, chunkZ);
		for (int x = 0; x < 16; x++)
		{
			POS.setX(x);
			for (int z = 0; z < 16; z++)
			{
				POS.setZ(z);
				int index = x << 11 | z << 7 | 0x7F;
				for (int y = 127; y >= 0; y--)
				{
					POS.setY(y);
					int id = BLOCKS[index];
					if (id == ID_STONE)
						chunk.setBlockState(POS, STONE, false);
					else if (id == ID_DIRT)
						chunk.setBlockState(POS, DIRT, false);
					else if (id == ID_GRASS)
						chunk.setBlockState(POS, GRASS, false);
					else if (id == ID_SAND)
						chunk.setBlockState(POS, SAND, false);
					else if (id == ID_GRAVEL)
						chunk.setBlockState(POS, GRAVEL, false);
					else if (id == ID_WATER)
						chunk.setBlockState(POS, WATER, false);
					index--;
				}
			}
		}
		
		IBiomeArray array = (IBiomeArray) chunk.getBiomeArray();
		for (int x = 0; x < 4; x++)
		{
			int px = (x << 2) | 2;
			int wx = x | (chunkX << 2);
			for (int z = 0; z < 4; z++)
			{
				int pz = (z << 2) | 2;
				int wz = z | (chunkZ << 2);
				int h = getSolidHeight(px, pz);
				if (h < 63)
					array.setBiome(x, z, oceanBiomes.sample(wx, wz));
			}
		}
	}
	
	public void fillArray(int chunkX, int chunkZ)
	{
		lastX = chunkX;
		lastZ = chunkZ;
		RANDOM.setSeed(chunkX * RND_X + chunkZ * RND_Z);
		
		for (int x = 0; x < 4; x++)
		{
			for (int z = 0; z < 4; z++)
			{
				double[][] noise = new double[33][4];
				int px = (chunkX << 2) + x;
				int pz = (chunkZ << 2) + z;
				
				for (int py = 0; py < noise.length; py++)
				{
					noise[py][0] = getNoise(px, py, pz);
					noise[py][1] = getNoise(px, py, pz + 1);
					noise[py][2] = getNoise(px + 1, py, pz);
					noise[py][3] = getNoise(px + 1, py, pz + 1);
				}
				
				for (int py = 0; py < 32; py++)
				{
					double n1 = noise[py][0];
					double n2 = noise[py][1];
					double n3 = noise[py][2];
					double n4 = noise[py][3];
					double n5 = noise[(py + 1)][0];
					double n7 = noise[(py + 1)][1];
					double n8 = noise[(py + 1)][2];
					double n9 = noise[(py + 1)][3];
					for (int bx = 0; bx < 4; bx++)
					{
						double mixX = bx / 4.0;
						double nx1 = n1 + (n5 - n1) * mixX;
						double nx2 = n2 + (n7 - n2) * mixX;
						double nx3 = n3 + (n8 - n3) * mixX;
						double nx4 = n4 + (n9 - n4) * mixX;
						for (int bz = 0; bz < 4; bz++)
						{
							double mixZ = bz / 4.0;
							double nz1 = nx1 + (nx3 - nx1) * mixZ;
							double nz2 = nx2 + (nx4 - nx2) * mixZ;
							int index = bz + (x << 2) << 11 | 0 + (z << 2) << 7 | (py << 2) + bx;
							for (int by = 0; by < 4; by++)
							{
								double mixY = by / 4.0;
								double noiseValue = nz1 + (nz2 - nz1) * mixY;
								byte blockID = ID_AIR;
								if ((py << 2) + bx < 64)
								{
									blockID = ID_WATER;
								}
								if (noiseValue > 0.0D)
								{
									blockID = ID_STONE;
								}
								BLOCKS[index] = blockID;
								index += 128;
							}
						}
					}
				}
			}
		}
		
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				double d1 = (chunkX << 4) + x;
				double n1 = (chunkZ << 4) + z;
				int sandNoise = this.noiseSand.eval(d1 * 0.03125, n1 * 0.03125, 0.0) + RANDOM.nextDouble() * 0.2 > 0.0 ? 1 : 0;
				int gravelNoise = this.noiseSand.eval(n1 * 0.03125, 109.0134, d1 * 0.03125) + RANDOM.nextDouble() * 0.2 > 3.0 ? 1 : 0;
				int height = (int) (this.noiseHeightmap.eval(d1 * 0.03125D * 2.0, n1 * 0.03125D * 2.0) / 3.0 + 3.0 + RANDOM.nextDouble() * 0.25);
				int index = x << 11 | z << 7 | 0x7F;
				int checker = -1;
				int blockID1 = ID_GRASS;
				int blockID2 = ID_DIRT;
				for (int y = 127; y >= 0; y--)
				{
					if (BLOCKS[index] == ID_AIR)
					{
						checker = -1;
					}
					else if (BLOCKS[index] == ID_STONE)
					{
						if (checker == -1)
						{
							if (height <= 0)
							{
								blockID1 = ID_AIR;
								blockID2 = ID_STONE;
							}
							else if ((y >= 60) && (y <= 65))
							{
								blockID1 = ID_GRASS;
								blockID2 = ID_DIRT;
								if (gravelNoise != 0)
								{
									blockID1 = ID_AIR;
									blockID2 = ID_GRAVEL;
								}
								if (sandNoise != 0)
								{
									blockID1 = ID_SAND;
									blockID2 = ID_SAND;
								}
							}
							if ((y < 64) && (blockID1 == ID_AIR))
							{
								blockID1 = ID_WATER;
							}
							checker = height;
							if (y >= 63)
							{
								BLOCKS[index] = ((byte) blockID1);
							}
							else
							{
								BLOCKS[index] = ((byte) blockID2);
							}
						}
						else if (checker > 0)
						{
							checker--;
							BLOCKS[index] = ((byte) blockID2);
						}
					}
					index--;
				}
			}
		}
	}
	
	private double getNoise(double x, double y, double z)
	{
		double d1;
		if ((d1 = y * 4.0 - 64.0) < 0.0)
		{
			d1 *= 3.0;
		}
		
		double d2;
		double res;
		
		if ((d2 = this.noiseC.eval(x * 684.412 / 80.0, y * 684.412 / 400.0, z * 684.412 / 80.0) / 2.0) < -1)
		{
			if ((res = (this.noiseA.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0) - d1) < -10.0)
			{
				res = -10.0;
			}
			if (res > 10.0)
			{
				res = 10.0;
			}
		}
		else if (d2 > 1.0)
		{
			if ((res = (this.noiseB.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0) - d1) < -10.0)
			{
				res = -10.0;
			}
			if (res > 10.0)
			{
				res = 10.0;
			}
		}
		else
		{
			double d5 = this.noiseA.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0 - d1;
			double d6 = this.noiseB.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0 - d1;
			if (d5 < -10.0)
			{
				d5 = -10.0;
			}
			if (d5 > 10.0)
			{
				d5 = 10.0;
			}
			if (d6 < -10.0)
			{
				d6 = -10.0;
			}
			if (d6 > 10.0)
			{
				d6 = 10.0;
			}
			double d7 = (d2 + 1.0) / 2.0;
			res = d5 + (d6 - d5) * d7;
		}
		
		return res;
	}
	
	public int getHeight(int px, int pz)
	{
		int chunkX = px >> 4;
		int chunkZ = pz >> 4;
		if (isCached(chunkX, chunkZ))
		{
			return getArrayHeight(px & 15, pz & 15);
		}
		else
		{
			fillArray(chunkX, chunkZ);
			return getArrayHeight(px & 15, pz & 15);
		}
	}
	
	private boolean isCached(int cx, int cz)
	{
		return lastX == cx && lastZ == cz;
	}
	
	private int getArrayHeight(int x, int z)
	{
		int index = x << 11 | z << 7 | 0x7F;
		for (int y = 127; y >= 0; y--)
		{
			if (BLOCKS[index] != ID_AIR)
				return y;
			index--;
		}
		return 0;
	}
	
	private int getSolidHeight(int x, int z)
	{
		int index = x << 11 | z << 7 | 0x7F;
		for (int y = 127; y >= 0; y--)
		{
			if (BLOCKS[index] != ID_AIR && BLOCKS[index] != ID_WATER)
				return y;
			index--;
		}
		return 0;
	}
}