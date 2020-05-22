package paulevs.infgen.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.TemperatureGroup;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class BiomeLayerSamplerConfigurable extends BiomeLayerSampler
{
	private static final Random RANDOM = new Random();
	
	private List<Biome> coldBiomes = new ArrayList<Biome>();
	private List<Biome> normBiomes = new ArrayList<Biome>();
	private List<Biome> warmBiomes = new ArrayList<Biome>();
	private List<Biome> all = new ArrayList<Biome>();
	
	public BiomeLayerSamplerConfigurable(LayerFactory<CachingLayerSampler> layerFactory, Set<Biome> biomes)
	{
		super(layerFactory);
		for (Biome biome: biomes)
		{
			TemperatureGroup group = biome.getTemperatureGroup();
			if (group == TemperatureGroup.MEDIUM)
				normBiomes.add(biome);
			else if (group == TemperatureGroup.WARM)
				warmBiomes.add(biome);
			else if (group == TemperatureGroup.COLD)
				coldBiomes.add(biome);
			all.add(biome);
		}
	}

	public Biome sample(int x, int y)
	{
		Biome biome = super.sample(x, y);
		if (all.contains(biome))
			return biome;
		
		TemperatureGroup group = biome.getTemperatureGroup();
		long seed = Registry.BIOME.getId(biome).hashCode();
		RANDOM.setSeed(seed);
		RANDOM.nextInt();
		RANDOM.nextInt();
		
		if (group == TemperatureGroup.MEDIUM && !normBiomes.isEmpty())
			return normBiomes.get(RANDOM.nextInt(normBiomes.size()));
		else if (group == TemperatureGroup.WARM && !warmBiomes.isEmpty())
			return warmBiomes.get(RANDOM.nextInt(warmBiomes.size()));
		else if (group == TemperatureGroup.COLD && !coldBiomes.isEmpty())
			return coldBiomes.get(RANDOM.nextInt(coldBiomes.size()));
		
		return all.get(RANDOM.nextInt(all.size()));
	}
}