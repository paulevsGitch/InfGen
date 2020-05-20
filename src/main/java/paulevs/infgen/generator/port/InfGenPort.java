package paulevs.infgen.generator.port;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.IWorld;
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
	private ValueNoiseOctaved noiseTrees;
	
	private static final Mutable POS = new Mutable();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState SAND = Blocks.SAND.getDefaultState();
	
	private static final byte[] BLOCKS = new byte[32768];
	private static final double[][] NOISE = new double[33][4];
	private int lastX = Integer.MAX_VALUE;
	private int lastZ = Integer.MAX_VALUE;
	
	private static final Ore ORE_COAL = new Ore(Blocks.COAL_ORE.getDefaultState());
	private static final Ore ORE_IRON = new Ore(Blocks.IRON_ORE.getDefaultState());
	private static final Ore ORE_GOLD = new Ore(Blocks.GOLD_ORE.getDefaultState());
	private static final Ore ORE_DIAMOND = new Ore(Blocks.DIAMOND_ORE.getDefaultState());
	private static final Tree TREE = new Tree();

	public InfGenPort(long paramLong)
	{
		RANDOM.setSeed(paramLong);
		this.noiseA = new ValueNoiseOctaved(RANDOM, 16);
		this.noiseB = new ValueNoiseOctaved(RANDOM, 16);
		this.noiseC = new ValueNoiseOctaved(RANDOM, 8);
		this.noiseSand = new ValueNoiseOctaved(RANDOM, 4);
		this.noiseHeightmap = new ValueNoiseOctaved(RANDOM, 4);
		
		// Not in use, switches random on some points (from original code)
		new ValueNoiseOctaved(RANDOM, 5);
		
		this.noiseTrees = new ValueNoiseOctaved(RANDOM, 5);
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
		
		if (oceanBiomes != null)
		{
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
	}
	
	public void fillArray(int chunkX, int chunkZ)
	{
		lastX = chunkX;
		lastZ = chunkZ;
		RANDOM.setSeed(chunkX * RND_X + chunkZ * RND_Z);
		
		for (int x = 0; x < 4; x++)
		{
			int px = (chunkX << 2) | x;
			for (int z = 0; z < 4; z++)
			{
				int pz = (chunkZ << 2) | z;
				int iz = (z << 2) << 7;
				
				for (int py = 0; py < NOISE.length; py++)
				{
					NOISE[py][0] = getNoise(px, py, pz);
					NOISE[py][1] = getNoise(px, py, pz + 1);
					NOISE[py][2] = getNoise(px + 1, py, pz);
					NOISE[py][3] = getNoise(px + 1, py, pz + 1);
				}
				
				for (int py = 0; py < 32; py++)
				{
					double n1 = NOISE[py][0];
					double n2 = NOISE[py][1];
					double n3 = NOISE[py][2];
					double n4 = NOISE[py][3];
					double n5 = NOISE[(py + 1)][0];
					double n7 = NOISE[(py + 1)][1];
					double n8 = NOISE[(py + 1)][2];
					double n9 = NOISE[(py + 1)][3];
					for (int by = 0; by < 4; by++)
					{
						double mixY = by / 4.0;
						double nx1 = n1 + (n5 - n1) * mixY;
						double nx2 = n2 + (n7 - n2) * mixY;
						double nx3 = n3 + (n8 - n3) * mixY;
						double nx4 = n4 + (n9 - n4) * mixY;
						int iy = (py << 2) | by;
						for (int bx = 0; bx < 4; bx++)
						{
							int ix = ((x << 2) | bx) << 11;
							double mixX = bx / 4.0;
							double nz1 = nx1 + (nx3 - nx1) * mixX;
							double nz2 = nx2 + (nx4 - nx2) * mixX;
							int index = ix | iy | iz;
							for (int bz = 0; bz < 4; bz++)
							{
								double mixZ = bz / 4.0;
								double noiseValue = nz1 + (nz2 - nz1) * mixZ;
								byte blockID = ID_AIR;
								if ((py << 2) + by < 64)
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
				int height = (int) (this.noiseHeightmap.eval(d1 * 0.0625, n1 * 0.0625) / 3.0 + 3.0 + RANDOM.nextDouble() * 0.25);
				int index = x << 11 | z << 7 | 0x7F; // 0x7F = 127
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
		double elevGrad;
		if ((elevGrad = y * 4.0 - 64.0) < 0.0)
		{
			elevGrad *= 3.0;
		}
		
		double noise;
		double res;
		
		if ((noise = this.noiseC.eval(x * 8.55515, y * 1.71103, z * 8.55515) / 2.0) < -1)
		{
			res = clamp(this.noiseA.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0 - elevGrad, -10.0, 10.0);
		}
		else if (noise > 1.0)
		{
			res = clamp(this.noiseB.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0 - elevGrad, -10.0, 10.0);
		}
		else
		{
			double noise2 = clamp(this.noiseA.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0 - elevGrad, -10.0, 10.0);
			double noise3 = clamp(this.noiseB.eval(x * 684.412, y * 984.412, z * 684.412) / 512.0 - elevGrad, -10.0, 10.0);
			double mix = (noise + 1.0) / 2.0;
			res = noise2 + (noise3 - noise2) * mix;
		}
		
		return res;
	}
	
	private double clamp(double x, double min, double max)
	{
		return x < min ? min : x > max ? max : x;
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
	
	public void populate(IWorld world, Chunk chunk)
	{
		int posX = chunk.getPos().x;
		int posZ = chunk.getPos().z;
		
		RANDOM.setSeed((long) posX * 318279123L + (long) posZ * 919871212L);
		
		posX <<= 4;
		posZ <<= 4;

		for(int i = 0; i < 20; ++i)
		{
			int x = posX + RANDOM.nextInt(16);
			int y = RANDOM.nextInt(128);
			int z = posZ + RANDOM.nextInt(16);
			ORE_COAL.generate(world, RANDOM, x, y, z);
		}

		for(int i = 0; i < 10; ++i)
		{
			int var15 = posX + RANDOM.nextInt(16);
			int var19 = RANDOM.nextInt(64);
			int var23 = posZ + RANDOM.nextInt(16);
			ORE_IRON.generate(world, RANDOM, var15, var19, var23);
		}

		if (RANDOM.nextInt(2) == 0)
		{
			int x = posX + RANDOM.nextInt(16);
			int y = RANDOM.nextInt(32);
			int z = posZ + RANDOM.nextInt(16);
			ORE_GOLD.generate(world, RANDOM, x, y, z);
		}

		if (RANDOM.nextInt(8) == 0)
		{
			int x = posX + RANDOM.nextInt(16);
			int y = RANDOM.nextInt(16);
			int z = posZ + RANDOM.nextInt(16);
			ORE_DIAMOND.generate(world, RANDOM, x, y, z);
		}

		int count = (int) noiseTrees.eval((double) posX * 0.25D, (double) posZ * 0.25D) << 3;
		TREE.chunkReset();
		for(int i = 0; i < count; ++ i)
		{
			int px = posX + RANDOM.nextInt(16) + 8;
			int pz = posZ + RANDOM.nextInt(16) + 8;
			int py = getHeight(px, pz, world);
			TREE.generate(world, RANDOM, px, py, pz);
		}
	}
	
	private int getHeight(int x, int z, IWorld world)
	{
		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		x &= 15;
		z &= 15;
		return chunk.getHeightmap(Type.WORLD_SURFACE).get(x, z);
	}
}