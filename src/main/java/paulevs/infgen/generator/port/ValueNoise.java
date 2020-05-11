package paulevs.infgen.generator.port;

import java.util.Random;

public final class ValueNoise
{
	private int[] values;
	private double offsetX;
	private double offsetY;
	private double offsetZ;

	public ValueNoise()
	{
		this(new Random());
	}

	public ValueNoise(Random random)
	{
		this.values = new int[512];
		this.offsetX = random.nextDouble() * 256;
		this.offsetY = random.nextDouble() * 256;
		this.offsetZ = random.nextDouble() * 256;

		for (int i = 0; i < 256; this.values[i] = i++);

		for (int i = 0; i < 256; ++i)
		{
			int index = random.nextInt(256 - i) + i;
			int value = this.values[i];
			this.values[i] = this.values[index];
			this.values[index] = value;
			this.values[i + 256] = this.values[i];
		}
	}

	private double interpolate(double x, double y, double z)
	{
		double posX = x + this.offsetX;
		double posY = y + this.offsetY;
		double posZ = z + this.offsetZ;
		int ix = floor(posX) & 255;
		int iy = floor(posY) & 255;
		int iz = floor(posZ) & 255;
		posX = posX - (double) floor(posX);
		posY = posY - (double) floor(posY);
		posZ = posZ - (double) floor(posZ);
		double mixX = sigmoid(posX);
		double mixY = sigmoid(posY);
		double mixZ = sigmoid(posZ);
		int valX = this.values[ix] + iy;
		int valZ = this.values[valX] + iz;
		valX = this.values[valX + 1] + iz;
		ix = this.values[ix + 1] + iy;
		iy = this.values[ix] + iz;
		ix = this.values[ix + 1] + iz;
		return lerp(mixZ,
				lerp(mixY, lerp(mixX, a(this.values[valZ], posX, posY, posZ), a(this.values[iy], posX - 1.0D, posY, posZ)),
						lerp(mixX, a(this.values[valX], posX, posY - 1.0D, posZ),
								a(this.values[ix], posX - 1.0D, posY - 1.0D, posZ))),
				lerp(mixY, lerp(mixX, a(this.values[valZ + 1], posX, posY, posZ - 1.0D),
						a(this.values[iy + 1], posX - 1.0D, posY, posZ - 1.0D)),
						lerp(mixX, a(this.values[valX + 1], posX, posY - 1.0D, posZ - 1.0D),
								a(this.values[ix + 1], posX - 1.0D, posY - 1.0D, posZ - 1.0D))));
	}

	/**
	 * Sigmoid function (only 0-1 input)
	 * @param x
	 * @return
	 */
	private static double sigmoid(double x)
	{
		return x * x * x * (x * (x * 6.0D - 15.0D) + 10.0D);
	}

	private static double lerp(double mix, double a, double b)
	{
		return a + mix * (b - a);
	}

	private static double a(int var0, double var1, double var3, double var5)
	{
		int var12;
		double var8 = (var12 = var0 & 15) < 8 ? var1 : var3;
		double var10 = var12 < 4 ? var3 : (var12 != 12 && var12 != 14 ? var5 : var1);
		return ((var12 & 1) == 0 ? var8 : -var8) + ((var12 & 2) == 0 ? var10 : -var10);
	}

	public final double eval(double x, double y)
	{
		return this.interpolate(x, y, 0.0D);
	}

	public final double eval(double x, double y, double z)
	{
		return this.interpolate(x, y, z);
	}

	public static int floor(double x)
	{
		int ix = (int) x;
		if (x < ix)
		{
			return ix - 1;
		}
		return ix;
	}
}
