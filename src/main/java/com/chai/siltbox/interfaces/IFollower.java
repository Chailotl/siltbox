package com.chai.siltbox.interfaces;

import net.minecraft.util.math.BlockPos;

public interface IFollower
{
	public boolean getFollowing();
	public void setFollowing(boolean bool);
	public BlockPos getWanderOrigin();
	public void removeWanderOrigin();
}