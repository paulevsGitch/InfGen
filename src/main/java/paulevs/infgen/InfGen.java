package paulevs.infgen;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import paulevs.infgen.generator.InfChunkGenerator;

public class InfGen implements ModInitializer
{
	public static final String MOD_ID = "infgen";
	
	public static final Biome INFDEV = registerBiome("infdev", new InfdevBiome());
	
	public static ButtonWidget button_customize;
	
	@Override
	public void onInitialize()
	{
		InfChunkGenerator.register();
		InfdevWorldType.register();
	}
	
	public static Identifier getID(String id)
	{
		return new Identifier(MOD_ID, id);
	}
	
	private static Biome registerBiome(String name, Biome biome)
	{
		return Registry.register(Registry.BIOME, new Identifier(MOD_ID, name), biome);
	}
}
