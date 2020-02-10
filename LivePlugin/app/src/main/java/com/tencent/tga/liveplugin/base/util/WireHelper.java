package com.tencent.tga.liveplugin.base.util;

import com.squareup.tga.Wire;

public class WireHelper
{
	private static Wire wire = null;
	
	public static Wire wire()
	{
		if(wire == null)
		{
			wire = new Wire();
		}
		return wire;
	}
}
