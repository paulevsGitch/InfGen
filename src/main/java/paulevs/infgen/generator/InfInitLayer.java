package paulevs.infgen.generator;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum InfInitLayer implements InitLayer
{
	INSTANCE;
	
	@Override
	public int sample(LayerRandomnessSource context, int x, int y)
	{
		return Registry.BIOME.getRawId(Biomes.BEACH);
	}
}
