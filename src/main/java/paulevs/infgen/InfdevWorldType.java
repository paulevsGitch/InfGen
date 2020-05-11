package paulevs.infgen;

import java.lang.reflect.Constructor;

import net.minecraft.world.level.LevelGeneratorType;

public class InfdevWorldType
{
	public static final LevelGeneratorType INFDEV = makeInstance(10, "inf_gen");
	
	private static LevelGeneratorType makeInstance(int id, String name)
	{
		try
		{
			for(Constructor<?> constructor: LevelGeneratorType.class.getDeclaredConstructors())
				if (constructor.getParameterCount() == 2)
				{
					constructor.setAccessible(true);
					return (LevelGeneratorType) constructor.newInstance(id, name);
				}
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void register() {}
}
