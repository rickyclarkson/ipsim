package ipsim.network;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.network.ProblemBuilder.Stage2;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import java.util.Random;

public enum ProblemDifficulty {
    EASY {
        @Override
        public Problem invoke() {
            final ProblemBuilder builder = new ProblemBuilder();

            for (int a = 0; a < 1000; a++) {
                int randomNumber = random.nextInt(65536);
                randomNumber <<= 16;

                final IPAddress address = new IPAddress(randomNumber);
                final NetBlock block = new NetBlock(address, NetMaskUtility.createNetMaskFromPrefixLength(16));
                if (ProblemUtility.isValidNetworkNumber(block.networkNumber) && !ProblemUtility.isReservedNetworkNumber(block)) {
                    final Stage2 stage2 = builder.withSubnets(3).left().value();
                    return stage2.withNetBlock(block).left().value();
                }
            }

            throw new RuntimeException();
        }
    },
    MEDIUM {
        @Override
        public Problem invoke() {
            return generate(17, 4, 4);
        }
    },
    HARD {
        @Override
        public Problem invoke() {
            return generate(22, 2, 5);
        }
    };

    public abstract Problem invoke();

    public static final UnitTest testGeneration = new UnitTest() {
        @Override
        public boolean invoke() {
            EASY.invoke();
            MEDIUM.invoke();
            HARD.invoke();
            return true;
        }

        public String toString() {
            return "ProblemDifficulty.testGeneration";
        }
    };

    private static Problem generate(final int randomStart, final int randomRange, final int numSubnets) {
        final ProblemBuilder builder = new ProblemBuilder();

        for (int a = 0; a < 100; a++) {
            final int random1 = random.nextInt(65536);
            final int random2 = random.nextInt(65536);
            final int random3 = random1 << 16 + random2;

            final int netmaskLength = random.nextInt(randomRange) + randomStart;
            final NetMask netmask = NetMaskUtility.createNetMaskFromPrefixLength(netmaskLength);
            final int rawNetworkNumber = random3 & netmask.rawValue;

            if (!(0 == (rawNetworkNumber & 0xFF00))) {
                final IPAddress networkNumber = new IPAddress(rawNetworkNumber);

                final NetBlock netBlock = new NetBlock(networkNumber, netmask);
                if (ProblemUtility.isValidNetworkNumber(netBlock.networkNumber) && !ProblemUtility.isReservedNetworkNumber(netBlock)) {
                    final Stage2 stage2 = builder.withSubnets(numSubnets).left().value();
                    return stage2.withNetBlock(netBlock).left().value();
                }
            }
        }

        throw new RuntimeException();
    }

    static final Random random = new Random();
}