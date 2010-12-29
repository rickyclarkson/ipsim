package ipsim.network;

import fpeas.either.Either;
import static fpeas.either.EitherUtility.left;
import static fpeas.either.EitherUtility.right;
import fpeas.function.Function;
import fpeas.maybe.MaybeUtility;
import static ipsim.network.ProblemUtility.createProblem;
import static ipsim.network.ProblemUtility.isValidNetworkNumber;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import org.jetbrains.annotations.NotNull;

public class ProblemBuilder implements Function<Integer,Either<ProblemBuilder.Stage2,String>>
{
	public Either<Stage2,String> withSubnets(final int i)
	{
		return run(i);
	}

	@Override
    @NotNull
	public Either<Stage2,String> run(@NotNull final Integer numberOfSubnets)
	{
		if (numberOfSubnets<Problem.MIN_SUBNETS||numberOfSubnets>Problem.MAX_SUBNETS)
			return right("Invalid number of subnets "+numberOfSubnets);

		return left(new Stage2(numberOfSubnets));
	}

	public static class Stage2 implements Function<NetBlock,Either<Problem,String>>
	{
		private final int subnets;

		public Stage2(final int subnets)
		{
			this.subnets=subnets;
		}

		public Either<Problem,String> withNetBlock(final NetBlock block)
		{
			return run(block);
		}

		@Override
        @NotNull
		public Either<Problem,String> run(@NotNull final NetBlock netBlock)
		{
			final IPAddress networkNumber=netBlock.networkNumber;
			final int rawNetworkNumber=networkNumber.rawValue;

			if (isValidNetworkNumber(networkNumber))
			{
				final int rawMask=netBlock.netMask.rawValue;
				if (NetMaskUtility.isValid(netBlock.netMask)&&(rawNetworkNumber&rawMask)==rawNetworkNumber)
					return left(MaybeUtility.asJust(createProblem(netBlock,subnets)));

				return right("Invalid netMask "+netBlock.netMask.toString());
			}

			return right("Invalid network number "+networkNumber.asString());
		}
	}
}