package paulevs.infgen.generator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class InfWorldChunkGeneratorConfig extends OverworldChunkGeneratorConfig
{
	@Override
	public int getBiomeSize()
	{
		return 3;
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
		return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of()));
	}

	@SuppressWarnings("rawtypes")
	public static InfWorldChunkGeneratorConfig fromDynamic(Dynamic dynamic)
	{
		return new InfWorldChunkGeneratorConfig();
	}
}
