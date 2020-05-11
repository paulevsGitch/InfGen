package paulevs.infgen.generator;

import java.util.function.LongFunction;

import net.minecraft.world.biome.layer.OceanTemperatureLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.level.LevelGeneratorType;

public class InfOceanLayer
{
	public static BiomeLayerSampler build(long seed, LevelGeneratorType generatorType, InfWorldChunkGeneratorConfig settings)
	{
		LayerFactory<CachingLayerSampler> layerFactory = build(generatorType, settings, (salt) -> {
			return new CachingLayerContext(25, seed, salt);
		});
		return new BiomeLayerSampler(layerFactory);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LevelGeneratorType generatorType, InfWorldChunkGeneratorConfig settings, LongFunction<C> contextProvider)
	{
		LayerFactory<T> oceans = OceanTemperatureLayer.INSTANCE.create((LayerSampleContext) contextProvider.apply(2L));
		
		int biomeSize = settings.getBiomeSize();
		oceans = stack(2001L, ScaleLayer.NORMAL, oceans, biomeSize, contextProvider);
		
		return oceans;
	}

	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider)
	{
		LayerFactory<T> layerFactory = parent;

		for(int i = 0; i < count; ++i)
		{
			layerFactory = layer.create((LayerSampleContext<T>) contextProvider.apply(seed + (long)i), layerFactory);
		}

		return layerFactory;
	}
}
