package paulevs.infgen;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import paulevs.infgen.generator.InfChunkGenerator;
import paulevs.infgen.generator.port.InfGenPort;

public class InfdevPopulatorFeature extends Feature<DefaultFeatureConfig>
{
	private InfGenPort generator;
	
	public InfdevPopulatorFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer)
	{
		super(configDeserializer);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, DefaultFeatureConfig config)
	{
		if (generator instanceof InfChunkGenerator)
			((InfChunkGenerator) generator).populate(world, pos.getX() >> 4, pos.getZ() >> 4);
		else
		{
			if (this.generator == null)
				this.generator = new InfGenPort(world.getSeed());
			this.generator.populate(world, world.getChunk(pos.getX() >> 4, pos.getZ() >> 4));
		}
		return true;
	}
}