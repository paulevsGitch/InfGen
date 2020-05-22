package paulevs.infgen.generator;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;

public class SurfaceBiomes
{
	public static final Set<Biome> BIOMES;
	
	static
	{
		Set<Biome> biomes = new HashSet<Biome>();
		for (Biome biome: Registry.BIOME)
			if (!biome.hasParent() && isValidCategory(biome.getCategory()) && biome.getDepth() > -0.3)
			{
				biomes.add(biome);
			}
		BIOMES = ImmutableSet.copyOf(biomes);
	}
	
	private static boolean isValidCategory(Category category)
	{
		return 	category != Category.NONE &&
				category != Category.BEACH &&
				category != Category.OCEAN &&
				category != Category.NETHER &&
				category != Category.THEEND;
	}
}
