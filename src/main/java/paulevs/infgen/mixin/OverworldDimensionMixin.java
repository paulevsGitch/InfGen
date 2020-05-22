package paulevs.infgen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.Dynamic;

import net.minecraft.datafixer.NbtOps;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import paulevs.infgen.InfdevWorldType;
import paulevs.infgen.generator.InfChunkGenerator;
import paulevs.infgen.generator.InfWorldBiomeSource;
import paulevs.infgen.generator.InfWorldChunkGeneratorConfig;

@Mixin(OverworldDimension.class)
public abstract class OverworldDimensionMixin extends Dimension
{
	public OverworldDimensionMixin(World world, DimensionType type, float f)
	{
		super(world, type, f);
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "createChunkGenerator", at = @At("HEAD"), cancellable = true)
	private void makeChunkGenerator(CallbackInfoReturnable<ChunkGenerator<? extends ChunkGeneratorConfig>> info)
	{
		LevelGeneratorType levelGeneratorType = this.world.getLevelProperties().getGeneratorType();
		if (levelGeneratorType == InfdevWorldType.INFDEV)
		{
			@SuppressWarnings("rawtypes")
			InfWorldChunkGeneratorConfig config = InfWorldChunkGeneratorConfig.fromDynamic(new Dynamic(NbtOps.INSTANCE, this.world.getLevelProperties().getGeneratorOptions()));
			
			VanillaLayeredBiomeSourceConfig bSource = new VanillaLayeredBiomeSourceConfig(this.world.getLevelProperties());
			bSource.setGeneratorSettings(config);
			BiomeSource biomeSource = new InfWorldBiomeSource(bSource, config.onlyInfdev(), config.getBiomes());
			
			info.setReturnValue(InfChunkGenerator.INFDEV_CHUNK_GEN.create(this.world, biomeSource, config));
			info.cancel();
		}
	}
}
