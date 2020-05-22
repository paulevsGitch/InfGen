package paulevs.infgen.generator;

import java.util.Set;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import paulevs.infgen.InfGen;

public class InfWorldBiomeSource extends BiomeSource
{
	private final BiomeLayerSampler biomeSampler;
	private final boolean onlyInfdev;
	
	public InfWorldBiomeSource(VanillaLayeredBiomeSourceConfig config, boolean onlyInfdev, Set<Biome> biomes)
	{
		super(biomes);
		this.biomeSampler = onlyInfdev ? null : InfBiomeLayer.build(config.getSeed(), config.getGeneratorType(), config.getGeneratorSettings(), biomes);
		this.onlyInfdev = onlyInfdev;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
	{
		return onlyInfdev ? InfGen.INFDEV : this.biomeSampler.sample(biomeX, biomeZ);
	}
}