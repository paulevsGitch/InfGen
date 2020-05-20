package paulevs.infgen.generator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class InfWorldChunkGeneratorConfig extends OverworldChunkGeneratorConfig
{
	private boolean onlyInfdev;
	private int biomeSize;
	
	@Override
	public int getBiomeSize()
	{
		return biomeSize;
	}

	@Override
	public int getRiverSize()
	{
		return -1;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Environment(EnvType.CLIENT)
	public Dynamic<?> toDynamic(NbtOps dynamicOps)
	{
		return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(
				dynamicOps.createString("only_infdev_biome"), dynamicOps.createBoolean(false),
				dynamicOps.createString("biome_size"), dynamicOps.createInt(3)
				)));
	}

	@SuppressWarnings("rawtypes")
	public static InfWorldChunkGeneratorConfig fromDynamic(Dynamic dynamic)
	{
		InfWorldChunkGeneratorConfig config = new InfWorldChunkGeneratorConfig();
		
		config.onlyInfdev = dynamic.get("only_infdev_biome").asBoolean(false);
		config.biomeSize = dynamic.get("biome_size").asInt(3);
		
		return config;
	}
	
	public boolean onlyInfdev()
	{
		return onlyInfdev;
	}
}
