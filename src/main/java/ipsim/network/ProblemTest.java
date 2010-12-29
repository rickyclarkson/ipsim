package ipsim.network;

import com.rickyclarkson.testsuite.UnitTest;
import static fpeas.either.EitherUtility.isRight;
import static fpeas.either.EitherUtility.unsafeLeft;
import static ipsim.Caster.equalT;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;

public class ProblemTest
{
	public static UnitTest instance()
	{
		return new UnitTest()
		{
			@Override
            public boolean invoke()
			{
				return test1()&&testInvalidNetMaskRejection();
			}

			private boolean testInvalidNetMaskRejection()
			{
				return invalidNetMask("255.255.22.0")&&invalidNetMask("255.0.0.0");
			}

			private boolean invalidNetMask(final String mask)
			{
				try
				{
					return isRight(unsafeLeft(new ProblemBuilder().run(5)).run(new NetBlock(IPAddressUtility.valueOf("146.87.0.0"),NetMaskUtility.valueOf(mask))));
				}
				catch (final CheckedNumberFormatException exception)
				{
					throw new RuntimeException(exception);
				}
			}

			public boolean test1()
			{
				final IPAddress address=new IPAddress(221<<24);
				final NetMask mask=NetMaskUtility.getNetMask(255<<24);

				final NetBlock netBlock=new NetBlock(address, mask);

				try
				{
					return equalT(netBlock.networkNumber,IPAddressUtility.valueOf("221.0.0.0"));
				}
				catch (final CheckedNumberFormatException exception)
				{
					return false;
				}
			}

			public String toString()
			{
				return "ProblemTest";
			}
		};
	}
}