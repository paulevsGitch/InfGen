package paulevs.infgen.generator.port;

import java.util.Random;

public final class ValueNoiseOctaved
{
	private ValueNoise[] array;
	private int octaves;

	public ValueNoiseOctaved(Random random, int octaves)
	{
		this.octaves = octaves;
		this.array = new ValueNoise[octaves];

		for(int i = 0; i < octaves; ++i)
		{
			this.array[i] = new ValueNoise(random);
		}
	}

	public final double eval(double x, double y)
	{
		double res = 0.0D;
		double div = 1.0D;

		for(int var9 = 0; var9 < this.octaves; ++var9)
		{
			res += this.array[var9].eval(x / div, y / div) * div;
			div *= 2.0D;
		}

		return res;
	}

	public final double eval(double x, double y, double z)
	{
		double res = 0.0D;
		double div = 1.0D;

		for(int var11 = 0; var11 < this.octaves; ++var11)
		{
			res += this.array[var11].eval(x / div, y / div, z / div) * div;
			div *= 2.0D;
		}

		return res;
	}
}
