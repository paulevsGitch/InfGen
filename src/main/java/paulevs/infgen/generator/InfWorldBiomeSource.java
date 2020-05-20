package paulevs.infgen.generator;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import paulevs.infgen.InfGen;

public class InfWorldBiomeSource extends BiomeSource
{
	private final BiomeLayerSampler biomeSampler;
	private static final Set<Biome> BIOMES;
	private final boolean onlyInfdev;
	
	public InfWorldBiomeSource(VanillaLayeredBiomeSourceConfig config, boolean onlyInfdev)
	{
		super(BIOMES);
		this.biomeSampler = onlyInfdev ? null : InfBiomeLayer.build(config.getSeed(), config.getGeneratorType(), config.getGeneratorSettings());
		this.onlyInfdev = onlyInfdev;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
	{
		return onlyInfdev ? InfGen.INFDEV : this.biomeSampler.sample(biomeX, biomeZ);
	}
	
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