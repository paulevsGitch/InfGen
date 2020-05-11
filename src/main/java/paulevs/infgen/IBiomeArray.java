package paulevs.infgen;

import net.minecraft.world.biome.Biome;

public interface IBiomeArray
{
	public void setBiome(int x, int z, Biome biome);
	
	public void setBiome(int x, int y, int z, Biome biome);
}
