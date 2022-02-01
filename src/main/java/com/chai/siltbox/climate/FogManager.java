package com.chai.siltbox.climate;

import net.minecraft.util.math.Vec3d;

public class FogManager
{
	public static boolean foggy = false;
	public static boolean sandstorm = false;
	public static boolean blizzard = false;

	public static final Vec3d SANDSTORM_COLOR = new Vec3d(213f/255f, 196f/255f, 150f/255f);
	public static final Vec3d BLIZZARD_COLOR = new Vec3d(240f/255f, 253f/255f, 253f/255f);
	private static final int MAX_FOG_TIME = 15 * 20;

	private static int fogTime = 0;

	public static float getFogLevel()
	{
		return fogTime / (float) MAX_FOG_TIME;
	}

	public static float getFogStart()
	{
		return 1f - fogTime / (float) MAX_FOG_TIME;
	}

	public static float getFogEnd()
	{
		return 1f;
	}

	public static void tick()
	{
		fogTime += foggy ? 1 : -1;

		fogTime = Math.max(0, Math.min(MAX_FOG_TIME, fogTime));
	}
}