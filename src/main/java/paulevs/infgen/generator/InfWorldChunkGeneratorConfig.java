package paulevs.infgen.generator;

import java.util.HashMap;

import com.mojang.datafixers.Dynamic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.VillageFeature;

public class InfWorldChunkGeneratorConfig extends OverworldChunkGeneratorConfig
{
	private boolean onlyInfdev;
	private int biomeSize;
	private boolean hasVillages;
	private boolean hasDungeons;
	private boolean hasMineshafts;
	private boolean hasOtherStructures;
	
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
		HashMap<Tag, Tag> map = new HashMap<Tag, Tag>();
		
		map.put(dynamicOps.createString("only_infdev_biome"), dynamicOps.createBoolean(false));
		map.put(dynamicOps.createString("biome_size"), dynamicOps.createInt(3));
		map.put(dynamicOps.createString("has_villages"), dynamicOps.createBoolean(true));
		map.put(dynamicOps.createString("has_dungeons"), dynamicOps.createBoolean(true));
		map.put(dynamicOps.createString("has_mineshafts"), dynamicOps.createBoolean(true));
		map.put(dynamicOps.createString("has_other_structures"), dynamicOps.createBoolean(true));
		
		return new Dynamic(dynamicOps, dynamicOps.createMap(map));
	}

	@SuppressWarnings("rawtypes")
	public static InfWorldChunkGeneratorConfig fromDynamic(Dynamic dynamic)
	{
		InfWorldChunkGeneratorConfig config = new InfWorldChunkGeneratorConfig();
		
		config.onlyInfdev = dynamic.get("only_infdev_biome").asBoolean(false);
		config.biomeSize = dynamic.get("biome_size").asInt(3);
		config.hasVillages = dynamic.get("has_villages").asBoolean(true);
		config.hasDungeons = dynamic.get("has_dungeons").asBoolean(true);
		config.hasMineshafts = dynamic.get("has_mineshafts").asBoolean(true);
		config.hasOtherStructures = dynamic.get("has_other_structures").asBoolean(true);
		
		return config;
	}
	
	public boolean onlyInfdev()
	{
		return onlyInfdev;
	}
	
	public boolean hasStructure(StructureFeature<? extends FeatureConfig> structureFeature)
	{
		if (structureFeature instanceof VillageFeature)
			return hasVillages;
		else if (structureFeature instanceof MineshaftFeature)
			return hasMineshafts;
		else if (structureFeature instanceof AbstractTempleFeature)
			return hasDungeons;
		else
			return hasOtherStructures;
	}
}
