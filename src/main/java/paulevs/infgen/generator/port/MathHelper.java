package paulevs.infgen.generator.port;

public class MathHelper
{
	private static final float[] SINUSOIDE = new float[65536];

	public static final float sin(float x)
	{
		return SINUSOIDE[(int) (x * 10430.378F) & 65535];
	}
	
	public static final float cos(float x)
	{
		return SINUSOIDE[(int)(x * 10430.378F + 16384.0F) & 65535];
	}

	static
	{
		for(int var0 = 0; var0 < 65536; ++var0)
		{
			SINUSOIDE[var0] = (float)Math.sin((double)var0 * 3.141592653589793D * 2.0D / 65536.0D);
		}
	}

	public static int floor(double x)
	{
		return x < 0 ? (int) (x - 1) : (int) x;
	}
}
