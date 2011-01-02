package ipsim.network;

import fj.F;
import fj.data.Either;
import ipsim.network.ProblemBuilder.Stage2;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.ProblemUtility.createProblem;
import static ipsim.network.ProblemUtility.isValidNetworkNumber;

public class ProblemBuilder extends F<Integer, Either<Stage2, String>> {
    public Either<Stage2, String> withSubnets(final int i) {
        return f(i);
    }

    @Override
    @NotNull
    public Either<Stage2, String> f(@NotNull final Integer numberOfSubnets) {
        if (numberOfSubnets < Problem.MIN_SUBNETS || numberOfSubnets > Problem.MAX_SUBNETS)
            return Either.right("Invalid number of subnets " + numberOfSubnets);

        return Either.left(new Stage2(numberOfSubnets));
    }

    public static class Stage2 extends F<NetBlock, Either<Problem, String>> {
        private final int subnets;

        public Stage2(final int subnets) {
            this.subnets = subnets;
        }

        public Either<Problem, String> withNetBlock(final NetBlock block) {
            return f(block);
        }

        @Override
        @NotNull
        public Either<Problem, String> f(@NotNull final NetBlock netBlock) {
            final IPAddress networkNumber = netBlock.networkNumber;
            final int rawNetworkNumber = networkNumber.rawValue;

            if (isValidNetworkNumber(networkNumber)) {
                final int rawMask = netBlock.netMask.rawValue;
                if (NetMaskUtility.isValid(netBlock.netMask) && (rawNetworkNumber & rawMask) == rawNetworkNumber)
                    return Either.left(createProblem(netBlock, subnets).some());

                return Either.right("Invalid netMask " + netBlock.netMask.toString());
            }

            return Either.right("Invalid network number " + networkNumber.asString());
        }
    }
}