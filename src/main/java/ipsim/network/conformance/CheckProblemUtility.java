package ipsim.network.conformance;

import fj.F;
import ipsim.network.Network;
import ipsim.network.Problem;
import org.jetbrains.annotations.Nullable;

public class CheckProblemUtility {
    public static CheckResult check(final Network network, final F<Problem, CheckResult> func) {
        @Nullable
        final Problem problem = network.problem;

        return problem == null ? CheckResultUtility.fine() : func.f(problem);
    }
}