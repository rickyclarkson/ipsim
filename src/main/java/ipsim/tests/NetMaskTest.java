package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.network.connectivity.ip.NetMask;

public class NetMaskTest
{
	public static UnitTest testEquality()
	{
		return new UnitTest()
		{
			@Override
            public boolean invoke()
			{
				return new NetMask(0).equals(new NetMask(0));
			}

			public String toString()
			{
				return "testEquality";
			}
		};
	}
}