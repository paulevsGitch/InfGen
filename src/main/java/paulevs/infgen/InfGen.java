package paulevs.infgen;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import paulevs.infgen.generator.InfChunkGenerator;

public class InfGen implements ModInitializer
{
	public static final String MOD_ID = "infgen";
	
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
}
