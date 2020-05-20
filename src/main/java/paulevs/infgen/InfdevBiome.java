package paulevs.infgen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class InfdevBiome extends Biome
{
	public static final InfdevPopulatorFeature INFDEV_POPULATOR = Registry.register(Registry.FEATURE, new Identifier(InfGen.MOD_ID, "inf_populator"), new InfdevPopulatorFeature(DefaultFeatureConfig::deserialize));
	
	// ARGB
	private static final int GRASS_COLOR = getColor(178, 255, 107);
	private static final int LEAVES_COLOR = getColor(85, 245, 61);
	private static final int FOG_COLOR = getColor(153, 204, 255);

	protected InfdevBiome()
	{
		super(new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.FOREST)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.6F)
				.downfall(0.6F)
				.waterColor(getColor(43, 66, 255))
				.waterFogColor(getColor(0, 0, 99 - 82))
				.parent(null));

		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.PIG, 10, 4, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.COW, 8, 4, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 5, 4, 4));
		this.addSpawn(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
		
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, INFDEV_POPULATOR.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT)));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getGrassColorAt(double x, double z)
	{
		return GRASS_COLOR;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getFoliageColor()
	{
		return LEAVES_COLOR;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getSkyColor()
	{
		return FOG_COLOR;
	}
	
	private static int getColor(int r, int g, int b)
	{
		return (255 << 24) | (r << 16) | (g << 8) | b;
	}
}